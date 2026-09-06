package com.manu156.levelup.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
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
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = FocusBgDark.toArgb()
            window.navigationBarColor = FocusNavBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = FocusColorScheme,
        typography = Typography,
        content = content
    )
}
