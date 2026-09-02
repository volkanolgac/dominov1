package com.example.domino.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.engine.DominoEngine
import com.example.domino.model.PlacedTile
import com.example.domino.model.Tile
import com.example.domino.model.Translations
import com.example.domino.ui.theme.TextGold
import com.example.domino.ui.theme.TextMuted
import androidx.compose.material3.MaterialTheme

@Composable
fun PlayerHandView(
    hand: List<Tile>,
    board: List<PlacedTile>,
    isHumanTurn: Boolean,
    selectedTile: Tile?,
    languageId: String,
    onSelectTile: (Tile) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Translations.getString("your_hand", languageId),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "${hand.size} ${Translations.getString("tiles_count", languageId)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            hand.forEach { tile ->
                val isPlayable = isHumanTurn && DominoEngine.playableSides(tile, board).isNotEmpty()
                val isSelected = selectedTile?.id == tile.id

                DominoTileView(
                    a = tile.a,
                    b = tile.b,
                    orientation = TileOrientation.VERTICAL,
                    tileSize = TileSize.MEDIUM,
                    isSelected = isSelected,
                    isPlayable = isPlayable,
                    onClick = {
                        if (isHumanTurn) {
                            onSelectTile(tile)
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .testTag("tile_${tile.a}_${tile.b}")
                )
            }
        }
    }
}
