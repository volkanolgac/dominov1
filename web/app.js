/**
 * Mini Domino - Universal Web App
 * State Management, Audio Synthesizer, UI Flow, and Game Controller.
 */

// ==================== 1. AUDIO SYNTHESIZER ====================
class SoundManager {
    constructor() {
        this.ctx = null;
        this.enabled = true;
    }

    init() {
        if (!this.ctx) {
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            if (AudioContext) {
                this.ctx = new AudioContext();
            }
        }
        if (this.ctx && this.ctx.state === 'suspended') {
            this.ctx.resume();
        }
    }

    play(event) {
        if (!this.enabled) return;
        this.init();
        if (!this.ctx) return;

        const now = this.ctx.currentTime;

        try {
            switch (event) {
                case 'click': {
                    const osc = this.ctx.createOscillator();
                    const gain = this.ctx.createGain();
                    osc.type = 'sine';
                    osc.frequency.setValueAtTime(800, now);
                    osc.frequency.exponentialRampToValueAtTime(400, now + 0.04);
                    gain.gain.setValueAtTime(0.2, now);
                    gain.gain.exponentialRampToValueAtTime(0.01, now + 0.04);
                    osc.connect(gain);
                    gain.connect(this.ctx.destination);
                    osc.start(now);
                    osc.stop(now + 0.04);
                    break;
                }
                case 'place': {
                    // Wood click knock sound
                    const osc = this.ctx.createOscillator();
                    const gain = this.ctx.createGain();
                    osc.type = 'triangle';
                    osc.frequency.setValueAtTime(320, now);
                    osc.frequency.exponentialRampToValueAtTime(90, now + 0.08);
                    gain.gain.setValueAtTime(0.45, now);
                    gain.gain.exponentialRampToValueAtTime(0.01, now + 0.08);
                    osc.connect(gain);
                    gain.connect(this.ctx.destination);
                    osc.start(now);
                    osc.stop(now + 0.08);
                    break;
                }
                case 'draw': {
                    // Shuffle slide click
                    const osc = this.ctx.createOscillator();
                    const gain = this.ctx.createGain();
                    osc.type = 'sine';
                    osc.frequency.setValueAtTime(450, now);
                    osc.frequency.exponentialRampToValueAtTime(700, now + 0.06);
                    gain.gain.setValueAtTime(0.25, now);
                    gain.gain.exponentialRampToValueAtTime(0.01, now + 0.06);
                    osc.connect(gain);
                    gain.connect(this.ctx.destination);
                    osc.start(now);
                    osc.stop(now + 0.06);
                    break;
                }
                case 'win': {
                    // Major triad fanfare (C5, E5, G5, C6)
                    const notes = [523.25, 659.25, 783.99, 1046.50];
                    notes.forEach((freq, idx) => {
                        const noteTime = now + idx * 0.1;
                        const osc = this.ctx.createOscillator();
                        const gain = this.ctx.createGain();
                        osc.type = 'triangle';
                        osc.frequency.setValueAtTime(freq, noteTime);
                        gain.gain.setValueAtTime(0.3, noteTime);
                        gain.gain.exponentialRampToValueAtTime(0.001, noteTime + 0.28);
                        osc.connect(gain);
                        gain.connect(this.ctx.destination);
                        osc.start(noteTime);
                        osc.stop(noteTime + 0.28);
                    });
                    break;
                }
                case 'lose': {
                    // Descending minor tone
                    const notes = [392.00, 311.13, 261.63];
                    notes.forEach((freq, idx) => {
                        const noteTime = now + idx * 0.12;
                        const osc = this.ctx.createOscillator();
                        const gain = this.ctx.createGain();
                        osc.type = 'sawtooth';
                        osc.frequency.setValueAtTime(freq, noteTime);
                        gain.gain.setValueAtTime(0.2, noteTime);
                        gain.gain.exponentialRampToValueAtTime(0.001, noteTime + 0.3);
                        osc.connect(gain);
                        gain.connect(this.ctx.destination);
                        osc.start(noteTime);
                        osc.stop(noteTime + 0.3);
                    });
                    break;
                }
            }
        } catch (_) {}
    }
}

const sounds = new SoundManager();

// ==================== 2. OPPONENT PROFILES ====================
const AI_OPPONENTS = [
    { name: "Atlas", avatar: "🤖" },
    { name: "Vortex", avatar: "⚡" },
    { name: "Nova", avatar: "🌟" },
    { name: "Titan", avatar: "🛡️" },
    { name: "Shadow", avatar: "👤" },
    { name: "Phoenix", avatar: "🔥" }
];

// ==================== 3. APP STATE ====================
const state = {
    screen: 'splash',
    settings: {
        languageId: 'tr',
        themeId: 'classic_green',
        sound: true
    },
    profile: {
        name: 'Oyuncu'
    },
    stats: {
        matches: 0,
        wins: 0,
        losses: 0,
        bestScore: 0
    },
    // Game variables
    game: {
        round: 1,
        humanScore: 0,
        aiScore: 0,
        board: [],
        pool: [],
        humanHand: [],
        aiHand: [],
        turn: 'HUMAN', // 'HUMAN' or 'AI'
        phase: 'PLAYING', // 'PLAYING', 'ROUND_OVER', 'MATCH_OVER'
        selectedTile: null,
        pendingSides: [],
        passes: 0,
        humanMissing: new Set(),
        opponent: AI_OPPONENTS[0],
        aiThinking: false
    }
};

// ==================== 4. LOCAL STORAGE ====================
function loadPersistedState() {
    try {
        const savedSettings = localStorage.getItem('mini_domino_settings');
        if (savedSettings) state.settings = { ...state.settings, ...JSON.parse(savedSettings) };

        const savedProfile = localStorage.getItem('mini_domino_profile');
        if (savedProfile) state.profile = { ...state.profile, ...JSON.parse(savedProfile) };

        const savedStats = localStorage.getItem('mini_domino_stats');
        if (savedStats) state.stats = { ...state.stats, ...JSON.parse(savedStats) };
    } catch (_) {}

    sounds.enabled = state.settings.sound;
    applyTheme(state.settings.themeId);
}

function saveState() {
    try {
        localStorage.setItem('mini_domino_settings', JSON.stringify(state.settings));
        localStorage.setItem('mini_domino_profile', JSON.stringify(state.profile));
        localStorage.setItem('mini_domino_stats', JSON.stringify(state.stats));
    } catch (_) {}
}

function applyTheme(themeId) {
    document.body.setAttribute('data-theme', themeId);
    state.settings.themeId = themeId;
    saveState();

    // Update active theme chip
    document.querySelectorAll('#theme-chips-container .chip-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-theme') === themeId);
    });
}

// ==================== 5. TRANSLATIONS & I18N ====================
function t(key) {
    const lang = state.settings.languageId;
    const dict = window.TRANSLATIONS[lang] || window.TRANSLATIONS['en'];
    return dict[key] || window.TRANSLATIONS['en'][key] || key;
}

function updateUiTexts() {
    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        el.textContent = t(key);
    });

    // Update Player Name in UI
    const pName = state.profile.name || t('you');
    document.getElementById('menu-player-name').textContent = pName;
    document.getElementById('mm-player-name').textContent = pName;
    document.getElementById('game-player-name').textContent = pName;
    document.getElementById('modal-p1-label').textContent = pName;

    // Update Stats Display
    document.getElementById('stat-matches').textContent = state.stats.matches;
    document.getElementById('stat-wins').textContent = state.stats.wins;
    document.getElementById('stat-losses').textContent = state.stats.losses;
    
    const winRate = state.stats.matches > 0 
        ? Math.round((state.stats.wins / state.stats.matches) * 100) 
        : 0;
    document.getElementById('stat-win-rate').textContent = `${winRate}%`;
    document.getElementById('stat-best-score').textContent = state.stats.bestScore;

    // Update Quick Sound button
    document.getElementById('btn-quick-sound').textContent = state.settings.sound ? '🔊' : '🔇';

    // Update Language Chips
    document.querySelectorAll('#lang-chips-container .chip-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-lang') === state.settings.languageId);
    });
}

function setLanguage(langCode) {
    if (window.TRANSLATIONS[langCode]) {
        state.settings.languageId = langCode;
        saveState();
        updateUiTexts();
    }
}

// ==================== 6. DOMINO TILE ELEMENT FACTORY ====================
const PIP_POSITIONS = {
    0: [],
    1: [[1, 1]],
    2: [[0, 0], [2, 2]],
    3: [[0, 0], [1, 1], [2, 2]],
    4: [[0, 0], [0, 2], [2, 0], [2, 2]],
    5: [[0, 0], [0, 2], [1, 1], [2, 0], [2, 2]],
    6: [[0, 0], [0, 2], [1, 0], [1, 2], [2, 0], [2, 2]]
};

function createHalfElement(pips) {
    const half = document.createElement('div');
    half.className = 'tile-half';

    const activePositions = new Set(
        (PIP_POSITIONS[pips] || []).map(([r, c]) => `${r}-${c}`)
    );

    for (let r = 0; r < 3; r++) {
        for (let c = 0; c < 3; c++) {
            const cell = document.createElement('div');
            if (activePositions.has(`${r}-${c}`)) {
                const dot = document.createElement('div');
                dot.className = 'pip-dot';
                cell.appendChild(dot);
            }
            half.appendChild(cell);
        }
    }
    return half;
}

function renderDominoTile(a, b, orientation = 'vertical', size = 'medium', isPlayable = false, isSelected = false, onClick = null) {
    const tileDiv = document.createElement('div');
    tileDiv.className = `domino-tile ${orientation} ${size}`;
    if (isPlayable) tileDiv.classList.add('playable');
    if (isSelected) tileDiv.classList.add('selected');

    const half1 = createHalfElement(a);
    const divider = document.createElement('div');
    divider.className = 'tile-divider';
    const half2 = createHalfElement(b);

    tileDiv.appendChild(half1);
    tileDiv.appendChild(divider);
    tileDiv.appendChild(half2);

    if (onClick) {
        tileDiv.addEventListener('click', (e) => {
            e.stopPropagation();
            onClick();
        });
    }

    return tileDiv;
}

// ==================== 7. SCREEN NAVIGATION ====================
function showScreen(screenId) {
    document.querySelectorAll('.screen-view').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(screenId);
    if (target) {
        target.classList.add('active');
        state.screen = screenId;
    }
}

// ==================== 8. GAMEPLAY CONTROLLER ====================
function startMatchmaking() {
    sounds.play('click');
    const overlay = document.getElementById('matchmaking-overlay');
    overlay.classList.add('active');

    // Pick random AI opponent
    const randomOpp = AI_OPPONENTS[Math.floor(Math.random() * AI_OPPONENTS.length)];
    state.game.opponent = randomOpp;

    document.getElementById('mm-opponent-name').textContent = randomOpp.name;
    document.getElementById('mm-opponent-avatar').textContent = randomOpp.avatar;
    document.getElementById('mm-status').textContent = t('finding_opponent');

    setTimeout(() => {
        document.getElementById('mm-status').textContent = t('opponent_found');
        setTimeout(() => {
            document.getElementById('mm-status').textContent = t('game_starting');
            setTimeout(() => {
                overlay.classList.remove('active');
                startNewMatch();
            }, 1000);
        }, 800);
    }, 1200);
}

function startNewMatch() {
    state.game.round = 1;
    state.game.humanScore = 0;
    state.game.aiScore = 0;
    startNewRound();
    showScreen('game-screen');
}

function startNewRound() {
    const deal = DominoEngine.deal();
    state.game.board = [];
    state.game.pool = deal.pool;
    state.game.humanHand = deal.humanHand;
    state.game.aiHand = deal.aiHand;
    state.game.turn = deal.starter;
    state.game.phase = 'PLAYING';
    state.game.selectedTile = null;
    state.game.pendingSides = [];
    state.game.passes = 0;
    state.game.humanMissing = new Set();
    state.game.aiThinking = false;

    renderGameUi();
    checkAiTurn();
}

function renderGameUi() {
    const g = state.game;

    // Scores & Meta
    document.getElementById('game-round-badge').textContent = `${t('round_over').split(' ')[0].toUpperCase()} ${g.round}`;
    document.getElementById('game-player-score').textContent = g.humanScore;
    document.getElementById('game-opp-score').textContent = g.aiScore;
    document.getElementById('game-opp-name').textContent = g.opponent.name;
    document.getElementById('game-opp-avatar').textContent = g.opponent.avatar;
    document.getElementById('game-pool-count').textContent = g.pool.length;

    // Opponent Hand (Tiles Back)
    const oppHandEl = document.getElementById('opponent-hand');
    oppHandEl.innerHTML = '';
    for (let i = 0; i < g.aiHand.length; i++) {
        const back = document.createElement('div');
        back.className = 'opponent-tile-back';
        oppHandEl.appendChild(back);
    }

    // Board Tiles
    const boardTrack = document.getElementById('board-track');
    const boardHint = document.getElementById('board-empty-hint');
    boardTrack.innerHTML = '';

    if (g.board.length === 0) {
        boardHint.style.display = 'block';
    } else {
        boardHint.style.display = 'none';
        g.board.forEach(placed => {
            const orientation = placed.isDouble ? 'vertical' : 'horizontal';
            const tileEl = renderDominoTile(placed.orientedA, placed.orientedB, orientation, 'medium');
            boardTrack.appendChild(tileEl);
        });

        // Auto scroll board to ends
        const container = document.getElementById('board-container');
        setTimeout(() => {
            container.scrollLeft = container.scrollWidth;
        }, 50);
    }

    // Side Chooser Dialog
    const sideBanner = document.getElementById('side-chooser-banner');
    if (g.pendingSides.length > 0) {
        sideBanner.style.display = 'flex';
        document.getElementById('btn-choose-left').style.display = g.pendingSides.includes('LEFT') ? 'block' : 'none';
        document.getElementById('btn-choose-right').style.display = g.pendingSides.includes('RIGHT') ? 'block' : 'none';
    } else {
        sideBanner.style.display = 'none';
    }

    // Player Hand Tiles
    const playerHandEl = document.getElementById('player-hand');
    playerHandEl.innerHTML = '';

    const isHumanTurn = g.turn === 'HUMAN' && g.phase === 'PLAYING';
    const hasPlayable = DominoEngine.hasPlayable(g.humanHand, g.board);

    g.humanHand.forEach(tile => {
        const sides = DominoEngine.playableSides(tile, g.board);
        const playable = isHumanTurn && sides.length > 0;
        const selected = g.selectedTile && g.selectedTile.id === tile.id;

        const tileEl = renderDominoTile(
            tile.a, tile.b,
            'vertical', 'large',
            playable, selected,
            () => handleTileClick(tile)
        );
        playerHandEl.appendChild(tileEl);
    });

    // Control Buttons
    const canDraw = isHumanTurn && !hasPlayable && g.pool.length > 0;
    const canPass = isHumanTurn && !hasPlayable && g.pool.length === 0;

    document.getElementById('btn-game-draw').disabled = !canDraw;
    document.getElementById('btn-game-pass').disabled = !canPass;

    // Turn & Hint Banner
    const turnBadge = document.getElementById('turn-badge');
    const turnText = document.getElementById('turn-text');
    const hintText = document.getElementById('hint-text');

    if (g.phase !== 'PLAYING') {
        turnText.textContent = t('round_over');
        hintText.textContent = '';
    } else if (isHumanTurn) {
        turnText.textContent = t('your_turn');
        turnBadge.style.color = 'var(--gold-primary)';
        if (g.selectedTile) {
            hintText.textContent = t('hint_choose_side');
        } else if (hasPlayable) {
            hintText.textContent = t('hint_select_tile');
        } else if (canDraw) {
            hintText.textContent = t('hint_draw');
        } else {
            hintText.textContent = t('hint_pass');
        }
    } else {
        turnText.textContent = `${g.opponent.name} ${t('thinking')}`;
        turnBadge.style.color = '#38BDF8';
        hintText.textContent = t('hint_thinking');
    }
}

function handleTileClick(tile) {
    const g = state.game;
    if (g.turn !== 'HUMAN' || g.phase !== 'PLAYING') return;

    sounds.play('click');
    const sides = DominoEngine.playableSides(tile, g.board);

    if (sides.length === 1) {
        playTile('HUMAN', tile, sides[0]);
        g.selectedTile = null;
        g.pendingSides = [];
        renderGameUi();
    } else if (sides.length > 1) {
        if (g.selectedTile && g.selectedTile.id === tile.id) {
            g.selectedTile = null;
            g.pendingSides = [];
        } else {
            g.selectedTile = tile;
            g.pendingSides = sides;
        }
        renderGameUi();
    }
}

function chooseSideAndPlay(side) {
    const g = state.game;
    if (!g.selectedTile) return;
    playTile('HUMAN', g.selectedTile, side);
    g.selectedTile = null;
    g.pendingSides = [];
    renderGameUi();
}

function handleDraw() {
    const g = state.game;
    if (g.turn !== 'HUMAN' || g.phase !== 'PLAYING') return;
    if (g.pool.length === 0) return;
    if (DominoEngine.hasPlayable(g.humanHand, g.board)) return;

    sounds.play('draw');
    const drawn = g.pool.shift();
    g.humanHand.push(drawn);
    renderGameUi();
}

function handlePass() {
    const g = state.game;
    if (g.turn !== 'HUMAN' || g.phase !== 'PLAYING') return;
    if (g.pool.length > 0) return;
    if (DominoEngine.hasPlayable(g.humanHand, g.board)) return;

    sounds.play('click');
    const { left, right } = DominoEngine.boardEnds(g.board);
    if (left !== null) g.humanMissing.add(left);
    if (right !== null) g.humanMissing.add(right);

    g.passes++;
    if (g.passes >= 2) {
        finishRoundBlocked();
    } else {
        g.turn = 'AI';
        renderGameUi();
        checkAiTurn();
    }
}

function playTile(player, tile, side) {
    const g = state.game;
    const nextBoard = DominoEngine.placeTile(g.board, tile, side);
    if (!nextBoard) return;

    sounds.play('place');
    g.board = nextBoard;
    g.passes = 0;

    if (player === 'HUMAN') {
        g.humanHand = g.humanHand.filter(t => t.id !== tile.id);
        if (g.humanHand.length === 0) {
            finishRoundOut('HUMAN');
            return;
        }
        g.turn = 'AI';
        renderGameUi();
        checkAiTurn();
    } else {
        g.aiHand = g.aiHand.filter(t => t.id !== tile.id);
        if (g.aiHand.length === 0) {
            finishRoundOut('AI');
            return;
        }
        g.turn = 'HUMAN';
        renderGameUi();
    }
}

function checkAiTurn() {
    const g = state.game;
    if (g.turn !== 'AI' || g.phase !== 'PLAYING') return;

    g.aiThinking = true;
    renderGameUi();

    setTimeout(() => {
        if (g.turn !== 'AI' || g.phase !== 'PLAYING') return;

        const move = DominoAI.chooseMove(g.aiHand, g.board, g.humanMissing);
        g.aiThinking = false;

        if (move) {
            playTile('AI', move.tile, move.side);
        } else if (g.pool.length > 0) {
            sounds.play('draw');
            const drawn = g.pool.shift();
            g.aiHand.push(drawn);
            renderGameUi();
            checkAiTurn();
        } else {
            g.passes++;
            if (g.passes >= 2) {
                finishRoundBlocked();
            } else {
                g.turn = 'HUMAN';
                renderGameUi();
            }
        }
    }, 850);
}

function finishRoundOut(winner) {
    const g = state.game;
    const loserHand = winner === 'HUMAN' ? g.aiHand : g.humanHand;
    const points = DominoEngine.handValue(loserHand);

    if (winner === 'HUMAN') g.humanScore += points;
    else g.aiScore += points;

    showRoundModal(winner, points, 'OUT');
}

function finishRoundBlocked() {
    const g = state.game;
    const humanPips = DominoEngine.handValue(g.humanHand);
    const aiPips = DominoEngine.handValue(g.aiHand);

    let winner = null;
    let points = 0;

    if (humanPips < aiPips) {
        winner = 'HUMAN';
        points = DominoEngine.handValue(g.aiHand);
        g.humanScore += points;
    } else if (aiPips < humanPips) {
        winner = 'AI';
        points = DominoEngine.handValue(g.humanHand);
        g.aiScore += points;
    }

    showRoundModal(winner, points, 'BLOCKED');
}

function showRoundModal(winner, points, reason) {
    const g = state.game;
    const isMatchOver = g.humanScore >= DominoEngine.TARGET_SCORE || g.aiScore >= DominoEngine.TARGET_SCORE;
    g.phase = isMatchOver ? 'MATCH_OVER' : 'ROUND_OVER';

    if (winner === 'HUMAN') sounds.play('win');
    else if (winner === 'AI') sounds.play('lose');

    const modal = document.getElementById('round-over-modal');
    const titleEl = document.getElementById('modal-result-title');
    const iconEl = document.getElementById('modal-result-icon');
    const descEl = document.getElementById('modal-result-reason');
    const pointsEl = document.getElementById('modal-points-badge');
    const nextBtn = document.getElementById('btn-modal-next-round');

    document.getElementById('modal-p1-score').textContent = g.humanScore;
    document.getElementById('modal-p2-score').textContent = g.aiScore;

    if (winner === 'HUMAN') {
        iconEl.textContent = '🏆';
        titleEl.textContent = isMatchOver ? t('you_win') : t('you_win');
        descEl.textContent = reason === 'OUT' ? t('reason_out_human') : t('reason_blocked_human');
        pointsEl.textContent = `+${points} ${t('points_won')}`;
    } else if (winner === 'AI') {
        iconEl.textContent = '💀';
        titleEl.textContent = isMatchOver ? t('you_lose') : t('you_lose');
        descEl.textContent = reason === 'OUT' ? t('reason_out_ai') : t('reason_blocked_ai');
        pointsEl.textContent = `+${points} Puan (${g.opponent.name})`;
    } else {
        iconEl.textContent = '🤝';
        titleEl.textContent = t('draw_game');
        descEl.textContent = "Her iki oyuncunun taş toplamı eşit.";
        pointsEl.textContent = `0 ${t('points_won')}`;
    }

    nextBtn.textContent = isMatchOver ? t('new_game') : t('next_round');

    if (isMatchOver) {
        state.stats.matches++;
        if (g.humanScore > g.aiScore) {
            state.stats.wins++;
        } else {
            state.stats.losses++;
        }
        if (g.humanScore > state.stats.bestScore) {
            state.stats.bestScore = g.humanScore;
        }
        saveState();
        updateUiTexts();
    }

    modal.classList.add('active');
}

// ==================== 9. EVENT LISTENERS ====================
document.addEventListener('DOMContentLoaded', () => {
    loadPersistedState();
    updateUiTexts();

    // Splash tap to continue or auto transition
    const splash = document.getElementById('splash-screen');
    const finishSplash = () => {
        if (state.screen === 'splash') {
            sounds.init();
            showScreen('main-menu-screen');
        }
    };
    splash.addEventListener('click', finishSplash);
    setTimeout(finishSplash, 2000);

    // Main Menu Buttons
    document.getElementById('btn-play').addEventListener('click', startMatchmaking);
    document.getElementById('btn-how-to-play').addEventListener('click', () => {
        sounds.play('click');
        showScreen('how-to-play-screen');
    });
    document.getElementById('btn-settings').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('setting-name-input').value = state.profile.name;
        showScreen('settings-screen');
    });
    document.getElementById('btn-open-settings').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('setting-name-input').value = state.profile.name;
        showScreen('settings-screen');
    });

    // Quick sound toggle
    document.getElementById('btn-quick-sound').addEventListener('click', () => {
        state.settings.sound = !state.settings.sound;
        sounds.enabled = state.settings.sound;
        if (state.settings.sound) sounds.play('click');
        document.getElementById('setting-sound-toggle').checked = state.settings.sound;
        saveState();
        updateUiTexts();
    });

    // Back Buttons
    document.getElementById('btn-back-how-to-play').addEventListener('click', () => {
        sounds.play('click');
        showScreen('main-menu-screen');
    });
    document.getElementById('btn-back-settings').addEventListener('click', () => {
        sounds.play('click');
        showScreen('main-menu-screen');
    });

    // Settings Inputs
    const nameInput = document.getElementById('setting-name-input');
    nameInput.addEventListener('change', () => {
        const val = nameInput.value.trim();
        if (val) {
            state.profile.name = val;
            saveState();
            updateUiTexts();
        }
    });

    const soundToggle = document.getElementById('setting-sound-toggle');
    soundToggle.checked = state.settings.sound;
    soundToggle.addEventListener('change', () => {
        state.settings.sound = soundToggle.checked;
        sounds.enabled = state.settings.sound;
        if (sounds.enabled) sounds.play('click');
        saveState();
        updateUiTexts();
    });

    // Theme Chips
    document.querySelectorAll('#theme-chips-container .chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            sounds.play('click');
            const th = btn.getAttribute('data-theme');
            applyTheme(th);
        });
    });

    // Language Chips
    document.querySelectorAll('#lang-chips-container .chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            sounds.play('click');
            const lang = btn.getAttribute('data-lang');
            setLanguage(lang);
        });
    });

    // Reset Stats
    document.getElementById('btn-reset-stats').addEventListener('click', () => {
        if (confirm(t('reset_stats_confirm'))) {
            state.stats = { matches: 0, wins: 0, losses: 0, bestScore: 0 };
            saveState();
            updateUiTexts();
            sounds.play('click');
        }
    });

    // Game Actions
    document.getElementById('btn-game-draw').addEventListener('click', handleDraw);
    document.getElementById('btn-game-pass').addEventListener('click', handlePass);
    document.getElementById('btn-game-menu').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('quit-confirm-modal').classList.add('active');
    });

    // Side Chooser
    document.getElementById('btn-choose-left').addEventListener('click', () => chooseSideAndPlay('LEFT'));
    document.getElementById('btn-choose-right').addEventListener('click', () => chooseSideAndPlay('RIGHT'));

    // Round Modal Buttons
    document.getElementById('btn-modal-next-round').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('round-over-modal').classList.remove('active');
        if (state.game.phase === 'MATCH_OVER') {
            startNewMatch();
        } else {
            state.game.round++;
            startNewRound();
        }
    });
    document.getElementById('btn-modal-menu').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('round-over-modal').classList.remove('active');
        showScreen('main-menu-screen');
    });

    // Quit Modal Buttons
    document.getElementById('btn-quit-yes').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('quit-confirm-modal').classList.remove('active');
        showScreen('main-menu-screen');
    });
    document.getElementById('btn-quit-no').addEventListener('click', () => {
        sounds.play('click');
        document.getElementById('quit-confirm-modal').classList.remove('active');
    });
});
