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
            category = SessionCategory.CODING,
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
            jobMinutes = 200L,     // ~44-45%
            codingMinutes = 112L,  // 25%
            projectsMinutes = 96L, // ~21%
            researchMinutes = 40L, // ~8-9%
            sessionCount = 4
        )

        assertEquals("7h 28m", stats.totalHoursStr)
        assertTrue(stats.jobPercent in 44..45)
        assertEquals(25, stats.codingPercent)
        assertTrue(stats.projectsPercent in 21..22)
        assertTrue(stats.researchPercent in 8..9)
        assertEquals("1h 52m", stats.avgSessionLengthStr)
    }

    @Test
    fun gitSessionSerializer_legacyCategoryMigration_mapsToProjects() {
        val legacyJson = """
            {
              "version": 1,
              "id": "legacy-session-123",
              "info": {
                "title": "Deep Code",
                "category": "DEEP_WORK",
                "tag": "Focus",
                "notes": "Testing legacy mapping"
              },
              "timing": {
                "startEpochMs": 1700000000000,
                "endEpochMs": 1700003600000
              }
            }
        """.trimIndent()

        val deserialized = com.manu156.levelup.data.git.GitSessionSerializer.deserializeSession(legacyJson)
        assertEquals(SessionCategory.PROJECTS, deserialized.category)
        assertEquals("legacy-session-123", deserialized.id)
        assertEquals("Deep Code", deserialized.title)
    }

    @Test
    fun gitSessionSerializer_newCategories_deserializeCorrectly() {
        val json = """
            {
              "version": 1,
              "id": "session-456",
              "info": {
                "title": "LLM Load Balancer",
                "category": "RESEARCH_STUDY",
                "tag": "Diffusion Models",
                "notes": "Paper reading"
              },
              "timing": {
                "startEpochMs": 1700000000000,
                "endEpochMs": 1700003600000
              }
            }
        """.trimIndent()

        val deserialized = com.manu156.levelup.data.git.GitSessionSerializer.deserializeSession(json)
        assertEquals(SessionCategory.RESEARCH_STUDY, deserialized.category)
        assertEquals("Diffusion Models", deserialized.tag)
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
