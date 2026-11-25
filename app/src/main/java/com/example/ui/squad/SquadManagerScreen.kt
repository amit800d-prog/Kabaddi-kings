package com.example.ui.squad

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KabaddiPlayer
import com.example.data.CustomSquadPlayer
import com.example.data.GameProgressRepository
import com.example.data.LineupPosition
import com.example.ui.theme.AlwaysWhite
import com.example.ui.theme.BodyFontFamily
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.ClayOrange
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.MatSlate
import com.example.ui.theme.PlainWhite
import com.example.ui.theme.RaidCyan
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StadiumNavy

@Composable
fun SquadManagerScreen(
    sharedPrefs: SharedPreferences,
    onPlayWithSquad: (List<KabaddiPlayer>) -> Unit,
    onBack: () -> Unit
) {
    val squad by remember { mutableStateOf(GameProgressRepository.getCustomSquad(sharedPrefs)) }
    var lineup by remember { mutableStateOf(GameProgressRepository.getStartingLineup(sharedPrefs, squad)) }
    var selectedSlot by remember { mutableStateOf<LineupPosition?>(null) }

    val byName = remember(squad) { squad.associateBy { it.name } }
    val startingPlayers = remember(lineup, byName) {
        LineupPosition.entries.mapNotNull { pos -> lineup[pos]?.let { byName[it] } }
    }
    val benchPlayers = remember(squad, lineup) {
        val starterNames = lineup.values.toSet()
        squad.filter { it.name !in starterNames }
    }

    val avgAttack = remember(startingPlayers) {
        if (startingPlayers.isEmpty()) 0 else startingPlayers.map { it.raidingAbility }.average().toInt()
    }
    val avgDefense = remember(startingPlayers) {
        if (startingPlayers.isEmpty()) 0 else startingPlayers.map { it.defendingAbility }.average().toInt()
    }

    fun swapPlayerIntoSlot(slot: LineupPosition, chosenPlayer: CustomSquadPlayer) {
        val updated = lineup.toMutableMap()
        val oldOwnerSlot = updated.entries.firstOrNull { it.value == chosenPlayer.name }?.key
        val previousInSlot = updated[slot]
        updated[slot] = chosenPlayer.name
        if (oldOwnerSlot != null && previousInSlot != null) {
            updated[oldOwnerSlot] = previousInSlot
        }
        lineup = updated
        GameProgressRepository.saveStartingLineup(sharedPrefs, updated)
        selectedSlot = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("squad_manager_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_squad_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SQUAD & LINEUP",
                    fontFamily = DisplayFontFamily,
                    color = ChampionGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Kabaddi Kings XI • ${squad.size} Players",
                    fontFamily = BodyFontFamily,
                    color = SlateMuted,
                    fontSize = 11.sp
                )
            }

            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Team Chemistry & Rating Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MatSlate),
            border = BorderStroke(1.dp, Color(0xFF2E395E)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                RatingMetric("ATTACK", "$avgAttack", ClayOrange)
                RatingMetric("DEFENSE", "$avgDefense", RaidCyan)
                RatingMetric("STARTERS", "${startingPlayers.size}/7", ChampionGold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (selectedSlot == null)
                "Tap a Starting 7 position to swap or customize lineup:"
            else
                "Select a player below to assign to ${selectedSlot!!.label}:",
            fontFamily = BodyFontFamily,
            color = if (selectedSlot == null) SlateMuted else ChampionGold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LineupPosition.entries) { pos ->
                val player = lineup[pos]?.let { byName[it] }
                val isSelected = selectedSlot == pos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            selectedSlot = if (isSelected) null else pos
                        },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) ClayOrange.copy(alpha = 0.2f) else MatSlate),
                    border = BorderStroke(1.5.dp, if (isSelected) ClayOrange else Color(0xFF2E395E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = StadiumNavy,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ChampionGold.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = pos.shortLabel,
                                fontFamily = DisplayFontFamily,
                                color = ChampionGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player?.name ?: "Unassigned",
                                fontFamily = BodyFontFamily,
                                color = PlainWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${pos.label} • ${player?.role?.name?.replace('_', ' ') ?: "-"}",
                                fontFamily = BodyFontFamily,
                                color = SlateMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (player != null) {
                            Text("R:${player.raidingAbility} D:${player.defendingAbility}", fontFamily = BodyFontFamily, color = RaidCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = if (isSelected) ClayOrange else SlateMuted)
                    }
                }
            }

            if (benchPlayers.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SUBSTITUTES & BENCH (${benchPlayers.size})",
                        fontFamily = DisplayFontFamily,
                        color = PlainWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(benchPlayers) { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = selectedSlot != null) {
                                selectedSlot?.let { swapPlayerIntoSlot(it, sub) }
                            },
                        colors = CardDefaults.cardColors(containerColor = StadiumNavy),
                        border = BorderStroke(1.dp, if (selectedSlot != null) RaidCyan else Color(0xFF2E395E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sub.name, fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "${sub.role.name.replace('_', ' ')} • Stamina ${sub.stamina}%",
                                    fontFamily = BodyFontFamily,
                                    color = SlateMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = if (selectedSlot != null) "TAP TO SWAP" else "R:${sub.raidingAbility} D:${sub.defendingAbility}",
                                fontFamily = BodyFontFamily,
                                color = if (selectedSlot != null) RaidCyan else ChampionGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val starters = GameProgressRepository.getStartingSevenPlayers(sharedPrefs)
                onPlayWithSquad(starters)
            },
            colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_play_custom_squad")
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("PLAY MATCH WITH STARTING 7", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun RatingMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontFamily = DisplayFontFamily, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
        Text(label, fontFamily = BodyFontFamily, color = SlateMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}
