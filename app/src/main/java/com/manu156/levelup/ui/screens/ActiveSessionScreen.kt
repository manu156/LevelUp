package com.manu156.levelup.ui.screens

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.CatFaceIcon
import com.manu156.levelup.ui.components.CircularTimerGauge
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.components.nekoTwitchClick
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusCoral
import com.manu156.levelup.ui.theme.FocusCoralDark
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

@Composable
fun ActiveSessionScreen(
    taskTitle: String,
    elapsedSeconds: Long,
    onBackClick: () -> Unit,
    onCheckOutClick: (notes: String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var isEditingNotes by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        // Floating sakura petals
        SakuraFloatingOverlay(particleCount = 16)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Top bar: Back arrow + More options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FocusCardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = FocusTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sleeping Cat Timer Gauge
            CircularTimerGauge(
                elapsedSeconds = if (elapsedSeconds == 0L) (3 * 3600 + 24 * 60 + 17) else elapsedSeconds,
                progressFraction = 0.65f,
                size = 270.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Card 1: Today's Total
            AnimeGlowCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Today's total",
                        color = FocusTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3h 42m",
                        color = FocusTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Increase",
                            tint = FocusMint,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2h vs. yesterday",
                            color = FocusMint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rounded horizontal progress bar
                    LinearProgressIndicator(
                        progress = { 0.46f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = FocusMint,
                        trackColor = FocusCardBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 2: What's on your mind? (optional)
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditingNotes = !isEditingNotes }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24264A)),
                                contentAlignment = Alignment.Center
                            ) {
                                CatFaceIcon(tint = FocusPurpleLight, size = 18.dp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "What's on your mind?",
                                    color = FocusTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "(optional)",
                                    color = FocusTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Expand",
                            tint = FocusTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isEditingNotes) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = {
                                Text(
                                    "Jot down your reflection or breakthrough...",
                                    color = FocusTextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FocusPurple,
                                unfocusedBorderColor = FocusCardBorder,
                                focusedTextColor = FocusTextPrimary,
                                unfocusedTextColor = FocusTextPrimary,
                                focusedContainerColor = FocusBgDark,
                                unfocusedContainerColor = FocusBgDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Coral Check Out Pill Button with Sakura Quest Stamp Hanko feedback!
            AnimePillButton(
                text = "Check Out",
                modifier = Modifier.fillMaxWidth(),
                feedbackStyle = AnimeFeedbackStyle.SAKURA_STAMP,
                icon = {
                    CatFaceIcon(tint = DarkButtonText, size = 20.dp)
                },
                gradientColors = listOf(FocusCoral, FocusCoralDark),
                onClick = { onCheckOutClick(notes) }
            )

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
