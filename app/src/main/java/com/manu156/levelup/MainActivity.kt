package com.manu156.levelup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.data.git.ConflictResolutionChoice
import com.manu156.levelup.data.git.GitConflictItem
import com.manu156.levelup.data.git.GitSyncConfig
import com.manu156.levelup.data.model.WorkSession
import com.manu156.levelup.data.repository.FocusSessionRepository
import com.manu156.levelup.ui.components.AnimeBottomNavBar
import com.manu156.levelup.ui.components.NavTab
import com.manu156.levelup.ui.screens.ActiveSessionScreen
import com.manu156.levelup.ui.screens.CheckInScreen
import com.manu156.levelup.ui.screens.DailyBreakdownScreen
import com.manu156.levelup.ui.screens.GoalsScreen
import com.manu156.levelup.ui.screens.HomeScreen
import com.manu156.levelup.ui.screens.InsightsScreen
import com.manu156.levelup.ui.screens.OnboardingNameScreen
import com.manu156.levelup.ui.screens.ProfileScreen
import com.manu156.levelup.ui.screens.SessionCompleteScreen
import com.manu156.levelup.ui.screens.SettingsScreen
import com.manu156.levelup.ui.screens.SplashScreen
import com.manu156.levelup.ui.screens.StatsScreen
import com.manu156.levelup.ui.theme.LevelUpTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class AppModalScreen {
    NONE,
    CHECK_IN,
    ACTIVE_SESSION,
    SESSION_COMPLETE,
    DAILY_BREAKDOWN,
    INSIGHTS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpTheme {
                FocusFlowApp()
            }
        }
    }
}

@Composable
fun FocusFlowApp() {
    val context = LocalContext.current
    val repository = remember { FocusSessionRepository.getInstance(context) }

    var showSplash by remember { mutableStateOf(true) }
    var showNamePrompt by remember { mutableStateOf(!repository.hasUserRegistered()) }
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var activeModal by remember { mutableStateOf(AppModalScreen.NONE) }
    var completedSession by remember { mutableStateOf<WorkSession?>(null) }

    val isSessionActive by repository.isSessionActive.collectAsState()
    val activeTaskTitle by repository.activeTaskTitle.collectAsState()
    val elapsedSeconds by repository.elapsedSeconds.collectAsState()
    val todaySessions by repository.todaySessions.collectAsState()
    val userProfile by repository.userProfile.collectAsState()
    val dailyGoalHours by repository.dailyGoalHours.collectAsState()

    val gitSyncConfig by repository.gitSyncConfig.collectAsState()
    val isGitSyncing by repository.isGitSyncing.collectAsState()
    val gitSyncMessage by repository.gitSyncMessage.collectAsState()
    val pendingGitConflicts by repository.pendingGitConflicts.collectAsState()
    val gitSyncScope = rememberCoroutineScope()

    BackHandler(enabled = activeModal != AppModalScreen.NONE) {
        activeModal = AppModalScreen.NONE
    }

    if (showSplash) {
        SplashScreen(
            onDismiss = { showSplash = false }
        )
    } else if (showNamePrompt) {
        // First-time launch: ask for user name!
        OnboardingNameScreen(
            onNameSubmitted = { name ->
                repository.saveUserName(name)
                showNamePrompt = false
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (activeModal == AppModalScreen.NONE) {
                    AnimeBottomNavBar(
                        selectedTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = activeModal,
                    transitionSpec = {
                        (fadeIn() + slideInVertically(initialOffsetY = { it / 3 }))
                            .togetherWith(fadeOut() + slideOutVertically(targetOffsetY = { -it / 3 }))
                    },
                    label = "screen_flow_transition"
                ) { modal ->
                    when (modal) {
                        AppModalScreen.NONE -> {
                            when (currentTab) {
                                NavTab.HOME -> {
                                    HomeScreen(
                                        userName = userProfile.name,
                                        avatarUri = userProfile.avatarUri,
                                        sessions = todaySessions,
                                        dayStats = repository.getDayStats(),
                                        dailyGoalHours = dailyGoalHours,
                                        onCheckInClick = {
                                            activeModal = if (isSessionActive) {
                                                AppModalScreen.ACTIVE_SESSION
                                            } else {
                                                AppModalScreen.CHECK_IN
                                            }
                                        },
                                        onAvatarClick = { currentTab = NavTab.PROFILE }
                                    )
                                }

                                NavTab.STATS -> {
                                    StatsScreen(
                                        weeklyGoalProgress = repository.getWeeklyGoalProgress(),
                                        onDailyBreakdownClick = {
                                            activeModal = AppModalScreen.DAILY_BREAKDOWN
                                        },
                                        onInsightsClick = {
                                            activeModal = AppModalScreen.INSIGHTS
                                        }
                                    )
                                }

                                NavTab.GOALS -> {
                                    GoalsScreen(
                                        dailyGoalHours = dailyGoalHours,
                                        onUpdateGoal = { repository.updateDailyGoal(it) },
                                        weeklyGoalProgress = repository.getWeeklyGoalProgress()
                                    )
                                }

                                NavTab.PROFILE -> {
                                    ProfileScreen(
                                        userProfile = userProfile,
                                        onSettingsClick = { activeModal = AppModalScreen.SETTINGS },
                                        onAvatarChange = { repository.saveAvatar(it) },
                                        onPickGalleryImage = { repository.saveCustomAvatarFromUri(it) }
                                    )
                                }
                            }
                        }

                        AppModalScreen.CHECK_IN -> {
                            CheckInScreen(
                                onBackClick = { activeModal = AppModalScreen.NONE },
                                onStartSession = { title, category ->
                                    repository.startSession(title, category)
                                    activeModal = AppModalScreen.ACTIVE_SESSION
                                }
                            )
                        }

                        AppModalScreen.ACTIVE_SESSION -> {
                            ActiveSessionScreen(
                                taskTitle = activeTaskTitle,
                                elapsedSeconds = elapsedSeconds,
                                dailyGoalHours = dailyGoalHours,
                                todayTotalMinutes = repository.getDayStats().totalMinutes,
                                onBackClick = { activeModal = AppModalScreen.NONE },
                                onCheckOutClick = { notes ->
                                    val session = repository.checkoutSession(notes)
                                    completedSession = session
                                    activeModal = AppModalScreen.SESSION_COMPLETE
                                },
                                onCancelSession = {
                                    repository.cancelSession()
                                    activeModal = AppModalScreen.NONE
                                }
                            )
                        }

                        AppModalScreen.SESSION_COMPLETE -> {
                            SessionCompleteScreen(
                                session = completedSession,
                                onBackToHomeClick = {
                                    activeModal = AppModalScreen.NONE
                                    currentTab = NavTab.HOME
                                }
                            )
                        }

                        AppModalScreen.DAILY_BREAKDOWN -> {
                            DailyBreakdownScreen(
                                stats = repository.getDayStats(),
                                onBackClick = { activeModal = AppModalScreen.NONE }
                            )
                        }

                        AppModalScreen.INSIGHTS -> {
                            InsightsScreen(
                                sessions = todaySessions,
                                dayStats = repository.getDayStats(),
                                hasData = todaySessions.isNotEmpty(),
                                onBackClick = { activeModal = AppModalScreen.NONE }
                            )
                        }

                        AppModalScreen.SETTINGS -> {
                            SettingsScreen(
                                currentName = userProfile.name,
                                dailyGoalHours = dailyGoalHours,
                                onBackClick = { activeModal = AppModalScreen.NONE },
                                onUpdateName = { repository.saveUserName(it) },
                                onUpdateGoal = { repository.updateDailyGoal(it) },
                                gitSyncConfig = gitSyncConfig,
                                isGitSyncing = isGitSyncing,
                                gitSyncMessage = gitSyncMessage,
                                pendingGitConflicts = pendingGitConflicts,
                                onPushClick = {
                                    gitSyncScope.launch(Dispatchers.IO) {
                                        repository.pushToGitHub(gitSyncConfig)
                                    }
                                },
                                onPullClick = {
                                    gitSyncScope.launch(Dispatchers.IO) {
                                        repository.pullFromGitHub(gitSyncConfig)
                                    }
                                },
                                onTestConnectionClick = { config, callback ->
                                    gitSyncScope.launch {
                                        repository.testGitHubConnection(config, callback)
                                    }
                                },
                                onSaveGitSettingsClick = { config ->
                                    repository.updateGitConfig(
                                        config.remoteUrl,
                                        config.personalAccessToken,
                                        config.branch,
                                        config.authorName,
                                        config.authorEmail
                                    )
                                },
                                onConflictResolve = { conflict, choice ->
                                    gitSyncScope.launch(Dispatchers.IO) {
                                        repository.resolveGitConflict(conflict, choice, gitSyncConfig)
                                    }
                                },
                                onAbortConflicts = {
                                    gitSyncScope.launch(Dispatchers.IO) {
                                        repository.abortGitConflictMerge()
                                    }
                                },
                                onClearGitMessage = { repository.clearGitMessage() }
                            )
                        }
                    }
                }
            }
        }
    }
}
