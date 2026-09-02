package com.example.domino.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.TileBackgroundLight
import com.example.domino.ui.theme.TileBackgroundSelected
import com.example.domino.ui.theme.TileBorderLight
import com.example.domino.ui.theme.TilePipColor

enum class TileOrientation {
    VERTICAL,
    HORIZONTAL
}

enum class TileSize(val widthDp: Dp, val heightDp: Dp, val pipRadiusDp: Dp) {
    SMALL(24.dp, 48.dp, 2.dp),
    MEDIUM(36.dp, 72.dp, 3.5.dp),
    LARGE(44.dp, 88.dp, 4.5.dp)
}

@Composable
fun DominoTileView(
    a: Int,
    b: Int,
    modifier: Modifier = Modifier,
    orientation: TileOrientation = TileOrientation.VERTICAL,
    tileSize: TileSize = TileSize.MEDIUM,
    isSelected: Boolean = false,
    isPlayable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isHorizontal = orientation == TileOrientation.HORIZONTAL
    val width = if (isHorizontal) tileSize.heightDp else tileSize.widthDp
    val height = if (isHorizontal) tileSize.widthDp else tileSize.heightDp

    val bgColor = when {
        isSelected -> TileBackgroundSelected
        else -> TileBackgroundLight
    }

    val borderColor = when {
        isSelected -> GoldPrimary
        isPlayable -> GoldPrimary
        else -> TileBorderLight
    }

    val borderWidth = if (isSelected || isPlayable) 2.5.dp else 1.dp

    val baseModifier = modifier
        .size(width, height)
        .shadow(elevation = if (isSelected) 8.dp else 3.dp, shape = RoundedCornerShape(6.dp))
        .clip(RoundedCornerShape(6.dp))
        .background(bgColor)
        .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )

    Box(
        modifier = baseModifier,
        contentAlignment = Alignment.Center
    ) {
        if (!isHorizontal) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PipGrid(count = a, pipRadius = tileSize.pipRadiusDp)
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(1.5.dp)
                        .background(Color(0xFF888888))
                )

                // Bottom half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PipGrid(count = b, pipRadius = tileSize.pipRadiusDp)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    PipGrid(count = a, pipRadius = tileSize.pipRadiusDp)
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .width(1.5.dp)
                        .background(Color(0xFF888888))
                )

                // Right half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    PipGrid(count = b, pipRadius = tileSize.pipRadiusDp)
                }
            }
        }
    }
}

@Composable
private fun PipGrid(
    count: Int,
    pipRadius: Dp
) {
    Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
        val w = size.width
        val h = size.height
        val r = pipRadius.toPx()

        val left = w * 0.28f
        val midX = w * 0.5f
        val right = w * 0.72f

        val top = h * 0.28f
        val midY = h * 0.5f
        val bottom = h * 0.72f

        fun drawDot(x: Float, y: Float) {
            drawCircle(
                color = TilePipColor,
                radius = r,
                center = Offset(x, y)
            )
        }

        when (count) {
            1 -> {
                drawDot(midX, midY)
            }
            2 -> {
                drawDot(left, top)
                drawDot(right, bottom)
            }
            3 -> {
                drawDot(left, top)
                drawDot(midX, midY)
                drawDot(right, bottom)
            }
            4 -> {
                drawDot(left, top)
                drawDot(right, top)
                drawDot(left, bottom)
                drawDot(right, bottom)
            }
            5 -> {
                drawDot(left, top)
                drawDot(right, top)
                drawDot(midX, midY)
                drawDot(left, bottom)
                drawDot(right, bottom)
            }
            6 -> {
                drawDot(left, top)
                drawDot(right, top)
                drawDot(left, midY)
                drawDot(right, midY)
                drawDot(left, bottom)
                drawDot(right, bottom)
            }
        }
    }
}
