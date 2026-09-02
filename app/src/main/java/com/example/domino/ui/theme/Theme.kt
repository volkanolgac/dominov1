package com.example.domino.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.example.domino.model.AppTheme
import com.example.domino.ui.theme.NavyGradientEnd
import com.example.domino.ui.theme.BurgundyGradientEnd
import com.example.domino.ui.theme.ObsidianGradientEnd
import com.example.domino.ui.theme.PinkGradientEnd
import com.example.domino.ui.theme.WoodGradientEnd
import com.example.domino.ui.theme.ClassicGradientEnd
import com.example.domino.ui.theme.GoldDepth
import com.example.domino.ui.theme.NavyDepth
import com.example.domino.ui.theme.BurgundyDepth
import com.example.domino.ui.theme.ObsidianDepth
import com.example.domino.ui.theme.PinkDepth
import com.example.domino.ui.theme.WoodDepth
import com.example.domino.ui.theme.GreenDepth

private val ClassicColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldDark,
    onPrimaryContainer = Color.White,
    secondary = FeltGreenMedium,
    onSecondary = Color.White,
    background = CanvasDark,
    onBackground = Color.White,
    surface = SurfaceGreenCard,
    onSurface = Color.White,
    surfaceVariant = FeltGreenDark,
    onSurfaceVariant = TextMuted,
    outline = SurfaceGreenCardBorder
)

private val NavyColorScheme = darkColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    secondary = NavySecondary,
    onSecondary = Color.White,
    background = Color(0xFF000814),
    onBackground = Color.White,
    surface = NavySurface,
    onSurface = Color.White,
    surfaceVariant = NavyBg,
    onSurfaceVariant = Color(0xFF8AA9D6),
    outline = NavySecondary
)

private val BurgundyColorScheme = darkColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color.White,
    secondary = BurgundySecondary,
    onSecondary = Color.White,
    background = Color(0xFF140000),
    onBackground = Color.White,
    surface = BurgundySurface,
    onSurface = Color.White,
    surfaceVariant = BurgundyBg,
    onSurfaceVariant = Color(0xFFD68A8A),
    outline = BurgundySecondary
)

private val ObsidianColorScheme = darkColorScheme(
    primary = ObsidianPrimary,
    onPrimary = Color.Black,
    secondary = ObsidianSecondary,
    onSecondary = Color.White,
    background = Color(0xFF050505),
    onBackground = Color.White,
    surface = ObsidianSurface,
    onSurface = Color.White,
    surfaceVariant = ObsidianBg,
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = ObsidianSecondary
)

private val PinkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.Black,
    secondary = PinkSecondary,
    onSecondary = Color.White,
    background = Color(0xFF1A0A12),
    onBackground = Color.White,
    surface = PinkSurface,
    onSurface = Color.White,
    surfaceVariant = PinkBg,
    onSurfaceVariant = Color(0xFFFFB3C6),
    outline = PinkSecondary
)

private val WoodColorScheme = darkColorScheme(
    primary = WoodPrimary,
    onPrimary = Color.Black,
    secondary = WoodSecondary,
    onSecondary = Color.White,
    background = Color(0xFF241409),
    onBackground = Color.White,
    surface = WoodSurface,
    onSurface = Color.White,
    surfaceVariant = WoodBg,
    onSurfaceVariant = Color(0xFFBC8A5F),
    outline = WoodSecondary
)

@Composable
fun DominoTheme(themeId: String = AppTheme.CLASSIC_GREEN.id, content: @Composable () -> Unit) {
    val colorScheme = when (themeId) {
        AppTheme.NAVY_NIGHT.id -> NavyColorScheme
        AppTheme.ROYAL_BURGUNDY.id -> BurgundyColorScheme
        AppTheme.OBSIDIAN_BLACK.id -> ObsidianColorScheme
        AppTheme.PINK_BLOSSOM.id -> PinkColorScheme
        AppTheme.CLASSIC_WOOD.id -> WoodColorScheme
        else -> ClassicColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

object ThemeGradients {
    @Composable
    fun backgroundBrush(themeId: String): androidx.compose.ui.graphics.Brush {
        val startColor = MaterialTheme.colorScheme.background
        val endColor = when (themeId) {
            AppTheme.NAVY_NIGHT.id -> NavyGradientEnd
            AppTheme.ROYAL_BURGUNDY.id -> BurgundyGradientEnd
            AppTheme.OBSIDIAN_BLACK.id -> ObsidianGradientEnd
            AppTheme.PINK_BLOSSOM.id -> PinkGradientEnd
            AppTheme.CLASSIC_WOOD.id -> WoodGradientEnd
            else -> ClassicGradientEnd
        }
        return androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(startColor, endColor)
        )
    }

    @Composable
    fun WoodGrainTexture(modifier: Modifier = Modifier) {
        val color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
        androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Draw subtle vertical grain lines
            val lineCount = 15
            for (i in 0..lineCount) {
                val x = (width / lineCount) * i
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(x, 0f)
                    // Add slight curves to lines
                    cubicTo(
                        x + (Math.sin(i.toDouble()).toFloat() * 20f), height * 0.33f,
                        x - (Math.cos(i.toDouble()).toFloat() * 20f), height * 0.66f,
                        x, height
                    )
                }
                drawPath(
                    path = path,
                    color = color,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
            }

            // Draw a few subtle "knots"
            val knots = listOf(
                androidx.compose.ui.geometry.Offset(width * 0.2f, height * 0.4f),
                androidx.compose.ui.geometry.Offset(width * 0.8f, height * 0.1f),
                androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.8f)
            )

            knots.forEach { center ->
                drawCircle(
                    color = color,
                    radius = 40f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
                drawCircle(
                    color = color,
                    radius = 20f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )
            }
        }
    }

    @Composable
    fun getDepthColor(themeId: String, isPrimary: Boolean = true): androidx.compose.ui.graphics.Color {
        return when (themeId) {
            AppTheme.NAVY_NIGHT.id -> if (isPrimary) NavyDepth else Color(0xFF002244)
            AppTheme.ROYAL_BURGUNDY.id -> if (isPrimary) BurgundyDepth else Color(0xFF440000)
            AppTheme.OBSIDIAN_BLACK.id -> if (isPrimary) ObsidianDepth else Color(0xFF222222)
            AppTheme.PINK_BLOSSOM.id -> if (isPrimary) PinkDepth else Color(0xFF662746)
            AppTheme.CLASSIC_WOOD.id -> if (isPrimary) WoodDepth else Color(0xFF4A3125)
            else -> if (isPrimary) GoldDepth else GreenDepth
        }
    }
}
