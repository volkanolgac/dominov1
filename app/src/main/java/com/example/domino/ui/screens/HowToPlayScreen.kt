package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.domino.model.Translations
import com.example.domino.ui.components.ThreeDButton
import com.example.domino.ui.theme.ThemeGradients
import com.example.domino.ui.theme.CanvasDark
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.GoldVariant
import com.example.domino.ui.theme.SurfaceGreenCard
import com.example.domino.ui.theme.SurfaceGreenCardBorder
import com.example.domino.ui.theme.TextMuted
import androidx.compose.material3.MaterialTheme

private data class RuleItem(val title: String, val text: String)

@Composable
fun HowToPlayScreen(
    languageId: String,
    themeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val rules = listOf(
        RuleItem(Translations.getString("rule_1_title", languageId), Translations.getString("rule_1_text", languageId)),
        RuleItem(Translations.getString("rule_2_title", languageId), Translations.getString("rule_2_text", languageId)),
        RuleItem(Translations.getString("rule_3_title", languageId), Translations.getString("rule_3_text", languageId)),
        RuleItem(Translations.getString("rule_4_title", languageId), Translations.getString("rule_4_text", languageId)),
        RuleItem(Translations.getString("rule_5_title", languageId), Translations.getString("rule_5_text", languageId)),
        RuleItem(Translations.getString("rule_6_title", languageId), Translations.getString("rule_6_text", languageId))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeGradients.backgroundBrush(themeId))
    ) {
        ThemeGradients.WoodGrainTexture()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Translations.getString("how_to_play", languageId),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rules.forEach { rule ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = rule.title,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rule.text,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ThreeDButton(
                text = Translations.getString("back", languageId),
                onClick = onBack,
                containerColor = MaterialTheme.colorScheme.primary,
                depthColor = ThemeGradients.getDepthColor(themeId, true),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("back_button")
            )
        }
    }
}
