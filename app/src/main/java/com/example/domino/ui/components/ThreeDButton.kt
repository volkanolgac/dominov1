package com.example.domino.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.style.TextAlign

@Composable
fun ThreeDButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    depthColor: Color = Color.Unspecified,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Black,
    cornerRadius: Dp = 12.dp,
    depth: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val actualContainerColor = if (enabled) containerColor else Color.Gray.copy(alpha = 0.3f)
    val actualContentColor = if (enabled) contentColor else Color.Gray
    
    val actualDepthColor = if (!enabled) {
        Color.Transparent
    } else if (depthColor == Color.Unspecified) {
        containerColor.copy(alpha = 0.7f)
    } else depthColor

    val verticalOffset by animateDpAsState(targetValue = if (isPressed && enabled) depth else 0.dp)
    val actualDepth = if (enabled) depth else 0.dp

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow/Depth layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = actualDepth)
                .clip(RoundedCornerShape(cornerRadius))
                .background(actualDepthColor)
        )

        // Face layer
        Box(
            modifier = Modifier
                .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
                .offset(y = verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(actualContainerColor)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = actualContentColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                letterSpacing = 1.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
