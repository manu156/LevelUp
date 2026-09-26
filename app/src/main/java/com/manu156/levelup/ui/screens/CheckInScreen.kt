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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.R
import com.manu156.levelup.data.model.SessionCategory
import com.manu156.levelup.ui.components.AnimeFeedbackStyle
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.components.slimeBounceClick
import com.manu156.levelup.ui.theme.FocusAmber
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusCoral
import com.manu156.levelup.ui.theme.FocusCyan
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary
import com.manu156.levelup.ui.theme.LocalDayTheme

@Composable
fun CheckInScreen(
    onBackClick: () -> Unit,
    suggestedSubtagsProvider: (SessionCategory) -> List<String> = { emptyList() },
    onStartSession: (title: String, category: SessionCategory, tag: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(SessionCategory.JOB) }
    var selectedSubtag by remember { mutableStateOf("") }
    var customSubtagInput by remember { mutableStateOf("") }

    // Query learned and seed suggestions for current category
    val dynamicSubtags = remember(selectedCategory) {
        suggestedSubtagsProvider(selectedCategory)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        // Top Artwork: Anime girl with headphones at twilight window
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.splash_twilight_girl),
                contentDescription = "Twilight Focus",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                FocusBgDark.copy(alpha = 0.6f),
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
                    text = "Time to level up!",
                    color = FocusTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose your focus quest",
                    color = FocusTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // Drifting Sakura Petals
        SakuraFloatingOverlay(particleCount = 14)

        // Bottom Form Card
        AnimeGlowCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Section 1: Main Tag / Category Chips
                Text(
                    text = "Select Focus Category",
                    color = FocusTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4 Main Anime Category Chips in a 2x2 grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val entries = SessionCategory.entries
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0..1) {
                                val index = row * 2 + col
                                if (index < entries.size) {
                                    val cat = entries[index]
                                    val isSelected = selectedCategory == cat
                                    val chipGlowColor = when (cat) {
                                        SessionCategory.JOB -> FocusCyan
                                        SessionCategory.CODING -> FocusPurple
                                        SessionCategory.PROJECTS -> FocusCoral
                                        SessionCategory.RESEARCH_STUDY -> FocusAmber
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) chipGlowColor.copy(alpha = 0.22f)
                                                else FocusCardBorder.copy(alpha = 0.25f)
                                            )
                                            .border(
                                                width = if (isSelected) 1.8.dp else 1.dp,
                                                color = if (isSelected) chipGlowColor else FocusCardBorder.copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .slimeBounceClick {
                                                selectedCategory = cat
                                                selectedSubtag = ""
                                                customSubtagInput = ""
                                            }
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = cat.emoji, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = cat.displayName,
                                                color = if (isSelected) FocusTextPrimary else FocusTextSecondary,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Subtag Area (Only shown for non-Job categories)
                if (selectedCategory != SessionCategory.JOB) {
                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Subtag (topic / project)",
                        color = FocusTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggested Subtag Chips Flow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dynamicSubtags.take(3).forEach { subtag ->
                            val isSelected = selectedSubtag.equals(subtag, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) FocusPurple.copy(alpha = 0.3f)
                                        else FocusCardBorder.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) FocusPurpleLight else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .slimeBounceClick {
                                        selectedSubtag = subtag
                                        customSubtagInput = subtag
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = subtag,
                                    color = if (isSelected) FocusPurpleLight else FocusTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Single Custom Subtag Input
                    OutlinedTextField(
                        value = customSubtagInput,
                        onValueChange = {
                            customSubtagInput = it
                            selectedSubtag = it
                        },
                        placeholder = {
                            Text(
                                text = "e.g. LLM Load Balancer, Diffusion Models...",
                                color = FocusTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Pencil",
                                tint = FocusPurpleLight,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
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
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Resolved Display Action Text
                val effectiveTag = when {
                    selectedCategory == SessionCategory.JOB -> "Job"
                    customSubtagInput.isNotBlank() -> customSubtagInput.trim()
                    selectedSubtag.isNotBlank() -> selectedSubtag.trim()
                    else -> selectedCategory.displayName
                }

                val buttonLabel = if (selectedCategory == SessionCategory.JOB) {
                    "Check In • 💼 Job"
                } else {
                    "Check In • ${selectedCategory.displayName} : $effectiveTag"
                }

                // Check In Button with DayTheme feedback and gradient!
                AnimePillButton(
                    text = buttonLabel,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = LocalDayTheme.current.buttonStyle.textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = {
                        val sessionTitle = if (selectedCategory == SessionCategory.JOB) "Job" else effectiveTag
                        onStartSession(sessionTitle, selectedCategory, effectiveTag)
                    }
                )
            }
        }
    }
}
