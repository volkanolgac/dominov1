package com.example.domino.engine

import com.example.domino.model.Pip
import com.example.domino.model.PlacedTile
import com.example.domino.model.PlayerId
import com.example.domino.model.Side
import com.example.domino.model.Tile

data class DealResult(
    val pool: List<Tile>,
    val humanHand: List<Tile>,
    val aiHand: List<Tile>,
    val starter: PlayerId
)

object DominoEngine {
    const val HAND_SIZE = 7
    const val TARGET_SCORE = 100

    fun generateFullSet(): List<Tile> {
        val list = mutableListOf<Tile>()
        for (i in 0..6) {
            for (j in i..6) {
                list.add(Tile(id = "${i}_${j}", a = i, b = j))
            }
        }
        return list
    }

    fun deal(): DealResult {
        val full = generateFullSet().shuffled()
        val humanHand = full.take(HAND_SIZE)
        val aiHand = full.drop(HAND_SIZE).take(HAND_SIZE)
        val pool = full.drop(HAND_SIZE * 2)

        val starter = determineStarter(humanHand, aiHand)

        return DealResult(
            pool = pool,
            humanHand = humanHand,
            aiHand = aiHand,
            starter = starter
        )
    }

    private fun determineStarter(humanHand: List<Tile>, aiHand: List<Tile>): PlayerId {
        val humanDoubles = humanHand.filter { it.isDouble }.maxByOrNull { it.weight }
        val aiDoubles = aiHand.filter { it.isDouble }.maxByOrNull { it.weight }

        return when {
            humanDoubles != null && aiDoubles != null -> {
                if (humanDoubles.weight >= aiDoubles.weight) PlayerId.HUMAN else PlayerId.AI
            }
            humanDoubles != null -> PlayerId.HUMAN
            aiDoubles != null -> PlayerId.AI
            else -> {
                val humanMax = humanHand.maxByOrNull { it.weight }?.weight ?: 0
                val aiMax = aiHand.maxByOrNull { it.weight }?.weight ?: 0
                if (humanMax >= aiMax) PlayerId.HUMAN else PlayerId.AI
            }
        }
    }

    fun boardEnds(board: List<PlacedTile>): Pair<Pip?, Pip?> {
        if (board.isEmpty()) return Pair(null, null)
        val left = board.first().orientedA
        val right = board.last().orientedB
        return Pair(left, right)
    }

    fun playableSides(tile: Tile, board: List<PlacedTile>): List<Side> {
        if (board.isEmpty()) return listOf(Side.LEFT)

        val (left, right) = boardEnds(board)
        val sides = mutableListOf<Side>()

        if (left != null && (tile.a == left || tile.b == left)) {
            sides.add(Side.LEFT)
        }
        if (right != null && (tile.a == right || tile.b == right)) {
            if (left == right && tile.a == left && tile.b == left) {
                // If left and right match and tile is double or fits both, can play either
            }
            sides.add(Side.RIGHT)
        }

        return sides.distinct()
    }

    fun hasPlayable(hand: List<Tile>, board: List<PlacedTile>): Boolean {
        if (board.isEmpty()) return hand.isNotEmpty()
        return hand.any { playableSides(it, board).isNotEmpty() }
    }

    fun placeTile(board: List<PlacedTile>, tile: Tile, side: Side): List<PlacedTile>? {
        if (board.isEmpty()) {
            val placed = PlacedTile(
                tile = tile,
                orientedA = tile.a,
                orientedB = tile.b,
                side = side,
                isDouble = tile.isDouble
            )
            return listOf(placed)
        }

        val (left, right) = boardEnds(board) ?: return null

        return if (side == Side.LEFT) {
            val needed = left ?: return null
            val (a, b) = when {
                tile.b == needed -> Pair(tile.a, tile.b)
                tile.a == needed -> Pair(tile.b, tile.a)
                else -> return null
            }
            val newPlaced = PlacedTile(
                tile = tile,
                orientedA = a,
                orientedB = b,
                side = Side.LEFT,
                isDouble = tile.isDouble
            )
            listOf(newPlaced) + board
        } else {
            val needed = right ?: return null
            val (a, b) = when {
                tile.a == needed -> Pair(tile.a, tile.b)
                tile.b == needed -> Pair(tile.b, tile.a)
                else -> return null
            }
            val newPlaced = PlacedTile(
                tile = tile,
                orientedA = a,
                orientedB = b,
                side = Side.RIGHT,
                isDouble = tile.isDouble
            )
            board + newPlaced
        }
    }

    fun handValue(hand: List<Tile>): Int {
        return hand.sumOf { it.weight }
    }
}
