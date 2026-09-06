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
    val dailyGoalHours: Int = 8
)
