package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testAllTeamsMeetRosterRequirements() {
        for (team in PKL_SEASON_10_TEAMS) {
            val players = getPlayersForTeam(team.name)
            
            // Check that size is between 10 and 15
            assertTrue(
                "Team ${team.name} has ${players.size} players, expected between 10 and 15",
                players.size in 10..15
            )
            
            val raiders = players.count { it.role == PlayerRole.RAIDER }
            val defenders = players.count { it.role == PlayerRole.DEFENDER }
            val allRounders = players.count { it.role == PlayerRole.ALL_ROUNDER }
            
            assertTrue(
                "Team ${team.name} has only $raiders Raiders, expected >= 3",
                raiders >= 3
            )
            assertTrue(
                "Team ${team.name} has only $defenders Defenders, expected >= 3",
                defenders >= 3
            )
            assertTrue(
                "Team ${team.name} has only $allRounders All Rounders, expected >= 3",
                allRounders >= 3
            )
        }
    }
}
