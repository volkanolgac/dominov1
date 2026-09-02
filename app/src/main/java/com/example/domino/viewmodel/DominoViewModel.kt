package com.example.domino.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domino.engine.DominoAI
import com.example.domino.engine.DominoEngine
import com.example.domino.model.AppScreen
import com.example.domino.model.Phase
import com.example.domino.model.Pip
import com.example.domino.model.PlacedTile
import com.example.domino.model.PlayerId
import com.example.domino.model.RoundReason
import com.example.domino.model.RoundResult
import com.example.domino.model.Settings
import com.example.domino.model.Side
import com.example.domino.model.Stats
import com.example.domino.model.Tile
import androidx.room.Room
import com.example.domino.data.AppDatabase
import com.example.domino.data.UserEntity
import com.example.domino.data.UserRepository
import com.example.domino.model.AppTheme
import com.example.domino.model.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.MAIN_MENU,
    val board: List<PlacedTile> = emptyList(),
    val pool: List<Tile> = emptyList(),
    val humanHand: List<Tile> = emptyList(),
    val aiHand: List<Tile> = emptyList(),
    val turn: PlayerId = PlayerId.HUMAN,
    val phase: Phase = Phase.PLAYING,
    val humanScore: Int = 0,
    val aiScore: Int = 0,
    val lastRoundResult: RoundResult? = null,
    val matchWinner: PlayerId? = null,
    val humanMissing: Set<Pip> = emptySet(),
    val passes: Int = 0,
    val selectedTile: Tile? = null,
    val pendingSides: List<Side> = emptyList(),
    val round: Int = 1,
    val aiThinking: Boolean = false,
    val settings: Settings = Settings(),
    val stats: Stats = Stats(),
    val profile: Profile = Profile(),
    val isMatchmaking: Boolean = false,
    val matchmakingStep: String = "",
    val opponentName: String = "AI",
    val opponentAvatar: Int = 0,
    val showNameEntry: Boolean = false,
    val soundEvent: String? = null
)

class DominoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "domino_database"
    ).fallbackToDestructiveMigration().build()
    private val repository = UserRepository(db.userDao())

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val opponentNames = listOf(
        "Arda", "Cemil", "Orhan", "Taylan", "Recep", "Necip", "Selim", "Fevzi", "Cafer", "Abdülbaki", 
        "Baki", "Mahmut", "İbrahim", "İdris", "Serkan", "Adem", "Ali", "Fuat", "Suat", "Hamza", 
        "Haydar", "Elif", "Ceyda", "Cavidan", "Sena", "Sibel", "Arzu", "Alev", "Şahika", "Buket", 
        "Damla", "Duru", "Deniz", "Gülperi", "Gülnihal", "Zehra", "Zeliha", "Emel", "Türkan", "Fulya", 
        "Ada", "Gül", "Çiçek", "Remziye", "Füsun", "Fatma", "Ayşe", "Hayriye", "Aleyna", "Zekiye", 
        "Zülfiye", "Zafer", "Güneş", "Mehmet", "Hakan", "Hasan", "Kenan", "Kamil", "Kıvanç", "Kerem", 
        "Atakan", "Batuhan", "Burak", "Berke", "Berk", "Berkcan", "Alican", "Aziz", "Alex", "Sasha", 
        "Tom", "Natalia", "Tomas", "Olga", "Helga", "Natasha", "Vladimir", "Rex", "Timofei", "Roberto", 
        "Roberti", "Aleksandr", "Aleksandra", "Henry", "Sam", "Samantha", "Samuel", "Sanchez", "Max", 
        "Martinez", "Martin", "Martina", "David", "Dominguez", "Carlos", "Osvaldo", "Elizabeth", "Frank", 
        "Franco", "Jackson", "John", "Jonathon", "Kristina", "Chris", "Michel", "Michael", "Tim", 
        "Justin", "Sergey", "Maxim", "Timothy", "Igor", "Anthony", "Anton", "Sofia", "Sofya", "Safiye", "Ayla"
    ).map { name ->
        name.lowercase(java.util.Locale("tr", "TR"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("tr", "TR")) else it.toString() }
    }
    private val opponentAvatars = listOf(0, 1, 2, 3, 4, 5) // Placeholder for icon indices

    init {
        viewModelScope.launch {
            repository.userProfile.collectLatest { entity ->
                if (entity == null) {
                    _uiState.update { it.copy(showNameEntry = true) }
                } else {
                    val formattedName = entity.name.trim().lowercase(java.util.Locale("tr", "TR"))
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("tr", "TR")) else it.toString() }
                    _uiState.update {
                        it.copy(
                            profile = Profile(name = formattedName),
                            settings = it.settings.copy(
                                themeId = entity.themeId,
                                languageId = entity.languageId
                            ),
                            stats = Stats(
                                matches = entity.matches,
                                wins = entity.wins,
                                losses = entity.losses,
                                bestScore = entity.bestScore
                            ),
                            showNameEntry = false
                        )
                    }
                }
            }
        }
    }

    fun saveUserName(name: String) {
        val formattedName = name.trim().lowercase(java.util.Locale("tr", "TR"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("tr", "TR")) else it.toString() }
        viewModelScope.launch {
            repository.saveProfile(formattedName, _uiState.value.settings.themeId, _uiState.value.settings.languageId)
        }
    }

    fun setLanguage(languageId: String) {
        viewModelScope.launch {
            repository.updateLanguage(languageId)
        }
        _uiState.update { it.copy(settings = it.settings.copy(languageId = languageId)) }
    }

    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.GAME) {
            startMatchmaking()
            return
        }
        _uiState.update { it.copy(currentScreen = screen) }
    }

    private fun startMatchmaking() {
        viewModelScope.launch {
            val name = opponentNames.random()
            val avatar = Random.nextInt(opponentAvatars.size)
            
            _uiState.update {
                it.copy(
                    currentScreen = AppScreen.GAME,
                    isMatchmaking = true,
                    matchmakingStep = "finding",
                    opponentName = name,
                    opponentAvatar = avatar
                )
            }
            
            delay(1500)
            _uiState.update { it.copy(matchmakingStep = "found") }
            delay(1000)
            _uiState.update { it.copy(matchmakingStep = "starting") }
            delay(2000)
            
            _uiState.update { it.copy(isMatchmaking = false) }
            startNewMatch()
        }
    }

    fun startNewMatch() {
        val deal = DominoEngine.deal()
        _uiState.update {
            it.copy(
                board = emptyList(),
                pool = deal.pool,
                humanHand = deal.humanHand,
                aiHand = deal.aiHand,
                turn = deal.starter,
                phase = Phase.PLAYING,
                humanScore = 0,
                aiScore = 0,
                lastRoundResult = null,
                matchWinner = null,
                humanMissing = emptySet(),
                passes = 0,
                selectedTile = null,
                pendingSides = emptyList(),
                round = 1,
                aiThinking = false
            )
        }
        checkAiTurn()
    }

    fun nextRound() {
        val currentState = _uiState.value
        val deal = DominoEngine.deal()
        _uiState.update {
            it.copy(
                board = emptyList(),
                pool = deal.pool,
                humanHand = deal.humanHand,
                aiHand = deal.aiHand,
                turn = deal.starter,
                phase = Phase.PLAYING,
                lastRoundResult = null,
                matchWinner = null,
                humanMissing = emptySet(),
                passes = 0,
                selectedTile = null,
                pendingSides = emptyList(),
                round = currentState.round + 1,
                aiThinking = false
            )
        }
        checkAiTurn()
    }

    fun selectTile(tile: Tile) {
        val currentState = _uiState.value
        if (currentState.turn != PlayerId.HUMAN || currentState.phase != Phase.PLAYING) return

        triggerSound("click")
        val sides = DominoEngine.playableSides(tile, currentState.board)

        if (sides.size == 1) {
            playTileInternal(PlayerId.HUMAN, tile, sides.first())
            _uiState.update { it.copy(selectedTile = null, pendingSides = emptyList()) }
        } else if (sides.size > 1) {
            _uiState.update {
                if (it.selectedTile?.id == tile.id) {
                    it.copy(selectedTile = null, pendingSides = emptyList())
                } else {
                    it.copy(selectedTile = tile, pendingSides = sides)
                }
            }
        }
    }

    fun chooseSideAndPlay(side: Side) {
        val selected = _uiState.value.selectedTile ?: return
        playTileInternal(PlayerId.HUMAN, selected, side)
        _uiState.update { it.copy(selectedTile = null, pendingSides = emptyList()) }
    }

    fun drawTile() {
        val currentState = _uiState.value
        if (currentState.turn != PlayerId.HUMAN || currentState.phase != Phase.PLAYING) return
        if (currentState.pool.isEmpty()) return
        if (DominoEngine.hasPlayable(currentState.humanHand, currentState.board)) return

        triggerSound("draw")
        val drawn = currentState.pool.first()
        val restPool = currentState.pool.drop(1)

        _uiState.update {
            it.copy(
                pool = restPool,
                humanHand = it.humanHand + drawn
            )
        }
    }

    fun pass() {
        val currentState = _uiState.value
        if (currentState.turn != PlayerId.HUMAN || currentState.phase != Phase.PLAYING) return
        if (currentState.pool.isNotEmpty()) return
        if (DominoEngine.hasPlayable(currentState.humanHand, currentState.board)) return

        triggerSound("click")
        val (left, right) = DominoEngine.boardEnds(currentState.board)
        val newMissing = currentState.humanMissing.toMutableSet()
        if (left != null) newMissing.add(left)
        if (right != null) newMissing.add(right)

        val newPasses = currentState.passes + 1

        if (newPasses >= 2) {
            finishRoundBlocked(newPasses, newMissing)
        } else {
            _uiState.update {
                it.copy(
                    passes = newPasses,
                    humanMissing = newMissing,
                    turn = PlayerId.AI,
                    selectedTile = null,
                    pendingSides = emptyList()
                )
            }
            checkAiTurn()
        }
    }

    private fun playTileInternal(player: PlayerId, tile: Tile, side: Side) {
        val currentState = _uiState.value
        val nextBoard = DominoEngine.placeTile(currentState.board, tile, side) ?: return

        triggerSound("place")

        val newHumanHand = if (player == PlayerId.HUMAN) currentState.humanHand.filter { it.id != tile.id } else currentState.humanHand
        val newAiHand = if (player == PlayerId.AI) currentState.aiHand.filter { it.id != tile.id } else currentState.aiHand

        val moverHand = if (player == PlayerId.HUMAN) newHumanHand else newAiHand

        if (moverHand.isEmpty()) {
            finishRoundOut(player, newHumanHand, newAiHand, nextBoard)
            return
        }

        val nextTurn = if (player == PlayerId.HUMAN) PlayerId.AI else PlayerId.HUMAN

        _uiState.update {
            it.copy(
                board = nextBoard,
                humanHand = newHumanHand,
                aiHand = newAiHand,
                turn = nextTurn,
                passes = 0
            )
        }

        if (nextTurn == PlayerId.AI) {
            checkAiTurn()
        }
    }

    private fun checkAiTurn() {
        if (_uiState.value.turn != PlayerId.AI || _uiState.value.phase != Phase.PLAYING) return

        viewModelScope.launch {
            _uiState.update { it.copy(aiThinking = true) }
            val delayMs = if (_uiState.value.settings.animations) 800L else 200L
            delay(delayMs)

            val currentState = _uiState.value
            if (currentState.turn != PlayerId.AI || currentState.phase != Phase.PLAYING) return@launch

            val move = DominoAI.chooseMove(currentState.aiHand, currentState.board, currentState.humanMissing)
            _uiState.update { it.copy(aiThinking = false) }

            if (move != null) {
                playTileInternal(PlayerId.AI, move.tile, move.side)
            } else if (currentState.pool.isNotEmpty()) {
                triggerSound("draw")
                val drawn = currentState.pool.first()
                val rest = currentState.pool.drop(1)
                _uiState.update {
                    it.copy(
                        pool = rest,
                        aiHand = it.aiHand + drawn
                    )
                }
                checkAiTurn()
            } else {
                val newPasses = currentState.passes + 1
                if (newPasses >= 2) {
                    finishRoundBlocked(newPasses, currentState.humanMissing)
                } else {
                    _uiState.update {
                        it.copy(
                            passes = newPasses,
                            turn = PlayerId.HUMAN
                        )
                    }
                }
            }
        }
    }

    private fun finishRoundOut(
        winner: PlayerId,
        humanHand: List<Tile>,
        aiHand: List<Tile>,
        finalBoard: List<PlacedTile>
    ) {
        val loserHand = if (winner == PlayerId.HUMAN) aiHand else humanHand
        val points = DominoEngine.handValue(loserHand)

        val newHumanScore = _uiState.value.humanScore + (if (winner == PlayerId.HUMAN) points else 0)
        val newAiScore = _uiState.value.aiScore + (if (winner == PlayerId.AI) points else 0)

        val matchWinner = when {
            newHumanScore >= DominoEngine.TARGET_SCORE -> PlayerId.HUMAN
            newAiScore >= DominoEngine.TARGET_SCORE -> PlayerId.AI
            else -> null
        }

        val result = RoundResult(winner = winner, points = points, reason = RoundReason.OUT)

        triggerSound(if (winner == PlayerId.HUMAN) "win" else "lose")

        _uiState.update {
            it.copy(
                board = finalBoard,
                humanHand = humanHand,
                aiHand = aiHand,
                humanScore = newHumanScore,
                aiScore = newAiScore,
                lastRoundResult = result,
                matchWinner = matchWinner,
                phase = if (matchWinner != null) Phase.MATCH_OVER else Phase.ROUND_OVER
            )
        }

        if (matchWinner != null) {
            recordMatchStats(won = matchWinner == PlayerId.HUMAN, finalScore = newHumanScore)
        }
    }

    private fun finishRoundBlocked(passes: Int, humanMissing: Set<Pip>) {
        val state = _uiState.value
        val humanPips = DominoEngine.handValue(state.humanHand)
        val aiPips = DominoEngine.handValue(state.aiHand)

        val winner: PlayerId? = when {
            humanPips < aiPips -> PlayerId.HUMAN
            aiPips < humanPips -> PlayerId.AI
            else -> null
        }

        val loserHand = when (winner) {
            PlayerId.HUMAN -> state.aiHand
            PlayerId.AI -> state.humanHand
            else -> emptyList()
        }
        val points = DominoEngine.handValue(loserHand)

        val newHumanScore = state.humanScore + (if (winner == PlayerId.HUMAN) points else 0)
        val newAiScore = state.aiScore + (if (winner == PlayerId.AI) points else 0)

        val matchWinner = when {
            newHumanScore >= DominoEngine.TARGET_SCORE -> PlayerId.HUMAN
            newAiScore >= DominoEngine.TARGET_SCORE -> PlayerId.AI
            else -> null
        }

        val result = RoundResult(winner = winner, points = points, reason = RoundReason.BLOCKED)

        if (winner != null) {
            triggerSound(if (winner == PlayerId.HUMAN) "win" else "lose")
        }

        _uiState.update {
            it.copy(
                passes = passes,
                humanMissing = humanMissing,
                humanScore = newHumanScore,
                aiScore = newAiScore,
                lastRoundResult = result,
                matchWinner = matchWinner,
                phase = if (matchWinner != null) Phase.MATCH_OVER else Phase.ROUND_OVER
            )
        }

        if (matchWinner != null) {
            recordMatchStats(won = matchWinner == PlayerId.HUMAN, finalScore = newHumanScore)
        }
    }

    private fun recordMatchStats(won: Boolean, finalScore: Int) {
        viewModelScope.launch {
            repository.updateStats(won, finalScore)
        }
    }

    fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            repository.saveProfile(_uiState.value.profile.name, settings.themeId, settings.languageId)
        }
        _uiState.update { it.copy(settings = settings) }
    }

    fun resetStats() {
        viewModelScope.launch {
            repository.resetStats()
        }
    }

    private fun triggerSound(event: String) {
        if (_uiState.value.settings.sound) {
            _uiState.update { it.copy(soundEvent = event) }
        }
    }

    fun clearSoundEvent() {
        _uiState.update { it.copy(soundEvent = null) }
    }
}
