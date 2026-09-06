package com.manu156.levelup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.CatFaceIcon
import com.manu156.levelup.ui.components.CrownIcon
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.theme.FocusAmber
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.manu156.levelup.ui.components.AnimePillButton
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.nekoTwitchClick
import com.manu156.levelup.ui.components.sparkleBurstClick
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder

data class InsightDetail(
    val title: String,
    val subtitle: String,
    val highlightValue: String,
    val description: String,
    val tips: List<String>,
    val badgeIcon: @Composable () -> Unit
)

@Composable
fun InsightsScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedInsight by remember { mutableStateOf<InsightDetail?>(null) }

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

            // Header: Back Button + Insights ✨ + Cat face
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(FocusCardBg)
                        .border(1.dp, FocusCardBorder, CircleShape)
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

                Spacer(modifier = Modifier.width(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Insights",
                        color = FocusTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                CatFaceIcon(tint = FocusPurpleLight, size = 26.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Banner Card: You're on track! + Anime girl sticker
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .sparkleBurstClick {
                        selectedInsight = InsightDetail(
                            title = "Weekly Velocity",
                            subtitle = "Overall Progress",
                            highlightValue = "+12% vs. Last Week",
                            description = "Your dedicated focus time increased significantly compared to the previous week. You maintained consistent output across morning and afternoon sessions.",
                            tips = listOf(
                                "Maintain your regular morning start time at 9:00 AM.",
                                "Keep daily work blocks between 45–90 minutes with 10-minute breaks.",
                                "Drink water and stretch between sessions to avoid fatigue."
                            ),
                            badgeIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1B3835)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Idea",
                                        tint = FocusMint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B3835)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Idea",
                                    tint = FocusMint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "You're on track!",
                                color = FocusTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "You've increased your work hours by 12% compared to last week. Keep it up!",
                            color = FocusTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Anime girl peeking sticker
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.anime_girl_peeking),
                            contentDescription = "Anime Cheer",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Insight Card 1: Best Day
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .sparkleBurstClick {
                        selectedInsight = InsightDetail(
                            title = "Peak Day Record",
                            subtitle = "Best Day Performance",
                            highlightValue = "Thursday • 8h 12m",
                            description = "Thursday was your highest output day this week. You completed 4 uninterrupted focus sessions with an impressive 92% deep work completion rate.",
                            tips = listOf(
                                "Zero interruptions were logged during your morning session.",
                                "Batching communication in the late afternoon preserved mental energy.",
                                "Apply Thursday's setup to Tuesday and Wednesday for maximum flow."
                            ),
                            badgeIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF28231E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CrownIcon(tint = FocusAmber, size = 22.dp)
                                }
                            }
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF28231E)),
                            contentAlignment = Alignment.Center
                        ) {
                            CrownIcon(tint = FocusAmber, size = 22.dp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Best day",
                                color = FocusTextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Thursday  8h 12m",
                                color = FocusTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "View",
                        tint = FocusTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Insight Card 2: Longest Session
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .sparkleBurstClick {
                        selectedInsight = InsightDetail(
                            title = "Endurance Focus Record",
                            subtitle = "Longest Continuous Session",
                            highlightValue = "5h 47m Duration • Apr 17",
                            description = "Recorded on April 17, 2025. You entered deep flow while tackling critical project tasks without breaking concentration.",
                            tips = listOf(
                                "Ensure proper hydration during marathon focus sessions.",
                                "Use the 20-20-20 rule to rest your eyes periodically.",
                                "Follow up marathon blocks with light walking or stretching."
                            ),
                            badgeIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF252646)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Clock",
                                        tint = FocusPurpleLight,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF252646)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Clock",
                                tint = FocusPurpleLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Longest session",
                                color = FocusTextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "5h 47m  (Apr 17, 2025)",
                                color = FocusTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "View",
                        tint = FocusTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Insight Card 3: Most Productive Time
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .sparkleBurstClick {
                        selectedInsight = InsightDetail(
                            title = "Circadian Flow Window",
                            subtitle = "Optimal Productivity Hours",
                            highlightValue = "9:00 AM – 12:00 PM Peak",
                            description = "Your internal clock consistently peaks during this 3-hour window. Over 68% of your total daily output is generated before noon.",
                            tips = listOf(
                                "Protect this morning window: decline non-essential meetings.",
                                "Schedule your most challenging problem-solving work here.",
                                "Keep notifications silenced or phone in another room."
                            ),
                            badgeIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF382348)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Lightning",
                                        tint = Color(0xFFD67EFF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF382348)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Lightning",
                                tint = Color(0xFFD67EFF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Most productive time",
                                color = FocusTextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "9 AM – 12 PM",
                                color = FocusTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "View",
                        tint = FocusTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Detail Dialog
        if (selectedInsight != null) {
            val item = selectedInsight!!
            AlertDialog(
                onDismissRequest = { selectedInsight = null },
                containerColor = FocusCardBg,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item.badgeIcon()
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.title,
                                color = FocusTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = item.subtitle,
                                color = FocusTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Highlight Pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(FocusPurpleLight.copy(alpha = 0.15f))
                                .border(1.dp, FocusPurpleLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(vertical = 10.dp, horizontal = 14.dp)
                        ) {
                            Text(
                                text = item.highlightValue,
                                color = FocusMint,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = item.description,
                            color = FocusTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Productivity Tips ✨",
                            color = FocusTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        item.tips.forEach { tip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "• ",
                                    color = FocusPurpleLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tip,
                                    color = FocusTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    AnimePillButton(
                        text = "Understood ✨",
                        textColor = DarkButtonText,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { selectedInsight = null }
                    )
                }
            )
        }
    }
}
