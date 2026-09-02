package com.example.domino

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domino.model.AppScreen
import com.example.domino.model.Phase
import com.example.domino.model.Translations
import com.example.domino.ui.screens.GameScreen
import com.example.domino.ui.screens.HowToPlayScreen
import com.example.domino.ui.screens.MainMenuScreen
import com.example.domino.ui.screens.SettingsScreen
import com.example.domino.ui.theme.DominoTheme
import com.example.domino.viewmodel.DominoViewModel

class MainActivity : ComponentActivity() {
    private var toneGenerator: ToneGenerator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (_: Exception) {
            // Audio tone generator fallback
        }

        setContent {
            val viewModel: DominoViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()

            DominoTheme(themeId = state.settings.themeId) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DominoApp(
                        modifier = Modifier.padding(innerPadding),
                        onPlaySound = { sound -> playSoundTone(sound) },
                        viewModel = viewModel
                    )
                }
                
                if (state.showNameEntry) {
                    NameEntryDialog(
                        languageId = state.settings.languageId,
                        onSave = { name -> viewModel.saveUserName(name) }
                    )
                }
            }
        }
    }

    private fun playSoundTone(event: String) {
        try {
            when (event) {
                "click" -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                "place" -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 80)
                "draw" -> toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 70)
                "win" -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 250)
                "lose" -> toneGenerator?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 250)
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator?.release()
        toneGenerator = null
    }
}

@Composable
fun NameEntryDialog(languageId: String, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = { },
        title = { Text(Translations.getString("welcome_title", languageId)) },
        text = {
            Column {
                Text(Translations.getString("welcome_desc", languageId))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(Translations.getString("player_name", languageId)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name) },
                enabled = name.isNotBlank()
            ) {
                Text(Translations.getString("save_and_start", languageId))
            }
        }
    )
}

@Composable
fun DominoApp(
    modifier: Modifier = Modifier,
    onPlaySound: (String) -> Unit,
    viewModel: DominoViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.soundEvent) {
        val sound = state.soundEvent
        if (sound != null) {
            onPlaySound(sound)
            viewModel.clearSoundEvent()
        }
    }

    when (state.currentScreen) {
        AppScreen.MAIN_MENU -> {
            MainMenuScreen(
                stats = state.stats,
                languageId = state.settings.languageId,
                themeId = state.settings.themeId,
                onPlay = { viewModel.navigateTo(AppScreen.GAME) },
                onHowTo = { viewModel.navigateTo(AppScreen.HOW_TO_PLAY) },
                onSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                modifier = modifier
            )
        }
        AppScreen.GAME -> {
            GameScreen(
                state = state,
                onSelectTile = { tile ->
                    viewModel.selectTile(tile)
                },
                onChooseSide = { side -> viewModel.chooseSideAndPlay(side) },
                onDraw = { viewModel.drawTile() },
                onPass = { viewModel.pass() },
                onMenu = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                onNextRoundOrMatch = {
                    if (state.phase == Phase.MATCH_OVER) {
                        viewModel.startNewMatch()
                    } else {
                        viewModel.nextRound()
                    }
                },
                modifier = modifier
            )
        }
        AppScreen.HOW_TO_PLAY -> {
            HowToPlayScreen(
                languageId = state.settings.languageId,
                themeId = state.settings.themeId,
                onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                modifier = modifier
            )
        }
        AppScreen.SETTINGS -> {
            SettingsScreen(
                settings = state.settings,
                profileName = state.profile.name,
                onUpdateName = { newName -> viewModel.saveUserName(newName) },
                onUpdateSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                onResetStats = { viewModel.resetStats() },
                onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                modifier = modifier
            )
        }
    }
}
