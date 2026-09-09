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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.R
import com.manu156.levelup.data.model.Goal
import com.manu156.levelup.data.model.GoalCadence
import com.manu156.levelup.data.model.GoalCategory
import com.manu156.levelup.data.model.GoalUnit
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.BullseyeTargetIcon
import com.manu156.levelup.ui.components.RoundedCapsuleBarChart
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.components.slimeBounceClick
import com.manu156.levelup.ui.components.sparkleBurstClick
import com.manu156.levelup.ui.theme.FocusAmber
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

@Composable
fun GoalsScreen(
    goals: List<Goal> = Goal.defaultFixedGoals(),
    onUpdateGoalTarget: (goalId: String, newTarget: Double) -> Unit = { _, _ -> },
    onAddCustomGoal: (title: String, target: Double, unit: GoalUnit, cadence: GoalCadence) -> Unit = { _, _, _, _ -> },
    onDeleteGoal: (goalId: String) -> Unit = {}
) {
    var editingGoal by remember { mutableStateOf<Goal?>(null) }
    var showAddCustomDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        SakuraFloatingOverlay(particleCount = 12)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Header: Target icon + Goals + Add Custom Goal Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BullseyeTargetIcon(tint = FocusPurpleLight, size = 28.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Goals",
                        color = FocusTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 20.sp)
                }

                // Add Goal Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(FocusPurple)
                        .clickable { showAddCustomDialog = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Goal",
                            tint = FocusTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Custom Goal",
                            color = FocusTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Goals List
            goals.forEach { goal ->
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF26233B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BullseyeTargetIcon(
                                            tint = when (goal.category) {
                                                GoalCategory.WORK -> FocusAmber
                                                GoalCategory.RUNNING -> FocusMint
                                                GoalCategory.BEDTIME -> FocusPurpleLight
                                                GoalCategory.WAKE_TIME -> Color(0xFFFF9F43)
                                                GoalCategory.CUSTOM -> Color(0xFF54a0ff)
                                            },
                                            size = 20.dp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = goal.title,
                                            color = FocusTextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${goal.category.displayName} • ${goal.cadence.displayName}",
                                                color = FocusTextSecondary,
                                                fontSize = 12.sp
                                            )
                                            if (goal.isFixed) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "📌 Fixed",
                                                    color = FocusTextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!goal.isFixed) {
                                    // Delete Custom Goal Button
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3D2331))
                                            .clickable { onDeleteGoal(goal.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFFF6B6B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                // Fuji or Badge Illustration
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, FocusAmber.copy(alpha = 0.6f), CircleShape)
                                        .sparkleBurstClick { }
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.badge_fuji),
                                        contentDescription = "Badge",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Progress",
                                color = FocusTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${goal.formattedCurrent()} / ${goal.formattedTarget()}",
                                color = FocusMint,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { goal.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = FocusMint,
                            trackColor = FocusCardBorder
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Edit Target Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(Color(0xFF22284E))
                                .border(1.dp, FocusCardBorder, RoundedCornerShape(19.dp))
                                .slimeBounceClick { editingGoal = goal },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Target",
                                    tint = FocusPurpleLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit Goal Target",
                                    color = FocusTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Card: Weekly Overview Chart
            AnimeGlowCard(
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            text = "Weekly Progress Overview",
                            color = FocusTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "70%",
                            color = FocusTextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    RoundedCapsuleBarChart(height = 140.dp)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Edit Goal Target Dialog
        editingGoal?.let { targetGoal ->
            var tempTarget by remember { mutableDoubleStateOf(targetGoal.targetValue) }

            AlertDialog(
                onDismissRequest = { editingGoal = null },
                containerColor = FocusCardBg,
                title = {
                    Text(
                        text = "Edit Target for ${targetGoal.title}",
                        color = FocusTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        val formattedTemp = when (targetGoal.unit) {
                            GoalUnit.TIME_OF_DAY -> Goal.formatTimeOfDay(tempTarget)
                            GoalUnit.HOURS -> "${tempTarget.toInt()} hours"
                            GoalUnit.KILOMETERS -> String.format(java.util.Locale.US, "%.1f km", tempTarget)
                            GoalUnit.MINUTES -> "${tempTarget.toInt()} minutes"
                            GoalUnit.COUNT -> "${tempTarget.toInt()}"
                        }

                        Text(
                            text = "New Target: $formattedTemp",
                            color = FocusPurpleLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val (rangeMin, rangeMax, stepCount) = when (targetGoal.unit) {
                            GoalUnit.TIME_OF_DAY -> Triple(18f, 28f, 20) // 6:00 PM to 4:00 AM / 10:00 AM
                            GoalUnit.HOURS -> Triple(1f, 16f, 15)
                            GoalUnit.KILOMETERS -> Triple(1f, 20f, 38)
                            GoalUnit.MINUTES -> Triple(10f, 180f, 17)
                            GoalUnit.COUNT -> Triple(1f, 20f, 19)
                        }

                        Slider(
                            value = tempTarget.toFloat(),
                            onValueChange = { tempTarget = it.toDouble() },
                            valueRange = rangeMin..rangeMax,
                            steps = stepCount,
                            colors = SliderDefaults.colors(
                                thumbColor = FocusPurple,
                                activeTrackColor = FocusPurple,
                                inactiveTrackColor = FocusCardBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onUpdateGoalTarget(targetGoal.id, tempTarget)
                            editingGoal = null
                        }
                    ) {
                        Text("Save", color = FocusMint, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingGoal = null }) {
                        Text("Cancel", color = FocusTextSecondary)
                    }
                }
            )
        }

        // Add Custom Goal Dialog
        if (showAddCustomDialog) {
            var customTitle by remember { mutableStateOf("") }
            var customTargetText by remember { mutableStateOf("1.0") }
            var selectedUnit by remember { mutableStateOf(GoalUnit.HOURS) }
            var selectedCadence by remember { mutableStateOf(GoalCadence.DAILY) }

            AlertDialog(
                onDismissRequest = { showAddCustomDialog = false },
                containerColor = FocusCardBg,
                title = {
                    Text(
                        text = "Create Custom Goal ✨",
                        color = FocusTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = customTitle,
                            onValueChange = { customTitle = it },
                            label = { Text("Goal Title", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Reading, Meditation", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FocusPurple,
                                unfocusedBorderColor = FocusCardBorder,
                                focusedTextColor = FocusTextPrimary,
                                unfocusedTextColor = FocusTextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customTargetText,
                            onValueChange = { customTargetText = it },
                            label = { Text("Target Threshold Value", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FocusPurple,
                                unfocusedBorderColor = FocusCardBorder,
                                focusedTextColor = FocusTextPrimary,
                                unfocusedTextColor = FocusTextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Unit", color = FocusTextSecondary, fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf(GoalUnit.HOURS, GoalUnit.KILOMETERS, GoalUnit.MINUTES, GoalUnit.COUNT).forEach { unit ->
                                val isSelected = selectedUnit == unit
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) FocusPurple else FocusCardBorder)
                                        .clickable { selectedUnit = unit }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = unit.name,
                                        color = FocusTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Cadence", color = FocusTextSecondary, fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf(GoalCadence.DAILY, GoalCadence.WEEKLY).forEach { cadence ->
                                val isSelected = selectedCadence == cadence
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) FocusPurple else FocusCardBorder)
                                        .clickable { selectedCadence = cadence }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cadence.displayName,
                                        color = FocusTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val targetVal = customTargetText.toDoubleOrNull() ?: 1.0
                            if (customTitle.isNotBlank()) {
                                onAddCustomGoal(customTitle, targetVal, selectedUnit, selectedCadence)
                            }
                            showAddCustomDialog = false
                        }
                    ) {
                        Text("Create Goal", color = FocusMint, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCustomDialog = false }) {
                        Text("Cancel", color = FocusTextSecondary)
                    }
                }
            )
        }
    }
}
