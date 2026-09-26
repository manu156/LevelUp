package com.manu156.levelup

import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import com.manu156.levelup.ui.theme.DayThemeCatalog
import com.manu156.levelup.ui.theme.DayThemeCycleMode
import com.manu156.levelup.ui.theme.DayThemeManager
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DayThemeTest {

    @Test
    fun weeklyThemes_coversAllSevenDaysOfWeek() {
        val themes = DayThemeCatalog.weeklyThemes
        assertEquals(7, themes.size)

        val daysCovered = themes.map { it.dayOfWeek }.toSet()
        assertEquals(DayOfWeek.values().toSet(), daysCovered)
    }

    @Test
    fun weeklyThemes_eachThemeHasCompleteVisualAndInteractionProfile() {
        DayThemeCatalog.weeklyThemes.forEach { theme ->
            assertTrue("Theme ${theme.id} name should not be blank", theme.name.isNotBlank())
            assertTrue("Theme ${theme.id} tagline should not be blank", theme.tagline.isNotBlank())
            assertTrue("Theme ${theme.id} emoji should not be blank", theme.vibeEmoji.isNotBlank())
            assertTrue("Theme ${theme.id} should have at least 2 primary gradient colors", theme.buttonStyle.primaryGradient.size >= 2)
            assertTrue("Theme ${theme.id} should have at least 2 secondary gradient colors", theme.buttonStyle.secondaryGradient.size >= 2)
            assertNotNull(theme.buttonStyle.feedbackStyle)
        }
    }

    @Test
    fun themeCatalog_resolvesExpectedStyleForSpecificDays() {
        val monday = DayThemeCatalog.themeFor(DayOfWeek.MONDAY)
        assertEquals("Cyber Katana", monday.name)
        assertEquals(AnimeFeedbackStyle.KATANA, monday.buttonStyle.feedbackStyle)

        val tuesday = DayThemeCatalog.themeFor(DayOfWeek.TUESDAY)
        assertEquals("Sakura Bloom", tuesday.name)
        assertEquals(AnimeFeedbackStyle.SPARKLE_BURST, tuesday.buttonStyle.feedbackStyle)

        val wednesday = DayThemeCatalog.themeFor(DayOfWeek.WEDNESDAY)
        assertEquals("Slime Mochi", wednesday.name)
        assertEquals(AnimeFeedbackStyle.SLIME, wednesday.buttonStyle.feedbackStyle)

        val thursday = DayThemeCatalog.themeFor(DayOfWeek.THURSDAY)
        assertEquals("Neko Cafe", thursday.name)
        assertEquals(AnimeFeedbackStyle.NEKO_TWITCH, thursday.buttonStyle.feedbackStyle)

        val friday = DayThemeCatalog.themeFor(DayOfWeek.FRIDAY)
        assertEquals("Neon Overdrive", friday.name)
        assertEquals(AnimeFeedbackStyle.KATANA, friday.buttonStyle.feedbackStyle)

        val saturday = DayThemeCatalog.themeFor(DayOfWeek.SATURDAY)
        assertEquals("Mahou Stardust", saturday.name)
        assertEquals(AnimeFeedbackStyle.SPARKLE_BURST, saturday.buttonStyle.feedbackStyle)

        val sunday = DayThemeCatalog.themeFor(DayOfWeek.SUNDAY)
        assertEquals("Twilight Torii", sunday.name)
        assertEquals(AnimeFeedbackStyle.SLIME, sunday.buttonStyle.feedbackStyle)
    }

    @Test
    fun dayThemeManager_manualDaySelection_updatesCurrentThemeAndMode() {
        DayThemeManager.selectDay(DayOfWeek.FRIDAY)
        assertEquals(DayThemeCycleMode.MANUAL, DayThemeManager.cycleMode)
        assertEquals(DayOfWeek.FRIDAY, DayThemeManager.currentDayTheme.dayOfWeek)
        assertEquals("Neon Overdrive", DayThemeManager.currentDayTheme.name)

        DayThemeManager.selectDay(DayOfWeek.MONDAY)
        assertEquals(DayOfWeek.MONDAY, DayThemeManager.currentDayTheme.dayOfWeek)
        assertEquals("Cyber Katana", DayThemeManager.currentDayTheme.name)
    }

    @Test
    fun dayThemeManager_resetToAutoCycle_resolvesToToday() {
        DayThemeManager.selectDay(DayOfWeek.WEDNESDAY)
        assertEquals(DayThemeCycleMode.MANUAL, DayThemeManager.cycleMode)

        DayThemeManager.resetToAutoCycle()
        assertEquals(DayThemeCycleMode.AUTO_DAY_OF_WEEK, DayThemeManager.cycleMode)
        val expectedToday = LocalDate.now().dayOfWeek
        assertEquals(expectedToday, DayThemeManager.currentDayTheme.dayOfWeek)
    }
}
