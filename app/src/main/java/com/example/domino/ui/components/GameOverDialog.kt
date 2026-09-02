package com.example.domino.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domino.model.Phase
import com.example.domino.model.PlayerId
import com.example.domino.model.RoundReason
import com.example.domino.model.RoundResult
import com.example.domino.model.Translations
import com.example.domino.ui.theme.GoldPrimary
import com.example.domino.ui.theme.GoldVariant
import com.example.domino.ui.theme.SurfaceGreenCard
import com.example.domino.ui.theme.TextMuted
import androidx.compose.material3.MaterialTheme

@Composable
fun GameOverDialog(
    result: RoundResult,
    phase: Phase,
    matchWinner: PlayerId?,
    humanScore: Int,
    aiScore: Int,
    languageId: String,
    onNext: () -> Unit,
    onMenu: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .padding(4.dp)
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isMatchOver = phase == Phase.MATCH_OVER
                val headerKey = if (isMatchOver) "match_over" else "round_over"
                val headerText = Translations.getString(headerKey, languageId)

                val winnerText = when {
                    isMatchOver -> if (matchWinner == PlayerId.HUMAN) "🏆 ${Translations.getString("you_win", languageId)}" else "🤖 ${Translations.getString("you_lose", languageId)}"
                    result.winner == PlayerId.HUMAN -> "🎉 ${Translations.getString("you_win", languageId)}"
                    result.winner == PlayerId.AI -> "🤖 ${Translations.getString("you_lose", languageId)}"
                    else -> "🤝 ${Translations.getString("draw_game", languageId)}"
                }

                val reasonKey = when (result.reason) {
                    RoundReason.OUT -> if (result.winner == PlayerId.HUMAN) "reason_out_human" else "reason_out_ai"
                    RoundReason.BLOCKED -> if (result.winner == PlayerId.HUMAN) "reason_blocked_human" else "reason_blocked_ai"
                }
                val reasonText = Translations.getString(reasonKey, languageId)

                Text(
                    text = headerText,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = winnerText,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = reasonText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                if (result.winner != null && result.points > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${result.points} ${Translations.getString("points_won", languageId)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score board summary inside dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = Translations.getString("you", languageId), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$humanScore", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }

                    Text(text = "—", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = Translations.getString("opponent", languageId), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$aiScore", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onMenu,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dialog_menu_button")
                    ) {
                        Text(text = Translations.getString("menu", languageId), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("dialog_next_button")
                    ) {
                        val btnKey = if (isMatchOver) "new_game" else "next_round"
                        Text(
                            text = Translations.getString(btnKey, languageId),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
