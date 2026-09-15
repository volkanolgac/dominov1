/**
 * Mini Domino - Core Engine & AI (Web Edition)
 * Pure JavaScript matching Android DominoEngine.kt and DominoAI.kt with 100% parity.
 */

class Tile {
    constructor(a, b) {
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
        this.id = `${this.a}_${this.b}`;
        this.isDouble = this.a === this.b;
        this.weight = this.a + this.b;
    }
}

class PlacedTile {
    constructor(tile, orientedA, orientedB, side, isDouble) {
        this.tile = tile;
        this.orientedA = orientedA;
        this.orientedB = orientedB;
        this.side = side;
        this.isDouble = isDouble;
    }
}

const DominoEngine = {
    HAND_SIZE: 7,
    TARGET_SCORE: 100,

    generateFullSet() {
        const list = [];
        for (let i = 0; i <= 6; i++) {
            for (let j = i; j <= 6; j++) {
                list.push(new Tile(i, j));
            }
        }
        return list;
    },

    shuffle(array) {
        const arr = [...array];
        for (let i = arr.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [arr[i], arr[j]] = [arr[j], arr[i]];
        }
        return arr;
    },

    deal() {
        const full = this.shuffle(this.generateFullSet());
        const humanHand = full.slice(0, this.HAND_SIZE);
        const aiHand = full.slice(this.HAND_SIZE, this.HAND_SIZE * 2);
        const pool = full.slice(this.HAND_SIZE * 2);

        const starter = this.determineStarter(humanHand, aiHand);

        return {
            pool,
            humanHand,
            aiHand,
            starter
        };
    },

    determineStarter(humanHand, aiHand) {
        const humanDoubles = humanHand.filter(t => t.isDouble).sort((x, y) => y.weight - x.weight);
        const aiDoubles = aiHand.filter(t => t.isDouble).sort((x, y) => y.weight - x.weight);

        if (humanDoubles.length > 0 && aiDoubles.length > 0) {
            return humanDoubles[0].weight >= aiDoubles[0].weight ? 'HUMAN' : 'AI';
        }
        if (humanDoubles.length > 0) return 'HUMAN';
        if (aiDoubles.length > 0) return 'AI';

        const humanMax = Math.max(...humanHand.map(t => t.weight), 0);
        const aiMax = Math.max(...aiHand.map(t => t.weight), 0);
        return humanMax >= aiMax ? 'HUMAN' : 'AI';
    },

    boardEnds(board) {
        if (!board || board.length === 0) return { left: null, right: null };
        return {
            left: board[0].orientedA,
            right: board[board.length - 1].orientedB
        };
    },

    playableSides(tile, board) {
        if (!board || board.length === 0) return ['LEFT'];

        const { left, right } = this.boardEnds(board);
        const sides = [];

        if (left !== null && (tile.a === left || tile.b === left)) {
            sides.push('LEFT');
        }
        if (right !== null && (tile.a === right || tile.b === right)) {
            sides.push('RIGHT');
        }

        return [...new Set(sides)];
    },

    hasPlayable(hand, board) {
        if (!board || board.length === 0) return hand.length > 0;
        return hand.some(t => this.playableSides(t, board).length > 0);
    },

    placeTile(board, tile, side) {
        if (!board || board.length === 0) {
            return [new PlacedTile(tile, tile.a, tile.b, side, tile.isDouble)];
        }

        const { left, right } = this.boardEnds(board);

        if (side === 'LEFT') {
            if (left === null) return null;
            let a, b;
            if (tile.b === left) {
                a = tile.a;
                b = tile.b;
            } else if (tile.a === left) {
                a = tile.b;
                b = tile.a;
            } else {
                return null;
            }
            const newPlaced = new PlacedTile(tile, a, b, 'LEFT', tile.isDouble);
            return [newPlaced, ...board];
        } else {
            if (right === null) return null;
            let a, b;
            if (tile.a === right) {
                a = tile.a;
                b = tile.b;
            } else if (tile.b === right) {
                a = tile.b;
                b = tile.a;
            } else {
                return null;
            }
            const newPlaced = new PlacedTile(tile, a, b, 'RIGHT', tile.isDouble);
            return [...board, newPlaced];
        }
    },

    handValue(hand) {
        return hand.reduce((sum, t) => sum + t.weight, 0);
    }
};

const DominoAI = {
    chooseMove(aiHand, board, humanMissingPips = new Set()) {
        if (!board || board.length === 0) {
            const doubles = aiHand.filter(t => t.isDouble).sort((x, y) => y.weight - x.weight);
            const starterTile = doubles[0] || [...aiHand].sort((x, y) => y.weight - x.weight)[0];
            if (!starterTile) return null;
            return { tile: starterTile, side: 'LEFT' };
        }

        const legalMoves = [];
        for (const tile of aiHand) {
            const sides = DominoEngine.playableSides(tile, board);
            for (const side of sides) {
                legalMoves.push({ tile, side });
            }
        }

        if (legalMoves.length === 0) return null;

        function scoreMove(move) {
            let score = 0.0;
            if (move.tile.isDouble) score += 30.0;
            score += move.tile.weight * 2.0;

            const testBoard = DominoEngine.placeTile(board, move.tile, move.side);
            if (testBoard) {
                const { left: newLeft, right: newRight } = DominoEngine.boardEnds(testBoard);

                if (newLeft !== null && humanMissingPips.has(newLeft)) score += 25.0;
                if (newRight !== null && humanMissingPips.has(newRight)) score += 25.0;

                const remainingInHand = aiHand.filter(t => t.id !== move.tile.id);
                if (newLeft !== null && remainingInHand.some(t => t.a === newLeft || t.b === newLeft)) score += 15.0;
                if (newRight !== null && remainingInHand.some(t => t.a === newRight || t.b === newRight)) score += 15.0;
            }

            return score;
        }

        let bestMove = legalMoves[0];
        let bestScore = scoreMove(bestMove);

        for (let i = 1; i < legalMoves.length; i++) {
            const s = scoreMove(legalMoves[i]);
            if (s > bestScore) {
                bestScore = s;
                bestMove = legalMoves[i];
            }
        }

        return bestMove;
    }
};

window.DominoEngine = DominoEngine;
window.DominoAI = DominoAI;
window.Tile = Tile;
window.PlacedTile = PlacedTile;
