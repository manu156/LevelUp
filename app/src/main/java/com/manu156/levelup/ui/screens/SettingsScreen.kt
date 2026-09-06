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
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
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
                        .clickable { onBackClick() },
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
                    // Action 1: Generate Sample / Dummy Data
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFF232B50))
                            .border(1.dp, FocusPurple.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                            .clickable {
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

                    // Action 2: Delete All Data
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color(0xFF381C28))
                            .border(1.dp, FocusCoral.copy(alpha = 0.6f), RoundedCornerShape(25.dp))
                            .clickable { showDeleteConfirmDialog = true },
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
