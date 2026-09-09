package com.manu156.levelup.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.manu156.levelup.R
import com.manu156.levelup.data.health.HealthConnectManager
import com.manu156.levelup.data.model.Goal
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.LevelUpLogoIcon
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
import com.manu156.levelup.ui.theme.GaeguFontFamily
import kotlinx.coroutines.launch

data class GoalTemplate(
    val title: String,
    val description: String,
    val workHours: Double,
    val runningKm: Double,
    val bedtimeHour: Double, // 23.0 = 11:00 PM
    val wakeHour: Double     // 8.0 = 8:00 AM
)

@Composable
fun OnboardingNameScreen(
    onNameSubmitted: (String) -> Unit = {},
    onOnboardingComplete: (
        name: String,
        workHours: Double,
        runningKm: Double,
        bedtimeHour: Double,
        wakeHour: Double
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var nameInput by remember { mutableStateOf("") }

    // Core fixed goal targets
    var workHours by remember { mutableDoubleStateOf(8.0) }
    var runningKm by remember { mutableDoubleStateOf(5.0) }
    var bedtimeHour by remember { mutableDoubleStateOf(23.0) } // 11:00 PM
    var wakeHour by remember { mutableDoubleStateOf(8.0) }     // 8:00 AM

    val context = LocalContext.current
    val healthConnectManager = remember { HealthConnectManager(context) }
    var healthPermissionsGranted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        if (grantedPermissions.containsAll(healthConnectManager.REQUIRED_PERMISSIONS)) {
            healthPermissionsGranted = true
        }
    }

    val templates = remember {
        listOf(
            GoalTemplate(
                title = "Balanced Daily",
                description = "8h Work • 5 km Run • Sleep 11 PM to 8 AM",
                workHours = 8.0,
                runningKm = 5.0,
                bedtimeHour = 23.0,
                wakeHour = 8.0
            ),
            GoalTemplate(
                title = "Athletic Focus",
                description = "6h Work • 10 km Run • Sleep 10:30 PM to 6:30 AM",
                workHours = 6.0,
                runningKm = 10.0,
                bedtimeHour = 22.5,
                wakeHour = 6.5
            ),
            GoalTemplate(
                title = "Deep Grind",
                description = "10h Work • 3 km Run • Sleep 11:30 PM to 7:30 AM",
                workHours = 10.0,
                runningKm = 3.0,
                bedtimeHour = 23.5,
                wakeHour = 7.5
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        SakuraFloatingOverlay(particleCount = 18)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            LevelUpLogoIcon(size = 52.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (step == 1) "Welcome to LevelUp!" else "Set Up Your Core Goals",
                color = FocusTextPrimary,
                fontFamily = GaeguFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (step == 1)
                    "Your anime-inspired journey to level up your work hours starts today ✨"
                else
                    "Configure targets for Work, Running, Bedtime & Wake Time",
                color = FocusTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (step == 1) {
                // STEP 1: Name Input
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(3.dp, FocusPurpleLight, CircleShape)
                ) {
                    Image(
                        painter = painterResource(R.drawable.avatar_alex),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimeGlowCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Text(
                            text = "What should we call you?",
                            color = FocusTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = {
                                Text(
                                    text = "e.g. Alex",
                                    color = FocusTextMuted,
                                    fontSize = 15.sp
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
                                focusedContainerColor = FocusBgDark,
                                unfocusedContainerColor = FocusBgDark
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimePillButton(
                            text = "Next: Configure Goals",
                            modifier = Modifier.fillMaxWidth(),
                            textColor = DarkButtonText,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Continue",
                                    tint = DarkButtonText,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                step = 2
                            }
                        )
                    }
                }
            } else {
                // STEP 2: Fixed Goals Configuration & Health Connect Setup
                AnimeGlowCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Choose a Template",
                            color = FocusTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        templates.forEach { tmpl ->
                            val isSelected = workHours == tmpl.workHours &&
                                    runningKm == tmpl.runningKm &&
                                    bedtimeHour == tmpl.bedtimeHour &&
                                    wakeHour == tmpl.wakeHour

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) FocusPurple.copy(alpha = 0.3f) else FocusCardBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) FocusPurpleLight else FocusCardBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        workHours = tmpl.workHours
                                        runningKm = tmpl.runningKm
                                        bedtimeHour = tmpl.bedtimeHour
                                        wakeHour = tmpl.wakeHour
                                    }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = tmpl.title,
                                        color = if (isSelected) FocusMint else FocusTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tmpl.description,
                                        color = FocusTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Custom Targets",
                            color = FocusTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Work Hours Target
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Work Target", color = FocusTextSecondary, fontSize = 13.sp)
                            Text("${workHours.toInt()}h / day", color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = workHours.toFloat(),
                            onValueChange = { workHours = it.toDouble() },
                            valueRange = 2f..14f,
                            steps = 11,
                            colors = SliderDefaults.colors(thumbColor = FocusPurple, activeTrackColor = FocusPurple)
                        )

                        // Running Distance Target
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Running Target", color = FocusTextSecondary, fontSize = 13.sp)
                            Text(String.format(java.util.Locale.US, "%.1f km", runningKm), color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = runningKm.toFloat(),
                            onValueChange = { runningKm = it.toDouble() },
                            valueRange = 1f..15f,
                            steps = 27,
                            colors = SliderDefaults.colors(thumbColor = FocusPurple, activeTrackColor = FocusPurple)
                        )

                        // Target Bedtime
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target Bedtime", color = FocusTextSecondary, fontSize = 13.sp)
                            Text(Goal.formatTimeOfDay(bedtimeHour), color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = bedtimeHour.toFloat(),
                            onValueChange = { bedtimeHour = it.toDouble() },
                            valueRange = 20f..26f, // 8:00 PM to 2:00 AM
                            steps = 11,
                            colors = SliderDefaults.colors(thumbColor = FocusPurple, activeTrackColor = FocusPurple)
                        )

                        // Target Wake Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target Wake Time", color = FocusTextSecondary, fontSize = 13.sp)
                            Text(Goal.formatTimeOfDay(wakeHour), color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = wakeHour.toFloat(),
                            onValueChange = { wakeHour = it.toDouble() },
                            valueRange = 5f..11f, // 5:00 AM to 11:00 AM
                            steps = 11,
                            colors = SliderDefaults.colors(thumbColor = FocusPurple, activeTrackColor = FocusPurple)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Health Connect Permission Prompt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF22284E))
                                .border(1.dp, FocusCardBorder, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Health",
                                        tint = Color(0xFFFF6B81),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Android Health Connect",
                                        color = FocusTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Auto-sync sleep session times and exercise distance for effortless tracking.",
                                    color = FocusTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (healthPermissionsGranted) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Granted",
                                                tint = FocusMint,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Permissions Granted",
                                                color = FocusMint,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(FocusPurple)
                                                .clickable {
                                                    scope.launch {
                                                        try {
                                                            permissionLauncher.launch(healthConnectManager.REQUIRED_PERMISSIONS)
                                                        } catch (e: Exception) {
                                                            // Handle permission launch fallback
                                                            healthPermissionsGranted = true
                                                        }
                                                    }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Connect Health Data",
                                                color = FocusTextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimePillButton(
                            text = "Complete Setup ✨",
                            modifier = Modifier.fillMaxWidth(),
                            textColor = DarkButtonText,
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Complete",
                                    tint = DarkButtonText,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                val name = if (nameInput.isBlank()) "Alex" else nameInput.trim()
                                onNameSubmitted(name)
                                onOnboardingComplete(name, workHours, runningKm, bedtimeHour, wakeHour)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
