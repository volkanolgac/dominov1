package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.model.PlacedTile
import com.example.domino.model.Side
import com.example.domino.ui.theme.FeltGreenBorder
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.FeltGreenMedium
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.TextMuted

@Composable
fun GameBoardView(
    board: List<PlacedTile>,
    pendingSides: List<Side>,
    onChooseSide: (Side) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(board.size) {
        if (board.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(FeltGreenDark)
            .border(2.dp, FeltGreenBorder, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (board.isEmpty()) {
            Text(
                text = "Masa boş.\nİlk hamle bekleniyor...",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Pending Side Chooser Prompt
                if (pendingSides.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(FeltGreenMedium)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Hangi uca koyulsun? ",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (pendingSides.contains(Side.LEFT)) {
                            Button(
                                onClick = { onChooseSide(Side.LEFT) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("choose_left_button")
                            ) {
                                Text("SOL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (pendingSides.contains(Side.LEFT) && pendingSides.contains(Side.RIGHT)) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (pendingSides.contains(Side.RIGHT)) {
                            Button(
                                onClick = { onChooseSide(Side.RIGHT) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("choose_right_button")
                            ) {
                                Text("SAĞ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    board.forEachIndexed { index, placed ->
                        val orientation = if (placed.isDouble) TileOrientation.VERTICAL else TileOrientation.HORIZONTAL

                        DominoTileView(
                            a = placed.orientedA,
                            b = placed.orientedB,
                            orientation = orientation,
                            tileSize = TileSize.MEDIUM
                        )

                        if (index < board.size - 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}
