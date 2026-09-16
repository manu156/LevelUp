package com.manu156.levelup.data.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.manu156.levelup.data.git.ConflictResolutionChoice
import com.manu156.levelup.data.git.GitConflictItem
import com.manu156.levelup.data.git.GitSyncConfig
import com.manu156.levelup.data.git.GitSyncManager
import com.manu156.levelup.data.git.GitSyncResult
import com.manu156.levelup.data.model.DayProgress
import com.manu156.levelup.data.model.DayStats
import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.data.model.UserProfile
import com.manu156.levelup.data.model.WeeklyGoalProgress
import com.manu156.levelup.data.model.WorkSession
import com.manu156.levelup.service.SessionForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class FocusSessionRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("levelup_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Default)
    private var tickerJob: Job? = null

    private val sessionsDir = File(context.filesDir, "sessions_data")

    // State flows
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _activeTaskTitle = MutableStateFlow("Focus Session")
    val activeTaskTitle: StateFlow<String> = _activeTaskTitle.asStateFlow()

    private val _activeCategory = MutableStateFlow(SessionCategory.DEEP_WORK)
    val activeCategory: StateFlow<SessionCategory> = _activeCategory.asStateFlow()

    private val _activeStartTime = MutableStateFlow(0L)
    val activeStartTime: StateFlow<Long> = _activeStartTime.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _todaySessions = MutableStateFlow<List<WorkSession>>(emptyList())
    val todaySessions: StateFlow<List<WorkSession>> = _todaySessions.asStateFlow()

    private val _allSessions = MutableStateFlow<List<WorkSession>>(emptyList())
    val allSessions: StateFlow<List<WorkSession>> = _allSessions.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _dailyGoalHours = MutableStateFlow(8f)
    val dailyGoalHours: StateFlow<Float> = _dailyGoalHours.asStateFlow()

    // Git Sync States
    private val gitSyncManager = GitSyncManager(context)

    private val _gitSyncConfig = MutableStateFlow(GitSyncConfig.load(context))
    val gitSyncConfig: StateFlow<GitSyncConfig> = _gitSyncConfig.asStateFlow()

    private val _isGitSyncing = MutableStateFlow(false)
    val isGitSyncing: StateFlow<Boolean> = _isGitSyncing.asStateFlow()

    private val _gitSyncMessage = MutableStateFlow<String?>(null)
    val gitSyncMessage: StateFlow<String?> = _gitSyncMessage.asStateFlow()

    private val _pendingGitConflicts = MutableStateFlow<List<GitConflictItem>>(emptyList())
    val pendingGitConflicts: StateFlow<List<GitConflictItem>> = _pendingGitConflicts.asStateFlow()

    init {
        loadSessions()

        val savedName = prefs.getString(KEY_USER_NAME, null)
        val initialName = savedName ?: "Alex"
        val savedStreak = prefs.getInt(KEY_USER_STREAK, 0)
        val savedTotalWork = prefs.getInt(KEY_TOTAL_WORK, 0)
        val savedGoal = prefs.getFloat(KEY_DAILY_GOAL, 8f)
        val savedAvatar = prefs.getString(KEY_AVATAR_URI, "preset:hug") ?: "preset:hug"

        _dailyGoalHours.value = savedGoal
        _userProfile.value = UserProfile(
            name = initialName,
            subtitle = "Building a better tomorrow ✨",
            dayStreak = savedStreak,
            totalWorkHours = savedTotalWork,
            dailyGoalHours = savedGoal.toInt(),
            avatarUri = savedAvatar
        )

        val savedStartTime = prefs.getLong(KEY_ACTIVE_START, 0L)
        updateUserProfileStats()
        if (savedStartTime > 0L) {
            val title = prefs.getString(KEY_ACTIVE_TITLE, "Focus Session") ?: "Focus Session"
            val categoryName = prefs.getString(KEY_ACTIVE_CATEGORY, null)
                ?: SessionCategory.DEEP_WORK.name
            resumeSession(savedStartTime, title, categoryName)
        }
    }

    private fun saveSessions() {
        try {
            sessionsDir.mkdirs()
            val file = File(sessionsDir, "sessions.json")
            val array = JSONArray()
            _allSessions.value.forEach { session ->
                array.put(sessionToJson(session))
            }
            file.writeText(array.toString(2))
        } catch (e: Exception) {
            kotlin.runCatching {
                val file = File(sessionsDir, "sessions.json")
                file.parentFile?.mkdirs()
                file.writeText("[]")
            }
        }
    }

    private fun loadSessions() {
        try {
            val file = File(sessionsDir, "sessions.json")
            if (file.exists() && file.length() > 0) {
                val content = file.readText()
                val array = JSONArray(content)
                val sessions = mutableListOf<WorkSession>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    sessions.add(sessionFromJson(obj))
                }
                _allSessions.value = sessions.sortedByDescending { it.startTimeMillis }
                _todaySessions.value = sessions.filter { isSessionToday(it) }.sortedByDescending { it.startTimeMillis }
            }
        } catch (e: Exception) {
            kotlin.runCatching {
                val file = File(sessionsDir, "sessions.json")
                if (file.exists()) file.delete()
                _todaySessions.value = emptyList()
                _allSessions.value = emptyList()
            }
        }
    }

    private fun isSessionToday(session: WorkSession): Boolean {
        val now = java.util.Calendar.getInstance()
        val today = now.clone() as java.util.Calendar
        today.set(java.util.Calendar.HOUR_OF_DAY, 0)
        today.set(java.util.Calendar.MINUTE, 0)
        today.set(java.util.Calendar.SECOND, 0)
        today.set(java.util.Calendar.MILLISECOND, 0)
        val sessionDay = java.util.Calendar.getInstance().apply { timeInMillis = session.startTimeMillis }
        return sessionDay.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR)
            && sessionDay.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun sessionToJson(session: WorkSession): JSONObject {
        val obj = JSONObject()
        obj.put("id", session.id)
        obj.put("title", session.title)
        obj.put("category", session.category.name)
        obj.put("startTimeMillis", session.startTimeMillis)
        obj.put("endTimeMillis", session.endTimeMillis)
        obj.put("tag", session.tag)
        obj.put("notes", session.notes)
        return obj
    }

    private fun sessionFromJson(obj: JSONObject): WorkSession {
        val categoryName = obj.optString("category", "DEEP_WORK")
        val category = try {
            SessionCategory.valueOf(categoryName)
        } catch (e: Exception) {
            SessionCategory.DEEP_WORK
        }
        return WorkSession(
            id = obj.optString("id", UUID.randomUUID().toString()),
            title = obj.optString("title", "Focus Session"),
            category = category,
            startTimeMillis = obj.optLong("startTimeMillis", System.currentTimeMillis()),
            endTimeMillis = obj.optLong("endTimeMillis", System.currentTimeMillis() + 3 * 3600 * 1000),
            tag = obj.optString("tag", "Work"),
            notes = obj.optString("notes", "")
        )
    }

    fun hasUserRegistered(): Boolean {
        return prefs.contains(KEY_USER_NAME)
    }

    fun saveUserName(name: String) {
        val cleanName = if (name.isBlank()) "Alex" else name.trim()
        prefs.edit().putString(KEY_USER_NAME, cleanName).apply()
        _userProfile.update { it.copy(name = cleanName) }
    }

    fun saveAvatar(avatarUri: String) {
        prefs.edit().putString(KEY_AVATAR_URI, avatarUri).apply()
        _userProfile.update { it.copy(avatarUri = avatarUri) }
    }

    fun saveCustomAvatarFromUri(sourceUri: android.net.Uri): String? {
        return try {
            val targetFile = java.io.File(context.filesDir, "custom_avatar.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                java.io.FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            val path = targetFile.absolutePath
            saveAvatar(path)
            path
        } catch (e: Exception) {
            null
        }
    }


    fun startSession(title: String, category: SessionCategory = SessionCategory.DEEP_WORK) {
        val now = System.currentTimeMillis()
        _isSessionActive.value = true
        _activeTaskTitle.value = if (title.isBlank()) "Deep Focus" else title
        _activeCategory.value = category
        _activeStartTime.value = now
        _elapsedSeconds.value = 0L

        prefs.edit()
            .putLong(KEY_ACTIVE_START, now)
            .putString(KEY_ACTIVE_TITLE, _activeTaskTitle.value)
            .putString(KEY_ACTIVE_CATEGORY, category.name)
            .apply()

        startTicker()
        startForegroundService()
    }

    private fun startForegroundService() {
        val intent = Intent(context, SessionForegroundService::class.java).apply {
            action = SessionForegroundService.ACTION_START
            putExtra(SessionForegroundService.EXTRA_TASK_TITLE, _activeTaskTitle.value)
            putExtra(SessionForegroundService.EXTRA_START_TIME, _activeStartTime.value)
        }
        context.startForegroundService(intent)
    }

    private fun stopForegroundService() {
        val intent = Intent(context, SessionForegroundService::class.java).apply {
            action = SessionForegroundService.ACTION_STOP
        }
        context.stopService(intent)
    }

    private fun resumeSession(startTime: Long, title: String, categoryName: String) {
        _isSessionActive.value = true
        _activeTaskTitle.value = title
        _activeCategory.value = try {
            SessionCategory.valueOf(categoryName)
        } catch (e: Exception) {
            SessionCategory.DEEP_WORK
        }
        _activeStartTime.value = startTime
        val now = System.currentTimeMillis()
        _elapsedSeconds.value = ((now - startTime) / 1000).coerceAtLeast(0)
        startTicker()
        startForegroundService()
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (_isSessionActive.value) {
                delay(1000)
                val now = System.currentTimeMillis()
                _elapsedSeconds.value = ((now - _activeStartTime.value) / 1000).coerceAtLeast(0)
            }
        }
    }

    fun checkoutSession(notes: String = ""): WorkSession {
        val now = System.currentTimeMillis()
        val start = _activeStartTime.value

        val session = WorkSession(
            id = UUID.randomUUID().toString(),
            title = _activeTaskTitle.value,
            category = _activeCategory.value,
            startTimeMillis = if (start > 0) start else now - 3 * 3600 * 1000,
            endTimeMillis = now,
            tag = "Work",
            notes = notes
        )

        if (isSessionToday(session)) {
            _todaySessions.update { listOf(session) + it }
        }
        _allSessions.update { listOf(session) + it }
        saveSessions()
        updateUserProfileStats()

        _isSessionActive.value = false
        _elapsedSeconds.value = 0L
        _activeStartTime.value = 0L
        tickerJob?.cancel()

        prefs.edit().remove(KEY_ACTIVE_START).remove(KEY_ACTIVE_TITLE).remove(KEY_ACTIVE_CATEGORY).apply()

        val config = gitSyncManager.getSavedConfig()
        if (config.isConfigured) {
            scope.launch {
                gitSyncManager.pushToRemote(listOf(session), config)
            }
        }

        stopForegroundService()
        return session
    }

    fun cancelSession() {
        _isSessionActive.value = false
        _elapsedSeconds.value = 0L
        _activeStartTime.value = 0L
        _activeCategory.value = SessionCategory.DEEP_WORK
        tickerJob?.cancel()
        prefs.edit().remove(KEY_ACTIVE_START).remove(KEY_ACTIVE_TITLE).remove(KEY_ACTIVE_CATEGORY).apply()
        stopForegroundService()
    }

    private fun updateUserProfileStats() {
        val totalHours = getTotalWorkHours()
        val streak = getDayStreak()
        _userProfile.update { it.copy(totalWorkHours = totalHours, dayStreak = streak) }
        prefs.edit()
            .putInt(KEY_TOTAL_WORK, totalHours)
            .putInt(KEY_USER_STREAK, streak)
            .apply()
    }

    fun getTotalWorkHours(): Int {
        val totalMillis = _allSessions.value.sumOf { it.durationMillis }
        return (totalMillis / 3_600_000).toInt()
    }

    fun getDayStreak(): Int {
        val dates = _allSessions.value.map { session ->
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = session.startTimeMillis }
            "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        }.toSet()
        if (dates.isEmpty()) return 0
        var streak = 0
        val cal = java.util.Calendar.getInstance()
        val todayKey = "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        if (!dates.contains(todayKey)) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        while (dates.contains("${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}")) {
            streak++
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }

    fun getDayStatsForDate(timestamp: Long): DayStats {
        val sessions = _allSessions.value.filter { isSameDay(it.startTimeMillis, timestamp) }
        var deepWorkMins = 0L
        var meetingMins = 0L
        var breakMins = 0L

        sessions.forEach { s ->
            when (s.category) {
                SessionCategory.DEEP_WORK -> deepWorkMins += s.durationMinutes
                SessionCategory.MEETINGS -> meetingMins += s.durationMinutes
                SessionCategory.BREAKS -> breakMins += s.durationMinutes
                SessionCategory.GENERAL_WORK -> deepWorkMins += s.durationMinutes
            }
        }

        val totalMins = deepWorkMins + meetingMins + breakMins
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())

        return DayStats(
            dateLabel = sdf.format(java.util.Date(timestamp)),
            totalMinutes = totalMins,
            deepWorkMinutes = deepWorkMins,
            meetingsMinutes = meetingMins,
            breaksMinutes = breakMins,
            sessionCount = sessions.size,
            diffVsYesterdayMinutes = 0
        )
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR)
            && cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun updateDailyGoal(hours: Float) {
        _dailyGoalHours.value = hours
        prefs.edit().putFloat(KEY_DAILY_GOAL, hours).apply()
        _userProfile.update { it.copy(dailyGoalHours = hours.toInt()) }
    }

    fun getDayStats(): DayStats {
        val sessions = _todaySessions.value
        var deepWorkMins = 0L
        var meetingMins = 0L
        var breakMins = 0L

        sessions.forEach { s ->
            when (s.category) {
                SessionCategory.DEEP_WORK -> deepWorkMins += s.durationMinutes
                SessionCategory.MEETINGS -> meetingMins += s.durationMinutes
                SessionCategory.BREAKS -> breakMins += s.durationMinutes
                SessionCategory.GENERAL_WORK -> deepWorkMins += s.durationMinutes
            }
        }

        val totalMins = deepWorkMins + meetingMins + breakMins
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())

        return DayStats(
            dateLabel = sdf.format(java.util.Date()),
            totalMinutes = totalMins,
            deepWorkMinutes = deepWorkMins,
            meetingsMinutes = meetingMins,
            breaksMinutes = breakMins,
            sessionCount = sessions.size,
            diffVsYesterdayMinutes = 0
        )
    }

    fun getWeeklyGoalProgress(): WeeklyGoalProgress {
        val sessions = _todaySessions.value
        val totalMinsToday = sessions.sumOf { it.durationMinutes }
        val hoursToday = totalMinsToday / 60f

        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calendar = java.util.Calendar.getInstance()
        val currentDayOfWeek = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7

        val days = dayNames.mapIndexed { index, name ->
            if (index == currentDayOfWeek) {
                DayProgress(name, hoursToday, isTargetReached = hoursToday >= _dailyGoalHours.value)
            } else {
                DayProgress(name, 0f)
            }
        }

        val completionPercent = if (_dailyGoalHours.value > 0) {
            ((hoursToday / _dailyGoalHours.value) * 100).toInt().coerceAtMost(100)
        } else 0

        return WeeklyGoalProgress(
            targetHoursPerDay = _dailyGoalHours.value,
            currentWorkedHoursToday = hoursToday,
            completionPercent = completionPercent,
            days = days
        )
    }

    fun getAllSessions(): List<WorkSession> = _allSessions.value

    fun upsertSessions(remoteSessions: List<WorkSession>) {
        val existingIds = _allSessions.value.map { it.id }.toSet()
        val toAdd = remoteSessions.filter { it.id !in existingIds }
        if (toAdd.isNotEmpty()) {
            _allSessions.update { existing ->
                (existing + toAdd).sortedByDescending { it.startTimeMillis }
            }
            _todaySessions.update { existing ->
                (existing + toAdd.filter { isSessionToday(it) }).sortedByDescending { it.startTimeMillis }
            }
            saveSessions()
            updateUserProfileStats()
        }
    }

    // Git Repository Synchronization
    fun getSavedGitConfig(): GitSyncConfig = gitSyncManager.getSavedConfig()

    suspend fun pushToGitHub(config: GitSyncConfig): GitSyncResult {
        _isGitSyncing.value = true
        _gitSyncMessage.value = "Pushing to GitHub..."
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val sessions = _allSessions.value
                val result = gitSyncManager.pushToRemote(sessions, config)
                _gitSyncMessage.value = when (result) {
                    is GitSyncResult.Success -> result.message
                    is GitSyncResult.Failure -> result.errorMessage
                    is GitSyncResult.AlreadyUpToDate -> "Already up to date"
                    is GitSyncResult.Conflicts -> "${result.conflictItems.size} conflicts detected"
                }
                result
            } catch (e: Exception) {
                val msg = "Push failed: ${e.message ?: e.javaClass.simpleName}"
                _gitSyncMessage.value = msg
                GitSyncResult.Failure(msg, e)
            } finally {
                _isGitSyncing.value = false
            }
        }
    }

    suspend fun pullFromGitHub(config: GitSyncConfig): Pair<GitSyncResult, List<WorkSession>?> {
        _isGitSyncing.value = true
        _gitSyncMessage.value = "Pulling from GitHub..."
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val (result, sessions) = gitSyncManager.pullFromRemote(config)
                if (result is GitSyncResult.Success && sessions != null) {
                    upsertSessions(sessions)
                }
                _gitSyncMessage.value = when (result) {
                    is GitSyncResult.Success -> result.message
                    is GitSyncResult.Failure -> result.errorMessage
                    is GitSyncResult.AlreadyUpToDate -> "Already up to date"
                    is GitSyncResult.Conflicts -> "${result.conflictItems.size} conflicts detected"
                }
                Pair(result, sessions)
            } catch (e: Exception) {
                val msg = "Pull failed: ${e.message ?: e.javaClass.simpleName}"
                _gitSyncMessage.value = msg
                Pair(GitSyncResult.Failure(msg, e), null)
            } finally {
                _isGitSyncing.value = false
            }
        }
    }

    suspend fun resolveGitConflict(
        conflict: GitConflictItem,
        choice: ConflictResolutionChoice,
        config: GitSyncConfig
    ): Result<Unit> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        gitSyncManager.resolveConflict(conflict, choice, config)
    }

    suspend fun finishGitConflictResolution(config: GitSyncConfig): GitSyncResult = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        gitSyncManager.finishConflictResolution(config)
    }

    suspend fun abortGitConflictMerge(): Result<Unit> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        gitSyncManager.abortConflictedMerge()
    }

    suspend fun testGitHubConnection(config: GitSyncConfig): Result<String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        gitSyncManager.testConnection(config)
    }

    fun testGitHubConnection(config: GitSyncConfig, onResult: (Result<String>) -> Unit) {
        scope.launch {
            onResult(testGitHubConnection(config))
        }
    }

    fun updateGitConfig(
        remoteUrl: String,
        pat: String,
        branch: String,
        authorName: String,
        authorEmail: String
    ) {
        val newConfig = _gitSyncConfig.value.copy(
            remoteUrl = remoteUrl.trim(),
            personalAccessToken = pat.trim(),
            branch = branch.trim().ifBlank { "main" },
            authorName = authorName.trim().ifBlank { "LevelUp User" },
            authorEmail = authorEmail.trim().ifBlank { "user@levelup.local" }
        )
        GitSyncConfig.save(context, newConfig)
        _gitSyncConfig.value = newConfig
    }

    fun clearGitMessage() {
        _gitSyncMessage.value = null
    }

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AVATAR_URI = "avatar_uri"
        private const val KEY_USER_STREAK = "user_streak"
        private const val KEY_TOTAL_WORK = "total_work"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_ACTIVE_START = "active_session_start"
        private const val KEY_ACTIVE_TITLE = "active_session_title"
        private const val KEY_ACTIVE_CATEGORY = "active_session_category"

        @Volatile
        private var INSTANCE: FocusSessionRepository? = null

        fun getInstance(context: Context): FocusSessionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FocusSessionRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
