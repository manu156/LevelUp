package com.manu156.levelup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.R
import com.manu156.levelup.data.health.HealthConnectSyncedData
import com.manu156.levelup.data.model.CheckInGoalItem
import com.manu156.levelup.data.model.Goal
import com.manu156.levelup.data.model.GoalCategory
import com.manu156.levelup.data.model.GoalUnit
import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.components.slimeBounceClick
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary
import kotlinx.coroutines.launch

class CheckInItemFormState(
    val goal: Goal,
    val initialSyncedValue: Double?,
    val isHcSynced: Boolean
) {
    var userValueText by mutableStateOf(
        when (goal.unit) {
            GoalUnit.TIME_OF_DAY -> Goal.formatTimeOfDay(initialSyncedValue ?: goal.targetValue)
            GoalUnit.HOURS -> String.format(java.util.Locale.US, "%.1f", initialSyncedValue ?: goal.targetValue)
            GoalUnit.KILOMETERS -> String.format(java.util.Locale.US, "%.1f", initialSyncedValue ?: goal.targetValue)
            else -> "${(initialSyncedValue ?: goal.targetValue).toInt()}"
        }
    )
    var isConfirmed by mutableStateOf(true)
}

@Composable
fun CheckInScreen(
    goals: List<Goal> = emptyList(),
    healthSyncedData: HealthConnectSyncedData = HealthConnectSyncedData(),
    onSyncHealthConnect: suspend () -> Unit = {},
    onSubmitCheckIn: (List<CheckInGoalItem>) -> Unit = {},
    onBackClick: () -> Unit,
    onStartSession: (title: String, category: SessionCategory) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dual Verification Check-in, 1: Focus Session
    var taskTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SessionCategory.DEEP_WORK) }
    var isSyncing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Trigger on-demand sync when Check-in screen opens
    LaunchedEffect(Unit) {
        isSyncing = true
        onSyncHealthConnect()
        isSyncing = false
    }

    // Prepare item states
    val checkInStates = remember(goals, healthSyncedData) {
        goals.map { goal ->
            val (syncedVal, isHc) = when (goal.category) {
                GoalCategory.RUNNING -> Pair(healthSyncedData.runningKm, healthSyncedData.isAvailable)
                GoalCategory.BEDTIME -> Pair(healthSyncedData.bedtimeHourFraction, healthSyncedData.isAvailable)
                GoalCategory.WAKE_TIME -> Pair(healthSyncedData.wakeHourFraction, healthSyncedData.isAvailable)
                else -> Pair(null, false)
            }
            CheckInItemFormState(goal, syncedVal, isHc)
        }
    }

    val presetChips = listOf(
        "Project Apollo",
        "Client calls",
        "Deep Work",
        "Code Review",
        "Study Session"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.splash_twilight_girl),
                    contentDescription = "Twilight Focus",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    FocusBgDark.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    FocusBgDark
                                )
                            )
                        )
                )

                // Top Bar: Back arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FocusCardBg.copy(alpha = 0.7f))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FocusTextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Title overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daily Check-in ✨",
                        color = FocusTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Review & confirm auto-synced goals",
                        color = FocusTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            // Sakura floating petals
            SakuraFloatingOverlay(particleCount = 12)

            // Tab Row (Check-in Goals vs Start Work Session)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = FocusBgDark,
                contentColor = FocusPurpleLight,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = FocusPurple
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Goals Verification",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Start Focus Work",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Dual Verification Model Section
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Health Connect",
                                    tint = Color(0xFFFF6B81),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Health Connect Synced",
                                    color = FocusTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Sync Status & Refresh Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(FocusCardBorder.copy(alpha = 0.5f))
                                    .clickable {
                                        scope.launch {
                                            isSyncing = true
                                            onSyncHealthConnect()
                                            isSyncing = false
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sync Now",
                                        tint = FocusPurpleLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSyncing) "Syncing..." else "Sync Now",
                                        color = FocusPurpleLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render Goal Verification Cards
                        checkInStates.forEach { formState ->
                            val g = formState.goal
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(FocusCardBg)
                                    .border(
                                        1.dp,
                                        if (formState.isConfirmed) FocusPurple.copy(alpha = 0.6f) else FocusCardBorder,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(
                                                checked = formState.isConfirmed,
                                                onCheckedChange = { formState.isConfirmed = it },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = FocusPurple,
                                                    uncheckedColor = FocusTextMuted
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Column {
                                                Text(
                                                    text = g.title,
                                                    color = FocusTextPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Target: ${g.formattedTarget()}",
                                                    color = FocusTextSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        // Synced Badge
                                        if (formState.isHcSynced) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(FocusMint.copy(alpha = 0.15f))
                                                    .border(1.dp, FocusMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Auto-Synced ✨",
                                                    color = FocusMint,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(FocusCardBorder.copy(alpha = 0.3f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Manual Log",
                                                    color = FocusTextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // User Value Edit / Override
                                    OutlinedTextField(
                                        value = formState.userValueText,
                                        onValueChange = { formState.userValueText = it },
                                        label = {
                                            Text("Recorded Value (${g.unit.symbol.ifEmpty { "time" }})", fontSize = 12.sp)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = FocusPurple,
                                            unfocusedBorderColor = FocusCardBorder,
                                            focusedTextColor = FocusTextPrimary,
                                            unfocusedTextColor = FocusTextPrimary,
                                            focusedContainerColor = FocusBgDark.copy(alpha = 0.5f),
                                            unfocusedContainerColor = FocusBgDark.copy(alpha = 0.5f)
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Submit Confirmation Button
                        AnimePillButton(
                            text = "Confirm & Submit Check-in",
                            modifier = Modifier.fillMaxWidth(),
                            feedbackStyle = AnimeFeedbackStyle.KATANA,
                            textColor = DarkButtonText,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Confirm",
                                    tint = DarkButtonText,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                val submittedItems = checkInStates.map { state ->
                                    val parsedVal = when (state.goal.unit) {
                                        GoalUnit.TIME_OF_DAY -> Goal.parseTimeOfDayToFraction(state.userValueText)
                                        else -> state.userValueText.toDoubleOrNull() ?: state.goal.targetValue
                                    }
                                    CheckInGoalItem(
                                        goalId = state.goal.id,
                                        goalTitle = state.goal.title,
                                        category = state.goal.category,
                                        targetValue = state.goal.targetValue,
                                        unit = state.goal.unit,
                                        syncedValue = state.initialSyncedValue,
                                        userValue = parsedVal,
                                        isConfirmed = state.isConfirmed,
                                        isHealthConnectSynced = state.isHcSynced
                                    )
                                }
                                onSubmitCheckIn(submittedItems)
                                onBackClick()
                            }
                        )
                    }
                }
            } else {
                // Focus Work Session Start Section
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "What are you working on?",
                            color = FocusTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            placeholder = {
                                Text(
                                    text = "e.g. Project Apollo, Client calls, etc.",
                                    color = FocusTextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Pencil",
                                    tint = FocusPurpleLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FocusPurple,
                                unfocusedBorderColor = FocusCardBorder,
                                focusedTextColor = FocusTextPrimary,
                                unfocusedTextColor = FocusTextPrimary,
                                focusedContainerColor = FocusBgDark.copy(alpha = 0.6f),
                                unfocusedContainerColor = FocusBgDark.copy(alpha = 0.6f)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presetChips.take(3).forEach { chip ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(FocusCardBorder.copy(alpha = 0.5f))
                                        .slimeBounceClick { taskTitle = chip }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = chip,
                                        color = FocusTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        AnimePillButton(
                            text = "Start Focus Session",
                            modifier = Modifier.fillMaxWidth(),
                            feedbackStyle = AnimeFeedbackStyle.KATANA,
                            textColor = DarkButtonText,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = DarkButtonText,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                val title = if (taskTitle.isBlank()) "Project Apollo" else taskTitle
                                onStartSession(title, selectedCategory)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
