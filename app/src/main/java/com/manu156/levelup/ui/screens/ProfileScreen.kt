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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu156.levelup.R
import com.manu156.levelup.data.model.UserProfile
import com.manu156.levelup.ui.components.AnimeGlowCard
import com.manu156.levelup.ui.components.FlameStreakIcon
import com.manu156.levelup.ui.components.SakuraFloatingOverlay
import com.manu156.levelup.ui.theme.AnimeThemeManager
import com.manu156.levelup.ui.theme.FocusAmber
import com.manu156.levelup.ui.theme.FocusBgDark
import com.manu156.levelup.ui.theme.FocusCardBg
import com.manu156.levelup.ui.theme.FocusCardBorder
import com.manu156.levelup.ui.theme.FocusCoral
import com.manu156.levelup.ui.theme.FocusMint
import com.manu156.levelup.ui.theme.FocusPurple
import com.manu156.levelup.ui.theme.FocusPurpleLight
import com.manu156.levelup.ui.theme.FocusTextMuted
import com.manu156.levelup.ui.theme.FocusTextPrimary
import com.manu156.levelup.ui.theme.FocusTextSecondary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.PhotoLibrary
import com.manu156.levelup.ui.components.AnimeUserAvatar
import com.manu156.levelup.ui.components.DarkButtonText
import com.manu156.levelup.ui.components.nekoTwitchClick
import com.manu156.levelup.ui.components.slimeBounceClick
import com.manu156.levelup.ui.components.sparkleBurstClick

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onSettingsClick: () -> Unit = {},
    onAvatarChange: (String) -> Unit = {},
    onPickGalleryImage: (Uri) -> Unit = {}
) {
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FocusBgDark)
    ) {
        SakuraFloatingOverlay(particleCount = 12)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Torii Gate Silhouette Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.profile_torii_banner),
                    contentDescription = "Torii Header",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    FocusBgDark.copy(alpha = 0.5f),
                                    FocusBgDark
                                )
                            )
                        )
                )
            }

            // Circular Avatar overlapping banner with neko twitch physics
            Box(
                modifier = Modifier
                    .offset(y = (-45).dp)
                    .size(90.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(3.dp, FocusPurpleLight, CircleShape)
                        .nekoTwitchClick { showAvatarDialog = true }
                ) {
                    AnimeUserAvatar(
                        avatarUri = userProfile.avatarUri,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(FocusPurple)
                        .border(2.dp, FocusBgDark, CircleShape)
                        .nekoTwitchClick { showAvatarDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Name & Motto
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-36).dp)
            ) {
                Text(
                    text = userProfile.name,
                    color = FocusTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userProfile.subtitle,
                    color = FocusTextSecondary,
                    fontSize = 15.sp
                )
            }

            // 3 Stat Metric Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                AnimeGlowCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FlameStreakIcon(tint = FocusCoral, size = 26.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${userProfile.dayStreak}",
                            color = FocusTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Day Streak",
                            color = FocusTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Total Work Card
                AnimeGlowCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Total Work",
                            tint = FocusPurpleLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${userProfile.totalWorkHours}h",
                            color = FocusTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Total Work",
                            color = FocusTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Daily Goal Card
                AnimeGlowCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Daily Goal",
                            tint = FocusAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${userProfile.dailyGoalHours}h",
                            color = FocusTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily Goal",
                            color = FocusTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menu Options Card (Help & Support removed as requested!)
            AnimeGlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "Target, data management & preferences",
                        onClick = onSettingsClick
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(FocusCardBorder.copy(alpha = 0.5f))
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Palette,
                        title = "Anime Theme Preset",
                        subtitle = AnimeThemeManager.currentPreset.title,
                        onClick = { showThemeDialog = true }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(FocusCardBorder.copy(alpha = 0.5f))
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "About LevelUp",
                        subtitle = "v1.0 • Anime & Material 3 Expressive",
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Theme Preset Switcher Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                containerColor = FocusCardBg,
                title = {
                    Text(
                        text = "Select Anime Theme",
                        color = FocusTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        AnimeThemeManager.presets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        AnimeThemeManager.switchTheme(preset.id)
                                        showThemeDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = AnimeThemeManager.currentPreset.id == preset.id,
                                    onClick = {
                                        AnimeThemeManager.switchTheme(preset.id)
                                        showThemeDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = FocusPurple,
                                        unselectedColor = FocusTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = preset.title,
                                    color = FocusTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close", color = FocusMint)
                    }
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                containerColor = FocusCardBg,
                title = { Text("LevelUp", color = FocusTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Daily work and goal tracking app.", color = FocusTextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Built with Google Material 3 Expressive and cute anime aesthetic.", color = FocusTextSecondary, fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("OK", color = FocusMint)
                    }
                }
            )
        }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                onPickGalleryImage(it)
                showAvatarDialog = false
            }
        }

        // Avatar Picker Dialog
        if (showAvatarDialog) {
            val presets = listOf(
                Pair("preset:alex", "Alex"),
                Pair("preset:chibi", "Chibi"),
                Pair("preset:twilight", "Twilight"),
                Pair("preset:cat", "Neko"),
                Pair("preset:hug", "Hug")
            )

            AlertDialog(
                onDismissRequest = { showAvatarDialog = false },
                containerColor = FocusCardBg,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Choose Avatar",
                            color = FocusTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "✨", fontSize = 18.sp)
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pick a character preset or select an image from your device.",
                            color = FocusTextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        // Row of Presets 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            presets.take(3).forEach { (id, name) ->
                                AvatarOptionItem(
                                    avatarUri = id,
                                    name = name,
                                    isSelected = userProfile.avatarUri == id,
                                    onClick = {
                                        onAvatarChange(id)
                                        showAvatarDialog = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Row of Presets 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            presets.drop(3).forEach { (id, name) ->
                                AvatarOptionItem(
                                    avatarUri = id,
                                    name = name,
                                    isSelected = userProfile.avatarUri == id,
                                    onClick = {
                                        onAvatarChange(id)
                                        showAvatarDialog = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pick from device button with Slime Squash & Stretch feedback
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(23.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(FocusPurple, FocusPurpleLight)
                                    )
                                )
                                .slimeBounceClick { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Pick Image",
                                    tint = DarkButtonText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Choose from Phone",
                                    color = DarkButtonText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAvatarDialog = false }) {
                        Text("Cancel", color = FocusTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun AvatarOptionItem(
    avatarUri: String,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .slimeBounceClick { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) FocusMint else FocusCardBorder,
                    shape = CircleShape
                )
        ) {
            AnimeUserAvatar(
                avatarUri = avatarUri,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            color = if (isSelected) FocusMint else FocusTextPrimary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22284A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = FocusPurpleLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = FocusTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = FocusTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "Open",
            tint = FocusTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
