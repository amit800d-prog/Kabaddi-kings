package com.example.data

import android.content.SharedPreferences
import com.example.KabaddiPlayer
import com.example.PKL_SEASON_10_TEAMS
import com.example.PlayerRole
import com.example.PklTeam
import com.example.WORLD_CUP_TEAMS
import com.example.getPlayersForTeam
import org.json.JSONArray
import org.json.JSONObject

enum class LineupPosition(val label: String, val shortLabel: String) {
    LEFT_CORNER("Left Corner", "LC"),
    RIGHT_CORNER("Right Corner", "RC"),
    LEFT_COVER("Left Cover", "LCV"),
    RIGHT_COVER("Right Cover", "RCV"),
    LEFT_IN("Left In", "LI"),
    RIGHT_IN("Right In", "RI"),
    CENTER_RAIDER("Center Raider", "CR")
}

data class CustomSquadPlayer(
    val name: String,
    val role: PlayerRole,
    val raidingAbility: Int,
    val defendingAbility: Int,
    val jerseyNumber: Int,
    val pricePaid: Int = 500,
    val upgradeLevel: Int = 1,
    val stamina: Int = 100
) {
    fun toKabaddiPlayer(): KabaddiPlayer = KabaddiPlayer(
        name = name,
        role = role,
        raidingAbility = raidingAbility,
        defendingAbility = defendingAbility,
        jerseyNumber = jerseyNumber
    )
}

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val progress: Int,
    val coinReward: Int,
    val gemReward: Int,
    val claimed: Boolean
) {
    val isReadyToClaim: Boolean get() = !claimed && progress >= target
}

data class ShopItem(
    val id: String,
    val name: String,
    val category: String, // "JERSEY", "STADIUM", "BOOST"
    val description: String,
    val coinCost: Int,
    val gemCost: Int,
    val isConsumable: Boolean = false
)

data class CareerTeamStanding(
    val teamName: String,
    val played: Int,
    val won: Int,
    val lost: Int,
    val points: Int
)

data class CareerState(
    val seasonNumber: Int,
    val roundIndex: Int, // 0..6 (7 rounds per season)
    val clubName: String,
    val trophiesWon: Int,
    val totalCareerWins: Int,
    val bestFinish: String,
    val standings: List<CareerTeamStanding>,
    val fixtureOpponents: List<String>,
    val seasonCompleted: Boolean
)

object GameProgressRepository {
    private const val KEY_COINS = "econ_coins"
    private const val KEY_GEMS = "econ_gems"
    private const val KEY_CUSTOM_SQUAD = "custom_squad_json"
    private const val KEY_LINEUP_SLOTS = "custom_lineup_slots_json"
    private const val KEY_DAILY_STREAK = "daily_streak_day"
    private const val KEY_DAILY_LAST_EPOCH = "daily_last_claim_epoch_day"
    private const val KEY_QUEST_DAY = "quests_epoch_day"
    private const val KEY_OWNED_ITEMS = "shop_owned_items"
    private const val KEY_EQUIPPED_JERSEY = "shop_equipped_jersey"
    private const val KEY_EQUIPPED_STADIUM = "shop_equipped_stadium"
    private const val KEY_RAID_BOOST_CHARGES = "shop_boost_raid"
    private const val KEY_DEFENSE_BOOST_CHARGES = "shop_boost_defense"
    private const val KEY_CAREER_STATE = "career_state_json"

    val SHOP_CATALOG = listOf(
        ShopItem("jersey_royal", "Royal Gold Jersey", "JERSEY", "Championship gold-trimmed match kit", 1500, 0),
        ShopItem("jersey_cyber", "Neon Cyber Jersey", "JERSEY", "Electric cyan & obsidian pro kit", 0, 25),
        ShopItem("jersey_emerald", "Emerald Warrior Kit", "JERSEY", "Forest green & gold heritage kit", 1800, 0),
        ShopItem("stadium_mumbai", "Mumbai Dome Mat", "STADIUM", "High-contrast blue & orange dome mat", 2200, 0),
        ShopItem("stadium_jaipur", "Jaipur Pink Arena", "STADIUM", "Iconic royal pink & gold floodlights", 0, 35),
        ShopItem("boost_raid", "Raider Focus Boost (+3)", "BOOST", "Boosts custom squad raiding by +3", 600, 0, isConsumable = true),
        ShopItem("boost_defense", "Iron Wall Boost (+3)", "BOOST", "Boosts custom squad defense by +3", 600, 0, isConsumable = true),
        ShopItem("boost_stamina", "Full Squad Recovery", "BOOST", "Restores all squad stamina to 100%", 400, 0, isConsumable = true)
    )

    fun getCoins(prefs: SharedPreferences): Int {
        if (!prefs.contains(KEY_COINS)) {
            prefs.edit().putInt(KEY_COINS, 12500).apply()
            return 12500
        }
        return prefs.getInt(KEY_COINS, 12500)
    }

    fun getGems(prefs: SharedPreferences): Int {
        if (!prefs.contains(KEY_GEMS)) {
            prefs.edit().putInt(KEY_GEMS, 120).apply()
            return 120
        }
        return prefs.getInt(KEY_GEMS, 120)
    }

    fun addCurrency(prefs: SharedPreferences, coinsDelta: Int = 0, gemsDelta: Int = 0): Pair<Int, Int> {
        val newCoins = (getCoins(prefs) + coinsDelta).coerceAtLeast(0)
        val newGems = (getGems(prefs) + gemsDelta).coerceAtLeast(0)
        prefs.edit()
            .putInt(KEY_COINS, newCoins)
            .putInt(KEY_GEMS, newGems)
            .apply()
        return newCoins to newGems
    }

    fun spendCurrency(prefs: SharedPreferences, coinsCost: Int, gemsCost: Int): Boolean {
        val curCoins = getCoins(prefs)
        val curGems = getGems(prefs)
        if (curCoins < coinsCost || curGems < gemsCost) return false
        prefs.edit()
            .putInt(KEY_COINS, curCoins - coinsCost)
            .putInt(KEY_GEMS, curGems - gemsCost)
            .apply()
        return true
    }

    fun getCustomTeam(): PklTeam = PklTeam(
        name = "Kabaddi Kings XI",
        nickName = "Kings",
        primaryColor = androidx.compose.ui.graphics.Color(0xFFFF5C22),
        secondaryColor = androidx.compose.ui.graphics.Color(0xFFFFC72C),
        championStars = 3
    )

    fun getCustomSquad(prefs: SharedPreferences): List<CustomSquadPlayer> {
        val raw = prefs.getString(KEY_CUSTOM_SQUAD, null)
        if (raw.isNullOrBlank()) {
            val starter = defaultStarterSquad()
            saveCustomSquad(prefs, starter)
            return starter
        }
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<CustomSquadPlayer>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val role = runCatching { PlayerRole.valueOf(obj.getString("role")) }.getOrDefault(PlayerRole.ALL_ROUNDER)
                list.add(
                    CustomSquadPlayer(
                        name = obj.getString("name"),
                        role = role,
                        raidingAbility = obj.getInt("raid"),
                        defendingAbility = obj.getInt("def"),
                        jerseyNumber = obj.getInt("jersey"),
                        pricePaid = obj.optInt("price", 500),
                        upgradeLevel = obj.optInt("lvl", 1),
                        stamina = obj.optInt("stamina", 100)
                    )
                )
            }
            if (list.isEmpty()) defaultStarterSquad() else list
        } catch (_: Throwable) {
            defaultStarterSquad()
        }
    }

    fun saveCustomSquad(prefs: SharedPreferences, squad: List<CustomSquadPlayer>) {
        val arr = JSONArray()
        squad.forEach { p ->
            val obj = JSONObject().apply {
                put("name", p.name)
                put("role", p.role.name)
                put("raid", p.raidingAbility)
                put("def", p.defendingAbility)
                put("jersey", p.jerseyNumber)
                put("price", p.pricePaid)
                put("lvl", p.upgradeLevel)
                put("stamina", p.stamina)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_SQUAD, arr.toString()).apply()
    }

    fun addAuctionPlayerToSquad(prefs: SharedPreferences, player: KabaddiPlayer, price: Int): List<CustomSquadPlayer> {
        val current = getCustomSquad(prefs).toMutableList()
        val existingIdx = current.indexOfFirst { it.name == player.name }
        if (existingIdx >= 0) {
            val existing = current[existingIdx]
            current[existingIdx] = existing.copy(
                raidingAbility = (existing.raidingAbility + 1).coerceAtMost(99),
                defendingAbility = (existing.defendingAbility + 1).coerceAtMost(99),
                pricePaid = price,
                upgradeLevel = existing.upgradeLevel + 1,
                stamina = 100
            )
        } else {
            val customPlayer = CustomSquadPlayer(
                name = player.name,
                role = player.role,
                raidingAbility = player.raidingAbility,
                defendingAbility = player.defendingAbility,
                jerseyNumber = player.jerseyNumber,
                pricePaid = price,
                upgradeLevel = 1,
                stamina = 100
            )
            current.add(0, customPlayer)
        }
        saveCustomSquad(prefs, current)
        // Put newly bought star into lineup if starting 7 has less or keep lineup valid
        val lineup = getStartingLineup(prefs, current)
        saveStartingLineup(prefs, lineup)
        return current
    }

    private fun defaultStarterSquad(): List<CustomSquadPlayer> {
        val seed = getPlayersForTeam("Puneri Paltan").take(9)
        return seed.map {
            CustomSquadPlayer(
                name = it.name,
                role = it.role,
                raidingAbility = it.raidingAbility,
                defendingAbility = it.defendingAbility,
                jerseyNumber = it.jerseyNumber,
                pricePaid = 400,
                upgradeLevel = 1,
                stamina = 100
            )
        }
    }

    fun getStartingLineup(prefs: SharedPreferences, squad: List<CustomSquadPlayer> = getCustomSquad(prefs)): Map<LineupPosition, String> {
        val raw = prefs.getString(KEY_LINEUP_SLOTS, null)
        val validNames = squad.map { it.name }.toSet()
        val parsed = mutableMapOf<LineupPosition, String>()
        if (!raw.isNullOrBlank()) {
            runCatching {
                val obj = JSONObject(raw)
                LineupPosition.entries.forEach { pos ->
                    val name = obj.optString(pos.name, "")
                    if (name.isNotBlank() && name in validNames && name !in parsed.values) {
                        parsed[pos] = name
                    }
                }
            }
        }
        // Fill any missing positions with remaining squad players
        val used = parsed.values.toMutableSet()
        val remaining = squad.filter { it.name !in used }.toMutableList()
        LineupPosition.entries.forEach { pos ->
            if (!parsed.containsKey(pos) && remaining.isNotEmpty()) {
                val preferred = when (pos) {
                    LineupPosition.LEFT_CORNER, LineupPosition.RIGHT_CORNER,
                    LineupPosition.LEFT_COVER, LineupPosition.RIGHT_COVER ->
                        remaining.firstOrNull { it.role == PlayerRole.DEFENDER || it.role == PlayerRole.ALL_ROUNDER }
                    else ->
                        remaining.firstOrNull { it.role == PlayerRole.RAIDER || it.role == PlayerRole.ALL_ROUNDER }
                } ?: remaining.first()
                parsed[pos] = preferred.name
                remaining.remove(preferred)
            }
        }
        return parsed
    }

    fun saveStartingLineup(prefs: SharedPreferences, lineup: Map<LineupPosition, String>) {
        val obj = JSONObject()
        lineup.forEach { (pos, name) -> obj.put(pos.name, name) }
        prefs.edit().putString(KEY_LINEUP_SLOTS, obj.toString()).apply()
    }

    fun getStartingSevenPlayers(prefs: SharedPreferences): List<KabaddiPlayer> {
        val squad = getCustomSquad(prefs)
        val lineup = getStartingLineup(prefs, squad)
        val byName = squad.associateBy { it.name }
        val starters = LineupPosition.entries.mapNotNull { pos ->
            lineup[pos]?.let { byName[it]?.toKabaddiPlayer() }
        }
        return if (starters.size >= 7) starters else squad.take(7).map { it.toKabaddiPlayer() }
    }

    fun getAllAuctionPool(): List<KabaddiPlayer> {
        val all = (PKL_SEASON_10_TEAMS + WORLD_CUP_TEAMS).flatMap { getPlayersForTeam(it.name) }
        return all.distinctBy { it.name }
            .sortedByDescending { maxOf(it.raidingAbility, it.defendingAbility) }
    }

    // --- DAILY LOGIN REWARDS ---
    private fun currentEpochDay(): Long = System.currentTimeMillis() / 86_400_000L

    fun getDailyStreakDay(prefs: SharedPreferences): Int = prefs.getInt(KEY_DAILY_STREAK, 1).coerceIn(1, 7)

    fun canClaimDailyBonus(prefs: SharedPreferences): Boolean {
        val last = prefs.getLong(KEY_DAILY_LAST_EPOCH, -1L)
        return last < currentEpochDay()
    }

    fun claimDailyBonus(prefs: SharedPreferences): Pair<Int, Int>? {
        if (!canClaimDailyBonus(prefs)) return null
        val streak = getDailyStreakDay(prefs)
        val coinReward = 300 + (streak * 150)
        val gemReward = if (streak == 7) 30 else streak * 3
        addCurrency(prefs, coinReward, gemReward)
        val nextStreak = if (streak >= 7) 1 else streak + 1
        prefs.edit()
            .putLong(KEY_DAILY_LAST_EPOCH, currentEpochDay())
            .putInt(KEY_DAILY_STREAK, nextStreak)
            .apply()
        return coinReward to gemReward
    }

    // --- DAILY QUESTS ---
    private fun ensureQuestDay(prefs: SharedPreferences) {
        val today = currentEpochDay()
        val savedDay = prefs.getLong(KEY_QUEST_DAY, -1L)
        if (savedDay != today) {
            val editor = prefs.edit().putLong(KEY_QUEST_DAY, today)
            listOf("q_match", "q_win", "q_raid", "q_tackle", "q_auction", "q_drill").forEach { id ->
                editor.putInt("quest_prog_$id", 0)
                editor.putBoolean("quest_claimed_$id", false)
            }
            editor.apply()
        }
    }

    fun getDailyQuests(prefs: SharedPreferences): List<DailyQuest> {
        ensureQuestDay(prefs)
        return listOf(
            DailyQuest("q_match", "Mat Ready", "Play 1 Kabaddi match", 1, prefs.getInt("quest_prog_q_match", 0), 500, 5, prefs.getBoolean("quest_claimed_q_match", false)),
            DailyQuest("q_win", "Victory Roar", "Win 1 match in any mode", 1, prefs.getInt("quest_prog_q_win", 0), 800, 10, prefs.getBoolean("quest_claimed_q_win", false)),
            DailyQuest("q_raid", "Raid Machine", "Score 10 raid points", 10, prefs.getInt("quest_prog_q_raid", 0), 600, 5, prefs.getBoolean("quest_claimed_q_raid", false)),
            DailyQuest("q_tackle", "Iron Defense", "Score 5 tackle points", 5, prefs.getInt("quest_prog_q_tackle", 0), 600, 5, prefs.getBoolean("quest_claimed_q_tackle", false)),
            DailyQuest("q_auction", "Big Spender", "Sign 1 star in Auction", 1, prefs.getInt("quest_prog_q_auction", 0), 500, 15, prefs.getBoolean("quest_claimed_q_auction", false)),
            DailyQuest("q_drill", "Sharp Reflex", "Complete 1 Training Drill", 1, prefs.getInt("quest_prog_q_drill", 0), 400, 5, prefs.getBoolean("quest_claimed_q_drill", false))
        )
    }

    fun incrementQuestProgress(prefs: SharedPreferences, questId: String, amount: Int) {
        ensureQuestDay(prefs)
        val cur = prefs.getInt("quest_prog_$questId", 0)
        prefs.edit().putInt("quest_prog_$questId", cur + amount).apply()
    }

    fun claimQuestReward(prefs: SharedPreferences, questId: String): Pair<Int, Int>? {
        val quest = getDailyQuests(prefs).firstOrNull { it.id == questId } ?: return null
        if (!quest.isReadyToClaim) return null
        prefs.edit().putBoolean("quest_claimed_$questId", true).apply()
        addCurrency(prefs, quest.coinReward, quest.gemReward)
        return quest.coinReward to quest.gemReward
    }

    fun getClaimableCount(prefs: SharedPreferences): Int {
        val daily = if (canClaimDailyBonus(prefs)) 1 else 0
        val quests = getDailyQuests(prefs).count { it.isReadyToClaim }
        return daily + quests
    }

    // --- PRO SHOP ---
    fun getOwnedShopItems(prefs: SharedPreferences): Set<String> {
        return prefs.getStringSet(KEY_OWNED_ITEMS, emptySet()) ?: emptySet()
    }

    fun getEquippedJersey(prefs: SharedPreferences): String = prefs.getString(KEY_EQUIPPED_JERSEY, "default") ?: "default"
    fun getEquippedStadium(prefs: SharedPreferences): String = prefs.getString(KEY_EQUIPPED_STADIUM, "default") ?: "default"

    fun buyShopItem(prefs: SharedPreferences, item: ShopItem): Boolean {
        val owned = getOwnedShopItems(prefs).toMutableSet()
        if (!item.isConsumable && item.id in owned) return false
        if (!spendCurrency(prefs, item.coinCost, item.gemCost)) return false

        when (item.id) {
            "boost_raid" -> {
                val squad = getCustomSquad(prefs).map {
                    it.copy(raidingAbility = (it.raidingAbility + 3).coerceAtMost(99))
                }
                saveCustomSquad(prefs, squad)
                prefs.edit().putInt(KEY_RAID_BOOST_CHARGES, prefs.getInt(KEY_RAID_BOOST_CHARGES, 0) + 1).apply()
            }
            "boost_defense" -> {
                val squad = getCustomSquad(prefs).map {
                    it.copy(defendingAbility = (it.defendingAbility + 3).coerceAtMost(99))
                }
                saveCustomSquad(prefs, squad)
                prefs.edit().putInt(KEY_DEFENSE_BOOST_CHARGES, prefs.getInt(KEY_DEFENSE_BOOST_CHARGES, 0) + 1).apply()
            }
            "boost_stamina" -> {
                val squad = getCustomSquad(prefs).map { it.copy(stamina = 100) }
                saveCustomSquad(prefs, squad)
            }
            else -> {
                owned.add(item.id)
                val editor = prefs.edit().putStringSet(KEY_OWNED_ITEMS, owned)
                if (item.category == "JERSEY") editor.putString(KEY_EQUIPPED_JERSEY, item.id)
                if (item.category == "STADIUM") editor.putString(KEY_EQUIPPED_STADIUM, item.id)
                editor.apply()
            }
        }
        return true
    }

    fun equipShopItem(prefs: SharedPreferences, item: ShopItem) {
        if (item.id !in getOwnedShopItems(prefs)) return
        val editor = prefs.edit()
        if (item.category == "JERSEY") editor.putString(KEY_EQUIPPED_JERSEY, item.id)
        if (item.category == "STADIUM") editor.putString(KEY_EQUIPPED_STADIUM, item.id)
        editor.apply()
    }

    // --- CAREER / FRANCHISE MODE ---
    fun getCareerState(prefs: SharedPreferences): CareerState {
        val raw = prefs.getString(KEY_CAREER_STATE, null)
        if (raw.isNullOrBlank()) {
            val fresh = createSeasonState(prefs, seasonNumber = 1, trophiesWon = 0, totalWins = 0, bestFinish = "In Progress")
            saveCareerState(prefs, fresh)
            return fresh
        }
        return try {
            val obj = JSONObject(raw)
            val standingsArr = obj.getJSONArray("standings")
            val standings = mutableListOf<CareerTeamStanding>()
            for (i in 0 until standingsArr.length()) {
                val s = standingsArr.getJSONObject(i)
                standings.add(
                    CareerTeamStanding(
                        teamName = s.getString("name"),
                        played = s.getInt("played"),
                        won = s.getInt("won"),
                        lost = s.getInt("lost"),
                        points = s.getInt("points")
                    )
                )
            }
            val fixArr = obj.getJSONArray("fixtures")
            val fixtures = mutableListOf<String>()
            for (i in 0 until fixArr.length()) fixtures.add(fixArr.getString(i))
            CareerState(
                seasonNumber = obj.getInt("season"),
                roundIndex = obj.getInt("round"),
                clubName = obj.getString("club"),
                trophiesWon = obj.getInt("trophies"),
                totalCareerWins = obj.getInt("totalWins"),
                bestFinish = obj.getString("bestFinish"),
                standings = standings,
                fixtureOpponents = fixtures,
                seasonCompleted = obj.getBoolean("completed")
            )
        } catch (_: Throwable) {
            createSeasonState(prefs, 1, 0, 0, "In Progress")
        }
    }

    fun createSeasonState(
        prefs: SharedPreferences,
        seasonNumber: Int,
        trophiesWon: Int,
        totalWins: Int,
        bestFinish: String
    ): CareerState {
        val clubName = prefs.getString("user_club_name", "Kabaddi Kings XI") ?: "Kabaddi Kings XI"
        val rivals = PKL_SEASON_10_TEAMS.take(7).map { it.name }
        val standings = (listOf(clubName) + rivals).map {
            CareerTeamStanding(it, 0, 0, 0, 0)
        }
        return CareerState(
            seasonNumber = seasonNumber,
            roundIndex = 0,
            clubName = clubName,
            trophiesWon = trophiesWon,
            totalCareerWins = totalWins,
            bestFinish = bestFinish,
            standings = standings,
            fixtureOpponents = rivals.shuffled(),
            seasonCompleted = false
        )
    }

    fun saveCareerState(prefs: SharedPreferences, state: CareerState) {
        val standingsArr = JSONArray()
        state.standings.forEach { st ->
            standingsArr.put(
                JSONObject().apply {
                    put("name", st.teamName)
                    put("played", st.played)
                    put("won", st.won)
                    put("lost", st.lost)
                    put("points", st.points)
                }
            )
        }
        val fixArr = JSONArray()
        state.fixtureOpponents.forEach { fixArr.put(it) }
        val obj = JSONObject().apply {
            put("season", state.seasonNumber)
            put("round", state.roundIndex)
            put("club", state.clubName)
            put("trophies", state.trophiesWon)
            put("totalWins", state.totalCareerWins)
            put("bestFinish", state.bestFinish)
            put("standings", standingsArr)
            put("fixtures", fixArr)
            put("completed", state.seasonCompleted)
        }
        prefs.edit().putString(KEY_CAREER_STATE, obj.toString()).apply()
    }

    data class CareerMatchOutcome(
        val updatedState: CareerState,
        val wonTrophy: Boolean,
        val rewardCoins: Int,
        val rewardGems: Int
    )

    fun recordCareerMatchResult(
        prefs: SharedPreferences,
        opponentName: String,
        userScore: Int,
        oppScore: Int,
        awardMatchCoinsAndQuests: Boolean = true
    ): CareerMatchOutcome {
        val career = getCareerState(prefs)
        val userWins = userScore > oppScore
        val isTie = userScore == oppScore

        val updatedStandings = career.standings.map { st ->
            when (st.teamName) {
                career.clubName -> st.copy(
                    played = st.played + 1,
                    won = st.won + if (userWins) 1 else 0,
                    lost = st.lost + if (!userWins && !isTie) 1 else 0,
                    points = st.points + when {
                        userWins -> 5
                        isTie -> 3
                        else -> 1
                    }
                )
                opponentName -> st.copy(
                    played = st.played + 1,
                    won = st.won + if (!userWins && !isTie) 1 else 0,
                    lost = st.lost + if (userWins) 1 else 0,
                    points = st.points + when {
                        !userWins && !isTie -> 5
                        isTie -> 3
                        else -> 1
                    }
                )
                else -> {
                    val aiWin = (1..10).random() <= 5
                    st.copy(
                        played = st.played + 1,
                        won = st.won + if (aiWin) 1 else 0,
                        lost = st.lost + if (!aiWin) 1 else 0,
                        points = st.points + if (aiWin) 5 else 1
                    )
                }
            }
        }

        val nextRound = (career.roundIndex + 1).coerceAtMost(career.fixtureOpponents.size)
        val isComplete = nextRound >= career.fixtureOpponents.size
        val rankAfter = updatedStandings
            .sortedWith(compareByDescending<CareerTeamStanding> { it.points }.thenByDescending { it.won })
            .indexOfFirst { it.teamName == career.clubName } + 1
        val wonTrophy = isComplete && rankAfter == 1

        val newCareer = career.copy(
            roundIndex = nextRound,
            trophiesWon = career.trophiesWon + if (wonTrophy) 1 else 0,
            totalCareerWins = career.totalCareerWins + if (userWins) 1 else 0,
            bestFinish = if (wonTrophy) "Champion (Season ${career.seasonNumber})" else if (isComplete) "#$rankAfter Finish" else career.bestFinish,
            standings = updatedStandings,
            seasonCompleted = isComplete
        )
        saveCareerState(prefs, newCareer)

        val rewardCoins = if (awardMatchCoinsAndQuests) {
            when {
                userWins -> 450
                isTie -> 260
                else -> 180
            }
        } else 0
        val rewardGems = if (wonTrophy) 40 else 0

        if (rewardCoins > 0 || rewardGems > 0) {
            addCurrency(prefs, rewardCoins, rewardGems)
        }
        if (awardMatchCoinsAndQuests) {
            incrementQuestProgress(prefs, "q_match", 1)
            if (userWins) incrementQuestProgress(prefs, "q_win", 1)
        }

        return CareerMatchOutcome(
            updatedState = newCareer,
            wonTrophy = wonTrophy,
            rewardCoins = rewardCoins,
            rewardGems = rewardGems
        )
    }

    fun resetAllProgress(prefs: SharedPreferences) {
        val editor = prefs.edit()
        editor.putInt(KEY_COINS, 12500)
        editor.putInt(KEY_GEMS, 120)
        editor.remove(KEY_CUSTOM_SQUAD)
        editor.remove(KEY_LINEUP_SLOTS)
        editor.remove(KEY_DAILY_STREAK)
        editor.remove(KEY_DAILY_LAST_EPOCH)
        editor.remove(KEY_QUEST_DAY)
        editor.remove(KEY_OWNED_ITEMS)
        editor.remove(KEY_EQUIPPED_JERSEY)
        editor.remove(KEY_EQUIPPED_STADIUM)
        editor.remove(KEY_RAID_BOOST_CHARGES)
        editor.remove(KEY_DEFENSE_BOOST_CHARGES)
        editor.remove(KEY_CAREER_STATE)
        prefs.all.keys
            .filter { it.startsWith("quest_prog_") || it.startsWith("quest_claimed_") }
            .forEach { editor.remove(it) }
        editor.apply()
    }
}
