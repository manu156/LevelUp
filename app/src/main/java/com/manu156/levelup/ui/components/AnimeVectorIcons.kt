package com.manu156.levelup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.manu156.levelup.R

@Composable
fun LevelUpLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(modifier = modifier.size(size)) {
        Image(
            painter = painterResource(R.drawable.levelup_logo),
            contentDescription = "LevelUp Logo",
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun AnimeHouseIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Chimney on top right
        val chimneyPath = Path().apply {
            moveTo(w * 0.68f, h * 0.32f)
            lineTo(w * 0.68f, h * 0.16f)
            lineTo(w * 0.78f, h * 0.16f)
            lineTo(w * 0.78f, h * 0.42f)
        }
        drawPath(chimneyPath, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))

        // Gabled Roof
        val roofPath = Path().apply {
            moveTo(w * 0.14f, h * 0.46f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.86f, h * 0.46f)
        }
        drawPath(roofPath, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))

        // Walls / House Body
        val houseBody = Path().apply {
            moveTo(w * 0.22f, h * 0.46f)
            lineTo(w * 0.22f, h * 0.86f)
            lineTo(w * 0.78f, h * 0.86f)
            lineTo(w * 0.78f, h * 0.46f)
        }
        drawPath(houseBody, color = tint, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))

        // Doorway: Cute cat silhouette inside door!
        val doorCatPath = Path().apply {
            moveTo(w * 0.38f, h * 0.86f)
            lineTo(w * 0.38f, h * 0.62f)
            // Left ear
            lineTo(w * 0.42f, h * 0.55f)
            lineTo(w * 0.46f, h * 0.60f)
            // Head curve
            lineTo(w * 0.54f, h * 0.60f)
            // Right ear
            lineTo(w * 0.58f, h * 0.55f)
            lineTo(w * 0.62f, h * 0.62f)
            lineTo(w * 0.62f, h * 0.86f)
            close()
        }
        drawPath(doorCatPath, color = tint.copy(alpha = 0.85f), style = Fill)
    }
}

@Composable
fun CatFaceIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val headPath = Path().apply {
            moveTo(w * 0.22f, h * 0.40f)
            lineTo(w * 0.15f, h * 0.12f)
            lineTo(w * 0.38f, h * 0.28f)
            quadraticTo(w * 0.5f, h * 0.32f, w * 0.62f, h * 0.28f)
            lineTo(w * 0.85f, h * 0.12f)
            lineTo(w * 0.78f, h * 0.40f)
            quadraticTo(w * 0.90f, h * 0.65f, w * 0.75f, h * 0.85f)
            quadraticTo(w * 0.5f, h * 0.92f, w * 0.25f, h * 0.85f)
            quadraticTo(w * 0.10f, h * 0.65f, w * 0.22f, h * 0.40f)
            close()
        }
        drawPath(headPath, color = tint, style = Fill)

        val eyeColor = Color(0xFF0C0F1E)
        drawCircle(color = eyeColor, radius = w * 0.055f, center = Offset(w * 0.38f, h * 0.58f))
        drawCircle(color = eyeColor, radius = w * 0.055f, center = Offset(w * 0.62f, h * 0.58f))

        val nosePath = Path().apply {
            moveTo(w * 0.47f, h * 0.67f)
            lineTo(w * 0.53f, h * 0.67f)
            lineTo(w * 0.50f, h * 0.72f)
            close()
        }
        drawPath(nosePath, color = eyeColor, style = Fill)
    }
}

@Composable
fun BullseyeTargetIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFC163),
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w * 0.46f, h * 0.54f)

        drawCircle(
            color = tint,
            radius = w * 0.38f,
            center = center,
            style = Stroke(width = w * 0.09f)
        )
        drawCircle(
            color = tint,
            radius = w * 0.20f,
            center = center,
            style = Stroke(width = w * 0.08f)
        )
        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = center
        )

        val arrowPath = Path().apply {
            moveTo(w * 0.88f, h * 0.12f)
            lineTo(w * 0.52f, h * 0.48f)
        }
        drawPath(arrowPath, color = tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
    }
}

@Composable
fun CrownIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFFC163),
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.12f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.52f)
            lineTo(w * 0.50f, h * 0.22f)
            lineTo(w * 0.65f, h * 0.52f)
            lineTo(w * 0.88f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.75f)
            close()
        }
        drawPath(path, color = tint)

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.78f),
            size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.05f)
        )
    }
}

@Composable
fun FlameStreakIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFFFF748D),
    size: Dp = 24.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            cubicTo(w * 0.65f, h * 0.30f, w * 0.85f, h * 0.48f, w * 0.82f, h * 0.70f)
            cubicTo(w * 0.80f, h * 0.88f, w * 0.65f, h * 0.94f, w * 0.50f, h * 0.94f)
            cubicTo(w * 0.35f, h * 0.94f, w * 0.20f, h * 0.88f, w * 0.18f, h * 0.70f)
            cubicTo(w * 0.15f, h * 0.52f, w * 0.38f, h * 0.35f, w * 0.40f, h * 0.25f)
            close()
        }
        drawPath(path, color = tint)

        val innerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.45f)
            cubicTo(w * 0.60f, h * 0.55f, w * 0.68f, h * 0.68f, w * 0.65f, h * 0.80f)
            cubicTo(w * 0.60f, h * 0.88f, w * 0.40f, h * 0.88f, w * 0.35f, h * 0.80f)
            cubicTo(w * 0.32f, h * 0.68f, w * 0.44f, h * 0.55f, w * 0.50f, h * 0.45f)
            close()
        }
        drawPath(innerPath, color = Color(0xFFFFD591))
    }
}
