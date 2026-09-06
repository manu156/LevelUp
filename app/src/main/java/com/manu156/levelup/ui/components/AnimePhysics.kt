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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class AnimeFeedbackStyle {
    DEFAULT,
    SLIME,
    KATANA,
    SAKURA_STAMP,
    NEKO_TWITCH,
    SPARKLE_BURST,
    PANIC_SHAKE
}

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
    var isBusy by remember { mutableStateOf(false) }

    this
        .scale(scale.value)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                scale.animateTo(0.92f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                delay(70)
                onClick()
                scale.animateTo(1.04f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                scale.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessLow))
                isBusy = false
            }
        }
}

// 5. Slime / Mochi Squash & Stretch (Jelly Physics + Sheen)
fun Modifier.slimeBounceClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val sheenProgress = remember { Animatable(0f) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .graphicsLayer {
            this.scaleX = scaleX.value
            this.scaleY = scaleY.value
        }
        .drawWithContent {
            drawContent()
            val sp = sheenProgress.value
            if (sp > 0f && sp < 1f) {
                val sheenX = size.width * (sp * 1.5f - 0.25f)
                val sheenWidth = size.width * 0.35f
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        startX = sheenX - sheenWidth,
                        endX = sheenX + sheenWidth
                    ),
                    size = size
                )
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                launch {
                    scaleX.animateTo(1.14f, tween(80, easing = FastOutSlowInEasing))
                    scaleX.animateTo(0.92f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
                    scaleX.animateTo(1.04f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium))
                    scaleX.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                launch {
                    scaleY.animateTo(0.80f, tween(80, easing = FastOutSlowInEasing))
                    scaleY.animateTo(1.14f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
                    scaleY.animateTo(0.96f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium))
                    scaleY.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                launch {
                    sheenProgress.snapTo(0f)
                    sheenProgress.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
                    sheenProgress.snapTo(0f)
                }
                // Allow user to see the squash before executing action
                delay(120)
                onClick()
                delay(200)
                isBusy = false
            }
        }
}

// 6. Katana Slash (Iaijutsu Cut + Electric Blade Flash)
fun Modifier.katanaSlashClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val slashProgress = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .scale(scale.value)
        .drawWithContent {
            drawContent()
            val sp = slashProgress.value
            if (sp > 0f && sp <= 1f) {
                val startX = -size.width * 0.1f
                val startY = -size.height * 0.2f
                val endX = size.width * 1.1f
                val endY = size.height * 1.2f

                val currentHeadX = startX + (endX - startX) * sp
                val currentHeadY = startY + (endY - startY) * sp

                val tailRatio = (sp - 0.35f).coerceAtLeast(0f)
                val currentTailX = startX + (endX - startX) * tailRatio
                val currentTailY = startY + (endY - startY) * tailRatio

                // Outer neon blade glow
                drawLine(
                    color = Color(0xFF6CFFCE).copy(alpha = 0.75f),
                    start = Offset(currentTailX, currentTailY),
                    end = Offset(currentHeadX, currentHeadY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Inner razor white core
                drawLine(
                    color = Color.White,
                    start = Offset(currentTailX, currentTailY),
                    end = Offset(currentHeadX, currentHeadY),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Sparks at the cutting head
                val sparkColor = Color(0xFFFFEAA7)
                drawCircle(
                    color = sparkColor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(currentHeadX, currentHeadY)
                )
            }

            if (flashAlpha.value > 0f) {
                drawRect(
                    color = Color.White.copy(alpha = flashAlpha.value),
                    size = size
                )
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                scale.animateTo(0.94f, tween(60, easing = FastOutSlowInEasing))
                launch {
                    flashAlpha.snapTo(0.35f)
                    flashAlpha.animateTo(0f, tween(180))
                }
                launch {
                    slashProgress.snapTo(0f)
                    slashProgress.animateTo(1f, tween(180, easing = LinearEasing))
                    slashProgress.snapTo(0f)
                }
                launch {
                    scale.animateTo(1.03f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                // Climax delay: Let the katana blade slice and flash impact before triggering action!
                delay(170)
                onClick()
                delay(150)
                isBusy = false
            }
        }
}

// 7. Neko Ear Twitch & Pounce (Cat Mascot Wobble)
fun Modifier.nekoTwitchClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val earPop = remember { Animatable(0f) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .scale(scale.value)
        .rotate(rotation.value)
        .drawWithContent {
            drawContent()
            val ep = earPop.value
            if (ep > 0f) {
                val earHeight = 14.dp.toPx() * ep
                val earWidth = 12.dp.toPx() * ep
                val leftCenter = size.width * 0.25f
                val rightCenter = size.width * 0.75f

                // Left ear
                val leftPath = Path().apply {
                    moveTo(leftCenter - earWidth / 2, 0f)
                    lineTo(leftCenter - earWidth * 0.15f, -earHeight)
                    lineTo(leftCenter + earWidth / 2, 0f)
                    close()
                }
                drawPath(leftPath, color = Color(0xFF7A5AF8))
                val leftInner = Path().apply {
                    moveTo(leftCenter - earWidth * 0.25f, 0f)
                    lineTo(leftCenter - earWidth * 0.15f, -earHeight * 0.65f)
                    lineTo(leftCenter + earWidth * 0.25f, 0f)
                    close()
                }
                drawPath(leftInner, color = Color(0xFFFFB2C9))

                // Right ear
                val rightPath = Path().apply {
                    moveTo(rightCenter - earWidth / 2, 0f)
                    lineTo(rightCenter + earWidth * 0.15f, -earHeight)
                    lineTo(rightCenter + earWidth / 2, 0f)
                    close()
                }
                drawPath(rightPath, color = Color(0xFF7A5AF8))
                val rightInner = Path().apply {
                    moveTo(rightCenter - earWidth * 0.25f, 0f)
                    lineTo(rightCenter + earWidth * 0.15f, -earHeight * 0.65f)
                    lineTo(rightCenter + earWidth * 0.25f, 0f)
                    close()
                }
                drawPath(rightInner, color = Color(0xFFFFB2C9))
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                launch {
                    scale.animateTo(0.90f, tween(60))
                    scale.animateTo(1.08f, spring(dampingRatio = 0.5f))
                    scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                launch {
                    rotation.animateTo(-12f, tween(70))
                    rotation.animateTo(14f, tween(80))
                    rotation.animateTo(-6f, tween(70))
                    rotation.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                }
                launch {
                    earPop.snapTo(0f)
                    earPop.animateTo(1f, tween(90, easing = FastOutSlowInEasing))
                    delay(120)
                    earPop.animateTo(0f, tween(150, easing = FastOutSlowInEasing))
                }
                // Allow ears and tilt to pop before action/navigation triggers
                delay(150)
                onClick()
                delay(150)
                isBusy = false
            }
        }
}

// 8. Sakura Quest Stamp (Hanko Seal + Blossom Shockwave)
fun Modifier.sakuraStampClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val stampScale = remember { Animatable(0f) }
    val stampAlpha = remember { Animatable(0f) }
    val ringExpansion = remember { Animatable(0f) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .scale(scale.value)
        .drawWithContent {
            drawContent()
            val sa = stampAlpha.value
            if (sa > 0f) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val ringRadius = (size.minDimension * 0.8f + ringExpansion.value * size.minDimension * 0.6f)

                // Expanding sakura shockwave ring
                drawCircle(
                    color = Color(0xFFFF85A1).copy(alpha = sa * 0.65f),
                    radius = ringRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3.dp.toPx() * (1f - ringExpansion.value).coerceAtLeast(0.1f))
                )

                // Circular Hanko stamp
                val stampRadius = (size.minDimension * 0.38f) * stampScale.value
                drawCircle(
                    color = Color(0xFFFF5277).copy(alpha = sa),
                    radius = stampRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFFF85A1).copy(alpha = sa * 0.8f),
                    radius = stampRadius * 0.82f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.dp.toPx())
                )
                for (i in 0 until 5) {
                    val angle = (i * 72f) * (PI.toFloat() / 180f)
                    val petalDist = stampRadius * 0.45f
                    val px = cx + cos(angle) * petalDist
                    val py = cy + sin(angle) * petalDist
                    drawCircle(
                        color = Color(0xFFFFD1DC).copy(alpha = sa),
                        radius = 2.5.dp.toPx() * stampScale.value,
                        center = Offset(px, py)
                    )
                }
                drawCircle(
                    color = Color(0xFFFFEAA7).copy(alpha = sa),
                    radius = 3.dp.toPx() * stampScale.value,
                    center = Offset(cx, cy)
                )
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                scale.animateTo(0.86f, tween(70, easing = FastOutSlowInEasing))
                launch {
                    stampAlpha.snapTo(1f)
                    stampScale.snapTo(1.6f)
                    stampScale.animateTo(1.0f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessHigh))
                    ringExpansion.snapTo(0f)
                    ringExpansion.animateTo(1f, tween(260, easing = FastOutSlowInEasing))
                    delay(100)
                    stampAlpha.animateTo(0f, tween(180))
                }
                launch {
                    scale.animateTo(1.06f, spring(dampingRatio = 0.5f))
                    scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                // Stamp slams down firmly, then triggers navigation!
                delay(200)
                onClick()
                delay(150)
                isBusy = false
            }
        }
}

// 9. Mahou Sparkle Burst (Stardust Diamond Explosion)
fun Modifier.sparkleBurstClick(
    enabled: Boolean = true,
    sparkleCount: Int = 8,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val burstProgress = remember { Animatable(0f) }
    var touchOffset by remember { mutableStateOf(Offset.Zero) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .drawWithContent {
            drawContent()
            val bp = burstProgress.value
            if (bp > 0f && bp < 1f) {
                val origin = if (touchOffset == Offset.Zero) Offset(size.width / 2, size.height / 2) else touchOffset
                val sparkColors = listOf(Color(0xFFFFEAA7), Color(0xFF6CFFCE), Color(0xFFFFB7C5), Color(0xFFB388FF))
                val radiusMax = 42.dp.toPx()
                val currentRadius = radiusMax * bp
                val alpha = (1f - bp).coerceIn(0f, 1f)

                for (i in 0 until sparkleCount) {
                    val angle = (i * (360f / sparkleCount) + (i * 17f)) * (PI.toFloat() / 180f)
                    val sx = origin.x + cos(angle) * currentRadius
                    val sy = origin.y + sin(angle) * currentRadius
                    val starSize = (7.dp.toPx() * (1f - bp * 0.5f)).coerceAtLeast(2f)

                    drawTwinklingStar(
                        center = Offset(sx, sy),
                        size = starSize,
                        color = sparkColors[i % sparkColors.size].copy(alpha = alpha),
                        rotation = bp * 180f + i * 45f
                    )
                }
            }
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onTap = { offset ->
                    if (isBusy) return@detectTapGestures
                    isBusy = true
                    touchOffset = offset
                    coroutineScope.launch {
                        burstProgress.snapTo(0f)
                        burstProgress.animateTo(1f, tween(360, easing = FastOutSlowInEasing))
                        burstProgress.snapTo(0f)
                    }
                    coroutineScope.launch {
                        delay(120) // Allow sparkles to burst before action
                        onClick()
                        delay(200)
                        isBusy = false
                    }
                }
            )
        }
}

// 10. Comic Sweat-Drop & Panic Jitter (Anxious Comedy Shake)
fun Modifier.animePanicShakeClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    val sweatDropAlpha = remember { Animatable(0f) }
    val sweatDropY = remember { Animatable(0f) }
    var isBusy by remember { mutableStateOf(false) }

    this
        .graphicsLayer {
            translationX = shakeOffset.value
        }
        .drawWithContent {
            drawContent()
            val sda = sweatDropAlpha.value
            if (sda > 0f) {
                val dropX = size.width - 6.dp.toPx()
                val dropY = -12.dp.toPx() + sweatDropY.value
                val dropWidth = 8.dp.toPx()
                val dropHeight = 14.dp.toPx()

                val dropPath = Path().apply {
                    moveTo(dropX, dropY - dropHeight / 2)
                    cubicTo(
                        dropX + dropWidth / 2, dropY,
                        dropX + dropWidth / 2, dropY + dropHeight / 2,
                        dropX, dropY + dropHeight / 2
                    )
                    cubicTo(
                        dropX - dropWidth / 2, dropY + dropHeight / 2,
                        dropX - dropWidth / 2, dropY,
                        dropX, dropY - dropHeight / 2
                    )
                    close()
                }
                drawPath(dropPath, color = Color(0xFF64B5F6).copy(alpha = sda))
                drawCircle(
                    color = Color.White.copy(alpha = sda * 0.9f),
                    radius = 1.8.dp.toPx(),
                    center = Offset(dropX - 1.5.dp.toPx(), dropY + 1.dp.toPx())
                )
            }
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled
        ) {
            if (isBusy) return@clickable
            isBusy = true
            coroutineScope.launch {
                launch {
                    sweatDropAlpha.snapTo(1f)
                    sweatDropY.snapTo(0f)
                    sweatDropY.animateTo(10.dp.value, tween(260, easing = FastOutSlowInEasing))
                    sweatDropAlpha.animateTo(0f, tween(140))
                }
                launch {
                    shakeOffset.animateTo(-8f, tween(25))
                    shakeOffset.animateTo(8f, tween(30))
                    shakeOffset.animateTo(-6f, tween(30))
                    shakeOffset.animateTo(6f, tween(35))
                    shakeOffset.animateTo(-3f, tween(35))
                    shakeOffset.animateTo(3f, tween(40))
                    shakeOffset.animateTo(0f, tween(40))
                }
                // Panic jitter rattles before triggering modal/action
                delay(180)
                onClick()
                delay(150)
                isBusy = false
            }
        }
}

// Unified Anime Feedback Modifier
fun Modifier.animeButtonFeedback(
    style: AnimeFeedbackStyle = AnimeFeedbackStyle.SLIME,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = when (style) {
    AnimeFeedbackStyle.KATANA -> this.katanaSlashClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.SLIME -> this.slimeBounceClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.NEKO_TWITCH -> this.nekoTwitchClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.SAKURA_STAMP -> this.sakuraStampClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.SPARKLE_BURST -> this.sparkleBurstClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.PANIC_SHAKE -> this.animePanicShakeClick(enabled = enabled, onClick = onClick)
    AnimeFeedbackStyle.DEFAULT -> this.animeSpringClick(enabled = enabled, onClick = onClick)
}
