package com.manu156.levelup.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.manu156.levelup.data.health.HealthConnectManager
import com.manu156.levelup.data.health.HealthConnectSyncedData
import com.manu156.levelup.data.model.CheckInGoalItem
import com.manu156.levelup.data.model.DailyCheckInRecord
import com.manu156.levelup.data.model.DayProgress
import com.manu156.levelup.data.model.DayStats
import com.manu156.levelup.data.model.Goal
import com.manu156.levelup.data.model.GoalCadence
import com.manu156.levelup.data.model.GoalCategory
import com.manu156.levelup.data.model.GoalUnit
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
import org.json.JSONArray
import org.json.JSONObject
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

    private val _activeTaskTitle = MutableStateFlow("Project Apollo")
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

    private val healthConnectManager = HealthConnectManager(context)

    private val _healthSyncedData = MutableStateFlow(HealthConnectSyncedData())
    val healthSyncedData: StateFlow<HealthConnectSyncedData> = _healthSyncedData.asStateFlow()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    init {
        // Load user profile
        val savedName = prefs.getString(KEY_USER_NAME, null)
        val initialName = savedName ?: "Alex"
        val savedStreak = prefs.getInt(KEY_USER_STREAK, 12)
        val savedTotalWork = prefs.getInt(KEY_TOTAL_WORK, 48)
        val savedGoal = prefs.getFloat(KEY_DAILY_GOAL, 8f)
        val savedAvatar = prefs.getString(KEY_AVATAR_URI, "preset:alex") ?: "preset:alex"

        _dailyGoalHours.value = savedGoal
        _userProfile.value = UserProfile(
            name = initialName,
            subtitle = "Building a better tomorrow ✨",
            dayStreak = savedStreak,
            totalWorkHours = savedTotalWork,
            dailyGoalHours = savedGoal.toInt(),
            avatarUri = savedAvatar
        )

        // Load goals
        val savedGoalsJson = prefs.getString(KEY_GOALS_JSON, null)
        val initialGoals = if (!savedGoalsJson.isNullOrEmpty()) {
            deserializeGoals(savedGoalsJson)
        } else {
            Goal.defaultFixedGoals(workHours = savedGoal.toDouble())
        }
        _goals.value = ensureFixedGoalsExist(initialGoals)

        // Load sessions
        val hasGenerated = prefs.getBoolean(KEY_HAS_DATA, true)
        if (hasGenerated) {
            _todaySessions.value = createDefaultSessions()
        }

        // Check if there was an ongoing session saved
        val savedStartTime = prefs.getLong(KEY_ACTIVE_START, 0L)
        if (savedStartTime > 0L) {
            val title = prefs.getString(KEY_ACTIVE_TITLE, "Focus Session") ?: "Focus Session"
            resumeSession(savedStartTime, title)
        }
    }

    private fun ensureFixedGoalsExist(existing: List<Goal>): List<Goal> {
        val existingCategories = existing.map { it.category }.toSet()
        val defaults = Goal.defaultFixedGoals(workHours = _dailyGoalHours.value.toDouble())
        val missingDefaults = defaults.filter { it.category !in existingCategories }
        return existing + missingDefaults
    }

    private fun saveGoalsToPrefs(goalsList: List<Goal>) {
        _goals.value = goalsList
        val json = serializeGoals(goalsList)
        prefs.edit().putString(KEY_GOALS_JSON, json).apply()

        val workGoal = goalsList.find { it.category == GoalCategory.WORK }
        if (workGoal != null) {
            _dailyGoalHours.value = workGoal.targetValue.toFloat()
            prefs.edit().putFloat(KEY_DAILY_GOAL, workGoal.targetValue.toFloat()).apply()
            _userProfile.update { it.copy(dailyGoalHours = workGoal.targetValue.toInt()) }
        }
    }

    fun updateGoalTarget(goalId: String, newTarget: Double) {
        val updated = _goals.value.map { goal ->
            if (goal.id == goalId) {
                goal.copy(targetValue = newTarget)
            } else goal
        }
        saveGoalsToPrefs(updated)
    }

    fun addCustomGoal(
        title: String,
        targetValue: Double,
        unit: GoalUnit = GoalUnit.HOURS,
        cadence: GoalCadence = GoalCadence.DAILY
    ): Goal {
        val newGoal = Goal(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            title = if (title.isBlank()) "Custom Goal" else title.trim(),
            category = GoalCategory.CUSTOM,
            cadence = cadence,
            targetValue = targetValue,
            unit = unit,
            isFixed = false
        )
        val updated = _goals.value + newGoal
        saveGoalsToPrefs(updated)
        return newGoal
    }

    fun deleteGoal(goalId: String): Boolean {
        val targetGoal = _goals.value.find { it.id == goalId }
        if (targetGoal == null || targetGoal.isFixed) {
            // Non-deletable if fixed goal
            return false
        }
        val updated = _goals.value.filterNot { it.id == goalId }
        saveGoalsToPrefs(updated)
        return true
    }

    suspend fun syncHealthConnectData(useSimulatedIfUnavailable: Boolean = true): HealthConnectSyncedData {
        val synced = healthConnectManager.fetchSyncedData(useSimulatedIfUnavailable = useSimulatedIfUnavailable)
        _healthSyncedData.value = synced

        // Update current values on fixed goals from synced Health Connect data
        if (synced.isAvailable) {
            val updated = _goals.value.map { goal ->
                when (goal.category) {
                    GoalCategory.RUNNING -> synced.runningKm?.let { goal.copy(currentValue = it) } ?: goal
                    GoalCategory.BEDTIME -> synced.bedtimeHourFraction?.let { goal.copy(currentValue = it) } ?: goal
                    GoalCategory.WAKE_TIME -> synced.wakeHourFraction?.let { goal.copy(currentValue = it) } ?: goal
                    else -> goal
                }
            }
            saveGoalsToPrefs(updated)
        }
        return synced
    }

    fun prepareCheckInGoalItems(): List<CheckInGoalItem> {
        val synced = _healthSyncedData.value
        val todaySessionsList = _todaySessions.value
        val todayWorkedHours = todaySessionsList.sumOf { it.durationHours.toDouble() }

        return _goals.value.map { goal ->
            val (syncedVal, isHcSynced) = when (goal.category) {
                GoalCategory.WORK -> Pair(todayWorkedHours, false)
                GoalCategory.RUNNING -> Pair(synced.runningKm, synced.isAvailable && synced.hasPermissions)
                GoalCategory.BEDTIME -> Pair(synced.bedtimeHourFraction, synced.isAvailable && synced.hasPermissions)
                GoalCategory.WAKE_TIME -> Pair(synced.wakeHourFraction, synced.isAvailable && synced.hasPermissions)
                GoalCategory.CUSTOM -> Pair(null, false)
            }

            val userVal = syncedVal ?: goal.currentValue

            CheckInGoalItem(
                goalId = goal.id,
                goalTitle = goal.title,
                category = goal.category,
                targetValue = goal.targetValue,
                unit = goal.unit,
                syncedValue = syncedVal,
                userValue = userVal,
                isConfirmed = true,
                isHealthConnectSynced = isHcSynced
            )
        }
    }

    fun submitCheckIn(items: List<CheckInGoalItem>): DailyCheckInRecord {
        val record = DailyCheckInRecord(
            dateIso = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
            timestampMillis = System.currentTimeMillis(),
            items = items
        )

        // Update goals currentValue from confirmed items
        val itemMap = items.associateBy { it.goalId }
        val updatedGoals = _goals.value.map { goal ->
            val checkInItem = itemMap[goal.id]
            if (checkInItem != null && checkInItem.isConfirmed) {
                goal.copy(currentValue = checkInItem.userValue)
            } else goal
        }

        saveGoalsToPrefs(updatedGoals)
        return record
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
        updateGoalTarget("fixed_work", hours.toDouble())
    }

    fun generateDummyData() {
        prefs.edit().putBoolean(KEY_HAS_DATA, true).apply()
        _todaySessions.value = createDefaultSessions()
        _userProfile.update {
            it.copy(
                dayStreak = 12,
                totalWorkHours = 48,
                dailyGoalHours = 8
            )
        }
        prefs.edit()
            .putInt(KEY_USER_STREAK, 12)
            .putInt(KEY_TOTAL_WORK, 48)
            .putFloat(KEY_DAILY_GOAL, 8f)
            .apply()
    }

    fun deleteAllData() {
        cancelSession()
        _todaySessions.value = emptyList()
        _userProfile.update {
            it.copy(
                dayStreak = 0,
                totalWorkHours = 0
            )
        }
        prefs.edit()
            .putBoolean(KEY_HAS_DATA, false)
            .putInt(KEY_USER_STREAK, 0)
            .putInt(KEY_TOTAL_WORK, 0)
            .remove(KEY_ACTIVE_START)
            .remove(KEY_ACTIVE_TITLE)
            .apply()
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

        val totalDeep = if (sessions.isNotEmpty()) deepWorkMins.coerceAtLeast(312L) else 0L
        val totalMeet = if (sessions.isNotEmpty()) meetingMins.coerceAtLeast(96L) else 0L
        val totalBreak = if (sessions.isNotEmpty()) breakMins.coerceAtLeast(40L) else 0L
        val totalMins = totalDeep + totalMeet + totalBreak

        return DayStats(
            dateLabel = "Apr 21, 2025",
            totalMinutes = totalMins,
            deepWorkMinutes = totalDeep,
            meetingsMinutes = totalMeet,
            breaksMinutes = totalBreak,
            sessionCount = sessions.size,
            diffVsYesterdayMinutes = if (sessions.isNotEmpty()) 120 else 0
        )
    }

    fun getWeeklyGoalProgress(): WeeklyGoalProgress {
        val days = listOf(
            DayProgress("Mon", 4.2f),
            DayProgress("Tue", 6.0f),
            DayProgress("Wed", 7.2f),
            DayProgress("Thu", 4.8f),
            DayProgress("Fri", 4.4f),
            DayProgress("Sat", 8.2f, isTargetReached = true),
            DayProgress("Sun", 4.8f)
        )
        return WeeklyGoalProgress(
            targetHoursPerDay = _dailyGoalHours.value,
            currentWorkedHoursToday = 3.7f,
            completionPercent = 70,
            days = days
        )
    }

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_AVATAR_URI = "avatar_uri"
        private const val KEY_USER_STREAK = "user_streak"
        private const val KEY_TOTAL_WORK = "total_work"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_GOALS_JSON = "goals_json"
        private const val KEY_HAS_DATA = "has_data"
        private const val KEY_ACTIVE_START = "active_session_start"
        private const val KEY_ACTIVE_TITLE = "active_session_title"

        private fun serializeGoals(goals: List<Goal>): String {
            val array = JSONArray()
            goals.forEach { goal ->
                val obj = JSONObject()
                obj.put("id", goal.id)
                obj.put("title", goal.title)
                obj.put("category", goal.category.name)
                obj.put("cadence", goal.cadence.name)
                obj.put("targetValue", goal.targetValue)
                obj.put("unit", goal.unit.name)
                obj.put("isFixed", goal.isFixed)
                obj.put("currentValue", goal.currentValue)
                array.put(obj)
            }
            return array.toString()
        }

        private fun deserializeGoals(jsonStr: String): List<Goal> {
            val list = mutableListOf<Goal>()
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Goal(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            category = GoalCategory.valueOf(obj.getString("category")),
                            cadence = GoalCadence.valueOf(obj.getString("cadence")),
                            targetValue = obj.getDouble("targetValue"),
                            unit = GoalUnit.valueOf(obj.getString("unit")),
                            isFixed = obj.getBoolean("isFixed"),
                            currentValue = obj.optDouble("currentValue", 0.0)
                        )
                    )
                }
            } catch (e: Exception) {
                return Goal.defaultFixedGoals()
            }
            return list
        }

        @Volatile
        private var INSTANCE: FocusSessionRepository? = null

        fun getInstance(context: Context): FocusSessionRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FocusSessionRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun createDefaultSessions(): List<WorkSession> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 16)
            cal.set(Calendar.MINUTE, 20)
            val s3Start = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 17)
            cal.set(Calendar.MINUTE, 5)
            val s3End = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 13)
            cal.set(Calendar.MINUTE, 15)
            val s2Start = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 14)
            cal.set(Calendar.MINUTE, 48)
            val s2End = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 12)
            val s1Start = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 12)
            cal.set(Calendar.MINUTE, 3)
            val s1End = cal.timeInMillis

            return listOf(
                WorkSession("1", "Architecture & API design", SessionCategory.DEEP_WORK, s1Start, s1End, "Work"),
                WorkSession("2", "Client alignment & review", SessionCategory.MEETINGS, s2Start, s2End, "Work"),
                WorkSession("3", "Component polish & animations", SessionCategory.DEEP_WORK, s3Start, s3End, "Work")
            )
        }
    }
}
