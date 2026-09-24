package com.manu156.levelup

import com.manu156.levelup.data.model.DayStats
import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.data.model.WorkSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusDomainTest {

    @Test
    fun workSession_durationFormatting_isAccurate() {
        val start = 1000000L
        val end = start + (3 * 3600 + 51 * 60) * 1000L // 3h 51m
        val session = WorkSession(
            id = "test_1",
            title = "Architecture",
            category = SessionCategory.DEEP_WORK,
            startTimeMillis = start,
            endTimeMillis = end
        )

        assertEquals("3h 51m", session.formattedDuration())
        assertEquals(231L, session.durationMinutes)
    }

    @Test
    fun dayStats_percentageBreakdown_isCorrect() {
        val stats = DayStats(
            dateLabel = "Apr 21, 2025",
            totalMinutes = 448L, // 7h 28m
            deepWorkMinutes = 312L, // 5h 12m ~ 70%
            meetingsMinutes = 96L,  // 1h 36m ~ 21-22%
            breaksMinutes = 40L,    // 40m ~ 8-9%
            sessionCount = 4
        )

        assertEquals("7h 28m", stats.totalHoursStr)
        assertTrue(stats.deepWorkPercent in 69..70)
        assertTrue(stats.meetingsPercent in 21..22)
        assertTrue(stats.breaksPercent in 8..9)
        assertEquals("1h 52m", stats.avgSessionLengthStr)
    }

    @Test
    fun userProfile_defaults_haveZeroStreakAndHours() {
        val profile = com.manu156.levelup.data.model.UserProfile()
        assertEquals(0, profile.dayStreak)
        assertEquals(0, profile.totalWorkHours)
    }

    @Test
    fun weeklyGoalProgress_targetReachedEvaluation() {
        val dayProgressList = listOf(
            com.manu156.levelup.data.model.DayProgress("Mon", 8.5f, isTargetReached = true),
            com.manu156.levelup.data.model.DayProgress("Tue", 4.0f, isTargetReached = false)
        )
        val weekly = com.manu156.levelup.data.model.WeeklyGoalProgress(
            targetHoursPerDay = 8f,
            currentWorkedHoursToday = 4.0f,
            completionPercent = 22,
            days = dayProgressList
        )
        assertTrue(weekly.days[0].isTargetReached)
        assertTrue(!weekly.days[1].isTargetReached)
        assertEquals(22, weekly.completionPercent)
    }
}
