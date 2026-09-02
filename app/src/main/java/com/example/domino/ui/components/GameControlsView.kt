package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.model.Translations
import com.example.domino.ui.components.ThreeDButton
import com.example.domino.ui.theme.ThemeGradients
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.TextGold
import com.example.domino.ui.theme.TextMuted
import androidx.compose.material3.MaterialTheme

@Composable
fun GameControlsView(
    canDraw: Boolean,
    canPass: Boolean,
    onDraw: () -> Unit,
    onPass: () -> Unit,
    onMenu: () -> Unit,
    hint: String,
    languageId: String,
    themeId: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hint text box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .padding(vertical = 6.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThreeDButton(
                text = Translations.getString("menu", languageId),
                onClick = onMenu,
                containerColor = MaterialTheme.colorScheme.surface,
                depthColor = MaterialTheme.colorScheme.outline,
                contentColor = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                depth = 2.dp,
                modifier = Modifier.weight(1f).testTag("menu_button")
            )

            Spacer(modifier = Modifier.width(8.dp))

            ThreeDButton(
                text = Translations.getString("draw", languageId),
                onClick = onDraw,
                enabled = canDraw,
                containerColor = MaterialTheme.colorScheme.primary,
                depthColor = ThemeGradients.getDepthColor(themeId, true),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                fontSize = 13.sp,
                depth = 3.dp,
                modifier = Modifier.weight(1.2f).testTag("draw_button")
            )

            Spacer(modifier = Modifier.width(8.dp))

            ThreeDButton(
                text = Translations.getString("pass", languageId),
                onClick = onPass,
                enabled = canPass,
                containerColor = MaterialTheme.colorScheme.primary,
                depthColor = ThemeGradients.getDepthColor(themeId, true),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                fontSize = 13.sp,
                depth = 3.dp,
                modifier = Modifier.weight(1f).testTag("pass_button")
            )
        }
    }
}
