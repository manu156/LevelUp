package com.manu156.levelup.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FocusColorScheme = darkColorScheme(
    primary = FocusPurple,
    onPrimary = FocusTextPrimary,
    primaryContainer = FocusPurpleSoft,
    onPrimaryContainer = FocusPurpleLight,
    secondary = FocusCoral,
    onSecondary = FocusTextPrimary,
    secondaryContainer = FocusCoralSoft,
    onSecondaryContainer = FocusCoralLight,
    tertiary = FocusMint,
    onTertiary = FocusTextPrimary,
    tertiaryContainer = FocusMintSoft,
    onTertiaryContainer = FocusMintLight,
    background = FocusBgDark,
    onBackground = FocusTextPrimary,
    surface = FocusCardBg,
    onSurface = FocusTextPrimary,
    surfaceVariant = FocusCardBgHover,
    onSurfaceVariant = FocusTextSecondary,
    outline = FocusCardBorder
)

@Composable
fun LevelUpTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val currentDayTheme = DayThemeManager.currentDayTheme
    val currentPreset = AnimeThemeManager.currentPreset

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = currentDayTheme.palette.background.toArgb()
            window.navigationBarColor = FocusNavBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    val dynamicColorScheme = FocusColorScheme.copy(
        primary = currentDayTheme.palette.primary,
        secondary = currentDayTheme.palette.secondary,
        tertiary = currentDayTheme.palette.accent,
        background = currentDayTheme.palette.background,
        surface = currentDayTheme.palette.surface,
        outline = currentDayTheme.palette.border
    )

    CompositionLocalProvider(
        LocalAnimeTheme provides currentPreset,
        LocalDayTheme provides currentDayTheme
    ) {
        MaterialTheme(
            colorScheme = dynamicColorScheme,
            typography = Typography,
            content = content
        )
    }
}
