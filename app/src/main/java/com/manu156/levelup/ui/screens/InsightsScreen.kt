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
    sessions: List<com.manu156.levelup.data.model.WorkSession> = emptyList(),
    dayStats: com.manu156.levelup.data.model.DayStats? = null,
    hasData: Boolean = sessions.isNotEmpty(),
    onBackClick: () -> Unit = {}
) {
    var selectedInsight by remember { mutableStateOf<InsightDetail?>(null) }

    val longest = remember(sessions) { sessions.maxByOrNull { it.durationMillis } }
    val longestStr = longest?.formattedDuration() ?: "—"
    val dateFmt = remember { java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()) }
    val hourFmt = remember { java.text.SimpleDateFormat("h a", java.util.Locale.getDefault()) }
    val longestDateStr = longest?.let { dateFmt.format(java.util.Date(it.endTimeMillis)) } ?: ""
    val totalStr = dayStats?.totalHoursStr ?: run {
        val mins = sessions.sumOf { it.durationMinutes }
        "${mins / 60}h ${mins % 60}m"
    }

    // Best day across all sessions
    val bestDayData = remember(sessions) {
        val cal = java.util.Calendar.getInstance()
        val dayGroups = sessions.groupBy { session ->
            cal.timeInMillis = session.startTimeMillis
            "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
        }
        val bestEntry = dayGroups.maxByOrNull { entry -> entry.value.sumOf { it.durationMinutes } }
        if (bestEntry != null && bestEntry.value.isNotEmpty()) {
            val sample = bestEntry.value.first()
            val totalMins = bestEntry.value.sumOf { it.durationMinutes }
            val formattedDate = dateFmt.format(java.util.Date(sample.startTimeMillis))
            val timeStr = "${totalMins / 60}h ${totalMins % 60}m"
            formattedDate to timeStr
        } else {
            "Today" to totalStr
        }
    }

    // Peak focus window across all sessions
    val peakHourStr = remember(sessions) {
        if (sessions.isEmpty()) "—"
        else {
            val cal = java.util.Calendar.getInstance()
            val hourGroups = sessions.groupBy { session ->
                cal.timeInMillis = session.startTimeMillis
                cal.get(java.util.Calendar.HOUR_OF_DAY)
            }
            val bestHour = hourGroups.maxByOrNull { it.value.sumOf { s -> s.durationMinutes } }?.key
            if (bestHour != null) {
                cal.set(java.util.Calendar.HOUR_OF_DAY, bestHour)
                cal.set(java.util.Calendar.MINUTE, 0)
                hourFmt.format(cal.time)
            } else "—"
        }
    }
    val sessionCount = dayStats?.sessionCount ?: sessions.size
    val totalLifetimeSessions = sessions.size

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

            if (!hasData) {
                AnimeGlowCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🐾", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No insights yet",
                            color = FocusTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete some focus sessions to see your productivity trends and personalized tips! ✨",
                            color = FocusTextSecondary,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                // Hero Banner Card: live summary + Anime girl sticker
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sparkleBurstClick {
                            selectedInsight = InsightDetail(
                                title = "Focus Summary",
                                subtitle = "Work Activity",
                                highlightValue = "$sessionCount session${if (sessionCount == 1) "" else "s"} today • $totalLifetimeSessions all-time",
                                description = "You have recorded $sessionCount session${if (sessionCount == 1) "" else "s"} totalling $totalStr today, and $totalLifetimeSessions focus session${if (totalLifetimeSessions == 1) "" else "s"} all-time across the app.",
                                tips = listOf(
                                    "Keep daily work blocks between 45–90 minutes with 10-minute breaks.",
                                    "Check in every session so totals stay accurate.",
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
                                    text = if (sessionCount > 0) "Nice progress!" else "Let's begin!",
                                    color = FocusTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (sessionCount > 0) "You've logged $sessionCount session${if (sessionCount == 1) "" else "s"} totalling $totalStr today. Keep it up!" else "No sessions yet today ($totalLifetimeSessions all-time).",
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

                // Insight Card 1: Best Day (historical across all sessions)
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sparkleBurstClick {
                            selectedInsight = InsightDetail(
                                title = "Best Day Record",
                                subtitle = "Top Performance",
                                highlightValue = "${bestDayData.first} • ${bestDayData.second}",
                                description = "Your most productive day recorded was ${bestDayData.first} with ${bestDayData.second} of focus work.",
                                tips = listOf(
                                    "Check in consistently so every day counts.",
                                    "Protect one distraction-free block each day.",
                                    "Schedule demanding tasks during your proven productive hours."
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
                                    text = "${bestDayData.first}  ${bestDayData.second}",
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

                // Insight Card 2: Longest Session (live)
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sparkleBurstClick {
                            selectedInsight = InsightDetail(
                                title = "Endurance Focus Record",
                                subtitle = "Longest Continuous Session",
                                highlightValue = "$longestStr${if (longestDateStr.isNotEmpty()) " • $longestDateStr" else ""}",
                                description = longest?.let { "Your longest recorded session is \"${it.title}\" at $longestStr. Great deep work!" } ?: "No sessions recorded yet.",
                                tips = listOf(
                                    "Ensure proper hydration during long focus sessions.",
                                    "Use the 20-20-20 rule to rest your eyes periodically.",
                                    "Follow up long blocks with light walking or stretching."
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
                                    text = if (longestDateStr.isNotEmpty()) "$longestStr  ($longestDateStr)" else longestStr,
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

                // Insight Card 3: Most Productive Time (live — based on longest session start)
                AnimeGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sparkleBurstClick {
                            selectedInsight = InsightDetail(
                                title = "Flow Window",
                                subtitle = "When You Focus Best",
                                highlightValue = if (longest != null) "Around $peakHourStr" else "No data yet",
                                description = longest?.let { "Your longest session started around $peakHourStr (\"${it.title}\"). More sessions will reveal your true peak window." } ?: "Log a few sessions to discover when you focus best.",
                                tips = listOf(
                                    "Protect your best window: decline non-essential meetings.",
                                    "Schedule your hardest work in that window.",
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
                                    text = peakHourStr,
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
