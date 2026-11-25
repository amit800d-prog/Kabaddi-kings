package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeScreenNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysAllNavigationOptions_andDispatchesClicks() {
        val selectedScreens = mutableListOf<ActiveScreen>()

        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(
                    coins = 12500,
                    gems = 120,
                    squadCount = 12,
                    matchesPlayed = 4,
                    matchesWon = 3,
                    claimableRewardsCount = 2,
                    onScreenSelect = { selectedScreens.add(it) }
                )
            }
        }

        val tagsToScreens = listOf(
            "btn_rewards_shop" to ActiveScreen.REWARDS_SHOP,
            "btn_quick_match" to ActiveScreen.QUICK_MATCH,
            "btn_tournament" to ActiveScreen.TOURNAMENT,
            "btn_auction" to ActiveScreen.AUCTION,
            "btn_squad_manager" to ActiveScreen.SQUAD_MANAGER,
            "btn_career_mode" to ActiveScreen.CAREER_MODE,
            "btn_training_drills" to ActiveScreen.TRAINING_DRILLS,
            "btn_statistics" to ActiveScreen.STATS,
            "btn_settings" to ActiveScreen.SETTINGS
        )

        composeTestRule.waitForIdle()

        tagsToScreens.forEach { (tag, expectedScreen) ->
            composeTestRule.onNodeWithTag(tag).assertIsDisplayed().performClick()
            composeTestRule.waitForIdle()
            assertEquals(expectedScreen, selectedScreens.last())
        }
    }
}
