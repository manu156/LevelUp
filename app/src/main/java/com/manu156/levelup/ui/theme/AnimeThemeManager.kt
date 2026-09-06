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
    @param:DrawableRes val avatar: Int = R.drawable.avatar_alex,
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
    val presets = listOf(
        AnimeThemePreset(
            id = "twilight_city",
            title = "Twilight City (Default)",
            primaryColor = FocusPurple,
            secondaryColor = FocusCoral,
            accentMint = FocusMint
        ),
        AnimeThemePreset(
            id = "sakura_bloom",
            title = "Sakura Bloom",
            primaryColor = Color(0xFFA67DFD),
            secondaryColor = Color(0xFFFF6584),
            accentMint = Color(0xFF38EF7D)
        ),
        AnimeThemePreset(
            id = "cyber_midnight",
            title = "Cyber Midnight",
            primaryColor = Color(0xFF7B61FF),
            secondaryColor = Color(0xFFFF5277),
            accentMint = Color(0xFF00F0FF)
        )
    )

    var currentPreset by mutableStateOf(presets[0])
        private set

    fun switchTheme(presetId: String) {
        presets.find { it.id == presetId }?.let {
            currentPreset = it
        }
    }
}

val LocalAnimeTheme = compositionLocalOf { AnimeThemeManager.currentPreset }
