package com.example.domino.engine

import com.example.domino.model.Pip
import com.example.domino.model.PlacedTile
import com.example.domino.model.Side
import com.example.domino.model.Tile

data class AiMove(
    val tile: Tile,
    val side: Side
)

object DominoAI {

    fun chooseMove(
        aiHand: List<Tile>,
        board: List<PlacedTile>,
        humanMissingPips: Set<Pip>
    ): AiMove? {
        if (board.isEmpty()) {
            val starterTile = aiHand.filter { it.isDouble }.maxByOrNull { it.weight }
                ?: aiHand.maxByOrNull { it.weight }
                ?: return null
            return AiMove(starterTile, Side.LEFT)
        }

        val legalMoves = mutableListOf<AiMove>()
        for (tile in aiHand) {
            val sides = DominoEngine.playableSides(tile, board)
            for (side in sides) {
                legalMoves.add(AiMove(tile, side))
            }
        }

        if (legalMoves.isEmpty()) return null

        val (leftEnd, rightEnd) = DominoEngine.boardEnds(board)

        fun scoreMove(move: AiMove): Double {
            var score = 0.0

            if (move.tile.isDouble) {
                score += 30.0
            }

            score += move.tile.weight * 2.0

            val testBoard = DominoEngine.placeTile(board, move.tile, move.side)
            if (testBoard != null) {
                val (newLeft, newRight) = DominoEngine.boardEnds(testBoard)

                if (newLeft != null && humanMissingPips.contains(newLeft)) {
                    score += 25.0
                }
                if (newRight != null && humanMissingPips.contains(newRight)) {
                    score += 25.0
                }

                val remainingInHand = aiHand.filter { it.id != move.tile.id }
                if (newLeft != null && remainingInHand.any { it.a == newLeft || it.b == newLeft }) {
                    score += 15.0
                }
                if (newRight != null && remainingInHand.any { it.a == newRight || it.b == newRight }) {
                    score += 15.0
                }
            }

            return score
        }

        return legalMoves.maxByOrNull { scoreMove(it) }
    }
}
