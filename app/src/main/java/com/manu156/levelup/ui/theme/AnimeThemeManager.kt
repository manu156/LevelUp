package com.manu156.levelup.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.manu156.levelup.R

data class ThemeArtwork(
    @param:DrawableRes val splashArtwork: Int = R.drawable.splash_twilight_girl,
    @param:DrawableRes val motivationBanner: Int = R.drawable.home_banner_steps,
    @param:DrawableRes val sleepingCatTimer: Int = R.drawable.cat_sleeping_timer,
    @param:DrawableRes val celebrationHug: Int = R.drawable.celebration_cat_hug,
    @param:DrawableRes val profileBanner: Int = R.drawable.profile_torii_banner,
    @param:DrawableRes val avatar: Int = R.drawable.avatar_hug,
    @param:DrawableRes val badgeFuji: Int = R.drawable.badge_fuji,
    @param:DrawableRes val insightSticker: Int = R.drawable.anime_girl_peeking
)

data class AnimeThemePreset(
    val id: String,
    val title: String,
    val primaryColor: Color = FocusPurple,
    val secondaryColor: Color = FocusCoral,
    val accentMint: Color = FocusMint,
    val artwork: ThemeArtwork = ThemeArtwork()
)

object AnimeThemeManager {
    private const val PREFS_NAME = "levelup_theme_prefs"
    private const val KEY_THEME_ID = "active_theme_id"

    val presets: List<AnimeThemePreset>
        get() = DayThemeCatalog.weeklyThemes.map { dayTheme ->
            AnimeThemePreset(
                id = dayTheme.id,
                title = "${dayTheme.vibeEmoji} ${dayTheme.name} (${dayTheme.dayOfWeek.name.take(3)})",
                primaryColor = dayTheme.palette.primary,
                secondaryColor = dayTheme.palette.secondary,
                accentMint = dayTheme.palette.accent,
                artwork = dayTheme.artwork
            )
        }

    var currentPreset by mutableStateOf(
        DayThemeCatalog.themeFor(java.time.LocalDate.now().dayOfWeek).let { dayTheme ->
            AnimeThemePreset(
                id = dayTheme.id,
                title = "${dayTheme.vibeEmoji} ${dayTheme.name} (${dayTheme.dayOfWeek.name.take(3)})",
                primaryColor = dayTheme.palette.primary,
                secondaryColor = dayTheme.palette.secondary,
                accentMint = dayTheme.palette.accent,
                artwork = dayTheme.artwork
            )
        }
    )
        private set

    private var appContext: android.content.Context? = null

    fun init(context: android.content.Context) {
        val app = context.applicationContext
        appContext = app
        syncWithDayTheme(DayThemeManager.currentDayTheme)
    }

    fun switchTheme(presetId: String) {
        val matched = DayThemeCatalog.weeklyThemes.find { it.id == presetId }
        if (matched != null) {
            DayThemeManager.selectDay(matched.dayOfWeek)
        }
    }

    fun syncWithDayTheme(dayTheme: DayTheme) {
        currentPreset = AnimeThemePreset(
            id = dayTheme.id,
            title = "${dayTheme.name} (${dayTheme.vibeEmoji} ${dayTheme.dayOfWeek.name.take(3)})",
            primaryColor = dayTheme.palette.primary,
            secondaryColor = dayTheme.palette.secondary,
            accentMint = dayTheme.palette.accent,
            artwork = dayTheme.artwork
        )
    }
}

val LocalAnimeTheme = compositionLocalOf { AnimeThemeManager.currentPreset }
