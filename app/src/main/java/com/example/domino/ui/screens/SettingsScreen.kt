package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.model.Settings
import com.example.domino.ui.theme.CanvasDark
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.GoldVariant
import com.example.domino.ui.theme.SurfaceGreenCard
import com.example.domino.ui.theme.SurfaceGreenCardBorder
import com.example.domino.ui.theme.TextMuted

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.example.domino.model.AppTheme
import com.example.domino.model.Language
import com.example.domino.model.Translations
import com.example.domino.ui.components.ThreeDButton
import com.example.domino.ui.theme.ThemeGradients
import androidx.compose.ui.draw.clip
import com.example.domino.ui.theme.NavyBg
import com.example.domino.ui.theme.BurgundyBg
import com.example.domino.ui.theme.ObsidianBg
import com.example.domino.ui.theme.PinkBg
import com.example.domino.ui.theme.WoodBg
import com.example.domino.ui.theme.FeltGreenMedium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign

@Composable
private fun getThemeColor(themeId: String): Color {
    return when (themeId) {
        AppTheme.NAVY_NIGHT.id -> NavyBg
        AppTheme.ROYAL_BURGUNDY.id -> BurgundyBg
        AppTheme.OBSIDIAN_BLACK.id -> ObsidianBg
        AppTheme.PINK_BLOSSOM.id -> PinkBg
        AppTheme.CLASSIC_WOOD.id -> WoodBg
        else -> FeltGreenMedium
    }
}

@Composable
fun SettingsScreen(
    settings: Settings,
    profileName: String,
    onUpdateName: (String) -> Unit,
    onUpdateSettings: (Settings) -> Unit,
    onResetStats: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = settings.languageId

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeGradients.backgroundBrush(settings.themeId))
    ) {
        ThemeGradients.WoodGrainTexture()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("settings_top_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = Translations.getString("settings", lang),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Name
                Text(Translations.getString("profile", lang), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = profileName,
                    onValueChange = onUpdateName,
                    label = { Text(Translations.getString("player_name", lang), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Language selection
                Text(Translations.getString("language", lang), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Language.entries) { language ->
                        val isSelected = settings.languageId == language.id
                        ThreeDButton(
                            text = language.displayName,
                            onClick = { onUpdateSettings(settings.copy(languageId = language.id)) },
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            depthColor = if (isSelected) ThemeGradients.getDepthColor(settings.themeId, true) else MaterialTheme.colorScheme.outline,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            depth = 2.dp,
                            fillMaxWidth = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Themes
                Text(Translations.getString("themes", lang), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AppTheme.entries) { theme ->
                        val isSelected = settings.themeId == theme.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onUpdateSettings(settings.copy(themeId = theme.id)) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(getThemeColor(theme.id))
                                    .border(
                                        if (isSelected) 3.dp else 1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                theme.displayName,
                                fontSize = 9.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(Translations.getString("game_settings", lang), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                // Sound switch
                SettingSwitchRow(
                    label = "🔊 ${Translations.getString("sound_effects", lang)}",
                    checked = settings.sound,
                    onCheckedChange = { onUpdateSettings(settings.copy(sound = it)) },
                    testTag = "sound_switch"
                )

                // Animations switch
                SettingSwitchRow(
                    label = "✨ ${Translations.getString("animations", lang)}",
                    checked = settings.animations,
                    onCheckedChange = { onUpdateSettings(settings.copy(animations = it)) },
                    testTag = "animations_switch"
                )

                // Haptics switch
                SettingSwitchRow(
                    label = "📳 ${Translations.getString("haptics", lang)}",
                    checked = settings.haptics,
                    onCheckedChange = { onUpdateSettings(settings.copy(haptics = it)) },
                    testTag = "haptics_switch"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset stats button
            ThreeDButton(
                text = Translations.getString("reset_stats", lang),
                onClick = onResetStats,
                containerColor = Color(0xFFFF6B6B),
                depthColor = Color(0xFFB71C1C),
                contentColor = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_stats_button")
            )

            Spacer(modifier = Modifier.weight(1f))

            ThreeDButton(
                text = Translations.getString("back", lang),
                onClick = onBack,
                containerColor = MaterialTheme.colorScheme.primary,
                depthColor = ThemeGradients.getDepthColor(settings.themeId, true),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_back_button")
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
