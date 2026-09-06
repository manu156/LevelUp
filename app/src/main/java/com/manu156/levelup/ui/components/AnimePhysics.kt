package com.manu156.levelup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// 1. Sakura Petal and Stardust Particle System
data class AnimeParticle(
    val initialXRatio: Float,
    val initialYRatio: Float,
    val size: Float,
    val speed: Float,
    val swayAmplitude: Float,
    val swayPeriodMs: Float,
    val rotationSpeed: Float,
    val initialRotation: Float,
    val phase: Float,
    val isStar: Boolean = false,
    val color: Color = Color(0xFFFFB7C5)
)

@Composable
fun SakuraFloatingOverlay(
    modifier: Modifier = Modifier,
    particleCount: Int = 18,
    tint: Color = Color(0xFFFFB2C9)
) {
    val particles = remember {
        List(particleCount) { index ->
            val isStar = index % 4 == 0
            AnimeParticle(
                initialXRatio = Random.nextFloat(),
                initialYRatio = Random.nextFloat(),
                size = if (isStar) Random.nextFloat() * 8f + 6f else Random.nextFloat() * 14f + 10f,
                speed = Random.nextFloat() * 25f + 15f,
                swayAmplitude = Random.nextFloat() * 24f + 12f,
                swayPeriodMs = Random.nextFloat() * 3000f + 2500f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 70f,
                initialRotation = Random.nextFloat() * 360f,
                phase = Random.nextFloat() * (2f * PI.toFloat()),
                isStar = isStar,
                color = if (isStar) Color(0xFFFFEAA7).copy(alpha = 0.85f) else tint.copy(alpha = 0.65f)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "sakura_sway")
    val timeMillis by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60_000f,
        animationSpec = infiniteRepeatable(
            animation = tween(60_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_ticker"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w == 0f || h == 0f) return@Canvas

        particles.forEach { p ->
            val elapsedSeconds = timeMillis / 1000f
            // Harmonic lateral sway physics: x = x0 + A * sin(omega * t + phi)
            val swayOffset = p.swayAmplitude * sin((2f * PI.toFloat() * (timeMillis % p.swayPeriodMs) / p.swayPeriodMs) + p.phase)
            val x = (p.initialXRatio * w + swayOffset).mod(w)
            // Downward drift
            val y = (p.initialYRatio * h + elapsedSeconds * p.speed * 8f).mod(h)
            // Flutter rotation physics
            val currentRotation = p.initialRotation + elapsedSeconds * p.rotationSpeed

            if (p.isStar) {
                drawTwinklingStar(
                    center = Offset(x, y),
                    size = p.size,
                    color = p.color,
                    rotation = currentRotation
                )
            } else {
                drawSakuraPetal(
                    center = Offset(x, y),
                    size = p.size,
                    color = p.color,
                    rotation = currentRotation
                )
            }
        }
    }
}

private fun DrawScope.drawSakuraPetal(center: Offset, size: Float, color: Color, rotation: Float) {
    rotate(rotation, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - size)
            cubicTo(
                center.x + size * 0.7f, center.y - size * 0.8f,
                center.x + size * 0.8f, center.y + size * 0.4f,
                center.x, center.y + size
            )
            cubicTo(
                center.x - size * 0.8f, center.y + size * 0.4f,
                center.x - size * 0.7f, center.y - size * 0.8f,
                center.x, center.y - size
            )
            close()
        }
        drawPath(path, color)
    }
}

private fun DrawScope.drawTwinklingStar(center: Offset, size: Float, color: Color, rotation: Float) {
    rotate(rotation, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - size)
            cubicTo(center.x + size * 0.2f, center.y - size * 0.2f, center.x + size * 0.2f, center.y - size * 0.2f, center.x + size, center.y)
            cubicTo(center.x + size * 0.2f, center.y + size * 0.2f, center.x + size * 0.2f, center.y + size * 0.2f, center.x, center.y + size)
            cubicTo(center.x - size * 0.2f, center.y + size * 0.2f, center.x - size * 0.2f, center.y + size * 0.2f, center.x - size, center.y)
            cubicTo(center.x - size * 0.2f, center.y - size * 0.2f, center.x - size * 0.2f, center.y - size * 0.2f, center.x, center.y - size)
            close()
        }
        drawPath(path, color)
    }
}

// 2. Anime Secondary Physics: Breathing rhythm for mascots and sleeping cat
@Composable
fun rememberBreathingScale(periodMs: Int = 3600, minScale: Float = 0.98f, maxScale: Float = 1.035f): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(periodMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )
    return scale
}

// 3. Anime Secondary Physics: Gentle rhythmic idle sway (for cat ears, mascots, badges)
@Composable
fun rememberIdleSway(angleDegrees: Float = 5f, periodMs: Int = 2400): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "idle_sway")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -angleDegrees,
        targetValue = angleDegrees,
        animationSpec = infiniteRepeatable(
            animation = tween(periodMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway_rotation"
    )
    return rotation
}

// 4. Anime Tactile Press with Spring Compression & Sparkle Burst
fun Modifier.animeSpringClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    this
        .scale(scale.value)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            coroutineScope.launch {
                scale.animateTo(0.92f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                scale.animateTo(1.04f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                scale.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessLow))
            }
            onClick()
        }
}
