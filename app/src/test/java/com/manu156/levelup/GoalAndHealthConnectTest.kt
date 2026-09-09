package com.manu156.levelup

import com.manu156.levelup.data.health.HealthConnectManager
import com.manu156.levelup.data.health.SimpleSleepSession
import com.manu156.levelup.data.model.Goal
import com.manu156.levelup.data.model.GoalCadence
import com.manu156.levelup.data.model.GoalCategory
import com.manu156.levelup.data.model.GoalUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.Duration

class GoalAndHealthConnectTest {

    @Test
    fun fixedGoals_cannotBeDeleted_andContainAllCoreCategories() {
        val defaults = Goal.defaultFixedGoals(
            workHours = 8.0,
            runningKm = 5.0,
            bedtimeHour = 23.0,
            wakeHour = 8.0
        )

        assertEquals(4, defaults.size)
        assertTrue(defaults.all { it.isFixed })

        val categories = defaults.map { it.category }.toSet()
        assertTrue(categories.contains(GoalCategory.WORK))
        assertTrue(categories.contains(GoalCategory.RUNNING))
        assertTrue(categories.contains(GoalCategory.BEDTIME))
        assertTrue(categories.contains(GoalCategory.WAKE_TIME))
    }

    @Test
    fun goal_timeOfDayFormattingAndParsing_isAccurate() {
        assertEquals("11:00 PM", Goal.formatTimeOfDay(23.0))
        assertEquals("8:00 AM", Goal.formatTimeOfDay(8.0))
        assertEquals("10:30 PM", Goal.formatTimeOfDay(22.5))

        assertEquals(23.0, Goal.parseTimeOfDayToFraction("11:00 PM"), 0.01)
        assertEquals(8.0, Goal.parseTimeOfDayToFraction("8:00 AM"), 0.01)
        assertEquals(22.5, Goal.parseTimeOfDayToFraction("10:30 PM"), 0.01)
    }

    @Test
    fun sleepFilter_napHandling_ignoresSessionsUnder3Hours() {
        val now = Instant.now()

        // 2-hour nap (120 minutes)
        val shortNap = SimpleSleepSession(
            startTime = now.minus(Duration.ofHours(4)),
            endTime = now.minus(Duration.ofHours(2)),
            packageName = "com.fit.napApp"
        )

        val filteredNap = HealthConnectManager.filterAndDeduplicateSleepSessions(listOf(shortNap))
        assertNull("Naps under 3 hours should be ignored", filteredNap)

        // 7-hour main sleep session
        val mainSleep = SimpleSleepSession(
            startTime = now.minus(Duration.ofHours(8)),
            endTime = now.minus(Duration.ofHours(1)),
            packageName = "com.fit.sleepTracker"
        )

        val filteredMain = HealthConnectManager.filterAndDeduplicateSleepSessions(listOf(shortNap, mainSleep))
        assertNotNull("Main sleep session >= 3 hours should be accepted", filteredMain)
        assertEquals("com.fit.sleepTracker", filteredMain?.packageName)
    }

    @Test
    fun sleepFilter_deduplicationStrategy_prioritizesByPackageNameAndStageCount() {
        val now = Instant.now()

        val stravaSleep = SimpleSleepSession(
            startTime = now.minus(Duration.ofHours(8)),
            endTime = now.minus(Duration.ofHours(1)),
            packageName = "com.strava.fit",
            stageCount = 2
        )

        val googleFitSleep = SimpleSleepSession(
            startTime = now.minus(Duration.ofHours(8)),
            endTime = now.minus(Duration.ofHours(1)),
            packageName = "com.google.android.apps.fitness",
            stageCount = 8 // More granular sleep stage data
        )

        // 1. Without priority packages, selects record with more granular stage data
        val selectedByGranularity = HealthConnectManager.filterAndDeduplicateSleepSessions(
            sessions = listOf(stravaSleep, googleFitSleep)
        )
        assertEquals("com.google.android.apps.fitness", selectedByGranularity?.packageName)

        // 2. With Strava in priority package list, selects Strava
        val selectedByPriority = HealthConnectManager.filterAndDeduplicateSleepSessions(
            sessions = listOf(stravaSleep, googleFitSleep),
            priorityPackages = listOf("com.strava.fit")
        )
        assertEquals("com.strava.fit", selectedByPriority?.packageName)
    }

    @Test
    fun customGoal_canBeAddedAndMarkedNonFixed() {
        val custom = Goal(
            id = "custom_reading",
            title = "Daily Reading",
            category = GoalCategory.CUSTOM,
            cadence = GoalCadence.DAILY,
            targetValue = 30.0,
            unit = GoalUnit.MINUTES,
            isFixed = false
        )

        assertFalse(custom.isFixed)
        assertEquals("30m", custom.formattedTarget())
    }
}
