package com.example.ui.rewards

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
fun RewardsShopScreen(
    sharedPrefs: SharedPreferences,
    coins: Int,
    gems: Int,
    onCurrencyUpdated: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var streakDay by remember { mutableIntStateOf(GameProgressRepository.getDailyStreakDay(sharedPrefs)) }
    var canClaimDaily by remember { mutableStateOf(GameProgressRepository.canClaimDailyBonus(sharedPrefs)) }
    var quests by remember { mutableStateOf(GameProgressRepository.getDailyQuests(sharedPrefs)) }
    var ownedItems by remember { mutableStateOf(GameProgressRepository.getOwnedShopItems(sharedPrefs)) }
    var equippedJersey by remember { mutableStateOf(GameProgressRepository.getEquippedJersey(sharedPrefs)) }
    var equippedStadium by remember { mutableStateOf(GameProgressRepository.getEquippedStadium(sharedPrefs)) }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("rewards_shop_screen")
    ) {
        // Header
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
                    .testTag("btn_rewards_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Text(
                text = "REWARDS & SHOP",
                fontFamily = DisplayFontFamily,
                color = ChampionGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MiniWalletChip(Icons.Default.MonetizationOn, "$coins", ChampionGold)
                MiniWalletChip(Icons.Default.Diamond, "$gems", RaidCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MatSlate,
            contentColor = ChampionGold,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                Text("DAILY", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
                Text("MISSIONS", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = {
                Text("PRO SHOP", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            })
        }

        if (feedbackBanner != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFF14532D),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF4ADE80))
            ) {
                Text(
                    text = feedbackBanner!!,
                    fontFamily = BodyFontFamily,
                    color = AlwaysWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Daily Login Calendar
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("7-DAY CHAMPION STREAK", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                                        Text("Current Streak: Day $streakDay of 7", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val claimed = GameProgressRepository.claimDailyBonus(sharedPrefs)
                                        if (claimed != null) {
                                            ArenaSoundEngine.playSuperRaid(context)
                                            streakDay = GameProgressRepository.getDailyStreakDay(sharedPrefs)
                                            canClaimDaily = false
                                            onCurrencyUpdated()
                                            feedbackBanner = "Claimed +${claimed.first} Coins & +${claimed.second} Gems!"
                                        }
                                    },
                                    enabled = canClaimDaily,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ClayOrange,
                                        contentColor = AlwaysWhite,
                                        disabledContainerColor = StadiumNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_claim_daily_bonus")
                                ) {
                                    Text(
                                        text = if (canClaimDaily) "CLAIM TODAY'S REWARD" else "TODAY'S REWARD CLAIMED",
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    items((1..7).toList()) { day ->
                        val coinAmt = 300 + (day * 150)
                        val gemAmt = if (day == 7) 30 else day * 3
                        val isToday = day == streakDay
                        val isDone = day < streakDay || (day == streakDay && !canClaimDaily)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isToday) ClayOrange.copy(alpha = 0.16f) else MatSlate),
                            border = BorderStroke(1.dp, if (isToday) ChampionGold else Color(0xFF2E395E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("DAY $day", fontFamily = DisplayFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("+$coinAmt Coins • +$gemAmt Gems", fontFamily = BodyFontFamily, color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (isDone) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Claimed", tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp))
                                } else {
                                    Text(if (isToday) "READY" else "LOCKED", fontFamily = BodyFontFamily, color = if (isToday) RaidCyan else SlateMuted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Matchday Missions
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quests) { q ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.dp, if (q.isReadyToClaim) ChampionGold else Color(0xFF2E395E)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = RaidCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(q.title, fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(q.description, fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 11.sp)
                                        }
                                    }
                                    Text(
                                        text = "+${q.coinReward}C +${q.gemReward}G",
                                        fontFamily = BodyFontFamily,
                                        color = ChampionGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { (q.progress.toFloat() / q.target.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (q.isReadyToClaim) Color(0xFF4ADE80) else ClayOrange,
                                    trackColor = StadiumNavy
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${q.progress.coerceAtMost(q.target)} / ${q.target}",
                                        fontFamily = BodyFontFamily,
                                        color = SlateMuted,
                                        fontSize = 11.sp
                                    )

                                    Button(
                                        onClick = {
                                            val res = GameProgressRepository.claimQuestReward(sharedPrefs, q.id)
                                            if (res != null) {
                                                ArenaSoundEngine.playRaidSuccess(context)
                                                quests = GameProgressRepository.getDailyQuests(sharedPrefs)
                                                onCurrencyUpdated()
                                                feedbackBanner = "${q.title}: +${res.first} Coins & +${res.second} Gems!"
                                            }
                                        },
                                        enabled = q.isReadyToClaim,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ChampionGold,
                                            contentColor = AlwaysDark,
                                            disabledContainerColor = StadiumNavy
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                q.claimed -> "CLAIMED"
                                                q.isReadyToClaim -> "CLAIM"
                                                else -> "IN PROGRESS"
                                            },
                                            fontFamily = BodyFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // Pro Shop
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(GameProgressRepository.SHOP_CATALOG) { item ->
                        val isOwned = !item.isConsumable && item.id in ownedItems
                        val isEquipped = (item.id == equippedJersey || item.id == equippedStadium)
                        val canAfford = coins >= item.coinCost && gems >= item.gemCost

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MatSlate),
                            border = BorderStroke(1.dp, if (isEquipped) ChampionGold else Color(0xFF2E395E)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(ClayOrange.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ChampionGold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.category, fontFamily = BodyFontFamily, color = RaidCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    Text(item.name, fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(item.description, fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (isOwned) {
                                            GameProgressRepository.equipShopItem(sharedPrefs, item)
                                            equippedJersey = GameProgressRepository.getEquippedJersey(sharedPrefs)
                                            equippedStadium = GameProgressRepository.getEquippedStadium(sharedPrefs)
                                            feedbackBanner = "Equipped ${item.name}!"
                                        } else if (GameProgressRepository.buyShopItem(sharedPrefs, item)) {
                                            ArenaSoundEngine.playAuctionGavel(context)
                                            ownedItems = GameProgressRepository.getOwnedShopItems(sharedPrefs)
                                            equippedJersey = GameProgressRepository.getEquippedJersey(sharedPrefs)
                                            equippedStadium = GameProgressRepository.getEquippedStadium(sharedPrefs)
                                            onCurrencyUpdated()
                                            feedbackBanner = "Purchased ${item.name}!"
                                        } else {
                                            feedbackBanner = "Not enough Coins or Gems!"
                                        }
                                    },
                                    enabled = isOwned || canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            isEquipped -> Color(0xFF15803D)
                                            isOwned -> RaidCyan
                                            else -> ClayOrange
                                        },
                                        contentColor = AlwaysWhite
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    val priceLabel = if (item.coinCost > 0) "${item.coinCost}C" else "${item.gemCost}G"
                                    Text(
                                        text = when {
                                            isEquipped -> "EQUIPPED"
                                            isOwned -> "EQUIP"
                                            else -> "BUY $priceLabel"
                                        },
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniWalletChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MatSlate,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}
