package com.example.domino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domino.engine.DominoEngine
import com.example.domino.model.Phase
import com.example.domino.model.PlayerId
import com.example.domino.model.Side
import com.example.domino.model.Tile
import com.example.domino.model.Translations
import com.example.domino.ui.components.GameBoardView
import com.example.domino.ui.components.GameControlsView
import com.example.domino.ui.components.GameOverDialog
import com.example.domino.ui.components.OpponentHandView
import com.example.domino.ui.components.PlayerHandView
import com.example.domino.ui.components.ScoreBoardView
import com.example.domino.ui.theme.ThemeGradients
import com.example.domino.ui.theme.CanvasDark
import com.example.domino.ui.theme.FeltGreenDark
import com.example.domino.ui.theme.GoldVariant
import com.example.domino.viewmodel.GameUiState

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.testTag

@Composable
fun GameScreen(
    state: GameUiState,
    onSelectTile: (Tile) -> Unit,
    onChooseSide: (Side) -> Unit,
    onDraw: () -> Unit,
    onPass: () -> Unit,
    onMenu: () -> Unit,
    onNextRoundOrMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    val isHumanTurn = state.turn == PlayerId.HUMAN && state.phase == Phase.PLAYING
    val humanCanPlay = DominoEngine.hasPlayable(state.humanHand, state.board)
    val canDraw = isHumanTurn && !humanCanPlay && state.pool.isNotEmpty()
    val canPass = isHumanTurn && !humanCanPlay && state.pool.isEmpty()
    val lang = state.settings.languageId
    val themeId = state.settings.themeId

    val showExitDialog = remember { mutableStateOf(false) }

    val turnLabel = when {
        state.phase != Phase.PLAYING -> Translations.getString("round_over", lang)
        isHumanTurn -> Translations.getString("your_turn", lang)
        else -> "${state.opponentName} ${Translations.getString("thinking", lang)}"
    }

    val hint = when {
        !isHumanTurn -> Translations.getString("hint_thinking", lang)
        state.selectedTile != null -> Translations.getString("hint_choose_side", lang)
        humanCanPlay -> Translations.getString("hint_select_tile", lang)
        canDraw -> Translations.getString("hint_draw", lang)
        else -> Translations.getString("hint_pass", lang)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeGradients.backgroundBrush(themeId))
    ) {
        ThemeGradients.WoodGrainTexture()
        
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreBoardView(
                        humanScore = state.humanScore,
                        aiScore = state.aiScore,
                        playerName = state.profile.name,
                        opponentName = state.opponentName,
                        opponentAvatarIndex = state.opponentAvatar,
                        turnLabel = turnLabel,
                        poolCount = state.pool.size,
                        languageId = lang
                    )
                    GameControlsView(
                        canDraw = canDraw,
                        canPass = canPass,
                        onDraw = onDraw,
                        onPass = onPass,
                        onMenu = onMenu,
                        hint = hint,
                        languageId = lang,
                        themeId = themeId
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OpponentHandView(count = state.aiHand.size, thinking = state.aiThinking, languageId = lang)
                    GameBoardView(board = state.board, pendingSides = state.pendingSides, onChooseSide = onChooseSide)
                    PlayerHandView(hand = state.humanHand, board = state.board, isHumanTurn = isHumanTurn, selectedTile = state.selectedTile, languageId = lang, onSelectTile = onSelectTile)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (state.phase == Phase.PLAYING && !state.isMatchmaking) {
                                showExitDialog.value = true
                            } else {
                                onMenu()
                            }
                        },
                        modifier = Modifier.testTag("game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = Translations.getString("app_name", lang),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scoreboard
                ScoreBoardView(
                    humanScore = state.humanScore,
                    aiScore = state.aiScore,
                    playerName = state.profile.name,
                    opponentName = state.opponentName,
                    opponentAvatarIndex = state.opponentAvatar,
                    turnLabel = turnLabel,
                    poolCount = state.pool.size,
                    languageId = lang
                )

                Spacer(modifier = Modifier.height(10.dp))

                // AI Opponent Hand
                OpponentHandView(
                    count = state.aiHand.size,
                    thinking = state.aiThinking,
                    languageId = lang
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Game Board Felt Table
                GameBoardView(
                    board = state.board,
                    pendingSides = state.pendingSides,
                    onChooseSide = onChooseSide
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Human Player Hand
                PlayerHandView(
                    hand = state.humanHand,
                    board = state.board,
                    isHumanTurn = isHumanTurn,
                    selectedTile = state.selectedTile,
                    languageId = lang,
                    onSelectTile = onSelectTile
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Game Action Controls
                GameControlsView(
                    canDraw = canDraw,
                    canPass = canPass,
                    onDraw = onDraw,
                    onPass = onPass,
                    onMenu = onMenu,
                    hint = hint,
                    languageId = lang,
                    themeId = themeId
                )
            }
        }

        // Matchmaking Overlay
        if (state.isMatchmaking) {
            MatchmakingOverlay(
                step = state.matchmakingStep,
                opponentName = state.opponentName,
                opponentAvatarIndex = state.opponentAvatar,
                languageId = lang
            )
        }

        // Exit Confirmation Dialog
        if (showExitDialog.value) {
            AlertDialog(
                onDismissRequest = { showExitDialog.value = false },
                title = { Text(text = Translations.getString("quit_game", lang)) },
                text = { Text(text = Translations.getString("quit_game_confirm", lang)) },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog.value = false
                        onMenu()
                    }) {
                        Text(Translations.getString("yes", lang))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog.value = false }) {
                        Text(Translations.getString("no", lang))
                    }
                }
            )
        }

        // Game / Round Over Modal Dialog
        if (state.phase != Phase.PLAYING && state.lastRoundResult != null) {
            GameOverDialog(
                result = state.lastRoundResult,
                phase = state.phase,
                matchWinner = state.matchWinner,
                humanScore = state.humanScore,
                aiScore = state.aiScore,
                languageId = lang,
                onNext = onNextRoundOrMatch,
                onMenu = onMenu
            )
        }
    }
}

@Composable
fun MatchmakingOverlay(step: String, opponentName: String, opponentAvatarIndex: Int, languageId: String) {
    val opponentIcon = when (opponentAvatarIndex % 4) {
        0 -> Icons.Default.Face
        1 -> Icons.Default.Person
        2 -> Icons.Default.Face
        else -> Icons.Default.Person
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (step) {
                "finding" -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(Translations.getString("finding_opponent", languageId), color = Color.White, fontWeight = FontWeight.Bold)
                }
                "found" -> {
                    Icon(
                        opponentIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .height(80.dp)
                            .width(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(Translations.getString("opponent_found", languageId), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(opponentName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                "starting" -> {
                    Text(Translations.getString("opponent_ready", languageId), color = Color.White, fontSize = 18.sp)
                    Text(Translations.getString("game_starting", languageId), color = MaterialTheme.colorScheme.primary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
