package com.example.ui.career

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KabaddiPlayer
import com.example.PKL_SEASON_10_TEAMS
import com.example.PklTeam
import com.example.audio.ArenaSoundEngine
import com.example.data.GameProgressRepository
import com.example.ui.theme.AlwaysDark
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
fun CareerModeScreen(
    sharedPrefs: SharedPreferences,
    coins: Int,
    onCurrencyUpdated: () -> Unit,
    onLaunchCareerMatch: (yourTeam: PklTeam, opponentTeam: PklTeam, starters: List<KabaddiPlayer>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var career by remember { mutableStateOf(GameProgressRepository.getCareerState(sharedPrefs)) }
    var squad by remember { mutableStateOf(GameProgressRepository.getCustomSquad(sharedPrefs)) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var statusMsg by remember { mutableStateOf<String?>(null) }

    val nextOpponentName = career.fixtureOpponents.getOrNull(career.roundIndex)
    val sortedStandings = remember(career) {
        career.standings.sortedWith(compareByDescending<com.example.data.CareerTeamStanding> { it.points }.thenByDescending { it.won })
    }

    fun simulateRound(opponentName: String) {
        val avgPower = squad.take(7).map { (it.raidingAbility + it.defendingAbility) / 2 }.average().toInt()
        val winProb = ((avgPower - 50) * 1.4f).coerceIn(35f, 82f)
        val userWins = (1..100).random() <= winProb
        val userPts = if (userWins) (32..46).random() else (22..31).random()
        val oppPts = if (userWins) (20..30).random() else (33..44).random()

        val outcome = GameProgressRepository.recordCareerMatchResult(
            prefs = sharedPrefs,
            opponentName = opponentName,
            userScore = userPts,
            oppScore = oppPts,
            awardMatchCoinsAndQuests = true
        )
        career = outcome.updatedState
        onCurrencyUpdated()

        if (outcome.wonTrophy) ArenaSoundEngine.playSuperRaid(context) else ArenaSoundEngine.playWhistle(context)
        statusMsg = if (outcome.wonTrophy) {
            "CHAMPIONS! Won Season ${career.seasonNumber} Trophy! +${outcome.rewardCoins} Coins & +${outcome.rewardGems} Gems!"
        } else {
            "Result: ${career.clubName} $userPts - $oppPts $opponentName (+${outcome.rewardCoins} Coins)"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("career_mode_screen")
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
                    .testTag("btn_career_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("FRANCHISE CAREER", fontFamily = DisplayFontFamily, color = ChampionGold, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text("Season ${career.seasonNumber} • Coins: $coins", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 11.sp)
            }

            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MatSlate,
            contentColor = ChampionGold,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                Text("SEASON", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
                Text("UPGRADE", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = {
                Text("TABLE", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
        }

        if (statusMsg != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = StadiumNavy,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ChampionGold)
            ) {
                Text(statusMsg!!, fontFamily = BodyFontFamily, color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Season Hub + Hall of Fame
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.5.dp, ClayOrange.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (career.seasonCompleted) "SEASON ${career.seasonNumber} COMPLETED" else "MATCHDAY ${career.roundIndex + 1} OF ${career.fixtureOpponents.size}",
                                    fontFamily = BodyFontFamily,
                                    color = RaidCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (!career.seasonCompleted && nextOpponentName != null) {
                                    Text("${career.clubName} vs $nextOpponentName", fontFamily = DisplayFontFamily, color = PlainWhite, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = {
                                                val oppTeam = PKL_SEASON_10_TEAMS.firstOrNull { it.name == nextOpponentName } ?: PKL_SEASON_10_TEAMS.first()
                                                val starters = GameProgressRepository.getStartingSevenPlayers(sharedPrefs)
                                                onLaunchCareerMatch(GameProgressRepository.getCustomTeam(), oppTeam, starters)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f).height(50.dp).testTag("btn_career_play_live")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("PLAY LIVE", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { simulateRound(nextOpponentName) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ChampionGold, contentColor = AlwaysDark),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f).height(50.dp).testTag("btn_career_sim_match")
                                        ) {
                                            Text("SIM MATCH", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Text("Ready for Season ${career.seasonNumber + 1}!", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            val nextSeason = GameProgressRepository.createSeasonState(
                                                prefs = sharedPrefs,
                                                seasonNumber = career.seasonNumber + 1,
                                                trophiesWon = career.trophiesWon,
                                                totalWins = career.totalCareerWins,
                                                bestFinish = career.bestFinish
                                            )
                                            career = nextSeason
                                            GameProgressRepository.saveCareerState(sharedPrefs, nextSeason)
                                            statusMsg = "Started Season ${nextSeason.seasonNumber}!"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RaidCyan, contentColor = AlwaysDark),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().height(50.dp)
                                    ) {
                                        Text("START SEASON ${career.seasonNumber + 1}", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }

                    // Hall of Fame Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.dp, ChampionGold.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ChampionGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("FRANCHISE HALL OF FAME", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    HofItem("TROPHIES", "${career.trophiesWon}")
                                    HofItem("CAREER WINS", "${career.totalCareerWins}")
                                    HofItem("BEST", career.bestFinish)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Player Upgrade Training
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(squad) { p ->
                        val upgradeCost = 350 + (p.upgradeLevel * 150)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.dp, Color(0xFF2E395E)),
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
                                    Text("${p.name} (Lv.${p.upgradeLevel})", fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Raid: ${p.raidingAbility} • Def: ${p.defendingAbility}", fontFamily = BodyFontFamily, color = RaidCyan, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        if (GameProgressRepository.spendCurrency(sharedPrefs, upgradeCost, 0)) {
                                            val updated = squad.map {
                                                if (it.name == p.name) {
                                                    it.copy(
                                                        raidingAbility = (it.raidingAbility + 2).coerceAtMost(99),
                                                        defendingAbility = (it.defendingAbility + 2).coerceAtMost(99),
                                                        upgradeLevel = it.upgradeLevel + 1
                                                    )
                                                } else it
                                            }
                                            squad = updated
                                            GameProgressRepository.saveCustomSquad(sharedPrefs, updated)
                                            onCurrencyUpdated()
                                            ArenaSoundEngine.playRaidSuccess(context)
                                            statusMsg = "Upgraded ${p.name} to Lv.${p.upgradeLevel + 1}!"
                                        } else {
                                            statusMsg = "Need $upgradeCost Coins to upgrade!"
                                        }
                                    },
                                    enabled = coins >= upgradeCost && (p.raidingAbility < 99 || p.defendingAbility < 99),
                                    colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${upgradeCost}C", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Standings Table
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(sortedStandings) { idx, st ->
                        val isUser = st.teamName == career.clubName
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isUser) ClayOrange.copy(alpha = 0.2f) else MatSlate),
                            border = BorderStroke(1.dp, if (isUser) ChampionGold else Color(0xFF2E395E)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#${idx + 1} ${st.teamName}", fontFamily = BodyFontFamily, color = if (isUser) ChampionGold else PlainWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text("P:${st.played} W:${st.won} L:${st.lost}", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("${st.points} PTS", fontFamily = DisplayFontFamily, color = RaidCyan, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HofItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontFamily = DisplayFontFamily, color = PlainWhite, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text(label, fontFamily = BodyFontFamily, color = SlateMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}
