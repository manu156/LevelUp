package com.manu156.levelup.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SessionCategory(val displayName: String) {
    DEEP_WORK("Deep Work"),
    MEETINGS("Meetings"),
    BREAKS("Breaks"),
    GENERAL_WORK("Work")
}

data class WorkSession(
    val id: String,
    val title: String,
    val category: SessionCategory = SessionCategory.DEEP_WORK,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val tag: String = "Work",
    val notes: String = ""
) {
    val durationMillis: Long get() = (endTimeMillis - startTimeMillis).coerceAtLeast(0)
    val durationMinutes: Long get() = durationMillis / 60_000
    val durationHours: Float get() = durationMillis / 3_600_000f

    fun formattedDuration(): String {
        val totalMins = durationMinutes
        val hrs = totalMins / 60
        val mins = totalMins % 60
        return when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
            hrs > 0 -> "${hrs}h"
            else -> "${mins}m"
        }
    }

    fun formattedTimeRange(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return "${sdf.format(Date(startTimeMillis))} – ${sdf.format(Date(endTimeMillis))}"
    }
}

data class DayStats(
    val dateLabel: String,
    val totalMinutes: Long,
    val deepWorkMinutes: Long,
    val meetingsMinutes: Long,
    val breaksMinutes: Long,
    val sessionCount: Int,
    val diffVsYesterdayMinutes: Long = 120
) {
    val totalHoursStr: String get() {
        val hrs = totalMinutes / 60
        val mins = totalMinutes % 60
        return "${hrs}h ${mins}m"
    }

    val avgSessionLengthStr: String get() {
        if (sessionCount == 0) return "0m"
        val avgMins = totalMinutes / sessionCount
        val hrs = avgMins / 60
        val mins = avgMins % 60
        return if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
    }

    val deepWorkPercent: Int get() = if (totalMinutes > 0) ((deepWorkMinutes * 100) / totalMinutes).toInt() else 0
    val meetingsPercent: Int get() = if (totalMinutes > 0) ((meetingsMinutes * 100) / totalMinutes).toInt() else 0
    val breaksPercent: Int get() = if (totalMinutes > 0) ((breaksMinutes * 100) / totalMinutes).toInt() else 0
}

data class DayProgress(
    val dayName: String, // Mon, Tue, Wed...
    val hours: Float,
    val isTargetReached: Boolean = false
)

data class WeeklyGoalProgress(
    val targetHoursPerDay: Float = 8f,
    val currentWorkedHoursToday: Float = 3.7f,
    val completionPercent: Int = 70,
    val days: List<DayProgress> = emptyList()
)

data class UserProfile(
    val name: String = "Alex",
    val subtitle: String = "Building a better tomorrow ✨",
    val dayStreak: Int = 12,
    val totalWorkHours: Int = 48,
    val dailyGoalHours: Int = 8,
    val avatarUri: String = "preset:alex"
)

enum class GoalCategory(val displayName: String, val isFixed: Boolean) {
    WORK("Work", true),
    RUNNING("Running", true),
    BEDTIME("Bedtime", true),
    WAKE_TIME("Wake Time", true),
    CUSTOM("Custom Goal", false)
}

enum class GoalCadence(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly")
}

enum class GoalUnit(val symbol: String) {
    HOURS("h"),
    KILOMETERS("km"),
    TIME_OF_DAY(""),
    MINUTES("m"),
    COUNT("x")
}

data class Goal(
    val id: String,
    val title: String,
    val category: GoalCategory,
    val cadence: GoalCadence = GoalCadence.DAILY,
    val targetValue: Double,
    val unit: GoalUnit,
    val isFixed: Boolean = category.isFixed,
    val currentValue: Double = 0.0
) {
    fun formattedTarget(): String {
        return when (unit) {
            GoalUnit.HOURS -> "${targetValue.toInt()}h"
            GoalUnit.KILOMETERS -> if (targetValue % 1.0 == 0.0) "${targetValue.toInt()} km" else String.format(Locale.US, "%.1f km", targetValue)
            GoalUnit.TIME_OF_DAY -> formatTimeOfDay(targetValue)
            GoalUnit.MINUTES -> "${targetValue.toInt()}m"
            GoalUnit.COUNT -> "${targetValue.toInt()}"
        }
    }

    fun formattedCurrent(): String {
        return when (unit) {
            GoalUnit.HOURS -> String.format(Locale.US, "%.1fh", currentValue)
            GoalUnit.KILOMETERS -> String.format(Locale.US, "%.1f km", currentValue)
            GoalUnit.TIME_OF_DAY -> if (currentValue > 0) formatTimeOfDay(currentValue) else "Not synced"
            GoalUnit.MINUTES -> "${currentValue.toInt()}m"
            GoalUnit.COUNT -> "${currentValue.toInt()}"
        }
    }

    val progressPercent: Float
        get() {
            if (targetValue <= 0) return 0f
            return when (category) {
                GoalCategory.BEDTIME -> {
                    // Bedtime target vs current: if current bedtime <= target bedtime (asleep earlier or on time), 100%
                    if (currentValue <= 0) 0f
                    else if (currentValue <= targetValue + 0.25) 1.0f else 0.5f
                }
                GoalCategory.WAKE_TIME -> {
                    // Wake time target vs current: if current wake <= target wake (awake before target time), 100%
                    if (currentValue <= 0) 0f
                    else if (currentValue <= targetValue + 0.25) 1.0f else 0.5f
                }
                else -> (currentValue / targetValue).toFloat().coerceIn(0f, 1f)
            }
        }

    companion object {
        fun formatTimeOfDay(hourFraction: Double): String {
            val totalMins = (hourFraction * 60).toInt().coerceIn(0, 1439)
            val hours24 = totalMins / 60
            val mins = totalMins % 60
            val period = if (hours24 >= 12) "PM" else "AM"
            val hours12 = when {
                hours24 == 0 -> 12
                hours24 > 12 -> hours24 - 12
                else -> hours24
            }
            return String.format(Locale.US, "%d:%02d %s", hours12, mins, period)
        }

        fun parseTimeOfDayToFraction(timeStr: String): Double {
            // Parses e.g. "23:00", "11:00 PM", "8:00 AM", "08:00"
            val clean = timeStr.trim().uppercase(Locale.US)
            val isPm = clean.contains("PM")
            val isAm = clean.contains("AM")
            val digitsOnly = clean.replace("[^0-9:]".toRegex(), "")
            val parts = digitsOnly.split(":")
            if (parts.size >= 2) {
                var h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                if (isPm && h < 12) h += 12
                if (isAm && h == 12) h = 0
                return h + (m / 60.0)
            }
            return 23.0
        }

        fun defaultFixedGoals(
            workHours: Double = 8.0,
            runningKm: Double = 5.0,
            bedtimeHour: Double = 23.0, // 11:00 PM
            wakeHour: Double = 8.0      // 8:00 AM
        ): List<Goal> {
            return listOf(
                Goal(
                    id = "fixed_work",
                    title = "Work Duration",
                    category = GoalCategory.WORK,
                    cadence = GoalCadence.DAILY,
                    targetValue = workHours,
                    unit = GoalUnit.HOURS,
                    isFixed = true
                ),
                Goal(
                    id = "fixed_running",
                    title = "Running Distance",
                    category = GoalCategory.RUNNING,
                    cadence = GoalCadence.DAILY,
                    targetValue = runningKm,
                    unit = GoalUnit.KILOMETERS,
                    isFixed = true
                ),
                Goal(
                    id = "fixed_bedtime",
                    title = "Target Bedtime",
                    category = GoalCategory.BEDTIME,
                    cadence = GoalCadence.DAILY,
                    targetValue = bedtimeHour,
                    unit = GoalUnit.TIME_OF_DAY,
                    isFixed = true
                ),
                Goal(
                    id = "fixed_wake",
                    title = "Target Wake Time",
                    category = GoalCategory.WAKE_TIME,
                    cadence = GoalCadence.DAILY,
                    targetValue = wakeHour,
                    unit = GoalUnit.TIME_OF_DAY,
                    isFixed = true
                )
            )
        }
    }
}

data class CheckInGoalItem(
    val goalId: String,
    val goalTitle: String,
    val category: GoalCategory,
    val targetValue: Double,
    val unit: GoalUnit,
    val syncedValue: Double?,
    val userValue: Double,
    val isConfirmed: Boolean = false,
    val isHealthConnectSynced: Boolean = false,
    val notes: String = ""
)

data class DailyCheckInRecord(
    val dateIso: String,
    val timestampMillis: Long,
    val items: List<CheckInGoalItem>
)
