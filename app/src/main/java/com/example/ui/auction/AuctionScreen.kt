package com.example.ui.auction

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KabaddiPlayer
import com.example.PlayerRole
import com.example.audio.ArenaSoundEngine
import com.example.data.CustomSquadPlayer
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

@Composable
fun AuctionScreen(
    sharedPrefs: SharedPreferences,
    coins: Int,
    onCurrencyUpdated: () -> Unit,
    onOpenSquadManager: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allPool = remember { GameProgressRepository.getAllAuctionPool() }
    var customSquad by remember { mutableStateOf(GameProgressRepository.getCustomSquad(sharedPrefs)) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var roleFilter by remember { mutableStateOf<PlayerRole?>(null) }

    val filteredPool = remember(allPool, roleFilter) {
        if (roleFilter == null) allPool else allPool.filter { it.role == roleFilter }
    }

    var lotIndex by remember { mutableIntStateOf(0) }
    val currentPlayer = filteredPool.getOrNull(lotIndex % filteredPool.size.coerceAtLeast(1)) ?: allPool.first()

    fun basePriceFor(p: KabaddiPlayer): Int {
        val peak = maxOf(p.raidingAbility, p.defendingAbility)
        return when {
            peak >= 94 -> 1400
            peak >= 88 -> 900
            else -> 500
        }
    }

    var currentBid by remember(currentPlayer) { mutableIntStateOf(basePriceFor(currentPlayer)) }
    var highestBidder by remember(currentPlayer) { mutableStateOf("Opening Lot") }
    var timeLeft by remember(currentPlayer) { mutableIntStateOf(10) }
    var lotStatus by remember(currentPlayer) { mutableStateOf("LIVE") } // LIVE, SOLD, UNSOLD
    var statusBanner by remember(currentPlayer) { mutableStateOf("Place your bid before the hammer falls!") }

    val aiFranchises = remember {
        listOf("Puneri Paltan", "Patna Pirates", "Jaipur Panthers", "Bengaluru Bulls", "U Mumba")
    }

    // Live auction countdown & AI competing bids
    LaunchedEffect(currentPlayer, lotStatus, timeLeft) {
        if (lotStatus == "LIVE" && selectedTab == 0) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
                // AI bidding logic when user or another team is ahead
                val maxAiValuation = basePriceFor(currentPlayer) + ((maxOf(currentPlayer.raidingAbility, currentPlayer.defendingAbility) - 75) * 45).coerceAtLeast(300)
                if (timeLeft in 2..7 && currentBid + 200 <= maxAiValuation) {
                    val shouldAiBid = when (highestBidder) {
                        "Opening Lot" -> (1..10).random() <= 6
                        "Your Club" -> (1..10).random() <= 5
                        else -> (1..10).random() <= 2
                    }
                    if (shouldAiBid) {
                        val aiBidder = aiFranchises.filter { it != highestBidder }.random()
                        currentBid += 200
                        highestBidder = aiBidder
                        timeLeft = (timeLeft + 3).coerceAtMost(8)
                        statusBanner = "$aiBidder raised bid to $currentBid!"
                    }
                }
            } else {
                // Hammer falls!
                if (highestBidder == "Your Club") {
                    if (GameProgressRepository.spendCurrency(sharedPrefs, currentBid, 0)) {
                        customSquad = GameProgressRepository.addAuctionPlayerToSquad(sharedPrefs, currentPlayer, currentBid)
                        GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_auction", 1)
                        onCurrencyUpdated()
                        ArenaSoundEngine.playAuctionGavel(context)
                        lotStatus = "SOLD"
                        statusBanner = "SOLD! ${currentPlayer.name} joins Kabaddi Kings XI for $currentBid coins!"
                    } else {
                        lotStatus = "UNSOLD"
                        statusBanner = "Insufficient coins when hammer fell."
                    }
                } else if (highestBidder == "Opening Lot") {
                    lotStatus = "UNSOLD"
                    statusBanner = "${currentPlayer.name} went UNSOLD."
                } else {
                    ArenaSoundEngine.playAuctionGavel(context)
                    lotStatus = "SOLD"
                    statusBanner = "SOLD to $highestBidder for $currentBid coins!"
                }
            }
        }
    }

    fun placeUserBid(increment: Int) {
        if (lotStatus != "LIVE") return
        val nextBid = if (highestBidder == "Opening Lot") currentBid else currentBid + increment
        if (coins < nextBid) {
            statusBanner = "Not enough coins! Need $nextBid coins."
            return
        }
        currentBid = nextBid
        highestBidder = "Your Club"
        timeLeft = 7
        statusBanner = "YOU lead the bid at $currentBid coins!"
        ArenaSoundEngine.playWhistle(context)
    }

    fun nextLot() {
        lotIndex = (lotIndex + 1) % filteredPool.size.coerceAtLeast(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("auction_screen")
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
                    .testTag("btn_auction_back")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PRO PLAYER AUCTION",
                    fontFamily = DisplayFontFamily,
                    color = ChampionGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Squad: ${customSquad.size} Players",
                    fontFamily = BodyFontFamily,
                    color = SlateMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MatSlate,
                border = BorderStroke(1.dp, ChampionGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = "Purse", tint = ChampionGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$coins", fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
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
                Text("LIVE BIDDING", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
                Text("MY SQUAD (${customSquad.size})", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Role Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(null to "ALL", PlayerRole.RAIDER to "RAID", PlayerRole.DEFENDER to "DEF", PlayerRole.ALL_ROUNDER to "ALL-R").forEach { (role, label) ->
                    val selected = roleFilter == role
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                roleFilter = role
                                lotIndex = 0
                            },
                        color = if (selected) ClayOrange else MatSlate,
                        border = BorderStroke(1.dp, if (selected) ClayOrange else Color(0xFF2E395E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = label,
                                fontFamily = BodyFontFamily,
                                color = if (selected) AlwaysWhite else PlainWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Auction Stage Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = when (currentPlayer.role) {
                                PlayerRole.RAIDER -> ClayOrange.copy(alpha = 0.2f)
                                PlayerRole.DEFENDER -> RaidCyan.copy(alpha = 0.2f)
                                PlayerRole.ALL_ROUNDER -> ChampionGold.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = currentPlayer.role.name.replace('_', ' '),
                                fontFamily = BodyFontFamily,
                                color = ChampionGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = if (timeLeft <= 3) Color(0xFFEF4444) else RaidCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lotStatus == "LIVE") "${timeLeft}s" else lotStatus,
                                fontFamily = DisplayFontFamily,
                                color = if (timeLeft <= 3 && lotStatus == "LIVE") Color(0xFFEF4444) else PlainWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.radialGradient(listOf(ClayOrange, Color(0xFF3B0764))),
                                    CircleShape
                                )
                                .border(2.dp, ChampionGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${currentPlayer.jerseyNumber}",
                                fontFamily = DisplayFontFamily,
                                color = AlwaysWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val isAlreadyOwned = customSquad.any { it.name == currentPlayer.name }
                            Text(
                                text = currentPlayer.name,
                                fontFamily = DisplayFontFamily,
                                color = PlainWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isAlreadyOwned) {
                                    "Base Price: ${basePriceFor(currentPlayer)} Coins • IN YOUR SQUAD (Win to Upgrade)"
                                } else {
                                    "Base Price: ${basePriceFor(currentPlayer)} Coins"
                                },
                                fontFamily = BodyFontFamily,
                                color = if (isAlreadyOwned) ChampionGold else SlateMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ability Bars
                    AbilityMeter("RAIDING", currentPlayer.raidingAbility, ClayOrange)
                    Spacer(modifier = Modifier.height(6.dp))
                    AbilityMeter("DEFENDING", currentPlayer.defendingAbility, RaidCyan)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Bid Spotlight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StadiumNavy)
                            .border(1.dp, if (highestBidder == "Your Club") ChampionGold else Color(0xFF2E395E), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CURRENT BID", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("$currentBid COINS", fontFamily = DisplayFontFamily, color = ChampionGold, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("HIGHEST BIDDER", fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = highestBidder,
                                    fontFamily = BodyFontFamily,
                                    color = if (highestBidder == "Your Club") Color(0xFF4ADE80) else PlainWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = statusBanner,
                        fontFamily = BodyFontFamily,
                        color = if (lotStatus == "SOLD" && highestBidder == "Your Club") Color(0xFF4ADE80) else ChampionGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bidding Controls
            if (lotStatus == "LIVE") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { placeUserBid(200) },
                        colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_bid_200")
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (highestBidder == "Opening Lot") "BID $currentBid" else "BID +200",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { placeUserBid(500) },
                        colors = ButtonDefaults.buttonColors(containerColor = ChampionGold, contentColor = AlwaysDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_bid_500")
                    ) {
                        Text("POWER +500", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { nextLot() },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SlateMuted.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_skip_lot")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, tint = PlainWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PASS & NEXT PLAYER", fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = { nextLot() },
                    colors = ButtonDefaults.buttonColors(containerColor = RaidCyan, contentColor = AlwaysDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_next_lot")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BRING NEXT PLAYER TO AUCTION", fontFamily = BodyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        } else {
            // My Auction Squad Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SIGNED PLAYERS (${customSquad.size})",
                    fontFamily = DisplayFontFamily,
                    color = PlainWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onOpenSquadManager,
                    colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LINEUP", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(customSquad) { p ->
                    SquadAuctionRow(p)
                }
            }
        }
    }
}

@Composable
private fun AbilityMeter(label: String, value: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontFamily = BodyFontFamily, color = SlateMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("$value / 99", fontFamily = BodyFontFamily, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (value / 99f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = StadiumNavy
        )
    }
}

@Composable
private fun SquadAuctionRow(p: CustomSquadPlayer) {
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
                Text(p.name, fontFamily = BodyFontFamily, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "${p.role.name.replace('_', ' ')} • #${p.jerseyNumber} • Paid ${p.pricePaid}",
                    fontFamily = BodyFontFamily,
                    color = SlateMuted,
                    fontSize = 11.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("R:${p.raidingAbility}", fontFamily = BodyFontFamily, color = ClayOrange, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("D:${p.defendingAbility}", fontFamily = BodyFontFamily, color = RaidCyan, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}
