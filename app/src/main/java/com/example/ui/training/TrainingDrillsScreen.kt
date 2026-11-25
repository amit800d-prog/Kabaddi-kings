package com.example.ui.training

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

private enum class DrillType { NONE, RAID_TIMING, SUPER_TACKLE, DO_OR_DIE }

@Composable
fun TrainingDrillsScreen(
    sharedPrefs: SharedPreferences,
    onCurrencyUpdated: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var activeDrill by remember { mutableStateOf(DrillType.NONE) }
    var bestScore by remember { mutableIntStateOf(sharedPrefs.getInt("drill_best_score", 0)) }
    var lastEarnedMsg by remember { mutableStateOf<String?>(null) }

    fun finishDrill(score: Int, drillName: String) {
        val coinsEarned = (score * 25).coerceAtLeast(100)
        GameProgressRepository.addCurrency(sharedPrefs, coinsDelta = coinsEarned)
        GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_drill", 1)
        if (score > bestScore) {
            bestScore = score
            sharedPrefs.edit().putInt("drill_best_score", score).apply()
        }
        onCurrencyUpdated()
        ArenaSoundEngine.playSuperRaid(context)
        lastEarnedMsg = "$drillName complete! Score: $score • Earned +$coinsEarned Coins"
        activeDrill = DrillType.NONE
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("training_drills_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (activeDrill != DrillType.NONE) activeDrill = DrillType.NONE else onBack()
                },
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_training_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TRAINING DRILLS", fontFamily = DisplayFontFamily, color = ChampionGold, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text("Best Drill Score: $bestScore", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 11.sp)
            }

            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (lastEarnedMsg != null && activeDrill == DrillType.NONE) {
            Surface(
                color = Color(0xFF14532D),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF4ADE80))
            ) {
                Text(
                    text = lastEarnedMsg!!,
                    fontFamily = BodyFontFamily,
                    color = AlwaysWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        when (activeDrill) {
            DrillType.NONE -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DrillSelectionCard(
                        title = "RAID TIMING DRILL",
                        subtitle = "Stop the oscillating cursor inside the golden Baulk Line zone!",
                        rewardText = "Up to +400 Coins",
                        icon = Icons.Default.Timer,
                        accent = ClayOrange,
                        testTag = "btn_drill_raid_timing",
                        onClick = { activeDrill = DrillType.RAID_TIMING }
                    )
                    DrillSelectionCard(
                        title = "SUPER TACKLE CHALLENGE",
                        subtitle = "Choose the counter-move against incoming star raiders with 3 defenders!",
                        rewardText = "Up to +350 Coins",
                        icon = Icons.Default.Shield,
                        accent = RaidCyan,
                        testTag = "btn_drill_super_tackle",
                        onClick = { activeDrill = DrillType.SUPER_TACKLE }
                    )
                    DrillSelectionCard(
                        title = "DO-OR-DIE RAID PRESSURE",
                        subtitle = "Score points across 5 high-stakes Do-or-Die raids without getting caught!",
                        rewardText = "Up to +450 Coins",
                        icon = Icons.Default.Bolt,
                        accent = ChampionGold,
                        testTag = "btn_drill_do_or_die",
                        onClick = { activeDrill = DrillType.DO_OR_DIE }
                    )
                }
            }

            DrillType.RAID_TIMING -> RaidTimingDrillPlay(onComplete = { finishDrill(it, "Raid Timing Drill") })
            DrillType.SUPER_TACKLE -> SuperTackleDrillPlay(onComplete = { finishDrill(it, "Super Tackle Challenge") })
            DrillType.DO_OR_DIE -> DoOrDieDrillPlay(onComplete = { finishDrill(it, "Do-or-Die Drill") })
        }
    }
}

@Composable
private fun DrillSelectionCard(
    title: String,
    subtitle: String,
    rewardText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MatSlate),
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(accent.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = DisplayFontFamily, color = PlainWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(subtitle, fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 12.sp, lineHeight = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(rewardText, fontFamily = BodyFontFamily, color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun RaidTimingDrillPlay(onComplete: (Int) -> Unit) {
    val context = LocalContext.current
    var cursor by remember { mutableFloatStateOf(0.1f) }
    var direction by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("Tap TOUCH LINE when the marker enters the gold zone!") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(20L)
            cursor += direction * 0.025f
            if (cursor >= 1f) {
                cursor = 1f
                direction = -1
            } else if (cursor <= 0f) {
                cursor = 0f
                direction = 1
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MatSlate),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ROUND $round / 5 • SCORE: $score", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StadiumNavy)
            ) {
                // Target Gold Sweet Spot (0.38..0.62)
                drawRect(
                    color = ChampionGold.copy(alpha = 0.45f),
                    topLeft = Offset(size.width * 0.38f, 0f),
                    size = Size(size.width * 0.24f, size.height)
                )
                // Moving cursor
                drawRect(
                    color = ClayOrange,
                    topLeft = Offset((size.width - 14f) * cursor, 4f),
                    size = Size(14f, size.height - 8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(feedback, fontFamily = BodyFontFamily, color = PlainWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val hit = when {
                        cursor in 0.44f..0.56f -> 3
                        cursor in 0.36f..0.64f -> 2
                        else -> 0
                    }
                    score += hit
                    if (hit > 0) ArenaSoundEngine.playRaidSuccess(context) else ArenaSoundEngine.playTackleSlap(context)
                    feedback = when (hit) {
                        3 -> "PERFECT BONUS TOUCH! +3"
                        2 -> "CLEAN TOUCH! +2"
                        else -> "MISSED ZONE! +0"
                    }
                    if (round >= 5) {
                        onComplete(score)
                    } else {
                        round += 1
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RAID TOUCH!", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SuperTackleDrillPlay(onComplete: (Int) -> Unit) {
    val context = LocalContext.current
    val attacks = remember { listOf("Running Hand Touch" to "Ankle Hold", "Dubki Dive" to "Chain Tackle", "Lion Jump" to "Thigh Hold") }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var currentAttack by remember { mutableStateOf(attacks.random()) }
    var feedback by remember { mutableStateOf("Pick the best defense move to counter the incoming raider!") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MatSlate),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("DEFENSE ROUND $round / 5 • SCORE: $score", fontFamily = DisplayFontFamily, color = RaidCyan, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Incoming Raider Move:", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 12.sp)
            Text(currentAttack.first.uppercase(), fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(feedback, fontFamily = BodyFontFamily, color = PlainWhite, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            listOf("Ankle Hold", "Thigh Hold", "Chain Tackle").forEach { move ->
                Button(
                    onClick = {
                        val pts = if (move == currentAttack.second) 3 else 1
                        score += pts
                        if (pts == 3) ArenaSoundEngine.playSuperRaid(context) else ArenaSoundEngine.playTackleSlap(context)
                        feedback = if (pts == 3) "SUPER TACKLE! $move stopped ${currentAttack.first}! (+3)" else "Partial block with $move (+1)"
                        if (round >= 5) {
                            onComplete(score)
                        } else {
                            round += 1
                            currentAttack = attacks.random()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StadiumNavy, contentColor = PlainWhite),
                    border = BorderStroke(1.dp, RaidCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(50.dp)
                ) {
                    Text(move.uppercase(), fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun DoOrDieDrillPlay(onComplete: (Int) -> Unit) {
    val context = LocalContext.current
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("Do-or-Die Raid! Choose your gamble wisely!") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MatSlate),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("DO-OR-DIE $round / 5 • SCORE: $score", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(feedback, fontFamily = BodyFontFamily, color = PlainWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))

            val options = listOf(
                Triple("Safe Bonus Line Toe-Touch", 75, 2),
                Triple("Aggressive Corner Hand-Touch", 60, 3),
                Triple("High-Risk Super Raid Escape", 40, 5)
            )
            options.forEach { (label, chance, reward) ->
                Button(
                    onClick = {
                        val success = (1..100).random() <= chance
                        if (success) {
                            score += reward
                            ArenaSoundEngine.playRaidSuccess(context)
                            feedback = "SUCCESS! +$reward points!"
                        } else {
                            ArenaSoundEngine.playBuzzer(context)
                            feedback = "TACKLED ON THE LOBBY! +0 points"
                        }
                        if (round >= 5) {
                            onComplete(score)
                        } else {
                            round += 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(52.dp)
                ) {
                    Text("$label ($chance% • +$reward)", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
