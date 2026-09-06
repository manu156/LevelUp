package com.manu156.levelup.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.components.animePanicShakeClick
import com.manu156.levelup.ui.components.animeSpringClick
import com.manu156.levelup.ui.components.katanaSlashClick
import com.manu156.levelup.ui.components.nekoTwitchClick
import com.manu156.levelup.ui.components.sakuraStampClick
import com.manu156.levelup.ui.components.slimeBounceClick
import com.manu156.levelup.ui.components.sparkleBurstClick
import com.manu156.levelup.ui.theme.AnimeThemeManager
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusCoral
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

@Composable
fun SettingsScreen(
    currentName: String,
    dailyGoalHours: Float,
    onBackClick: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateGoal: (Float) -> Unit,
    onGenerateDummyData: () -> Unit,
    onDeleteAllData: () -> Unit
) {
    val context = LocalContext.current
    var showNameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundHapticsEnabled by remember { mutableStateOf(true) }

    var lastDebugAction by remember { mutableStateOf("None (Tap any button below)") }
    var debugClickCount by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        SakuraFloatingOverlay(particleCount = 10)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FocusCardBg)
                        .nekoTwitchClick(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FocusTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Settings",
                    color = FocusTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Profile & Preferences
            Text(
                text = "Profile & Target",
                color = FocusPurpleLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // User Name row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Name", color = FocusTextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(currentName, color = FocusTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF262C52))
                                .clickable { showNameDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = FocusPurpleLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change", color = FocusPurpleLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(FocusCardBorder.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Goal setting
                    Text("Daily Goal: ${dailyGoalHours.toInt()}h per day", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = dailyGoalHours,
                        onValueChange = { onUpdateGoal(it) },
                        valueRange = 2f..14f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = FocusPurple,
                            activeTrackColor = FocusPurple,
                            inactiveTrackColor = FocusCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Visual Theme
            Text(
                text = "Theme & Customization",
                color = FocusPurpleLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showThemeDialog = true }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = "Theme", tint = FocusPurpleLight, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Anime Theme Preset", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(AnimeThemeManager.currentPreset.title, color = FocusTextSecondary, fontSize = 12.sp)
                        }
                    }
                    Text("Switch", color = FocusPurpleLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Focus & Notifications
            Text(
                text = "Reminders & Audio",
                color = FocusPurpleLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Daily Focus Reminder", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("Nudges to start your work sessions", color = FocusTextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FocusPurple,
                                checkedTrackColor = FocusPurple.copy(alpha = 0.4f),
                                uncheckedTrackColor = FocusCardBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(FocusCardBorder.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Haptic Feedback", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text("Tactile anime spring response", color = FocusTextSecondary, fontSize = 12.sp)
                        }
                        Switch(
                            checked = soundHapticsEnabled,
                            onCheckedChange = { soundHapticsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FocusPurple,
                                checkedTrackColor = FocusPurple.copy(alpha = 0.4f),
                                uncheckedTrackColor = FocusCardBorder
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 4: Data Management (At the bottom, as requested)
            Text(
                text = "Data Management",
                color = FocusPurpleLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Action 1: Generate Sample / Dummy Data with Sparkle Burst
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFF232B50))
                            .border(1.dp, FocusPurple.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                            .sparkleBurstClick {
                                onGenerateDummyData()
                                Toast.makeText(context, "Sample sessions and data generated ✨", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = FocusMint, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Dummy Data", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Action 2: Delete All Data with Comic Sweat-Drop & Panic Jitter
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFF381C28))
                            .border(1.dp, FocusCoral.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                            .animePanicShakeClick { showDeleteConfirmDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Delete", tint = FocusCoral, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete All Data", color = FocusCoral, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section 5: Debug (Anime Button & VFX Inspector)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Debug",
                    color = FocusPurpleLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FocusPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VFX Inspector",
                        color = FocusPurpleLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Inspector Status Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF161A30))
                            .border(1.dp, FocusPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Last Triggered Action", color = FocusTextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(lastDebugAction, color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            if (debugClickCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FocusPurple)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("#$debugClickCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 1. Katana Slash
                    Column {
                        Text("1. Katana Slash (170ms delay • Neon blade cut & impact flash)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimePillButton(
                            text = "⚔️ Katana Slash (Check In)",
                            gradientColors = listOf(Color(0xFF6CFFCE), Color(0xFF00E5FF)),
                            feedbackStyle = AnimeFeedbackStyle.KATANA,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lastDebugAction = "Katana Slash ⚔️ (170ms delay)"
                            debugClickCount++
                        }
                    }

                    // 2. Slime / Mochi Squash
                    Column {
                        Text("2. Slime / Mochi Bounce (120ms delay • Jelly squash & sheen)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimePillButton(
                            text = "🍮 Slime Squash & Stretch (CTA)",
                            gradientColors = listOf(FocusPurple, FocusPurpleLight),
                            feedbackStyle = AnimeFeedbackStyle.SLIME,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lastDebugAction = "Slime Squash 🍮 (120ms delay)"
                            debugClickCount++
                        }
                    }

                    // 3. Sakura Quest Stamp
                    Column {
                        Text("3. Sakura Quest Stamp (200ms delay • Hanko seal & shockwave)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimePillButton(
                            text = "💮 Sakura Hanko Stamp (Check Out)",
                            gradientColors = listOf(Color(0xFFFF5277), Color(0xFFFF85A1)),
                            feedbackStyle = AnimeFeedbackStyle.SAKURA_STAMP,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lastDebugAction = "Sakura Stamp 💮 (200ms delay)"
                            debugClickCount++
                        }
                    }

                    // 4. Comic Panic Jitter
                    Column {
                        Text("4. Comic Panic Jitter (180ms delay • 60Hz anxiety & sweat-drop)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color(0xFF381C28))
                                .border(1.dp, FocusCoral.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                                .animePanicShakeClick {
                                    lastDebugAction = "Comic Panic Jitter 💧 (180ms delay)"
                                    debugClickCount++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = FocusCoral, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("💧 Comic Panic Jitter (Danger)", color = FocusCoral, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 5. Mahou Sparkle Burst
                    Column {
                        Text("5. Mahou Sparkle Burst (120ms delay • Stardust diamond burst)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color(0xFF232B50))
                                .border(1.dp, FocusMint.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                                .sparkleBurstClick {
                                    lastDebugAction = "Sparkle Burst ✨ (120ms delay)"
                                    debugClickCount++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FocusMint, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("✨ Mahou Sparkle Burst", color = FocusTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 6. Neko Ear Twitch
                    Column {
                        Text("6. Neko Ear Twitch (150ms delay • Ears pop & head tilt)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color(0xFF262C52))
                                .border(1.dp, FocusPurpleLight.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                                .nekoTwitchClick {
                                    lastDebugAction = "Neko Ear Twitch 🐾 (150ms delay)"
                                    debugClickCount++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🐾", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Neko Ear Twitch (Nav & Avatar)", color = FocusPurpleLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 7. Tactile Spring
                    Column {
                        Text("7. Tactile Spring (70ms delay • Classic bouncy compression)", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimePillButton(
                            text = "🌸 Classic Anime Spring",
                            gradientColors = listOf(Color(0xFFFFB2C9), Color(0xFFFF85A1)),
                            feedbackStyle = AnimeFeedbackStyle.DEFAULT,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lastDebugAction = "Classic Spring 🌸 (70ms delay)"
                            debugClickCount++
                        }
                    }

                    // 8. Compact Controls & Chips
                    Column {
                        Text("8. Compact Controls & Chips", color = FocusTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF262C52))
                                    .border(1.dp, FocusPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .slimeBounceClick {
                                        lastDebugAction = "Preset Chip (+25m, Slime)"
                                        debugClickCount++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+25m Chip", color = FocusTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(FocusCardBg)
                                    .border(1.dp, FocusCardBorder, CircleShape)
                                    .nekoTwitchClick {
                                        lastDebugAction = "Neko Circle Icon 🐾"
                                        debugClickCount++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🐾", fontSize = 16.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, FocusMint.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .sparkleBurstClick {
                                        lastDebugAction = "Sparkle Badge ✨"
                                        debugClickCount++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FocusMint, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Badge", color = FocusMint, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Change Name Dialog
        if (showNameDialog) {
            var tempName by remember { mutableStateOf(currentName) }
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                containerColor = FocusCardBg,
                title = { Text("Change Your Name", color = FocusTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FocusPurple,
                            unfocusedBorderColor = FocusCardBorder,
                            focusedTextColor = FocusTextPrimary,
                            unfocusedTextColor = FocusTextPrimary
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (tempName.isNotBlank()) {
                            onUpdateName(tempName.trim())
                        }
                        showNameDialog = false
                    }) {
                        Text("Save", color = FocusMint, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameDialog = false }) {
                        Text("Cancel", color = FocusTextSecondary)
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                containerColor = FocusCardBg,
                title = { Text("Delete All Data?", color = FocusCoral, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete all work sessions, daily totals, and streak history? This action cannot be undone.",
                        color = FocusTextSecondary,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteAllData()
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "All data deleted.", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Delete Everything", color = FocusCoral, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel", color = FocusTextSecondary)
                    }
                }
            )
        }

        // Theme Switcher Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                containerColor = FocusCardBg,
                title = { Text("Select Anime Theme", color = FocusTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        AnimeThemeManager.presets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        AnimeThemeManager.switchTheme(preset.id)
                                        showThemeDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = AnimeThemeManager.currentPreset.id == preset.id,
                                    onClick = {
                                        AnimeThemeManager.switchTheme(preset.id)
                                        showThemeDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = FocusPurple)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(preset.title, color = FocusTextPrimary, fontSize = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close", color = FocusMint)
                    }
                }
            )
        }
    }
}
