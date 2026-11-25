package com.example.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsKabaddi
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ActiveScreen
import com.example.R
import com.example.loc
import com.example.ui.theme.AlwaysDark
import com.example.ui.theme.AlwaysWhite
import com.example.ui.theme.BodyFontFamily
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.ClayOrange
import com.example.ui.theme.DefenderBlue
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.MatSlate
import com.example.ui.theme.PlainWhite
import com.example.ui.theme.RaidCyan
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.StadiumNavy
import com.example.ui.theme.currentDarkModeState
import com.example.ui.theme.currentLanguageState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    coins: Int,
    gems: Int,
    squadCount: Int,
    matchesPlayed: Int = 0,
    matchesWon: Int = 0,
    claimableRewardsCount: Int = 0,
    onScreenSelect: (ActiveScreen) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
    }
    val managerName = sharedPrefs.getString("user_manager_name", "Amit Team Manager")
        ?: "Amit Team Manager"

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val winRate = remember(matchesPlayed, matchesWon) {
        if (matchesPlayed > 0) ((matchesWon.toFloat() / matchesPlayed.toFloat()) * 100f).toInt() else 0
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_root"),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 580.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            HomeTopHeader(
                coins = coins,
                gems = gems,
                onWalletClick = { onScreenSelect(ActiveScreen.REWARDS_SHOP) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(240)) + slideInVertically(
                    initialOffsetY = { it / 12 },
                    animationSpec = tween(260, easing = FastOutSlowInEasing)
                ),
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("home_nav_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 14.dp)
                ) {
                    // Club & Season Overview Material 3 Dashboard Card
                    item {
                        ManagerClubBanner(
                            managerName = managerName,
                            squadCount = squadCount,
                            matchesPlayed = matchesPlayed,
                            winRate = winRate
                        )
                    }

                    // Daily Rewards, Missions & Pro Shop Strip
                    item {
                        DailyRewardsShopStrip(
                            claimableCount = claimableRewardsCount,
                            onClick = { onScreenSelect(ActiveScreen.REWARDS_SHOP) }
                        )
                    }

                    // Primary M3 Dashboard Section
                    item {
                        SectionHeading(
                            title = "ARENA MODES".loc(),
                            subtitle = "SELECT YOUR PATH".loc()
                        )
                    }

                    if (isWide) {
                        // Wide / Tablet Material 3 Bento Dashboard Grid
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickMatchHeroCard(
                                    modifier = Modifier.weight(1.35f),
                                    onClick = { onScreenSelect(ActiveScreen.QUICK_MATCH) }
                                )
                                DashboardModeCard(
                                    modifier = Modifier.weight(1f).height(156.dp),
                                    title = "Tournaments".loc(),
                                    subtitle = "PKL & World Cup".loc(),
                                    badgeText = "LIVE".loc(),
                                    badgeColor = RaidCyan,
                                    gradientColors = listOf(
                                        DefenderBlue,
                                        Color(0xFF172554)
                                    ),
                                    icon = Icons.Rounded.EmojiEvents,
                                    testTag = "btn_tournament",
                                    onClick = { onScreenSelect(ActiveScreen.TOURNAMENT) }
                                )
                                DashboardModeCard(
                                    modifier = Modifier.weight(1f).height(156.dp),
                                    title = "Player Auctions".loc(),
                                    subtitle = "Bid & Draft Pro Stars".loc(),
                                    badgeText = "OPEN".loc(),
                                    badgeColor = ChampionGold,
                                    gradientColors = listOf(
                                        Color(0xFF6D28D9),
                                        Color(0xFF3B0764)
                                    ),
                                    icon = Icons.Rounded.Gavel,
                                    testTag = "btn_auction",
                                    onClick = { onScreenSelect(ActiveScreen.AUCTION) }
                                )
                            }
                        }
                    } else {
                        // Compact Phone Material 3 Dashboard Cards: Quick Match Hero + Tournaments & Player Auctions
                        item {
                            QuickMatchHeroCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onScreenSelect(ActiveScreen.QUICK_MATCH) }
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DashboardModeCard(
                                    modifier = Modifier.weight(1f).height(148.dp),
                                    title = "Tournaments".loc(),
                                    subtitle = "PKL & World Cup".loc(),
                                    badgeText = "LIVE".loc(),
                                    badgeColor = RaidCyan,
                                    gradientColors = listOf(
                                        DefenderBlue,
                                        Color(0xFF172554)
                                    ),
                                    icon = Icons.Rounded.EmojiEvents,
                                    testTag = "btn_tournament",
                                    onClick = { onScreenSelect(ActiveScreen.TOURNAMENT) }
                                )

                                DashboardModeCard(
                                    modifier = Modifier.weight(1f).height(148.dp),
                                    title = "Player Auctions".loc(),
                                    subtitle = "Bid & Draft Pro Stars".loc(),
                                    badgeText = "OPEN".loc(),
                                    badgeColor = ChampionGold,
                                    gradientColors = listOf(
                                        Color(0xFF6D28D9),
                                        Color(0xFF3B0764)
                                    ),
                                    icon = Icons.Rounded.Gavel,
                                    testTag = "btn_auction",
                                    onClick = { onScreenSelect(ActiveScreen.AUCTION) }
                                )
                            }
                        }
                    }

                    // Franchise & Training Section
                    item {
                        SectionHeading(
                            title = "FRANCHISE & TRAINING".loc(),
                            subtitle = "BUILD YOUR DYNASTY".loc()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CompactHubTile(
                                modifier = Modifier.weight(1f),
                                title = "SQUAD".loc(),
                                subtitle = "Lineup 7".loc(),
                                icon = Icons.Rounded.Groups,
                                accent = RaidCyan,
                                testTag = "btn_squad_manager",
                                onClick = { onScreenSelect(ActiveScreen.SQUAD_MANAGER) }
                            )
                            CompactHubTile(
                                modifier = Modifier.weight(1f),
                                title = "CAREER".loc(),
                                subtitle = "Seasons".loc(),
                                icon = Icons.Rounded.Workspaces,
                                accent = ChampionGold,
                                testTag = "btn_career_mode",
                                onClick = { onScreenSelect(ActiveScreen.CAREER_MODE) }
                            )
                            CompactHubTile(
                                modifier = Modifier.weight(1f),
                                title = "DRILLS".loc(),
                                subtitle = "Practice".loc(),
                                icon = Icons.Rounded.FitnessCenter,
                                accent = ClayOrange,
                                testTag = "btn_training_drills",
                                onClick = { onScreenSelect(ActiveScreen.TRAINING_DRILLS) }
                            )
                        }
                    }

                    // Statistics & Settings Material 3 Cards
                    item {
                        if (isWide) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UtilityNavCard(
                                    modifier = Modifier.weight(1f),
                                    title = "STATISTICS".loc(),
                                    subtitle = "Team Win Rate, Raid Ratio & Player Records".loc(),
                                    icon = Icons.Rounded.BarChart,
                                    accentColor = RaidCyan,
                                    testTag = "btn_statistics",
                                    onClick = { onScreenSelect(ActiveScreen.STATS) }
                                )
                                UtilityNavCard(
                                    modifier = Modifier.weight(1f),
                                    title = "SETTINGS".loc(),
                                    subtitle = "Themes, Language, Audio & Official Rules".loc(),
                                    icon = Icons.Rounded.Settings,
                                    accentColor = ChampionGold,
                                    testTag = "btn_settings",
                                    onClick = { onScreenSelect(ActiveScreen.SETTINGS) }
                                )
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                UtilityNavCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = "STATISTICS".loc(),
                                    subtitle = "Team Win Rate, Raid Ratio & Player Records".loc(),
                                    icon = Icons.Rounded.BarChart,
                                    accentColor = RaidCyan,
                                    testTag = "btn_statistics",
                                    onClick = { onScreenSelect(ActiveScreen.STATS) }
                                )
                                UtilityNavCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    title = "SETTINGS".loc(),
                                    subtitle = "Themes, Language, Audio & Official Rules".loc(),
                                    icon = Icons.Rounded.Settings,
                                    accentColor = ChampionGold,
                                    testTag = "btn_settings",
                                    onClick = { onScreenSelect(ActiveScreen.SETTINGS) }
                                )
                            }
                        }
                    }
                }
            }

            // Clean Arena Footer
            Text(
                text = "KABADDI KINGS • SEASON 10 ARENA",
                fontFamily = BodyFontFamily,
                color = SlateMuted.copy(alpha = 0.75f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun HomeTopHeader(
    coins: Int,
    gems: Int,
    onWalletClick: () -> Unit
) {
    val formattedCoins = remember(coins) {
        NumberFormat.getNumberInstance(Locale.US).format(coins)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF29313C),
                border = BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.7f)),
                tonalElevation = 4.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_crest),
                    contentDescription = "Kabaddi Kings Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column {
                val localizedTitle = "KABADDI KINGS".loc()
                if (localizedTitle == "कबड्डी किंग्स") {
                    Text(
                        text = localizedTitle,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = ChampionGold,
                        letterSpacing = 0.8.sp
                    )
                } else {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "KABADDI ",
                            fontFamily = DisplayFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = ClayOrange,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "KINGS",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = ChampionGold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Text(
                    text = "SEASON 10 ARENA".loc(),
                    fontFamily = BodyFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = SlateMuted,
                    letterSpacing = 1.4.sp
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onWalletClick)
        ) {
            CurrencyBadge(
                icon = Icons.Rounded.MonetizationOn,
                contentDescription = "Coins",
                valueText = formattedCoins,
                accent = ChampionGold
            )
            CurrencyBadge(
                icon = Icons.Rounded.Diamond,
                contentDescription = "Gems",
                valueText = "$gems",
                accent = RaidCyan
            )
        }
    }
}

@Composable
private fun CurrencyBadge(
    icon: ImageVector,
    contentDescription: String,
    valueText: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MatSlate,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = valueText,
                fontFamily = BodyFontFamily,
                color = PlainWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ManagerClubBanner(
    managerName: String,
    squadCount: Int,
    matchesPlayed: Int,
    winRate: Int
) {
    val isDark = currentDarkModeState.value
    val cardBorder = if (isDark) Color(0xFF2E395E) else Color(0xFFCBD5E1)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cardBorder, RoundedCornerShape(22.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(140.dp)
            ) {
                drawCircle(
                    color = ClayOrange.copy(alpha = 0.10f),
                    radius = size.minDimension / 1.1f,
                    center = Offset(size.width, size.height)
                )
                drawCircle(
                    color = ChampionGold.copy(alpha = 0.07f),
                    radius = size.minDimension / 1.7f,
                    center = Offset(size.width, size.height)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                Brush.radialGradient(colors = listOf(ClayOrange, ChampionGold)),
                                CircleShape
                            )
                            .padding(2.5.dp)
                            .background(StadiumNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SportsKabaddi,
                            contentDescription = "Manager Crest",
                            tint = ChampionGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = managerName,
                                fontFamily = BodyFontFamily,
                                color = PlainWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Pro Manager",
                                tint = RaidCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = if (currentLanguageState.value == "HINDI")
                                "क्लब: कबड्डी किंग्स XI"
                            else
                                "Club: Kabaddi Kings XI",
                            fontFamily = BodyFontFamily,
                            color = SlateMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LVL 12",
                                fontFamily = BodyFontFamily,
                                color = ChampionGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = { 0.72f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ClayOrange,
                                trackColor = StadiumNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StadiumNavy.copy(alpha = if (isDark) 0.7f else 0.75f),
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClubMiniStat(label = "SQUAD".loc(), value = "$squadCount", accent = RaidCyan)
                        HorizontalDivider(
                            modifier = Modifier
                                .height(22.dp)
                                .width(1.dp),
                            color = SlateMuted.copy(alpha = 0.2f)
                        )
                        ClubMiniStat(label = "PLAYED".loc(), value = "$matchesPlayed", accent = PlainWhite)
                        HorizontalDivider(
                            modifier = Modifier
                                .height(22.dp)
                                .width(1.dp),
                            color = SlateMuted.copy(alpha = 0.2f)
                        )
                        ClubMiniStat(label = "WIN RATE".loc(), value = "$winRate%", accent = ChampionGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubMiniStat(
    label: String,
    value: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = DisplayFontFamily,
            color = accent,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontFamily = BodyFontFamily,
            color = SlateMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun DailyRewardsShopStrip(
    claimableCount: Int,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("btn_rewards_shop"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = ClayOrange.copy(alpha = 0.16f)
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ChampionGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ChampionGold.copy(alpha = 0.18f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.CardGiftcard,
                        contentDescription = "Rewards and Shop",
                        tint = ChampionGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DAILY REWARDS, MISSIONS & PRO SHOP".loc(),
                    fontFamily = BodyFontFamily,
                    color = PlainWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (claimableCount > 0) "$claimableCount reward(s) ready to claim!" else "Unlock kits, arenas & claim daily bonus",
                    fontFamily = BodyFontFamily,
                    color = if (claimableCount > 0) ChampionGold else SlateMuted,
                    fontSize = 11.sp
                )
            }
            if (claimableCount > 0) {
                Surface(
                    color = ClayOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "CLAIM",
                        fontFamily = BodyFontFamily,
                        color = AlwaysWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = ChampionGold)
            }
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = DisplayFontFamily,
            color = PlainWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
        Text(
            text = subtitle,
            fontFamily = BodyFontFamily,
            color = SlateMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun QuickMatchHeroCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "QuickMatchPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PlayButtonPulse"
    )

    ElevatedCard(
        modifier = modifier
            .height(156.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("btn_quick_match"),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.5.dp, ClayOrange.copy(alpha = 0.75f), RoundedCornerShape(22.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_home_arena_hero),
                contentDescription = "Quick Match",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AlwaysDark.copy(alpha = 0.92f),
                                Color(0xFF3E1508).copy(alpha = 0.80f),
                                ClayOrange.copy(alpha = 0.38f)
                            )
                        )
                    )
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = ChampionGold.copy(alpha = 0.25f),
                    start = Offset(size.width * 0.62f, 0f),
                    end = Offset(size.width * 0.52f, size.height),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = ClayOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SportsKabaddi,
                                    contentDescription = null,
                                    tint = AlwaysWhite,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "PLAY NOW".loc(),
                                    fontFamily = BodyFontFamily,
                                    color = AlwaysWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Quick Match".loc(),
                        fontFamily = DisplayFontFamily,
                        color = AlwaysWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Instant 7v7 Raid & Tackle Match".loc(),
                        fontFamily = BodyFontFamily,
                        color = AlwaysWhite.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    modifier = Modifier
                        .size((54 * pulseScale).dp),
                    shape = CircleShape,
                    color = ChampionGold,
                    border = BorderStroke(2.dp, AlwaysWhite.copy(alpha = 0.9f)),
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(ClayOrange, ChampionGold))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Start Quick Match",
                            tint = AlwaysDark,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardModeCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    gradientColors: List<Color>,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AlwaysWhite.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(92.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AlwaysWhite.copy(alpha = 0.14f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = ChampionGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.8f))
                    ) {
                        Text(
                            text = badgeText,
                            fontFamily = BodyFontFamily,
                            color = AlwaysWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontFamily = DisplayFontFamily,
                        color = AlwaysWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.3.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subtitle,
                            fontFamily = BodyFontFamily,
                            color = AlwaysWhite.copy(alpha = 0.80f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = AlwaysWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactHubTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = currentDarkModeState.value
    val borderColor = if (isDark) Color(0xFF2E395E) else Color(0xFFCBD5E1)

    ElevatedCard(
        modifier = modifier
            .height(94.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = accent.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(19.dp))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontFamily = DisplayFontFamily,
                    color = PlainWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontFamily = BodyFontFamily,
                    color = SlateMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun UtilityNavCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val isDark = currentDarkModeState.value
    val borderColor = if (isDark) Color(0xFF2E395E) else Color(0xFFCBD5E1)

    ElevatedCard(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = DisplayFontFamily,
                    color = PlainWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontFamily = BodyFontFamily,
                    color = SlateMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "Open $title",
                tint = SlateMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
