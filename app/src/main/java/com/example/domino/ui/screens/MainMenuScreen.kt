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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import com.example.domino.model.Stats
import com.example.domino.model.Translations
import com.example.domino.ui.components.DominoTileView
import com.example.domino.ui.components.TileSize
import com.example.domino.ui.components.ThreeDButton
import com.example.domino.ui.theme.ThemeGradients
import com.example.domino.ui.theme.CanvasDark
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.FeltGreenMedium
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.GoldVariant
import com.example.domino.ui.theme.SurfaceGreenCard
import com.example.domino.ui.theme.SurfaceGreenCardBorder
import com.example.domino.ui.theme.TextGold
import com.example.domino.ui.theme.TextMuted

import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme

@Composable
fun MainMenuScreen(
    stats: Stats,
    languageId: String,
    themeId: String,
    onPlay: () -> Unit,
    onHowTo: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeGradients.backgroundBrush(themeId))
    ) {
        ThemeGradients.WoodGrainTexture()
        
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = Translations.getString("app_name", languageId),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Translations.getString("tagline", languageId),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DominoTileView(a = 6, b = 6, tileSize = TileSize.SMALL)
                        Spacer(modifier = Modifier.width(8.dp))
                        DominoTileView(a = 3, b = 5, tileSize = TileSize.SMALL)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ThreeDButton(
                        text = Translations.getString("play", languageId),
                        onClick = onPlay,
                        containerColor = MaterialTheme.colorScheme.primary,
                        depthColor = ThemeGradients.getDepthColor(themeId, true),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ThreeDButton(
                        text = Translations.getString("how_to_play", languageId),
                        onClick = onHowTo,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        depthColor = ThemeGradients.getDepthColor(themeId, false),
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ThreeDButton(
                        text = Translations.getString("settings", languageId),
                        onClick = onSettings,
                        containerColor = MaterialTheme.colorScheme.surface,
                        depthColor = MaterialTheme.colorScheme.outline,
                        contentColor = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    StatisticsCard(stats = stats, languageId = languageId)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hero Tiles Showcase
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DominoTileView(a = 6, b = 6, tileSize = TileSize.SMALL)
                    Spacer(modifier = Modifier.width(8.dp))
                    DominoTileView(a = 3, b = 5, tileSize = TileSize.SMALL)
                    Spacer(modifier = Modifier.width(8.dp))
                    DominoTileView(a = 0, b = 4, tileSize = TileSize.SMALL)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title Header
                Text(
                    text = Translations.getString("app_name", languageId),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Translations.getString("tagline", languageId),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ThreeDButton(
                        text = Translations.getString("play", languageId),
                        onClick = onPlay,
                        containerColor = MaterialTheme.colorScheme.primary,
                        depthColor = ThemeGradients.getDepthColor(themeId, true),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth().testTag("play_button")
                    )

                    ThreeDButton(
                        text = Translations.getString("how_to_play", languageId),
                        onClick = onHowTo,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        depthColor = ThemeGradients.getDepthColor(themeId, false),
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().testTag("how_to_play_button")
                    )

                    ThreeDButton(
                        text = Translations.getString("settings", languageId),
                        onClick = onSettings,
                        containerColor = MaterialTheme.colorScheme.surface,
                        depthColor = MaterialTheme.colorScheme.outline,
                        contentColor = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().testTag("settings_button")
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                StatisticsCard(stats = stats, languageId = languageId)
            }
        }
    }
}

@Composable
private fun StatisticsCard(stats: Stats, languageId: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = Translations.getString("stats", languageId),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            StatRow(label = Translations.getString("matches", languageId), value = "${stats.matches}")
            StatRow(label = Translations.getString("wins", languageId), value = "${stats.wins}")
            StatRow(label = Translations.getString("losses", languageId), value = "${stats.losses}")
            StatRow(label = Translations.getString("win_rate", languageId), value = "%${stats.winRate}")
            StatRow(label = Translations.getString("best_score", languageId), value = "${stats.bestScore}")
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
