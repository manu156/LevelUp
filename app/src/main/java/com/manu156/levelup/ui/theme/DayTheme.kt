package com.manu156.levelup.ui.theme

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import java.time.DayOfWeek
import java.time.LocalDate

data class DayThemePalette(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val accent: Color,
    val background: Color = FocusBgDark,
    val surface: Color = FocusCardBg,
    val border: Color = FocusCardBorder,
    val textPrimary: Color = FocusTextPrimary,
    val textSecondary: Color = FocusTextSecondary,
    val textMuted: Color = FocusTextMuted
)

data class DayThemeButtonStyle(
    val feedbackStyle: AnimeFeedbackStyle,
    val primaryGradient: List<Color>,
    val secondaryGradient: List<Color>,
    val textColor: Color = Color(0xFF0C0F1E),
    val glowColor: Color = primaryGradient.first().copy(alpha = 0.5f)
)

data class DayTheme(
    val dayOfWeek: DayOfWeek,
    val id: String,
    val name: String,
    val tagline: String,
    val vibeEmoji: String,
    val palette: DayThemePalette,
    val buttonStyle: DayThemeButtonStyle,
    val artwork: ThemeArtwork = ThemeArtwork()
)

enum class DayThemeCycleMode {
    AUTO_DAY_OF_WEEK,
    MANUAL
}

object DayThemeCatalog {
    val mondayKatana = DayTheme(
        dayOfWeek = DayOfWeek.MONDAY,
        id = "mon_cyber_katana",
        name = "Cyber Katana",
        tagline = "Laser Precision & Sharp Focus",
        vibeEmoji = "⚔️",
        palette = DayThemePalette(
            primary = Color(0xFF00E5FF),
            primaryVariant = Color(0xFF6CFFCE),
            secondary = Color(0xFF6CFFCE),
            secondaryVariant = Color(0xFF00B4D8),
            accent = Color(0xFF4ED6A3)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.KATANA,
            primaryGradient = listOf(Color(0xFF6CFFCE), Color(0xFF00E5FF)),
            secondaryGradient = listOf(Color(0xFF00E5FF), Color(0xFF7B61FF)),
            textColor = Color(0xFF0A121A),
            glowColor = Color(0xFF6CFFCE).copy(alpha = 0.55f)
        )
    )

    val tuesdaySakura = DayTheme(
        dayOfWeek = DayOfWeek.TUESDAY,
        id = "tue_sakura_bloom",
        name = "Sakura Bloom",
        tagline = "Floral Serenity & Gentle Harmony",
        vibeEmoji = "🌸",
        palette = DayThemePalette(
            primary = Color(0xFFA67DFD),
            primaryVariant = Color(0xFFFFB2C9),
            secondary = Color(0xFFFF6584),
            secondaryVariant = Color(0xFFFF8DA1),
            accent = Color(0xFFFFB2C9)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.SPARKLE_BURST,
            primaryGradient = listOf(Color(0xFFFFB2C9), Color(0xFFA67DFD)),
            secondaryGradient = listOf(Color(0xFFFF6584), Color(0xFFFFB2C9)),
            textColor = Color(0xFF1E0C16),
            glowColor = Color(0xFFFFB2C9).copy(alpha = 0.55f)
        )
    )

    val wednesdayMochi = DayTheme(
        dayOfWeek = DayOfWeek.WEDNESDAY,
        id = "wed_slime_mochi",
        name = "Slime Mochi",
        tagline = "Cheerful Bounce & Midweek Momentum",
        vibeEmoji = "🍮",
        palette = DayThemePalette(
            primary = FocusPurple,
            primaryVariant = FocusPurpleLight,
            secondary = FocusCoral,
            secondaryVariant = FocusCoralLight,
            accent = FocusMint
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.SLIME,
            primaryGradient = listOf(FocusPurple, FocusPurpleLight),
            secondaryGradient = listOf(FocusCoral, FocusCoralLight),
            textColor = Color(0xFF0C0F1E),
            glowColor = FocusPurple.copy(alpha = 0.55f)
        )
    )

    val thursdayNeko = DayTheme(
        dayOfWeek = DayOfWeek.THURSDAY,
        id = "thu_neko_cafe",
        name = "Neko Cafe",
        tagline = "Cozy Companionship & Warm Vibes",
        vibeEmoji = "🐾",
        palette = DayThemePalette(
            primary = Color(0xFFFF748D),
            primaryVariant = Color(0xFFFF92A5),
            secondary = Color(0xFFFFC163),
            secondaryVariant = Color(0xFFFFD591),
            accent = Color(0xFFFFC163)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.NEKO_TWITCH,
            primaryGradient = listOf(Color(0xFFFF748D), Color(0xFFFFC163)),
            secondaryGradient = listOf(Color(0xFFFFC163), Color(0xFFFF92A5)),
            textColor = Color(0xFF1F1014),
            glowColor = Color(0xFFFF748D).copy(alpha = 0.55f)
        )
    )

    val fridayOverdrive = DayTheme(
        dayOfWeek = DayOfWeek.FRIDAY,
        id = "fri_neon_overdrive",
        name = "Neon Overdrive",
        tagline = "Cyberpunk Hype & Maximum Output",
        vibeEmoji = "⚡",
        palette = DayThemePalette(
            primary = Color(0xFF7B61FF),
            primaryVariant = Color(0xFFFF2A85),
            secondary = Color(0xFFFF2A85),
            secondaryVariant = Color(0xFF00F0FF),
            accent = Color(0xFF00F0FF)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.KATANA,
            primaryGradient = listOf(Color(0xFF7B61FF), Color(0xFFFF2A85)),
            secondaryGradient = listOf(Color(0xFF00F0FF), Color(0xFF7B61FF)),
            textColor = Color(0xFFFFFFFF),
            glowColor = Color(0xFFFF2A85).copy(alpha = 0.55f)
        )
    )

    val saturdayStardust = DayTheme(
        dayOfWeek = DayOfWeek.SATURDAY,
        id = "sat_mahou_stardust",
        name = "Mahou Stardust",
        tagline = "Weekend Magic & Creative Sparkle",
        vibeEmoji = "✨",
        palette = DayThemePalette(
            primary = Color(0xFFB388FF),
            primaryVariant = Color(0xFFFFEAA7),
            secondary = Color(0xFFFFEAA7),
            secondaryVariant = Color(0xFF6CFFCE),
            accent = Color(0xFF6CFFCE)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.SPARKLE_BURST,
            primaryGradient = listOf(Color(0xFFFFEAA7), Color(0xFFB388FF)),
            secondaryGradient = listOf(Color(0xFF6CFFCE), Color(0xFFB388FF)),
            textColor = Color(0xFF140D24),
            glowColor = Color(0xFFFFEAA7).copy(alpha = 0.55f)
        )
    )

    val sundayTorii = DayTheme(
        dayOfWeek = DayOfWeek.SUNDAY,
        id = "sun_twilight_torii",
        name = "Twilight Torii",
        tagline = "Mindful Reflection & Zen Restoration",
        vibeEmoji = "⛩️",
        palette = DayThemePalette(
            primary = Color(0xFF6F63E5),
            primaryVariant = Color(0xFFFF92A5),
            secondary = Color(0xFFFF92A5),
            secondaryVariant = Color(0xFF45CAF5),
            accent = Color(0xFF45CAF5)
        ),
        buttonStyle = DayThemeButtonStyle(
            feedbackStyle = AnimeFeedbackStyle.SLIME,
            primaryGradient = listOf(Color(0xFFFF92A5), Color(0xFF6F63E5)),
            secondaryGradient = listOf(Color(0xFF6F63E5), Color(0xFF45CAF5)),
            textColor = Color(0xFF120E24),
            glowColor = Color(0xFF6F63E5).copy(alpha = 0.55f)
        )
    )

    val weeklyThemes = listOf(
        mondayKatana,
        tuesdaySakura,
        wednesdayMochi,
        thursdayNeko,
        fridayOverdrive,
        saturdayStardust,
        sundayTorii
    )

    fun themeFor(dayOfWeek: DayOfWeek): DayTheme {
        return weeklyThemes.firstOrNull { it.dayOfWeek == dayOfWeek } ?: saturdayStardust
    }
}

object DayThemeManager {
    private const val PREFS_NAME = "levelup_day_theme_prefs"
    private const val KEY_CYCLE_MODE = "cycle_mode"
    private const val KEY_MANUAL_DAY = "manual_day"

    private var appContext: Context? = null

    private var _cycleMode by mutableStateOf(DayThemeCycleMode.AUTO_DAY_OF_WEEK)
    val cycleMode: DayThemeCycleMode get() = _cycleMode

    private var _selectedManualDay by mutableStateOf<DayOfWeek?>(null)
    val selectedManualDay: DayOfWeek? get() = _selectedManualDay

    private var _currentDayTheme by mutableStateOf(resolveCurrentTheme())
    val currentDayTheme: DayTheme get() = _currentDayTheme

    fun init(context: Context) {
        val app = context.applicationContext
        appContext = app
        val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val savedModeStr = prefs.getString(KEY_CYCLE_MODE, DayThemeCycleMode.AUTO_DAY_OF_WEEK.name)
        val mode = try {
            DayThemeCycleMode.valueOf(savedModeStr ?: DayThemeCycleMode.AUTO_DAY_OF_WEEK.name)
        } catch (e: Exception) {
            DayThemeCycleMode.AUTO_DAY_OF_WEEK
        }
        _cycleMode = mode

        val savedManualDayStr = prefs.getString(KEY_MANUAL_DAY, null)
        _selectedManualDay = savedManualDayStr?.let {
            try {
                DayOfWeek.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }

        updateResolvedTheme()
    }

    fun setCycleMode(mode: DayThemeCycleMode) {
        _cycleMode = mode
        savePreferences()
        updateResolvedTheme()
    }

    fun selectDay(day: DayOfWeek) {
        _cycleMode = DayThemeCycleMode.MANUAL
        _selectedManualDay = day
        savePreferences()
        updateResolvedTheme()
    }

    fun resetToAutoCycle() {
        _cycleMode = DayThemeCycleMode.AUTO_DAY_OF_WEEK
        _selectedManualDay = null
        savePreferences()
        updateResolvedTheme()
    }

    fun refreshDayTheme() {
        updateResolvedTheme()
    }

    private fun resolveCurrentTheme(): DayTheme {
        return if (_cycleMode == DayThemeCycleMode.MANUAL && _selectedManualDay != null) {
            DayThemeCatalog.themeFor(_selectedManualDay!!)
        } else {
            val today = LocalDate.now().dayOfWeek
            DayThemeCatalog.themeFor(today)
        }
    }

    private fun updateResolvedTheme() {
        _currentDayTheme = resolveCurrentTheme()
        // Sync with AnimeThemeManager to keep legacy preset observers aligned
        AnimeThemeManager.syncWithDayTheme(_currentDayTheme)
    }

    private fun savePreferences() {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            putString(KEY_CYCLE_MODE, _cycleMode.name)
            putString(KEY_MANUAL_DAY, _selectedManualDay?.name)
            apply()
        }
    }
}

val LocalDayTheme = compositionLocalOf { DayThemeManager.currentDayTheme }
