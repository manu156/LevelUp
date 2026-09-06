package com.manu156.levelup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.R
import com.manu156.levelup.ui.theme.FocusAmber
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusCoral
import com.manu156.levelup.ui.theme.FocusCyan
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

// 1. Donut Chart with Center Cat Face
@Composable
fun DonutChartWithCat(
    totalTimeStr: String = "7h 28m",
    deepWorkPercent: Float = 0.70f,
    meetingsPercent: Float = 0.22f,
    breaksPercent: Float = 0.08f,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 26.dp.toPx()
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokeWidth) / 2
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val arcSize = Size(radius * 2, radius * 2)
            val topLeft = Offset(center.x - radius, center.y - radius)

            // Background subtle track
            drawArc(
                color = FocusCardBorder.copy(alpha = 0.5f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            val gap = 4f // degrees between segments
            val totalSweep = 360f - (3 * gap)

            val deepSweep = totalSweep * deepWorkPercent * animProgress.value
            val meetSweep = totalSweep * meetingsPercent * animProgress.value
            val breakSweep = totalSweep * breaksPercent * animProgress.value

            var currentAngle = -90f

            // 1. Deep Work segment (Purple)
            if (deepSweep > 0) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(FocusPurple, FocusPurpleLight)),
                    startAngle = currentAngle,
                    sweepAngle = deepSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                currentAngle += deepSweep + gap
            }

            // 2. Meetings segment (Coral)
            if (meetSweep > 0) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(FocusCoral, Color(0xFFFF8E9F))),
                    startAngle = currentAngle,
                    sweepAngle = meetSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                currentAngle += meetSweep + gap
            }

            // 3. Breaks segment (Amber)
            if (breakSweep > 0) {
                drawArc(
                    color = FocusAmber,
                    startAngle = currentAngle,
                    sweepAngle = breakSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Center Content: Time + Total work + Cute Cat Icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = totalTimeStr,
                color = FocusTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total work",
                color = FocusTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            CatFaceIcon(
                tint = FocusPurpleLight,
                size = 22.dp
            )
        }
    }
}

// 2. Rounded Capsule Bar Chart (Week Progress)
@Composable
fun RoundedCapsuleBarChart(
    days: List<Pair<String, Float>> = listOf(
        "Mon" to 4.2f,
        "Tue" to 6.0f,
        "Wed" to 7.2f,
        "Thu" to 4.8f,
        "Fri" to 4.4f,
        "Sat" to 8.2f,
        "Sun" to 4.8f
    ),
    maxHours: Float = 10f,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // Horizontal gridlines: 8h, 4h, 0h
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartBottom = size.height - 30.dp.toPx()
            val chartTop = 16.dp.toPx()
            val heightRange = chartBottom - chartTop

            val y0 = chartBottom
            val y4 = chartBottom - (4f / maxHours) * heightRange
            val y8 = chartBottom - (8f / maxHours) * heightRange

            val gridColor = FocusCardBorder.copy(alpha = 0.6f)
            val stroke = Stroke(width = 1.dp.toPx())

            drawLine(gridColor, Offset(35.dp.toPx(), y0), Offset(size.width, y0), strokeWidth = 1.dp.toPx())
            drawLine(gridColor, Offset(35.dp.toPx(), y4), Offset(size.width, y4), strokeWidth = 1.dp.toPx())
            drawLine(gridColor, Offset(35.dp.toPx(), y8), Offset(size.width, y8), strokeWidth = 1.dp.toPx())
        }

        // Y-axis labels
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 26.dp, top = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("8h", color = FocusTextMuted, fontSize = 11.sp)
            Text("4h", color = FocusTextMuted, fontSize = 11.sp)
            Text("0h", color = FocusTextMuted, fontSize = 11.sp)
        }

        // Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 12.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { (day, hrs) ->
                val barFraction = (hrs / maxHours).coerceIn(0.1f, 1f) * animProgress.value

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height((110 * barFraction).dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        FocusPurpleLight,
                                        FocusPurple
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = day,
                        color = FocusTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// 3. Circular Timer Gauge with Sleeping Cat Perched on Top
@Composable
fun CircularTimerGauge(
    elapsedSeconds: Long,
    progressFraction: Float = 0.65f,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val breathingScale = rememberBreathingScale(periodMs = 3800, minScale = 0.98f, maxScale = 1.035f)

    // Formatted time: HH:MM:SS
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val secs = elapsedSeconds % 60
    val timeStr = String.format("%d:%02d:%02d", hours, minutes, secs)

    val infiniteRotation = rememberInfiniteTransition(label = "gauge_glow")
    val glowAngle by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_rotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Glowing circular timer ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokeWidth - 28.dp.toPx()) / 2
            val center = Offset(this.size.width / 2, this.size.height / 2 + 10.dp.toPx())
            val arcSize = Size(radius * 2, radius * 2)
            val topLeft = Offset(center.x - radius, center.y - radius)

            // Outer subtle background arc
            drawArc(
                color = FocusCardBorder.copy(alpha = 0.6f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Glowing cyan/sky blue active arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        FocusCyan,
                        FocusPurpleLight,
                        FocusCyan
                    )
                ),
                startAngle = 135f,
                sweepAngle = (270f * progressFraction),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Sleeping Black Cat resting perched on top of the circle
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .size(92.dp)
                .scale(breathingScale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.cat_sleeping_timer),
                contentDescription = "Sleeping Cat Mascot",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
        }

        // Center Content: Timer digits + Current session label + Working badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = 20.dp)
        ) {
            Text(
                text = timeStr,
                color = FocusTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Current session",
                color = FocusTextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusWorkingBadge()
        }
    }
}
