package com.manu156.levelup.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.manu156.levelup.data.model.DayProgress
import com.manu156.levelup.data.model.DayStats
import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.data.model.UserProfile
import com.manu156.levelup.data.model.WeeklyGoalProgress
import com.manu156.levelup.data.model.WorkSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class FocusSessionRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("levelup_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Default)
    private var tickerJob: Job? = null

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

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _dailyGoalHours = MutableStateFlow(8f)
    val dailyGoalHours: StateFlow<Float> = _dailyGoalHours.asStateFlow()

    init {
        // Load user name
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

        // Check if there was an ongoing session saved
        val savedStartTime = prefs.getLong(KEY_ACTIVE_START, 0L)
        if (savedStartTime > 0L) {
            val title = prefs.getString(KEY_ACTIVE_TITLE, "Focus Session") ?: "Focus Session"
            resumeSession(savedStartTime, title)
        }
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
            .apply()

        startTicker()
    }

    private fun resumeSession(startTime: Long, title: String) {
        _isSessionActive.value = true
        _activeTaskTitle.value = title
        _activeStartTime.value = startTime
        val now = System.currentTimeMillis()
        _elapsedSeconds.value = ((now - startTime) / 1000).coerceAtLeast(0)
        startTicker()
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

        _todaySessions.update { listOf(session) + it }

        _isSessionActive.value = false
        _elapsedSeconds.value = 0L
        _activeStartTime.value = 0L
        tickerJob?.cancel()

        prefs.edit().remove(KEY_ACTIVE_START).remove(KEY_ACTIVE_TITLE).apply()
        return session
    }

    fun cancelSession() {
        _isSessionActive.value = false
        _elapsedSeconds.value = 0L
        _activeStartTime.value = 0L
        tickerJob?.cancel()
        prefs.edit().remove(KEY_ACTIVE_START).remove(KEY_ACTIVE_TITLE).apply()
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
        
        // In a real app, we'd fetch the last 7 days. 
        // For this prototype, we'll just show today's data and zeros for others if empty
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // 0=Mon, ..., 6=Sun
        
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

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AVATAR_URI = "avatar_uri"
        private const val KEY_USER_STREAK = "user_streak"
        private const val KEY_TOTAL_WORK = "total_work"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_ACTIVE_START = "active_session_start"
        private const val KEY_ACTIVE_TITLE = "active_session_title"

        @Volatile
        private var INSTANCE: FocusSessionRepository? = null

        fun getInstance(context: Context): FocusSessionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FocusSessionRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
