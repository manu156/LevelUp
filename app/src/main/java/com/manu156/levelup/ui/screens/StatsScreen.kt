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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
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
import com.manu156.levelup.data.model.WeeklyGoalProgress
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.CatFaceIcon
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.RoundedCapsuleBarChart
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

enum class StatsTab {
    WEEK, MONTH, YEAR
}

@Composable
fun StatsScreen(
    weekProgress: WeeklyGoalProgress,
    monthChartData: List<Pair<String, Float>>,
    yearChartData: List<Pair<String, Float>>,
    onDailyBreakdownClick: () -> Unit,
    onInsightsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(StatsTab.WEEK) }

    fun formatMins(mins: Long): String {
        val hrs = mins / 60
        val m = mins % 60
        return if (hrs > 0) "${hrs}h ${m}m" else "${m}m"
    }

    val weekTotalMins = weekProgress.days.sumOf { (it.hours * 60).toLong() }
    val weekDisplay = formatMins(weekTotalMins)
    val monthTotalMins = monthChartData.sumOf { (_, hrs) -> (hrs * 60).toLong() }
    val monthDisplay = formatMins(monthTotalMins)
    val yearTotalMins = yearChartData.sumOf { (_, hrs) -> (hrs * 60).toLong() }
    val yearDisplay = formatMins(yearTotalMins)

    val displayTotalTime: String
    val chartDays: List<Pair<String, Float>>
    val subtitleText: String
    val hasData: Boolean

    when (selectedTab) {
        StatsTab.WEEK -> {
            hasData = weekTotalMins > 0
            displayTotalTime = weekDisplay
            chartDays = weekProgress.days.map { it.dayName to it.hours }
            subtitleText = if (hasData) "${formatMins(weekTotalMins)} this week • ${weekProgress.completionPercent}% of daily goal" else "No work recorded yet"
        }
        StatsTab.MONTH -> {
            hasData = monthTotalMins > 0
            displayTotalTime = if (hasData) monthDisplay else "0m"
            chartDays = monthChartData
            subtitleText = if (hasData) "${monthDisplay} this month" else "No work recorded yet"
        }
        StatsTab.YEAR -> {
            hasData = yearTotalMins > 0
            displayTotalTime = if (hasData) yearDisplay else "0m"
            chartDays = yearChartData
            subtitleText = if (hasData) "${yearDisplay} this year" else "No work recorded yet"
        }
    }

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

            // Header: Stats ✨
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Stats",
                    color = FocusTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "✨", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Segmented Tab Switcher: Week / Month / Year
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF14182E))
                    .border(1.dp, FocusCardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatsTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) FocusPurple else Color.Transparent)
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (isSelected) DarkButtonText else FocusTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Total Work Hours Card with Capsule Bar Chart
            AnimeGlowCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Total work hours",
                        color = FocusTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayTotalTime,
                        color = FocusTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasData) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = "Increase",
                                tint = FocusMint,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = subtitleText,
                                color = FocusMint,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = subtitleText,
                                color = FocusTextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rounded Capsule Bar Chart
                    RoundedCapsuleBarChart(
                        days = chartDays,
                        height = 170.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action banner to open Daily Breakdown
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDailyBreakdownClick() }
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF262C52)),
                            contentAlignment = Alignment.Center
                        ) {
                            CatFaceIcon(tint = FocusPurple, size = 20.dp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Daily Breakdown",
                                color = FocusTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "View categories, donut chart & stats",
                                color = FocusTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Open",
                        tint = FocusTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action banner to open Insights
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInsightsClick() }
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF32284E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Insights & Trends",
                                color = FocusTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Best day, longest session & records",
                                color = FocusTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Open",
                        tint = FocusTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

        }
    }
}
