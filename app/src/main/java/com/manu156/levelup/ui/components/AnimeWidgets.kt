package com.manu156.levelup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusNavBg
import com.manu156.levelup.ui.theme.FocusNavBorder
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

val DarkButtonText = Color(0xFF0C0F1E)

@Composable
fun AnimeGlowCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    backgroundColor: Color = FocusCardBg,
    borderColor: Color = FocusCardBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor
    ) {
        content()
    }
}

@Composable
fun AnimePillButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    gradientColors: List<Color> = listOf(FocusPurple, FocusPurpleLight),
    textColor: Color = DarkButtonText, // Darker color as in user mockup
    height: Dp = 56.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(height / 2),
                ambientColor = gradientColors.first().copy(alpha = 0.4f),
                spotColor = gradientColors.first().copy(alpha = 0.6f)
            )
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = RoundedCornerShape(height / 2)
            )
            .animeSpringClick(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF2B2F4C)
) {
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.8.dp,
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = FocusTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatusWorkingBadge(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "working_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = FocusMint.copy(alpha = pulseAlpha),
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Working",
            color = FocusMint,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

enum class NavTab {
    HOME,
    STATS,
    GOALS,
    PROFILE
}

@Composable
fun AnimeBottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, FocusNavBorder),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = FocusNavBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                isSelected = selectedTab == NavTab.HOME,
                label = "Home",
                onClick = { onTabSelected(NavTab.HOME) },
                icon = { isSelected ->
                    // Anime House icon as in mockup!
                    AnimeHouseIcon(
                        tint = if (isSelected) FocusPurple else FocusTextMuted,
                        size = 24.dp
                    )
                }
            )

            NavItem(
                isSelected = selectedTab == NavTab.STATS,
                label = "Stats",
                onClick = { onTabSelected(NavTab.STATS) },
                icon = { isSelected ->
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Stats",
                        tint = if (isSelected) FocusPurple else FocusTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            NavItem(
                isSelected = selectedTab == NavTab.GOALS,
                label = "Goals",
                onClick = { onTabSelected(NavTab.GOALS) },
                icon = { isSelected ->
                    BullseyeTargetIcon(
                        tint = if (isSelected) FocusPurple else FocusTextMuted,
                        size = 24.dp
                    )
                }
            )

            NavItem(
                isSelected = selectedTab == NavTab.PROFILE,
                label = "Profile",
                onClick = { onTabSelected(NavTab.PROFILE) },
                icon = { isSelected ->
                    CatFaceIcon(
                        tint = if (isSelected) FocusPurple else FocusTextMuted,
                        size = 24.dp
                    )
                }
            )
        }
    }
}

@Composable
private fun NavItem(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable (Boolean) -> Unit
) {
    val scale = if (isSelected) 1.08f else 1.0f
    val textColor by animateColorAsState(
        targetValue = if (isSelected) FocusPurple else FocusTextMuted,
        label = "nav_text_color"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .animeSpringClick(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon(isSelected)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
