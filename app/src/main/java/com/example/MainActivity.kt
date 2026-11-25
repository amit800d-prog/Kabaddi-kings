package com.example

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.ArenaSoundEngine
import com.example.data.GameProgressRepository
import com.example.ui.auction.AuctionScreen
import com.example.ui.career.CareerModeScreen
import com.example.ui.home.HomeScreen
import com.example.ui.rewards.RewardsShopScreen
import com.example.ui.squad.SquadManagerScreen
import com.example.ui.theme.*
import com.example.ui.training.TrainingDrillsScreen
import kotlinx.coroutines.delay

// Represents the screen states for Kabaddi Kings
enum class ActiveScreen {
    HOME,
    QUICK_MATCH,
    MATCH_CONFIG,
    PLAYER_SELECTION,
    MATCH_PREVIEW,
    MATCH_ARENA,
    TOURNAMENT,
    AUCTION,
    SQUAD_MANAGER,
    REWARDS_SHOP,
    TRAINING_DRILLS,
    CAREER_MODE,
    STATS,
    SETTINGS
}

enum class PlayerRole {
    RAIDER,
    DEFENDER,
    ALL_ROUNDER
}

data class KabaddiPlayer(
    val name: String,
    val role: PlayerRole,
    val raidingAbility: Int,
    val defendingAbility: Int,
    val jerseyNumber: Int
)

fun getPlayersForTeam(teamName: String): List<KabaddiPlayer> {
    val wcTeamNames = listOf("India", "Iran", "Pakistan", "Republic of Korea", "Bangladesh", "Kenya", "Poland", "Japan", "Sri Lanka", "Thailand")
    if (teamName in wcTeamNames) {
        return getWorldCupTeamPlayers(teamName)
    }
    return when (teamName) {
        "Puneri Paltan" -> listOf(
            KabaddiPlayer("Aslam Inamdar", PlayerRole.ALL_ROUNDER, 94, 82, 10),
            KabaddiPlayer("Mohammadreza Shadloui", PlayerRole.ALL_ROUNDER, 85, 96, 99),
            KabaddiPlayer("Mohit Goyat", PlayerRole.RAIDER, 92, 70, 7),
            KabaddiPlayer("Pankaj Mohite", PlayerRole.RAIDER, 89, 45, 9),
            KabaddiPlayer("Gaurav Khatri", PlayerRole.DEFENDER, 40, 93, 2),
            KabaddiPlayer("Abinesh Nadarajan", PlayerRole.DEFENDER, 35, 91, 5),
            KabaddiPlayer("Sanket Sawant", PlayerRole.DEFENDER, 30, 86, 3),
            KabaddiPlayer("Aditya Shinde", PlayerRole.RAIDER, 82, 50, 11),
            KabaddiPlayer("Akash Shinde", PlayerRole.RAIDER, 85, 48, 8),
            KabaddiPlayer("Badal Singh", PlayerRole.DEFENDER, 30, 80, 4),
            KabaddiPlayer("Dadaso Pujari", PlayerRole.DEFENDER, 25, 78, 12),
            KabaddiPlayer("Vahid Rezaeimehr", PlayerRole.ALL_ROUNDER, 82, 82, 14)
        )
        "Jaipur Pink Panthers" -> listOf(
            KabaddiPlayer("Arjun Deshwal", PlayerRole.RAIDER, 97, 40, 1),
            KabaddiPlayer("Sunil Kumar", PlayerRole.DEFENDER, 45, 94, 7),
            KabaddiPlayer("Ankush Rathee", PlayerRole.DEFENDER, 30, 95, 5),
            KabaddiPlayer("Reza Mirbagheri", PlayerRole.DEFENDER, 40, 89, 11),
            KabaddiPlayer("V Ajith Kumar", PlayerRole.RAIDER, 88, 42, 9),
            KabaddiPlayer("Bhavani Rajput", PlayerRole.RAIDER, 85, 45, 12),
            KabaddiPlayer("Sahul Kumar", PlayerRole.DEFENDER, 35, 87, 3),
            KabaddiPlayer("Abhishek KS", PlayerRole.DEFENDER, 32, 83, 4),
            KabaddiPlayer("Ashish Cl", PlayerRole.ALL_ROUNDER, 78, 78, 8),
            KabaddiPlayer("Devank", PlayerRole.RAIDER, 80, 50, 15),
            KabaddiPlayer("Lucky Sharma", PlayerRole.ALL_ROUNDER, 72, 81, 2),
            KabaddiPlayer("Navneet", PlayerRole.ALL_ROUNDER, 78, 70, 10)
        )
        "Patna Pirates" -> listOf(
            KabaddiPlayer("Sachin Tanwar", PlayerRole.RAIDER, 95, 60, 10),
            KabaddiPlayer("Sudhakar M", PlayerRole.RAIDER, 90, 40, 9),
            KabaddiPlayer("Manjeet", PlayerRole.RAIDER, 88, 52, 11),
            KabaddiPlayer("Krishan Dhull", PlayerRole.DEFENDER, 35, 92, 2),
            KabaddiPlayer("Ankit Jaglan", PlayerRole.ALL_ROUNDER, 80, 88, 5),
            KabaddiPlayer("Babu M", PlayerRole.DEFENDER, 28, 81, 4),
            KabaddiPlayer("Mayur Kadam", PlayerRole.DEFENDER, 30, 85, 3),
            KabaddiPlayer("Sandeep Kumar", PlayerRole.RAIDER, 82, 45, 8),
            KabaddiPlayer("Rohit", PlayerRole.RAIDER, 80, 42, 7),
            KabaddiPlayer("Sajin Chandrasekar", PlayerRole.DEFENDER, 35, 80, 6),
            KabaddiPlayer("Manish", PlayerRole.ALL_ROUNDER, 76, 78, 12),
            KabaddiPlayer("Zheng-Wei Chen", PlayerRole.ALL_ROUNDER, 75, 70, 13)
        )
        "Bengaluru Bulls" -> listOf(
            KabaddiPlayer("Bharat Hooda", PlayerRole.ALL_ROUNDER, 93, 80, 18),
            KabaddiPlayer("Vikash Kandola", PlayerRole.RAIDER, 89, 42, 3),
            KabaddiPlayer("Neeraj Narwal", PlayerRole.RAIDER, 84, 55, 7),
            KabaddiPlayer("Saurabh Nandal", PlayerRole.DEFENDER, 32, 91, 1),
            KabaddiPlayer("Aman Antil", PlayerRole.DEFENDER, 30, 88, 5),
            KabaddiPlayer("Surjeet Singh", PlayerRole.DEFENDER, 35, 93, 4),
            KabaddiPlayer("Ran Singh", PlayerRole.ALL_ROUNDER, 75, 85, 9),
            KabaddiPlayer("Monu", PlayerRole.RAIDER, 80, 55, 12),
            KabaddiPlayer("Vishal Lather", PlayerRole.DEFENDER, 30, 84, 6),
            KabaddiPlayer("Sushil", PlayerRole.RAIDER, 82, 40, 10),
            KabaddiPlayer("Ponparthiban S", PlayerRole.DEFENDER, 25, 81, 11),
            KabaddiPlayer("Rakshith", PlayerRole.ALL_ROUNDER, 78, 70, 8)
        )
        "Dabang Delhi K.C." -> listOf(
            KabaddiPlayer("Ashu Malik", PlayerRole.RAIDER, 96, 55, 10),
            KabaddiPlayer("Naveen Kumar", PlayerRole.RAIDER, 98, 40, 11),
            KabaddiPlayer("Meetu Sharma", PlayerRole.RAIDER, 86, 45, 9),
            KabaddiPlayer("Yogesh Dahiya", PlayerRole.DEFENDER, 30, 92, 2),
            KabaddiPlayer("Ashish", PlayerRole.DEFENDER, 32, 87, 4),
            KabaddiPlayer("Vikrant", PlayerRole.DEFENDER, 28, 85, 3),
            KabaddiPlayer("Vishal Bhardwaj", PlayerRole.DEFENDER, 34, 90, 5),
            KabaddiPlayer("Mohit", PlayerRole.ALL_ROUNDER, 78, 82, 6),
            KabaddiPlayer("Vijay Malik", PlayerRole.ALL_ROUNDER, 85, 82, 8),
            KabaddiPlayer("Manjeet", PlayerRole.RAIDER, 82, 48, 7),
            KabaddiPlayer("Felix Li", PlayerRole.DEFENDER, 35, 80, 12),
            KabaddiPlayer("Himmat Antil", PlayerRole.ALL_ROUNDER, 75, 79, 13)
        )
        "Bengal Warriors" -> listOf(
            KabaddiPlayer("Maninder Singh", PlayerRole.RAIDER, 96, 42, 8),
            KabaddiPlayer("Shrikant Jadhav", PlayerRole.RAIDER, 87, 45, 9),
            KabaddiPlayer("Nitin Kumar", PlayerRole.RAIDER, 91, 40, 11),
            KabaddiPlayer("Shubham Shinde", PlayerRole.DEFENDER, 32, 90, 2),
            KabaddiPlayer("Aditya S. Shinde", PlayerRole.DEFENDER, 30, 85, 5),
            KabaddiPlayer("Vaibhav Garje", PlayerRole.DEFENDER, 35, 86, 3),
            KabaddiPlayer("Harsh Lad", PlayerRole.DEFENDER, 28, 81, 4),
            KabaddiPlayer("Aslam Saja", PlayerRole.ALL_ROUNDER, 80, 80, 10),
            KabaddiPlayer("Akshay Bharat", PlayerRole.RAIDER, 81, 44, 7),
            KabaddiPlayer("Suyog Gaikar", PlayerRole.RAIDER, 79, 41, 12),
            KabaddiPlayer("Vishwas S", PlayerRole.ALL_ROUNDER, 82, 75, 6),
            KabaddiPlayer("Darpan", PlayerRole.ALL_ROUNDER, 70, 80, 14)
        )
        "Gujarat Giants" -> listOf(
            KabaddiPlayer("Fazel Atrachali", PlayerRole.DEFENDER, 35, 96, 9),
            KabaddiPlayer("Sombir", PlayerRole.DEFENDER, 30, 89, 5),
            KabaddiPlayer("Rakesh Sangroya", PlayerRole.RAIDER, 90, 48, 10),
            KabaddiPlayer("Sonu Jaglan", PlayerRole.RAIDER, 88, 50, 4),
            KabaddiPlayer("Parteek Dahiya", PlayerRole.ALL_ROUNDER, 88, 78, 11),
            KabaddiPlayer("Rohit Gulia", PlayerRole.ALL_ROUNDER, 84, 75, 7),
            KabaddiPlayer("Arkam Shaikh", PlayerRole.DEFENDER, 32, 83, 3),
            KabaddiPlayer("Sourav Gulia", PlayerRole.DEFENDER, 28, 82, 2),
            KabaddiPlayer("Manuj", PlayerRole.DEFENDER, 25, 80, 12),
            KabaddiPlayer("Nitin", PlayerRole.RAIDER, 81, 42, 8),
            KabaddiPlayer("Narender Hooda", PlayerRole.RAIDER, 80, 44, 13),
            KabaddiPlayer("Ravi Kumar", PlayerRole.ALL_ROUNDER, 76, 83, 15)
        )
        "Haryana Steelers" -> listOf(
            KabaddiPlayer("Siddharth Desai", PlayerRole.RAIDER, 91, 38, 9),
            KabaddiPlayer("Vinay", PlayerRole.RAIDER, 93, 42, 10),
            KabaddiPlayer("Shivam Patare", PlayerRole.RAIDER, 88, 40, 7),
            KabaddiPlayer("Jaideep Dahiya", PlayerRole.DEFENDER, 32, 94, 4),
            KabaddiPlayer("Mohit Nandal", PlayerRole.DEFENDER, 35, 92, 5),
            KabaddiPlayer("Rahul Sethpal", PlayerRole.DEFENDER, 38, 91, 3),
            KabaddiPlayer("Mohit Khaler", PlayerRole.DEFENDER, 30, 86, 2),
            KabaddiPlayer("Ashish", PlayerRole.ALL_ROUNDER, 82, 82, 11),
            KabaddiPlayer("K. Prapanjan", PlayerRole.RAIDER, 84, 42, 8),
            KabaddiPlayer("Chandran Ranjit", PlayerRole.ALL_ROUNDER, 83, 76, 12),
            KabaddiPlayer("Naveen Kundu", PlayerRole.DEFENDER, 28, 81, 6),
            KabaddiPlayer("Monu Hooda", PlayerRole.ALL_ROUNDER, 70, 83, 14)
        )
        "Tamil Thalaivas" -> listOf(
            KabaddiPlayer("Narender Kandola", PlayerRole.RAIDER, 94, 45, 9),
            KabaddiPlayer("Ajinkya Pawar", PlayerRole.RAIDER, 91, 48, 10),
            KabaddiPlayer("Sagar Rathi", PlayerRole.DEFENDER, 30, 94, 2),
            KabaddiPlayer("Sahil Gulia", PlayerRole.DEFENDER, 32, 92, 5),
            KabaddiPlayer("Mohit Jakhar", PlayerRole.DEFENDER, 28, 85, 3),
            KabaddiPlayer("M. Abishek", PlayerRole.DEFENDER, 35, 87, 4),
            KabaddiPlayer("Himanshu", PlayerRole.ALL_ROUNDER, 82, 84, 7),
            KabaddiPlayer("Amirhossein Bastami", PlayerRole.ALL_ROUNDER, 72, 86, 1),
            KabaddiPlayer("Himanshu Singh", PlayerRole.RAIDER, 83, 40, 11),
            KabaddiPlayer("Jatin Phogat", PlayerRole.RAIDER, 80, 42, 12),
            KabaddiPlayer("Nitin Singh", PlayerRole.RAIDER, 82, 44, 8),
            KabaddiPlayer("Masanamuthu Lakshnanan", PlayerRole.ALL_ROUNDER, 78, 70, 13)
        )
        "Telugu Titans" -> listOf(
            KabaddiPlayer("Pawan Sehrawat", PlayerRole.ALL_ROUNDER, 97, 85, 17),
            KabaddiPlayer("Rajnish Dalal", PlayerRole.RAIDER, 86, 42, 10),
            KabaddiPlayer("Robin Chaudhary", PlayerRole.RAIDER, 83, 44, 9),
            KabaddiPlayer("Sandeep Dhull", PlayerRole.DEFENDER, 32, 89, 5),
            KabaddiPlayer("Parvesh Bhainswal", PlayerRole.DEFENDER, 35, 88, 4),
            KabaddiPlayer("Mohit Rathee", PlayerRole.DEFENDER, 28, 84, 3),
            KabaddiPlayer("Ajit Pawar", PlayerRole.DEFENDER, 30, 83, 2),
            KabaddiPlayer("Sanjeevi S", PlayerRole.ALL_ROUNDER, 78, 80, 7),
            KabaddiPlayer("Hamid Nader", PlayerRole.DEFENDER, 28, 82, 6),
            KabaddiPlayer("Nitin", PlayerRole.RAIDER, 80, 40, 8),
            KabaddiPlayer("Omkar Patil", PlayerRole.RAIDER, 79, 38, 11),
            KabaddiPlayer("Shankar Gadai", PlayerRole.ALL_ROUNDER, 75, 78, 12)
        )
        "U Mumba" -> listOf(
            KabaddiPlayer("Guman Singh", PlayerRole.RAIDER, 92, 45, 9),
            KabaddiPlayer("Amirmohammad Zafardanesh", PlayerRole.ALL_ROUNDER, 91, 80, 10),
            KabaddiPlayer("Visvanath V", PlayerRole.ALL_ROUNDER, 82, 82, 7),
            KabaddiPlayer("Surinder Singh", PlayerRole.DEFENDER, 35, 88, 4),
            KabaddiPlayer("Rinku Sharma", PlayerRole.DEFENDER, 32, 93, 2),
            KabaddiPlayer("Mahender Singh", PlayerRole.DEFENDER, 34, 89, 3),
            KabaddiPlayer("Girish Maruti Ernak", PlayerRole.DEFENDER, 30, 86, 5),
            KabaddiPlayer("Alireza Mirazeian", PlayerRole.RAIDER, 81, 40, 11),
            KabaddiPlayer("Heidarali Ekrami", PlayerRole.RAIDER, 83, 44, 8),
            KabaddiPlayer("Bittu", PlayerRole.DEFENDER, 25, 81, 12),
            KabaddiPlayer("Sombir", PlayerRole.ALL_ROUNDER, 75, 82, 6),
            KabaddiPlayer("Pranay Rane", PlayerRole.RAIDER, 80, 42, 13)
        )
        "UP Yoddhas" -> listOf(
            KabaddiPlayer("Pardeep Narwal", PlayerRole.RAIDER, 95, 38, 9),
            KabaddiPlayer("Surender Gill", PlayerRole.RAIDER, 93, 46, 7),
            KabaddiPlayer("Sumit Sangwan", PlayerRole.DEFENDER, 32, 93, 3),
            KabaddiPlayer("Nitesh Kumar", PlayerRole.DEFENDER, 35, 90, 5),
            KabaddiPlayer("Gurdeep", PlayerRole.ALL_ROUNDER, 80, 86, 4),
            KabaddiPlayer("Ashu Singh", PlayerRole.DEFENDER, 30, 85, 2),
            KabaddiPlayer("Vijay Malik", PlayerRole.ALL_ROUNDER, 84, 80, 8),
            KabaddiPlayer("Anil Kumar", PlayerRole.RAIDER, 81, 44, 10),
            KabaddiPlayer("Harendra Kumar", PlayerRole.DEFENDER, 28, 82, 11),
            KabaddiPlayer("Kiran Magar", PlayerRole.DEFENDER, 26, 81, 12),
            KabaddiPlayer("Gagana Gowda", PlayerRole.RAIDER, 84, 42, 15),
            KabaddiPlayer("Mahipal", PlayerRole.ALL_ROUNDER, 78, 70, 14)
        )
        else -> listOf()
    }
}

// Data model representing a Pro Kabaddi League Season 10 Real Team
data class PklTeam(
    val name: String,
    val nickName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val championStars: Int
)

// List of all 12 Real Pro Kabaddi League Season 10 Teams
val PKL_SEASON_10_TEAMS = listOf(
    PklTeam("Jaipur Pink Panthers", "Panthers", Color(0xFFFF3E6C), Color(0xFF1E88E5), 2),
    PklTeam("Puneri Paltan", "Paltan", Color(0xFFFF6D00), Color(0xFFF57C00), 1),
    PklTeam("Patna Pirates", "Pirates", Color(0xFF0F9D58), Color(0xFFF4B400), 3),
    PklTeam("Bengaluru Bulls", "Bulls", Color(0xFFB71C1C), Color(0xFF212121), 1),
    PklTeam("Dabang Delhi K.C.", "Dabang", Color(0xFF0D47A1), Color(0xFFD50000), 1),
    PklTeam("Bengal Warriors", "Warriors", Color(0xFF1565C0), Color(0xFFFF6D00), 1),
    PklTeam("Gujarat Giants", "Giants", Color(0xFFE64A19), Color(0xFFFFD600), 0),
    PklTeam("Haryana Steelers", "Steelers", Color(0xFF1A237E), Color(0xFFFFD600), 0),
    PklTeam("Tamil Thalaivas", "Thalaivas", Color(0xFF0D47A1), Color(0xFFFFD54F), 0),
    PklTeam("Telugu Titans", "Titans", Color(0xFFFBC02D), Color(0xFF212121), 0),
    PklTeam("U Mumba", "Mumbai", Color(0xFFFF3D00), Color(0xFF212121), 1),
    PklTeam("UP Yoddhas", "Yoddhas", Color(0xFFC62828), Color(0xFF1565C0), 0)
)

val WORLD_CUP_TEAMS = listOf(
    PklTeam("India", "India", Color(0xFFFF9933), Color(0xFF138808), 3),
    PklTeam("Iran", "Iran", Color(0xFF228B22), Color(0xFFDA291C), 0),
    PklTeam("Pakistan", "Pakistan", Color(0xFF005A36), Color(0xFFFFFFFF), 0),
    PklTeam("Republic of Korea", "Korea", Color(0xFF003472), Color(0xFFC60C30), 0),
    PklTeam("Bangladesh", "Bangladesh", Color(0xFF006A4E), Color(0xFFF42A41), 0),
    PklTeam("Kenya", "Kenya", Color(0xFF990000), Color(0xFF000000), 0),
    PklTeam("Poland", "Poland", Color(0xFFDC143C), Color(0xFFFFFFFF), 0),
    PklTeam("Japan", "Japan", Color(0xFFBC002D), Color(0xFFFFFFFF), 0),
    PklTeam("Sri Lanka", "Sri Lanka", Color(0xFFFFB61E), Color(0xFF8B0000), 0),
    PklTeam("Thailand", "Thailand", Color(0xFF2D2A4A), Color(0xFFA51931), 0)
)

fun getWorldCupTeamPlayers(teamName: String): List<KabaddiPlayer> {
    return when (teamName) {
        "India" -> listOf(
            KabaddiPlayer("Pawan Sehrawat", PlayerRole.ALL_ROUNDER, 96, 75, 9),
            KabaddiPlayer("Naveen Kumar", PlayerRole.RAIDER, 95, 40, 11),
            KabaddiPlayer("Arjun Deshwal", PlayerRole.RAIDER, 94, 42, 1),
            KabaddiPlayer("Aslam Inamdar", PlayerRole.ALL_ROUNDER, 91, 80, 10),
            KabaddiPlayer("Surjeet Singh", PlayerRole.DEFENDER, 30, 93, 4),
            KabaddiPlayer("Sunil Kumar", PlayerRole.DEFENDER, 35, 92, 7),
            KabaddiPlayer("Nitesh Kumar", PlayerRole.DEFENDER, 25, 89, 5),
            KabaddiPlayer("Vishal Bhardwaj", PlayerRole.DEFENDER, 28, 88, 2),
            KabaddiPlayer("Nitin Rawal", PlayerRole.ALL_ROUNDER, 83, 83, 8)
        )
        "Iran" -> listOf(
            KabaddiPlayer("Fazel Atrachali", PlayerRole.DEFENDER, 30, 96, 4),
            KabaddiPlayer("Mohammadreza Shadloui", PlayerRole.ALL_ROUNDER, 88, 97, 99),
            KabaddiPlayer("Heidarali Ekrami", PlayerRole.RAIDER, 84, 45, 8),
            KabaddiPlayer("Alireza Alipour", PlayerRole.RAIDER, 82, 40, 9),
            KabaddiPlayer("Amirhossein Bastami", PlayerRole.DEFENDER, 25, 87, 3),
            KabaddiPlayer("Hamid Nader", PlayerRole.DEFENDER, 20, 84, 5),
            KabaddiPlayer("Moein Shafiqi", PlayerRole.ALL_ROUNDER, 78, 80, 12),
            KabaddiPlayer("Milad Jabbari", PlayerRole.DEFENDER, 22, 82, 1)
        )
        "Pakistan" -> listOf(
            KabaddiPlayer("Mudassar Ali", PlayerRole.RAIDER, 88, 40, 7),
            KabaddiPlayer("Muhammad Imran", PlayerRole.ALL_ROUNDER, 82, 85, 10),
            KabaddiPlayer("Nasir Ali", PlayerRole.DEFENDER, 30, 89, 4),
            KabaddiPlayer("Sajjad Shaukat", PlayerRole.DEFENDER, 25, 87, 5),
            KabaddiPlayer("Khalid Butt", PlayerRole.RAIDER, 85, 42, 9),
            KabaddiPlayer("Kashif Razzaq", PlayerRole.DEFENDER, 20, 82, 3),
            KabaddiPlayer("Muhammad Safian", PlayerRole.RAIDER, 80, 40, 11)
        )
        "Bangladesh" -> listOf(
            KabaddiPlayer("Tuhin Tarafder", PlayerRole.RAIDER, 85, 45, 10),
            KabaddiPlayer("Aruduzzaman Munshi", PlayerRole.RAIDER, 83, 40, 9),
            KabaddiPlayer("Ziaur Rahman", PlayerRole.DEFENDER, 25, 88, 4),
            KabaddiPlayer("Sabu Sandhu", PlayerRole.DEFENDER, 20, 85, 5),
            KabaddiPlayer("Kamal Hossain", PlayerRole.RAIDER, 80, 42, 8),
            KabaddiPlayer("Roshan Ali", PlayerRole.DEFENDER, 22, 81, 2)
        )
        else -> {
            val prefix = teamName.substring(0, Math.min(teamName.length, 3)).uppercase()
            listOf(
                KabaddiPlayer("$prefix Raider 1", PlayerRole.RAIDER, 84, 40, 7),
                KabaddiPlayer("$prefix Raider 2", PlayerRole.RAIDER, 82, 42, 9),
                KabaddiPlayer("$prefix Defender 1", PlayerRole.DEFENDER, 30, 85, 2),
                KabaddiPlayer("$prefix Defender 2", PlayerRole.DEFENDER, 28, 83, 4),
                KabaddiPlayer("$prefix All Rounder 1", PlayerRole.ALL_ROUNDER, 80, 80, 10),
                KabaddiPlayer("$prefix Defender 3", PlayerRole.DEFENDER, 25, 81, 5),
                KabaddiPlayer("$prefix Raider 3", PlayerRole.RAIDER, 78, 40, 12),
                KabaddiPlayer("$prefix All Rounder 2", PlayerRole.ALL_ROUNDER, 76, 76, 8)
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
        val loadedThemeIndex = sharedPrefs.getInt("settings_theme_index", 0)
        currentThemeIndexState.value = if (loadedThemeIndex in 0..9) loadedThemeIndex else 0
        val loadedLang = sharedPrefs.getString("settings_app_language", "ENG") ?: "ENG"
        currentLanguageState.value = if (loadedLang == "HINDI") "HINDI" else "ENG"
        currentDarkModeState.value = sharedPrefs.getBoolean("settings_dark_mode", true)

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                    containerColor = StadiumNavy
                ) { innerPadding ->
                    KabaddiKingsApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KabaddiKingsApp(modifier: Modifier = Modifier) {
    var activeScreen by remember { mutableStateOf(ActiveScreen.HOME) }
    var selectedYourTeam by remember { mutableStateOf(PKL_SEASON_10_TEAMS[1]) } // Default: Puneri Paltan
    var selectedOpponentTeam by remember { mutableStateOf(PKL_SEASON_10_TEAMS[2]) } // Default: Patna Pirates
    var selectedDifficulty by remember { mutableStateOf("Medium") } // "Easy", "Medium", "Hard", "Pro"
    var selectedTiming by remember { mutableStateOf(10) } // 5, 10, 15, 20
    var selectedPlayers by remember { mutableStateOf<List<KabaddiPlayer>>(emptyList()) }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE) }

    var statsMatchesPlayed by remember { mutableStateOf(sharedPrefs.getInt("stats_matches_played", 0)) }
    var statsMatchesWon by remember { mutableStateOf(sharedPrefs.getInt("stats_matches_won", 0)) }
    var statsMatchesLost by remember { mutableStateOf(sharedPrefs.getInt("stats_matches_lost", 0)) }
    var statsMatchesTied by remember { mutableStateOf(sharedPrefs.getInt("stats_matches_tied", 0)) }
    var statsTotalUserPoints by remember { mutableStateOf(sharedPrefs.getInt("stats_total_user_points", 0)) }
    var statsTotalOpponentPoints by remember { mutableStateOf(sharedPrefs.getInt("stats_total_opponent_points", 0)) }
    var statsTotalRaidPoints by remember { mutableStateOf(sharedPrefs.getInt("stats_total_raid_points", 0)) }
    var statsTotalTacklePoints by remember { mutableStateOf(sharedPrefs.getInt("stats_total_tackle_points", 0)) }
    var statsHighestMatchScore by remember { mutableStateOf(sharedPrefs.getInt("stats_highest_match_score", 0)) }

    val updateStats: (
        Int, Int, Int, Int, Int, Int,
        List<LivePlayerState>, List<LivePlayerState>, List<LivePlayerState>
    ) -> Unit = { userScore, opponentScore, yourRaidPoints, yourTacklePoints, oppRaidPoints, oppTacklePoints, yourSquad, yourBench, oppSquad ->
        val newPlayed = statsMatchesPlayed + 1
        val newWon = statsMatchesWon + (if (userScore > opponentScore) 1 else 0)
        val newLost = statsMatchesLost + (if (userScore < opponentScore) 1 else 0)
        val newTied = statsMatchesTied + (if (userScore == opponentScore) 1 else 0)
        val newTotalUserPoints = statsTotalUserPoints + userScore
        val newTotalOpponentPoints = statsTotalOpponentPoints + opponentScore
        val newTotalRaidPoints = statsTotalRaidPoints + yourRaidPoints
        val newTotalTacklePoints = statsTotalTacklePoints + yourTacklePoints
        val newHighestScore = java.lang.Math.max(statsHighestMatchScore, userScore)

        statsMatchesPlayed = newPlayed
        statsMatchesWon = newWon
        statsMatchesLost = newLost
        statsMatchesTied = newTied
        statsTotalUserPoints = newTotalUserPoints
        statsTotalOpponentPoints = newTotalOpponentPoints
        statsTotalRaidPoints = newTotalRaidPoints
        statsTotalTacklePoints = newTotalTacklePoints
        statsHighestMatchScore = newHighestScore

        val editor = sharedPrefs.edit()
        editor.putInt("stats_matches_played", newPlayed)
        editor.putInt("stats_matches_won", newWon)
        editor.putInt("stats_matches_lost", newLost)
        editor.putInt("stats_matches_tied", newTied)
        editor.putInt("stats_total_user_points", newTotalUserPoints)
        editor.putInt("stats_total_opponent_points", newTotalOpponentPoints)
        editor.putInt("stats_total_raid_points", newTotalRaidPoints)
        editor.putInt("stats_total_tackle_points", newTotalTacklePoints)
        editor.putInt("stats_highest_match_score", newHighestScore)

        // Update player-specific persistent statistics
        val combinedYourPlayers = yourSquad + yourBench
        val allPlayers = combinedYourPlayers + oppSquad

        allPlayers.forEach { p ->
            val currentSuccessfulRaids = sharedPrefs.getInt("player_${p.name}_successfulRaids", 0)
            val currentUnsuccessfulRaids = sharedPrefs.getInt("player_${p.name}_unsuccessfulRaids", 0)
            val currentTotalRaidPoints = sharedPrefs.getInt("player_${p.name}_totalRaidPoints", 0)
            val currentSuccessfulTackles = sharedPrefs.getInt("player_${p.name}_successfulTackles", 0)
            val currentUnsuccessfulTackles = sharedPrefs.getInt("player_${p.name}_unsuccessfulTackles", 0)
            val currentTotalTacklePoints = sharedPrefs.getInt("player_${p.name}_totalTacklePoints", 0)
            val currentOverallTotalPoints = sharedPrefs.getInt("player_${p.name}_overallTotalPoints", 0)
            val currentTotalMatches = sharedPrefs.getInt("player_${p.name}_totalMatches", 0)

            editor.putInt("player_${p.name}_successfulRaids", currentSuccessfulRaids + p.successfulRaids)
            editor.putInt("player_${p.name}_unsuccessfulRaids", currentUnsuccessfulRaids + p.unsuccessfulRaids)
            editor.putInt("player_${p.name}_totalRaidPoints", currentTotalRaidPoints + p.totalRaidPoints)
            editor.putInt("player_${p.name}_successfulTackles", currentSuccessfulTackles + p.successfulTackles)
            editor.putInt("player_${p.name}_unsuccessfulTackles", currentUnsuccessfulTackles + p.unsuccessfulTackles)
            editor.putInt("player_${p.name}_totalTacklePoints", currentTotalTacklePoints + p.totalTacklePoints)
            editor.putInt("player_${p.name}_overallTotalPoints", currentOverallTotalPoints + p.overallTotalPoints)
            editor.putInt("player_${p.name}_totalMatches", currentTotalMatches + 1)
        }

        // Also increment totalMatches by 1 for the remaining benched opponent players not in oppSquadState,
        // to make sure everyone from both teams gets registered
        val opponentFullList = getPlayersForTeam(selectedOpponentTeam.name)
        val oppActiveNames = oppSquad.map { it.name }.toSet()
        opponentFullList.forEach { p ->
            if (p.name !in oppActiveNames) {
                val currentMatches = sharedPrefs.getInt("player_${p.name}_totalMatches", 0)
                editor.putInt("player_${p.name}_totalMatches", currentMatches + 1)
            }
        }
        editor.apply()
    }

    val resetStats: () -> Unit = {
        statsMatchesPlayed = 0
        statsMatchesWon = 0
        statsMatchesLost = 0
        statsMatchesTied = 0
        statsTotalUserPoints = 0
        statsTotalOpponentPoints = 0
        statsTotalRaidPoints = 0
        statsTotalTacklePoints = 0
        statsHighestMatchScore = 0

        val editor = sharedPrefs.edit()
        editor.putInt("stats_matches_played", 0)
        editor.putInt("stats_matches_won", 0)
        editor.putInt("stats_matches_lost", 0)
        editor.putInt("stats_matches_tied", 0)
        editor.putInt("stats_total_user_points", 0)
        editor.putInt("stats_total_opponent_points", 0)
        editor.putInt("stats_total_raid_points", 0)
        editor.putInt("stats_total_tackle_points", 0)
        editor.putInt("stats_highest_match_score", 0)

        // Clear all player-specific entries explicitly to avoid scope-related issues
        val keysToRemove = sharedPrefs.all.keys.filter { it.startsWith("player_") }
        keysToRemove.forEach { key ->
            editor.remove(key)
        }
        editor.apply()
    }
    
    // Global vault economy parameters tracked persistently
    var goldCoins by remember { mutableIntStateOf(GameProgressRepository.getCoins(sharedPrefs)) }
    var primeGems by remember { mutableIntStateOf(GameProgressRepository.getGems(sharedPrefs)) }
    var customSquadCount by remember { mutableIntStateOf(GameProgressRepository.getCustomSquad(sharedPrefs).size) }
    var claimableRewardsCount by remember { mutableIntStateOf(GameProgressRepository.getClaimableCount(sharedPrefs)) }

    val refreshWallet: () -> Unit = {
        goldCoins = GameProgressRepository.getCoins(sharedPrefs)
        primeGems = GameProgressRepository.getGems(sharedPrefs)
        customSquadCount = GameProgressRepository.getCustomSquad(sharedPrefs).size
        claimableRewardsCount = GameProgressRepository.getClaimableCount(sharedPrefs)
    }

    var launchedFromSquadManager by remember { mutableStateOf(false) }
    var isCareerMatchActive by remember { mutableStateOf(false) }
    var careerOpponentName by remember { mutableStateOf("") }

    // Player squad roster size (max of custom squad and active team)
    val squadCount = remember(selectedYourTeam.name, customSquadCount) {
        maxOf(customSquadCount, getPlayersForTeam(selectedYourTeam.name).size)
    }

    BackHandler(enabled = activeScreen != ActiveScreen.HOME) {
        val isTournActive = sharedPrefs.getBoolean("tournament_active", false)
        activeScreen = when (activeScreen) {
            ActiveScreen.MATCH_PREVIEW -> if (launchedFromSquadManager) ActiveScreen.MATCH_CONFIG else ActiveScreen.PLAYER_SELECTION
            ActiveScreen.PLAYER_SELECTION -> if (isTournActive) ActiveScreen.TOURNAMENT else ActiveScreen.MATCH_CONFIG
            ActiveScreen.MATCH_CONFIG -> if (launchedFromSquadManager) ActiveScreen.SQUAD_MANAGER else ActiveScreen.QUICK_MATCH
            ActiveScreen.MATCH_ARENA -> when {
                isCareerMatchActive -> {
                    isCareerMatchActive = false
                    ActiveScreen.CAREER_MODE
                }
                isTournActive -> ActiveScreen.TOURNAMENT
                else -> ActiveScreen.HOME
            }
            else -> ActiveScreen.HOME
        }
    }

    val backgroundBottom = if (currentDarkModeState.value) Color(0xFF070B16) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(StadiumNavy, backgroundBottom)
                )
            )
    ) {
        // Render current screen based on active selection with slick transitions
        AnimatedContent(
            targetState = activeScreen,
            transitionSpec = {
                slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith
                slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                ActiveScreen.HOME -> {
                    HomeScreen(
                        coins = goldCoins,
                        gems = primeGems,
                        squadCount = squadCount,
                        matchesPlayed = statsMatchesPlayed,
                        matchesWon = statsMatchesWon,
                        claimableRewardsCount = claimableRewardsCount,
                        onScreenSelect = {
                            refreshWallet()
                            activeScreen = it
                        }
                    )
                }
                ActiveScreen.QUICK_MATCH -> {
                    QuickMatchTeamSelectionScreen(
                        onBack = { activeScreen = ActiveScreen.HOME },
                        onNext = { yourTeam, oppTeam ->
                            selectedYourTeam = yourTeam
                            selectedOpponentTeam = oppTeam
                            launchedFromSquadManager = false
                            isCareerMatchActive = false
                            activeScreen = ActiveScreen.MATCH_CONFIG
                        }
                    )
                }
                ActiveScreen.MATCH_CONFIG -> {
                    MatchConfigSelectionScreen(
                        yourTeam = selectedYourTeam,
                        opponentTeam = selectedOpponentTeam,
                        selectedDifficulty = selectedDifficulty,
                        selectedTiming = selectedTiming,
                        onBack = {
                            activeScreen = if (launchedFromSquadManager) ActiveScreen.SQUAD_MANAGER else ActiveScreen.QUICK_MATCH
                        },
                        onDifficultySelected = { selectedDifficulty = it },
                        onTimingSelected = { selectedTiming = it },
                        onNext = {
                            activeScreen = if (launchedFromSquadManager) ActiveScreen.MATCH_PREVIEW else ActiveScreen.PLAYER_SELECTION
                        }
                    )
                }
                ActiveScreen.PLAYER_SELECTION -> {
                    val isTournActive = sharedPrefs.getBoolean("tournament_active", false)
                    PlayerSelectionScreen(
                        team = selectedYourTeam,
                        onBack = {
                            if (isTournActive) {
                                activeScreen = ActiveScreen.TOURNAMENT
                            } else {
                                activeScreen = ActiveScreen.MATCH_CONFIG
                            }
                        },
                        onPlayersConfirmed = { players ->
                            selectedPlayers = players
                            activeScreen = ActiveScreen.MATCH_PREVIEW
                        }
                    )
                }
                ActiveScreen.MATCH_PREVIEW -> {
                    MatchPreviewScreen(
                        yourTeam = selectedYourTeam,
                        opponentTeam = selectedOpponentTeam,
                        difficulty = selectedDifficulty,
                        timing = selectedTiming,
                        selectedPlayers = selectedPlayers,
                        onBack = {
                            activeScreen = if (launchedFromSquadManager) ActiveScreen.MATCH_CONFIG else ActiveScreen.PLAYER_SELECTION
                        },
                        onStartMatch = { activeScreen = ActiveScreen.MATCH_ARENA }
                    )
                }
                ActiveScreen.MATCH_ARENA -> {
                    val isTournActive = sharedPrefs.getBoolean("tournament_active", false)
                    MatchArenaScreen(
                        yourTeam = selectedYourTeam,
                        opponentTeam = selectedOpponentTeam,
                        difficulty = selectedDifficulty,
                        timing = selectedTiming,
                        selectedPlayers = selectedPlayers,
                        onMatchFinished = { userScore, opponentScore, yourRaid, yourTackle, oppRaid, oppTackle, yourSquad, yourBench, oppSquad ->
                            updateStats(userScore, opponentScore, yourRaid, yourTackle, oppRaid, oppTackle, yourSquad, yourBench, oppSquad)
                            val rewardCoins = when {
                                userScore > opponentScore -> 250
                                userScore == opponentScore -> 140
                                else -> 80
                            }
                            GameProgressRepository.addCurrency(sharedPrefs, coinsDelta = rewardCoins)
                            GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_match", 1)
                            if (userScore > opponentScore) {
                                GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_win", 1)
                            }
                            if (yourRaid > 0) GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_raid", yourRaid)
                            if (yourTackle > 0) GameProgressRepository.incrementQuestProgress(sharedPrefs, "q_tackle", yourTackle)
                            if (isCareerMatchActive && careerOpponentName.isNotBlank()) {
                                GameProgressRepository.recordCareerMatchResult(
                                    prefs = sharedPrefs,
                                    opponentName = careerOpponentName,
                                    userScore = userScore,
                                    oppScore = opponentScore,
                                    awardMatchCoinsAndQuests = false
                                )
                            }
                            refreshWallet()
                            val playMatchId = sharedPrefs.getInt("tournament_playing_match_id", -1)
                            if (isTournActive && playMatchId != -1) {
                                saveTournamentMatchResult(sharedPrefs, playMatchId, userScore, opponentScore, yourSquad, yourBench, oppSquad)
                            }
                        },
                        onBack = {
                            isCareerMatchActive = false
                            launchedFromSquadManager = false
                            activeScreen = ActiveScreen.HOME
                        },
                        onBackToTournament = if (isTournActive && !isCareerMatchActive) {
                            {
                                sharedPrefs.edit().putInt("tournament_playing_match_id", -1).apply()
                                activeScreen = ActiveScreen.TOURNAMENT
                            }
                        } else null,
                        onBackToCareer = if (isCareerMatchActive) {
                            {
                                isCareerMatchActive = false
                                activeScreen = ActiveScreen.CAREER_MODE
                            }
                        } else null
                    )
                }
                ActiveScreen.STATS -> {
                    TeamStatisticsScreen(
                        sharedPrefs = sharedPrefs,
                        matchesPlayed = statsMatchesPlayed,
                        matchesWon = statsMatchesWon,
                        matchesLost = statsMatchesLost,
                        matchesTied = statsMatchesTied,
                        totalUserPoints = statsTotalUserPoints,
                        totalOpponentPoints = statsTotalOpponentPoints,
                        totalRaidPoints = statsTotalRaidPoints,
                        totalTacklePoints = statsTotalTacklePoints,
                        highestMatchScore = statsHighestMatchScore,
                        onResetStats = resetStats,
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.SETTINGS -> {
                    GameSettingsScreen(
                        sharedPrefs = sharedPrefs,
                        onResetAllData = {
                            resetStats()
                            refreshWallet()
                        },
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.TOURNAMENT -> {
                    TournamentScreen(
                        onBack = { activeScreen = ActiveScreen.HOME },
                        onStartTournamentMatch = { yourTeam, oppTeam, difficulty, timing, players ->
                            selectedYourTeam = yourTeam
                            selectedOpponentTeam = oppTeam
                            selectedDifficulty = difficulty
                            selectedTiming = timing
                            selectedPlayers = players
                            launchedFromSquadManager = false
                            isCareerMatchActive = false
                            activeScreen = ActiveScreen.MATCH_ARENA
                        }
                    )
                }
                ActiveScreen.AUCTION -> {
                    AuctionScreen(
                        sharedPrefs = sharedPrefs,
                        coins = goldCoins,
                        onCurrencyUpdated = refreshWallet,
                        onOpenSquadManager = { activeScreen = ActiveScreen.SQUAD_MANAGER },
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.SQUAD_MANAGER -> {
                    SquadManagerScreen(
                        sharedPrefs = sharedPrefs,
                        onPlayWithSquad = { starters ->
                            selectedYourTeam = GameProgressRepository.getCustomTeam()
                            selectedOpponentTeam = PKL_SEASON_10_TEAMS.random()
                            selectedPlayers = starters
                            launchedFromSquadManager = true
                            isCareerMatchActive = false
                            activeScreen = ActiveScreen.MATCH_CONFIG
                        },
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.REWARDS_SHOP -> {
                    RewardsShopScreen(
                        sharedPrefs = sharedPrefs,
                        coins = goldCoins,
                        gems = primeGems,
                        onCurrencyUpdated = refreshWallet,
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.TRAINING_DRILLS -> {
                    TrainingDrillsScreen(
                        sharedPrefs = sharedPrefs,
                        onCurrencyUpdated = refreshWallet,
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
                ActiveScreen.CAREER_MODE -> {
                    CareerModeScreen(
                        sharedPrefs = sharedPrefs,
                        coins = goldCoins,
                        onCurrencyUpdated = refreshWallet,
                        onLaunchCareerMatch = { yourTeam, oppTeam, starters ->
                            selectedYourTeam = yourTeam
                            selectedOpponentTeam = oppTeam
                            selectedPlayers = starters
                            launchedFromSquadManager = false
                            isCareerMatchActive = true
                            careerOpponentName = oppTeam.name
                            sharedPrefs.edit().putInt("tournament_playing_match_id", -1).apply()
                            activeScreen = ActiveScreen.MATCH_ARENA
                        },
                        onBack = { activeScreen = ActiveScreen.HOME }
                    )
                }
            }
        }
    }
}

// HomeScreen and navigation hub cards are modularized in com.example.ui.home.HomeScreen

// ===============================================
// QUICK MATCH: PRO KABADDI SEASON 10 TEAM SELECT
// ===============================================
@Composable
fun QuickMatchTeamSelectionScreen(
    onBack: () -> Unit,
    onNext: (yourTeam: PklTeam, opponentTeam: PklTeam) -> Unit
) {
    var yourTeam by remember { mutableStateOf(PKL_SEASON_10_TEAMS[1]) }      // Default: Puneri Paltan
    var opponentTeam by remember { mutableStateOf(PKL_SEASON_10_TEAMS[2]) }  // Default: Patna Pirates
    var selectionMode by remember { mutableStateOf(0) }                      // 0: Your Team, 1: Opponent Team

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App top header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_close_team_selection")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Text(
                text = "PKL 10 TEAM SELECT",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = ChampionGold
            )

            Box(modifier = Modifier.size(48.dp))
        }

        // 1. Interactive Duel Matchup Cards Preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Your Team Slot Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectionMode == 0) yourTeam.primaryColor.copy(alpha = 0.25f) else Color(0xFF1E293B))
                    .border(
                        2.dp,
                        if (selectionMode == 0) ClayOrange else Color(0xFF2E395E).copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectionMode = 0 }
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "YOUR TEAM",
                        color = if (selectionMode == 0) ClayOrange else SlateMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(yourTeam.primaryColor, CircleShape)
                            .border(1.5.dp, PlainWhite.copy(0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = yourTeam.nickName.take(1),
                            color = PlainWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = yourTeam.name,
                        color = PlainWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        repeat(5) { starIndex ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (starIndex < yourTeam.championStars) ChampionGold else Color(0x22FFFFFF),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }

            // VS Connector Badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(ChampionGold, CircleShape)
                    .border(2.dp, PlainWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = StadiumNavy
                )
            }

            // Opponent Team Slot Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectionMode == 1) opponentTeam.primaryColor.copy(alpha = 0.25f) else Color(0xFF1E293B))
                    .border(
                        2.dp,
                        if (selectionMode == 1) RaidCyan else Color(0xFF2E395E).copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectionMode = 1 }
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "OPPONENT",
                        color = if (selectionMode == 1) RaidCyan else SlateMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(opponentTeam.primaryColor, CircleShape)
                            .border(1.5.dp, PlainWhite.copy(0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opponentTeam.nickName.take(1),
                            color = PlainWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = opponentTeam.name,
                        color = PlainWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        repeat(5) { starIndex ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (starIndex < opponentTeam.championStars) ChampionGold else Color(0x22FFFFFF),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Selection Guide text banner
        Text(
            text = if (selectionMode == 0) "👇 Select 'YOUR TEAM' below:" else "👇 Select 'OPPONENT TEAM' below:",
            color = if (selectionMode == 0) ClayOrange else RaidCyan,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 4.dp).align(Alignment.CenterHorizontally)
        )

        // 2. PKL 10 Grid List displaying Teams in elegant brand color cards (including custom franchise)
        val availableTeams = remember { listOf(GameProgressRepository.getCustomTeam()) + PKL_SEASON_10_TEAMS }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chunkedTeams = availableTeams.chunked(2)
            items(chunkedTeams) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { team ->
                        val isSelected = (selectionMode == 0 && yourTeam == team) || (selectionMode == 1 && opponentTeam == team)
                        val isOtherSelected = (selectionMode == 0 && opponentTeam == team) || (selectionMode == 1 && yourTeam == team)

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else if (isOtherSelected) 1.dp else 0.5.dp,
                                    color = if (isSelected) {
                                        if (selectionMode == 0) ClayOrange else RaidCyan
                                    } else if (isOtherSelected) {
                                        Color.White.copy(0.2f)
                                    } else {
                                        Color(0xFF2E395E).copy(0.4f)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (selectionMode == 0) {
                                        if (opponentTeam != team) {
                                            yourTeam = team
                                            selectionMode = 1 // auto-toggle selection model
                                        } else {
                                            opponentTeam = yourTeam
                                            yourTeam = team
                                            selectionMode = 1
                                        }
                                    } else {
                                        if (yourTeam != team) {
                                            opponentTeam = team
                                        } else {
                                            yourTeam = opponentTeam
                                            opponentTeam = team
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF131A32) else Color(0x80131A32)
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Subtle brand indicator stripes
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.12f)
                                        .background(team.primaryColor)
                                        .align(Alignment.CenterStart)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.04f)
                                        .background(team.secondaryColor)
                                        .align(Alignment.CenterStart)
                                        .offset(x = 12.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(start = 24.dp, end = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = team.name,
                                            color = PlainWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            lineHeight = 14.sp
                                        )
                                        Text(
                                            text = if (team.name == "Kabaddi Kings XI") "Your Custom Franchise" else "Season 10 Squad",
                                            color = SlateMuted,
                                            fontSize = 9.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = if (selectionMode == 0) ClayOrange else RaidCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else if (isOtherSelected) {
                                        Text(
                                            text = "USED",
                                            color = SlateMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp,
                                            modifier = Modifier
                                                .background(Color(0x33FFFFFF), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. NEXT BUTTON at bottom
        Button(
            onClick = { onNext(yourTeam, opponentTeam) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_team_select_next"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "NEXT",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = AlwaysWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
        }
    }
}

// ==========================================
// QUICK MATCH: MATCH DIFFICULTY & TIMING SELECTION SCREEN
// ==========================================
@Composable
fun MatchConfigSelectionScreen(
    yourTeam: PklTeam,
    opponentTeam: PklTeam,
    selectedDifficulty: String,
    selectedTiming: Int,
    onBack: () -> Unit,
    onDifficultySelected: (String) -> Unit,
    onTimingSelected: (Int) -> Unit,
    onNext: () -> Unit
) {
    val difficulties = listOf(
        Triple("Easy", "Casual training play. Relaxed defense.", Color(0xFF10B981)),
        Triple("Medium", "Standard competitive league parameters.", RaidCyan),
        Triple("Hard", "Tactical raiders & aggressive tackle chains.", ClayOrange),
        Triple("Pro", "Legendary championship level showdown.", ChampionGold)
    )

    val timings = listOf(5, 10, 15, 20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_close_match_config")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Text(
                text = "MATCH CONFIGURATION",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = ChampionGold
            )

            Box(modifier = Modifier.size(48.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match Preview Row banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MatSlate.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color(0xFF2E395E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(yourTeam.primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(yourTeam.nickName.take(1), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(yourTeam.nickName, color = PlainWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ClayOrange.copy(0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("VS", color = ClayOrange, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(opponentTeam.primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(opponentTeam.nickName.take(1), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(opponentTeam.nickName, color = PlainWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Select Difficulty section title
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(18.dp))
                    Text(
                        text = "SELECT DIFFICULTY LEVEL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PlainWhite,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Difficulty Cards (Easy, Medium, Hard, Pro)
            items(difficulties.size) { index ->
                val (name, desc, color) = difficulties[index]
                val isSelected = selectedDifficulty == name

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) color else Color(0xFF2E395E).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onDifficultySelected(name) }
                        .testTag("btn_difficulty_${name.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MatSlate else Color(0x331E2640)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name.uppercase(),
                                color = if (isSelected) PlainWhite else PlainWhite.copy(0.7f),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = desc,
                                color = SlateMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Select Timing title
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = ClayOrange, modifier = Modifier.size(18.dp))
                    Text(
                        text = "SELECT MATCH DURATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PlainWhite,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Timing choices layout row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timings.forEach { mins ->
                        val isSelected = selectedTiming == mins
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ClayOrange else Color(0xFF2E395E).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onTimingSelected(mins) }
                                .testTag("btn_timing_$mins"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MatSlate else Color(0x331E2640)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (isSelected) ClayOrange else SlateMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$mins Min",
                                    color = PlainWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Tip/Guide Box
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ClayOrange.copy(0.1f))
                        .border(0.5.dp, ClayOrange.copy(0.2f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ClayOrange, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Match is played digitally via high-fidelity raid vs defense simulator. Chosen metrics dictate speed and block strength.",
                        color = PlainWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Next Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_config_next"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "START MATCH SIMULATION",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = AlwaysWhite
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next")
        }
    }
}

// ==========================================
// QUICK MATCH: PLAYER SELECTION SCREEN (7 PLAYERS)
// ==========================================
@Composable
fun PlayerSelectionScreen(
    team: PklTeam,
    onBack: () -> Unit,
    onPlayersConfirmed: (List<KabaddiPlayer>) -> Unit
) {
    val context = LocalContext.current
    val availablePlayers = remember(team.name) {
        if (team.name == "Kabaddi Kings XI") {
            val prefs = context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
            GameProgressRepository.getCustomSquad(prefs).map { it.toKabaddiPlayer() }
        } else {
            getPlayersForTeam(team.name)
        }
    }
    val selected = remember(team.name) { mutableStateListOf<KabaddiPlayer>() }
    var activeFilter by remember { mutableStateOf<PlayerRole?>(null) } // null means "ALL"

    val raidersSelected = selected.count { it.role == PlayerRole.RAIDER }
    val defendersSelected = selected.count { it.role == PlayerRole.DEFENDER }
    val allRoundersSelected = selected.count { it.role == PlayerRole.ALL_ROUNDER }
    val totalSelected = selected.size

    val isValid = totalSelected == 7 &&
            raidersSelected >= 2 &&
            defendersSelected >= 2 &&
            allRoundersSelected >= 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // App Header Sector
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_close_player_selection")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${team.nickName.uppercase()} SQUAD",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ChampionGold
                )
                Text(
                    text = "SELECT REQUISITE 7 STARTING LINEUP",
                    fontSize = 10.sp,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(team.primaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = team.nickName.take(1),
                    color = PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }

        // Live Requirement Counter Deck
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MatSlate.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF2E395E))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LINEUP REQUIREMENTS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PlainWhite,
                        letterSpacing = 0.5.sp
                    )
                    
                    Text(
                        text = "$totalSelected / 7 SELECTED",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (totalSelected == 7) RaidCyan else ClayOrange
                    )
                }

                HorizontalDivider(color = Color(0xFF2E395E), thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Raider check
                    val rOk = raidersSelected >= 2
                    RequirementChip(
                        label = "Raiders",
                        current = raidersSelected,
                        target = 2,
                        isMet = rOk,
                        activeColor = RaidCyan
                    )

                    // Defender check
                    val dOk = defendersSelected >= 2
                    RequirementChip(
                        label = "Defenders",
                        current = defendersSelected,
                        target = 2,
                        isMet = dOk,
                        activeColor = Color(0xFF10B981)
                    )

                    // All Rounder check
                    val aOk = allRoundersSelected >= 1
                    RequirementChip(
                        label = "All Rounders",
                        current = allRoundersSelected,
                        target = 1,
                        isMet = aOk,
                        activeColor = ChampionGold
                    )
                }

                if (!isValid) {
                    Text(
                       text = "⚠️ Must select exactly 7 starters: min 2 Raiders, 2 Defenders, and 1 All-Rounder.",
                       fontSize = 10.sp,
                       lineHeight = 13.sp,
                       color = ClayOrange.copy(0.9f),
                       fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                       text = "✅ Ideal 7-player team set! Ready to challenge the opponent.",
                       fontSize = 10.sp,
                       color = Color(0xFF10B981),
                       fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Role Filters Panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                Pair(null, "ALL"),
                Pair(PlayerRole.RAIDER, "RAIDERS"),
                Pair(PlayerRole.DEFENDER, "DEFENDERS"),
                Pair(PlayerRole.ALL_ROUNDER, "ALL-ROUNDERS")
            )

            filterOptions.forEach { (role, label) ->
                val isSelected = activeFilter == role
                val borderCol = if (isSelected) team.primaryColor else Color(0xFF2E395E)
                val bgCol = if (isSelected) team.primaryColor.copy(0.15f) else Color(0x331E2640)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgCol)
                        .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                        .clickable { activeFilter = role }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) PlainWhite else SlateMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Player Roster Grid List
        val filteredPlayers = if (activeFilter == null) {
            availablePlayers
        } else {
            availablePlayers.filter { it.role == activeFilter }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPlayers) { player ->
                val isSelected = selected.contains(player)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) team.primaryColor else Color(0xFF2E395E).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            if (isSelected) {
                                selected.remove(player)
                            } else {
                                if (totalSelected < 7) {
                                    selected.add(player)
                                }
                            }
                        }
                        .testTag("player_card_${player.name.replace(" ", "_").lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MatSlate else Color(0x221E2640)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left Section: Jersey & Role Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Jersey Sphere
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) team.primaryColor else MatSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${player.jerseyNumber}",
                                    color = PlainWhite,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }

                            // Player Name and Role Badge
                            Column {
                                Text(
                                    text = player.name,
                                    color = PlainWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Role Badge
                                val (badgeBg, badgeText, badgeLabel) = when (player.role) {
                                    PlayerRole.RAIDER -> Triple(RaidCyan.copy(0.12f), RaidCyan, "RAIDER")
                                    PlayerRole.DEFENDER -> Triple(Color(0xFF10B981).copy(0.12f), Color(0xFF10B981), "DEFENDER")
                                    PlayerRole.ALL_ROUNDER -> Triple(ChampionGold.copy(0.12f), ChampionGold, "ALL ROUNDER")
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        color = badgeText,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        // Right Section: Abilities & Checkbox State
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Abilities
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = "Raid Ability", tint = RaidCyan, modifier = Modifier.size(12.dp))
                                    Text(text = "R: ${player.raidingAbility}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlainWhite)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Defend Ability", tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                    Text(text = "D: ${player.defendingAbility}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PlainWhite)
                                }
                            }

                            // Interactive selection circle indicator
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) team.primaryColor else Color(0xFF151D33))
                                    .border(1.dp, if (isSelected) Color.Transparent else Color(0xFF2E395E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = PlainWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Next Action button
        Button(
            onClick = { if (isValid) onPlayersConfirmed(selected.toList()) },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_player_sel_next"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite,
                disabledContainerColor = Color(0xFF232D4C),
                disabledContentColor = SlateMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "START DECK DUEL",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = if (isValid) AlwaysWhite else SlateMuted
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next", tint = if (isValid) AlwaysWhite else SlateMuted)
        }
    }
}

@Composable
fun RequirementChip(
    label: String,
    current: Int,
    target: Int,
    isMet: Boolean,
    activeColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF151D33))
            .border(0.5.dp, if (isMet) activeColor else Color(0xFF2E395E), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isMet) activeColor else SlateMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "$label: $current/$target",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isMet) PlainWhite else SlateMuted
        )
    }
}

// ==========================================
// QUICK MATCH: TEAM & PLAYER PREVIEW SCREEN
// ==========================================
@Composable
fun MatchPreviewScreen(
    yourTeam: PklTeam,
    opponentTeam: PklTeam,
    difficulty: String,
    timing: Int,
    selectedPlayers: List<KabaddiPlayer>,
    onBack: () -> Unit,
    onStartMatch: () -> Unit
) {
    // 1. Calculate Abilities based on roster
    val context = LocalContext.current
    val friendlyPlayers = remember(selectedPlayers, yourTeam.name) {
        when {
            selectedPlayers.isNotEmpty() -> selectedPlayers
            yourTeam.name == "Kabaddi Kings XI" -> {
                val prefs = context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
                GameProgressRepository.getStartingSevenPlayers(prefs)
            }
            else -> getPlayersForTeam(yourTeam.name)
        }
    }
    val opponentPlayers = remember(opponentTeam.name) {
        if (opponentTeam.name == "Kabaddi Kings XI") {
            val prefs = context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE)
            GameProgressRepository.getCustomSquad(prefs).map { it.toKabaddiPlayer() }
        } else {
            getPlayersForTeam(opponentTeam.name)
        }
    }
    
    // For opponent, we can sort by overall power and take the top 7 as their active starting lineup
    val opponentStarting = remember(opponentPlayers) {
        opponentPlayers.sortedByDescending { it.raidingAbility + it.defendingAbility }.take(7)
    }

    // Averages
    val yourAvgRaiding = remember(friendlyPlayers) {
        if (friendlyPlayers.isEmpty()) 0 else friendlyPlayers.map { it.raidingAbility }.average().toInt()
    }
    val yourAvgDefending = remember(friendlyPlayers) {
        if (friendlyPlayers.isEmpty()) 0 else friendlyPlayers.map { it.defendingAbility }.average().toInt()
    }
    val yourOverall = (yourAvgRaiding + yourAvgDefending) / 2

    val oppAvgRaiding = remember(opponentStarting) {
        if (opponentStarting.isEmpty()) 0 else opponentStarting.map { it.raidingAbility }.average().toInt()
    }
    val oppAvgDefending = remember(opponentStarting) {
        if (opponentStarting.isEmpty()) 0 else opponentStarting.map { it.defendingAbility }.average().toInt()
    }
    val oppOverall = (oppAvgRaiding + oppAvgDefending) / 2

    // Tab Selection for displaying players
    var selectedTab by remember { mutableStateOf(0) } // 0 = Your Team, 1 = Opponent Team

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_close_match_preview")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PlainWhite
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MATCHUP PREVIEW",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ChampionGold
                )
                Text(
                    text = "TEAM STRENGTH & SQUAD EVALUATION",
                    fontSize = 10.sp,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Simple empty placeholder to balance row spacing
            Box(modifier = Modifier.size(48.dp))
        }

        // --- CONTENT AREA (SCROLLABLE) ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Team Strengths Comparison Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MatSlate),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Head-To-Head Team visual banner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = yourTeam.nickName.uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = yourTeam.primaryColor
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(yourTeam.championStars) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Champion Star",
                                            tint = ChampionGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    if (yourTeam.championStars > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "YOUR SQUAD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateMuted
                                    )
                                }
                            }

                            // VS Badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ClayOrange)
                                    .border(1.dp, ChampionGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "VS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = PlainWhite
                                )
                            }

                            // Right Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = opponentTeam.nickName.uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = opponentTeam.primaryColor
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "OPPONENT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateMuted
                                    )
                                    if (opponentTeam.championStars > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    repeat(opponentTeam.championStars) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Champion Star",
                                            tint = ChampionGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFF2E395E), thickness = 0.5.dp)

                        // 1. Overall Team Rating comparison
                        ComparisonBar(
                            label = "OVERALL TEAM RATING",
                            leftValue = yourOverall,
                            rightValue = oppOverall,
                            leftColor = yourTeam.primaryColor,
                            rightColor = opponentTeam.primaryColor
                        )

                        // 2. Raiding comparison
                        ComparisonBar(
                            label = "TEAM RAID POWER (ATTACK)",
                            leftValue = yourAvgRaiding,
                            rightValue = oppAvgRaiding,
                            leftColor = RaidCyan,
                            rightColor = opponentTeam.primaryColor.copy(alpha = 0.8f)
                        )

                        // 3. Defending comparison
                        ComparisonBar(
                            label = "TEAM TACKLE POWER (DEFENSE)",
                            leftValue = yourAvgDefending,
                            rightValue = oppAvgDefending,
                            leftColor = DefenderBlue,
                            rightColor = opponentTeam.secondaryColor
                        )
                    }
                }
            }

            // Section 2: Tab selection button panel
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF151D33))
                        .padding(4.dp)
                ) {
                    TabPill(
                        text = "${yourTeam.nickName.uppercase()} SQUAD (${friendlyPlayers.size})",
                        isSelected = selectedTab == 0,
                        activeColor = yourTeam.primaryColor,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 0 }
                    )
                    TabPill(
                        text = "${opponentTeam.nickName.uppercase()} SQUAD (${opponentPlayers.size})",
                        isSelected = selectedTab == 1,
                        activeColor = opponentTeam.primaryColor,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 1 }
                    )
                }
            }

            // Section 3: Player roster cards
            val activePlayersList = if (selectedTab == 0) friendlyPlayers else opponentPlayers
            items(activePlayersList) { player ->
                PlayerAbilityCard(
                    player = player,
                    accentColor = if (selectedTab == 0) yourTeam.primaryColor else opponentTeam.primaryColor,
                    isStartingLineup = if (selectedTab == 0) true else opponentStarting.contains(player)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- BOTTOM NEXT ACTION BUTTON ---
        Button(
            onClick = onStartMatch,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_start_match_from_preview"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "START THE MATCH NOW",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Flash",
                tint = ChampionGold
            )
        }
    }
}

@Composable
fun TabPill(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = if (isSelected) PlainWhite else SlateMuted
        )
    }
}

@Composable
fun ComparisonBar(
    label: String,
    leftValue: Int,
    rightValue: Int,
    leftColor: Color,
    rightColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$leftValue",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = leftColor
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateMuted,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$rightValue",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = rightColor
            )
        }
        
        // Custom progress representation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF151D33)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left progress bar (takes proportion of left / (left+right))
            val leftWeight = if (leftValue + rightValue == 0) 0.5f else leftValue.toFloat() / (leftValue + rightValue)
            Box(
                modifier = Modifier
                    .weight(leftWeight)
                    .fillMaxHeight()
                    .background(leftColor)
            )
            // Tiny divider in physical bar
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF0D1527))
            )
            // Right progress bar
            val rightWeight = if (leftValue + rightValue == 0) 0.5f else rightValue.toFloat() / (leftValue + rightValue)
            Box(
                modifier = Modifier
                    .weight(rightWeight)
                    .fillMaxHeight()
                    .background(rightColor)
            )
        }
    }
}

@Composable
fun PlayerAbilityCard(
    player: KabaddiPlayer,
    accentColor: Color,
    isStartingLineup: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151D33)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Jersey Number Circle Box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${player.jerseyNumber}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }

            // Player basic description
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PlainWhite
                    )
                    
                    if (isStartingLineup) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ChampionGold.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STARTER",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChampionGold
                            )
                        }
                    }
                }
                
                // Role tag with custom visual indicators
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val roleLabel = when (player.role) {
                        PlayerRole.RAIDER -> "Raider"
                        PlayerRole.DEFENDER -> "Defender"
                        PlayerRole.ALL_ROUNDER -> "All-Rounder"
                    }
                    val roleColor = when (player.role) {
                        PlayerRole.RAIDER -> RaidCyan
                        PlayerRole.DEFENDER -> DefenderBlue
                        PlayerRole.ALL_ROUNDER -> ChampionGold
                    }
                    val roleIcon = when (player.role) {
                        PlayerRole.RAIDER -> Icons.Default.FlashOn
                        PlayerRole.DEFENDER -> Icons.Default.Shield
                        PlayerRole.ALL_ROUNDER -> Icons.Default.Star
                    }
                    
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = roleLabel,
                        tint = roleColor,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = roleLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = roleColor
                    )
                }
            }

            // Ability stats on the right side
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Raid Stat
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RAID",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = RaidCyan
                    )
                    Text(
                        text = "${player.raidingAbility}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = PlainWhite
                    )
                }
                
                // Tackle Stat
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TACKLE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = DefenderBlue
                    )
                    Text(
                        text = "${player.defendingAbility}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = PlainWhite
                    )
                }
            }
        }
    }
}

enum class AggressionMode(val displayName: String) {
    DEFENSIVE("Defensive"),
    NORMAL("Normal"),
    AGGRESSIVE("Aggressive"),
    VERY_AGGRESSIVE("Very Aggressive")
}

class LivePlayerState(
    val name: String,
    val role: PlayerRole,
    val jerseyNumber: Int,
    val raidingAbility: Int,
    val defendingAbility: Int,
    initialHealth: Float = 100f,
    initialIsOut: Boolean = false
) {
    var health by mutableStateOf(initialHealth)
    var isOut by mutableStateOf(initialIsOut)
    var isSelectedForRaid by mutableStateOf(false)

    // Detailed stats trackers
    var successfulRaids by mutableStateOf(0)
    var unsuccessfulRaids by mutableStateOf(0)
    var totalRaidPoints by mutableStateOf(0)
    var successfulTackles by mutableStateOf(0)
    var unsuccessfulTackles by mutableStateOf(0)
    var totalTacklePoints by mutableStateOf(0)
    var overallTotalPoints by mutableStateOf(0)
}

@Composable
fun PlayerCircleOnMat(
    player: LivePlayerState,
    isActiveRaider: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    // Determine scale highlight for selection
    val scale by animateFloatAsState(
        targetValue = if (isActiveRaider) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Visibility is 10% if out, 100% active, highlighted if active raider
    val alphaFactor = if (player.isOut) 0.1f else 1.0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = alphaFactor
            )
            .clickable(enabled = !player.isOut) { onClick() }
            .padding(1.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circumference Health display
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 3.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val radius = diameter / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background stroke track
                drawCircle(
                    color = Color(0xFF1E2638),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Health Arc Ring - Neon/Vibrant, highly visible colors
                val healthColor = when {
                    player.health > 75f -> Color(0xFF39FF14) // Neon green
                    player.health > 45f -> Color(0xFFFFEA00) // Neon Yellow
                    player.health > 20f -> Color(0xFFFF5722) // Neon Orange
                    else -> Color(0xFFFE019A) // Neon Pink/Hot Red
                }

                drawArc(
                    color = healthColor,
                    startAngle = -90f,
                    sweepAngle = (player.health / 100f) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    size = Size(diameter, diameter),
                    topLeft = Offset(center.x - radius, center.y - radius)
                )
            }

            // Inner Badge matching the mockup's circular player node
            val cardBg = if (isActiveRaider) ChampionGold else Color(0xFF43C065)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(cardBg)
                    .border(
                        width = if (isActiveRaider) 1.5.dp else 0.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val roleIndicator = when (player.role) {
                    PlayerRole.RAIDER -> "R"
                    PlayerRole.DEFENDER -> "D"
                    PlayerRole.ALL_ROUNDER -> "A"
                }

                Text(
                    text = roleIndicator,
                    color = if (isActiveRaider) StadiumNavy else PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display first name only
        val firstName = player.name.split(" ").firstOrNull() ?: player.name
        Text(
            text = firstName,
            color = if (isActiveRaider) ChampionGold else PlainWhite,
            fontSize = 8.sp,
            fontWeight = if (isActiveRaider) FontWeight.Black else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun StrategyPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ChampionGold else Color(0xFF1E283F))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) StadiumNavy else PlainWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

// ==========================================
// QUICK MATCH: ARENA & LIVE PKL KICKOFF DUEL
// ==========================================
@Composable
fun MatchArenaScreen(
    yourTeam: PklTeam,
    opponentTeam: PklTeam,
    difficulty: String = "Medium",
    timing: Int = 10,
    selectedPlayers: List<KabaddiPlayer> = emptyList(),
    onMatchFinished: (
        userScore: Int,
        opponentScore: Int,
        yourRaidPoints: Int,
        yourTacklePoints: Int,
        oppRaidPoints: Int,
        oppTacklePoints: Int,
        yourSquad: List<LivePlayerState>,
        yourBench: List<LivePlayerState>,
        oppSquad: List<LivePlayerState>
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onBack: () -> Unit,
    onBackToTournament: (() -> Unit)? = null,
    onBackToCareer: (() -> Unit)? = null
) {
    var matchStarted by remember { mutableStateOf(false) }
    var matchEnded by remember { mutableStateOf(false) }
    var showScorecardDialog by remember { mutableStateOf(false) }
    var showSubstitutionDialog by remember { mutableStateOf(false) }
    var selectedStarterToSub by remember { mutableStateOf<LivePlayerState?>(null) }
    var selectedBenchedToSub by remember { mutableStateOf<LivePlayerState?>(null) }
    var subsRemaining by remember { mutableStateOf(5) }
    var userScore by remember { mutableStateOf(0) }
    var opponentScore by remember { mutableStateOf(0) }
    var hasUpdatedStats by remember { mutableStateOf(false) }

    // Represent the match clock configured by the selected page timing (minutes)
    var matchMinutes by remember(timing) { mutableStateOf(timing) }
    var matchSeconds by remember { mutableStateOf(0) }

    val logs = remember { mutableStateListOf<String>() }
    var isPaused by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE) }
    val equippedJersey = remember { GameProgressRepository.getEquippedJersey(sharedPrefs) }
    val equippedStadium = remember { GameProgressRepository.getEquippedStadium(sharedPrefs) }
    val commentaryEnabled = remember { sharedPrefs.getBoolean("settings_commentary_enabled", true) }

    val yourActiveJerseyColor = remember(equippedJersey, yourTeam.primaryColor) {
        when (equippedJersey) {
            "jersey_royal" -> Color(0xFFF59E0B)
            "jersey_cyber" -> Color(0xFF06B6D4)
            "jersey_emerald" -> Color(0xFF10B981)
            else -> yourTeam.primaryColor
        }
    }
    val courtMatColor = remember(equippedStadium) {
        when (equippedStadium) {
            "stadium_mumbai" -> Color(0xFF0B2545)
            "stadium_jaipur" -> Color(0xFF2B1038)
            else -> Color(0xFF0C1327)
        }
    }
    val courtBorderColor = remember(equippedStadium) {
        when (equippedStadium) {
            "stadium_mumbai" -> Color(0xFFFF6D00).copy(alpha = 0.75f)
            "stadium_jaipur" -> Color(0xFFE84393).copy(alpha = 0.75f)
            else -> ChampionGold.copy(0.4f)
        }
    }
    val courtLineColor = remember(equippedStadium) {
        when (equippedStadium) {
            "stadium_mumbai" -> Color(0xFF38BDF8).copy(alpha = 0.45f)
            "stadium_jaipur" -> ChampionGold.copy(alpha = 0.45f)
            else -> PlainWhite.copy(alpha = 0.35f)
        }
    }
    LaunchedEffect(Unit) {
        ArenaSoundEngine.playStadiumAmbienceCue(context)
    }

    val initialSpeedFloat = remember { sharedPrefs.getFloat("settings_game_speed", 1.0f) }
    val initialSpeedInt = remember(initialSpeedFloat) {
        when (initialSpeedFloat) {
            1.0f -> 1
            1.5f -> 2
            2.0f -> 3
            else -> 1
        }
    }
    var gameSpeed by remember { mutableStateOf(initialSpeedInt) }

    // Turn States
    var isYourTurn by remember { mutableStateOf(true) } // User raids first!
    var activeRaider by remember { mutableStateOf<LivePlayerState?>(null) }
    var raidTimeRemaining by remember { mutableStateOf(15) }

    // Aggression strategies
    var yourAggression by remember { mutableStateOf(AggressionMode.NORMAL) }
    var opponentAggression by remember { mutableStateOf(AggressionMode.NORMAL) }

    val friendlyPlayers = remember(selectedPlayers, yourTeam.name) {
        when {
            selectedPlayers.isNotEmpty() -> selectedPlayers
            yourTeam.name == "Kabaddi Kings XI" -> GameProgressRepository.getStartingSevenPlayers(sharedPrefs)
            else -> getPlayersForTeam(yourTeam.name)
        }
    }
    val opponentPlayers = remember(opponentTeam.name) {
        if (opponentTeam.name == "Kabaddi Kings XI") {
            GameProgressRepository.getCustomSquad(sharedPrefs).map { it.toKabaddiPlayer() }
        } else {
            getPlayersForTeam(opponentTeam.name)
        }
    }

    // Create persistent live player states on the court (exactly 7 players per team)
    val yourSquadState = remember(friendlyPlayers) {
        val starting = friendlyPlayers.take(7)
        val initialList = starting.map { p ->
            LivePlayerState(
                name = p.name,
                role = p.role,
                jerseyNumber = p.jerseyNumber,
                raidingAbility = p.raidingAbility,
                defendingAbility = p.defendingAbility
            )
        }
        mutableStateListOf<LivePlayerState>().apply { addAll(initialList) }
    }

    val oppSquadState = remember(opponentPlayers) {
        val starting = opponentPlayers.sortedByDescending { it.raidingAbility + it.defendingAbility }.take(7)
        val initialList = starting.map { p ->
            LivePlayerState(
                name = p.name,
                role = p.role,
                jerseyNumber = p.jerseyNumber,
                raidingAbility = p.raidingAbility,
                defendingAbility = p.defendingAbility
            )
        }
        mutableStateListOf<LivePlayerState>().apply { addAll(initialList) }
    }

    val yourBenchState = remember(yourTeam.name, friendlyPlayers) {
        val allTeamPlayers = if (yourTeam.name == "Kabaddi Kings XI") {
            GameProgressRepository.getCustomSquad(sharedPrefs).map { it.toKabaddiPlayer() }
        } else {
            getPlayersForTeam(yourTeam.name)
        }
        val starterNames = friendlyPlayers.map { it.name }.toSet()
        val bench = allTeamPlayers.filter { it.name !in starterNames }
        val liveBenchPlayers = bench.map { p ->
            LivePlayerState(
                name = p.name,
                role = p.role,
                jerseyNumber = p.jerseyNumber,
                raidingAbility = p.raidingAbility,
                defendingAbility = p.defendingAbility,
                initialHealth = 100f,
                initialIsOut = false
            )
        }
        mutableStateListOf<LivePlayerState>().apply { addAll(liveBenchPlayers) }
    }

    LaunchedEffect(matchEnded) {
        if (matchEnded && !hasUpdatedStats) {
            hasUpdatedStats = true
            ArenaSoundEngine.playBuzzer(context)
            val yourRaidPoints = yourSquadState.sumOf { it.totalRaidPoints }
            val yourTacklePoints = yourSquadState.sumOf { it.totalTacklePoints }
            val oppRaidPoints = oppSquadState.sumOf { it.totalRaidPoints }
            val oppTacklePoints = oppSquadState.sumOf { it.totalTacklePoints }
            onMatchFinished(
                userScore,
                opponentScore,
                yourRaidPoints,
                yourTacklePoints,
                oppRaidPoints,
                oppTacklePoints,
                yourSquadState,
                yourBenchState,
                oppSquadState
            )
        }
    }

    // --- GAME REAL-TIME SIMULATION LOOP ---
    LaunchedEffect(isPaused, matchEnded, activeRaider, isYourTurn, gameSpeed) {
        if (!isPaused && !matchEnded) {
            // Case 1: Active Raider is raiding (Ticking down)
            if (activeRaider != null) {
                while (raidTimeRemaining > 0) {
                    delay(1000L / gameSpeed)
                    raidTimeRemaining--

                    val isUserRaid = isYourTurn
                    val raider = activeRaider

                    // Update health in real-time tick for yourSquadState
                    yourSquadState.forEach { p ->
                        val isRaider = (raider != null && p.name == raider.name)
                        if (isUserRaid) {
                            // User is raiding, only user raider participates
                            if (isRaider) {
                                p.health = (p.health - (0.4f + (1..10).random() / 20f)).coerceAtLeast(10f)
                            } else {
                                // Other players are resting on court
                                p.health = (p.health + (0.5f + (1..10).random() / 20f)).coerceAtMost(100f)
                            }
                        } else {
                            // Opponent is raiding, user is defending. Active on-court defenders participate.
                            if (!p.isOut) {
                                p.health = (p.health - (0.4f + (1..10).random() / 20f)).coerceAtLeast(10f)
                            } else {
                                // Out players are resting/waiting
                                p.health = (p.health + (0.5f + (1..10).random() / 20f)).coerceAtMost(100f)
                            }
                        }
                    }

                    // Update health in real-time tick for oppSquadState
                    oppSquadState.forEach { p ->
                        val isRaider = (raider != null && p.name == raider.name)
                        if (!isUserRaid) {
                            // Opponent is raiding, only opponent raider participates
                            if (isRaider) {
                                p.health = (p.health - (0.4f + (1..10).random() / 20f)).coerceAtLeast(10f)
                            } else {
                                // Other players are resting on court
                                p.health = (p.health + (0.5f + (1..10).random() / 20f)).coerceAtMost(100f)
                            }
                        } else {
                            // User is raiding, opponent is defending. Active on-court defenders participate.
                            if (!p.isOut) {
                                p.health = (p.health - (0.4f + (1..10).random() / 20f)).coerceAtLeast(10f)
                            } else {
                                // Out players are resting/waiting
                                p.health = (p.health + (0.5f + (1..10).random() / 20f)).coerceAtMost(100f)
                            }
                        }
                    }

                    // Update health in real-time tick for benched players (resting)
                    yourBenchState.forEach { p ->
                        p.health = (p.health + (0.6f + (1..10).random() / 20f)).coerceAtMost(100f)
                    }

                    // Simulate action climax in countdown
                    val isActionHappening = (1..10).random()
                    if (raidTimeRemaining <= 5 && isActionHappening > 4) {
                        break // Resolve early for responsive simulation
                    }
                }

                // --- Calculate Raid Outcome ---
                val isUserRaid = isYourTurn
                val raider = activeRaider!!

                // Compile passive defending abilities of court active players
                val defendingSquad = if (isUserRaid) oppSquadState else yourSquadState
                val activeDefenders = defendingSquad.filter { !it.isOut }
                val avgDefense = if (activeDefenders.isEmpty()) 50f else activeDefenders.map { it.defendingAbility }.average().toFloat()

                // Calculate base chance adjusted by individual abilities
                val abilityDiff = raider.raidingAbility - avgDefense
                var raidSuccessChance = 0.50f + (abilityDiff / 200f)

                // Incorporate strategies & health multipliers
                val raiderAggression = if (isUserRaid) yourAggression else opponentAggression
                val defenderAggression = if (isUserRaid) opponentAggression else yourAggression

                raidSuccessChance = when (raiderAggression) {
                    AggressionMode.DEFENSIVE -> raidSuccessChance - 0.15f
                    AggressionMode.NORMAL -> raidSuccessChance
                    AggressionMode.AGGRESSIVE -> raidSuccessChance + 0.12f
                    AggressionMode.VERY_AGGRESSIVE -> raidSuccessChance + 0.22f
                }

                raidSuccessChance = when (defenderAggression) {
                    AggressionMode.DEFENSIVE -> raidSuccessChance - 0.10f
                    AggressionMode.NORMAL -> raidSuccessChance
                    AggressionMode.AGGRESSIVE -> raidSuccessChance + 0.08f
                    AggressionMode.VERY_AGGRESSIVE -> raidSuccessChance + 0.15f
                }

                // Incorporate player ability, health AND difficulty (as requested):
                val healthRatio = raider.health / 100f
                val avgDefHealth = if (activeDefenders.isEmpty()) 100f else activeDefenders.map { it.health }.average().toFloat()
                val defHealthRatio = avgDefHealth / 100f

                // Base health effect: Raider is stronger if healthy, weaker if defenders are healthy/tired
                val healthMultiplier = (0.8f + (healthRatio * 0.2f)) * (1.10f - (defHealthRatio * 0.10f))
                raidSuccessChance *= healthMultiplier

                // Difficulty effect
                val diffFactor = when (difficulty.lowercase()) {
                    "easy" -> if (isUserRaid) 0.15f else -0.12f
                    "hard" -> if (isUserRaid) -0.12f else 0.12f
                    "pro" -> if (isUserRaid) -0.22f else 0.20f
                    else -> 0f
                }
                raidSuccessChance += diffFactor

                // Safety clamp
                raidSuccessChance = raidSuccessChance.coerceIn(0.15f, 0.85f)

                val isSuccessful = (Math.random() < raidSuccessChance)
                val attackerTeamName = if (isUserRaid) yourTeam.nickName else opponentTeam.nickName
                val defenderTeamName = if (isUserRaid) opponentTeam.nickName else yourTeam.nickName

                if (isSuccessful) {
                    // RAID SUCCESSFUL Points calculation
                    // 65% chance 1 pt, 25% chance 2 pt, 10% chance Super Raid (3-4 points)
                    val ptRoll = (1..100).random()
                    val pointsScored = when {
                        ptRoll <= 65 -> 1
                        ptRoll <= 90 -> 2
                        else -> (3..4).random()
                    }

                    // Update scorer stats
                    raider.successfulRaids += 1
                    raider.totalRaidPoints += pointsScored
                    raider.overallTotalPoints += pointsScored

                    // Update score
                    if (isUserRaid) userScore += pointsScored else opponentScore += pointsScored

                    // Reduce raider stamina
                    raider.health = (raider.health - 6f).coerceAtLeast(10f)

                    // Tag out defenders and mark their unsuccessful tackles
                    val defToOut = defendingSquad.filter { !it.isOut }.shuffled().take(pointsScored)
                    defToOut.forEach {
                        it.isOut = true
                        it.health = (it.health - 10f).coerceAtLeast(10f)
                        it.unsuccessfulTackles += 1
                    }

                    // Revive friendly players (FIFO order)
                    val attackingSquad = if (isUserRaid) yourSquadState else oppSquadState
                    val outFriendly = attackingSquad.filter { it.isOut }
                    val toRevive = outFriendly.take(pointsScored)
                    toRevive.forEach {
                        it.isOut = false
                        it.health = 100f
                    }

                    val pointsLabel = if (pointsScored >= 3) "$pointsScored points (SUPER RAID!)" else "$pointsScored points"
                    logs.add(0, "🦁 [$attackerTeamName] ${raider.name} scores $pointsLabel! Revived ${toRevive.size} defenders.")
                    if (pointsScored >= 3) ArenaSoundEngine.playSuperRaid(context) else ArenaSoundEngine.playRaidSuccess(context)

                } else {
                    // RAID TACKLED
                    val activeDefendersOnCourtCount = activeDefenders.size
                    val isSuperTackle = activeDefendersOnCourtCount <= 3

                    val pointsAwarded = if (isSuperTackle) 2 else 1
                    if (isUserRaid) opponentScore += pointsAwarded else userScore += pointsAwarded

                    // Tag raider out & mark unsuccessful raid
                    raider.isOut = true
                    raider.unsuccessfulRaids += 1
                    raider.health = (raider.health - 15f).coerceAtLeast(10f)

                    // Attribute tackle to defender based on user role distribution criteria
                    // 65% Defender, 30% All-Rounder, 5% Raider
                    val activeTacklers = defendingSquad.filter { !it.isOut }
                    val tacklerRoleRoll = (1..100).random()
                    val preferredRole = when {
                        tacklerRoleRoll <= 65 -> PlayerRole.DEFENDER
                        tacklerRoleRoll <= 95 -> PlayerRole.ALL_ROUNDER
                        else -> PlayerRole.RAIDER
                    }

                    val chosenTackler = activeTacklers.firstOrNull { it.role == preferredRole }
                        ?: activeTacklers.firstOrNull { it.role == PlayerRole.ALL_ROUNDER }
                        ?: activeTacklers.firstOrNull { it.role == PlayerRole.DEFENDER }
                        ?: activeTacklers.randomOrNull()

                    val tacklerName = chosenTackler?.name ?: "Defense Wall"
                    if (chosenTackler != null) {
                        chosenTackler.health = (chosenTackler.health - 6f).coerceAtLeast(10f)
                        chosenTackler.successfulTackles += 1
                        chosenTackler.totalTacklePoints += pointsAwarded
                        chosenTackler.overallTotalPoints += pointsAwarded
                    }

                    // Revive defending players
                    val outDefenders = defendingSquad.filter { it.isOut }
                    val toRevive = outDefenders.take(pointsAwarded)
                    toRevive.forEach {
                        it.isOut = false
                        it.health = 100f
                    }

                    if (isSuperTackle) {
                        logs.add(0, "🤼 [$defenderTeamName] SUPER TACKLE! $tacklerName stops ${raider.name}! +2 points!")
                        ArenaSoundEngine.playSuperRaid(context)
                    } else {
                        logs.add(0, "🛡️ [$defenderTeamName] Solid Tackle! $tacklerName grabs ${raider.name}! +1 point!")
                        ArenaSoundEngine.playTackleSlap(context)
                    }
                }

                // Check and handle ALL OUTS (when count hits 0)
                val isYourAllOut = yourSquadState.none { !it.isOut }
                val isOppAllOut = oppSquadState.none { !it.isOut }

                if (isYourAllOut) {
                    opponentScore += 2
                    yourSquadState.forEach {
                        it.isOut = false
                        it.health = 100f
                    }
                    logs.add(0, "🚨 ALL OUT! ${yourTeam.name} wiped out! ${opponentTeam.name} gets +2 bonus points!")
                }
                if (isOppAllOut) {
                    userScore += 2
                    oppSquadState.forEach {
                        it.isOut = false
                        it.health = 100f
                    }
                    logs.add(0, "🚨 ALL OUT! ${opponentTeam.name} wiped out! ${yourTeam.name} gets +2 bonus points!")
                }

                // Reset selection flags
                yourSquadState.forEach { it.isSelectedForRaid = false }
                oppSquadState.forEach { it.isSelectedForRaid = false }

                // Decrement 30 seconds from the match clock after each raid
                if (matchSeconds == 0) {
                    if (matchMinutes > 0) {
                        matchMinutes--
                        matchSeconds = 30
                    } else {
                        matchEnded = true
                    }
                } else {
                    matchSeconds = 0
                }

                if (matchMinutes == 0 && matchSeconds == 0) {
                    matchEnded = true
                }

                // Display result shortly before next turn transition
                delay(1200L / gameSpeed)
                activeRaider = null
                isYourTurn = !isYourTurn
            }

            // Case 2: Computer turn (Automatically picks computer active raider)
            if (!isYourTurn && activeRaider == null) {
                delay(1000L / gameSpeed)

                // AI updates strategy depending on score margin
                opponentAggression = when {
                    opponentScore < userScore - 4 -> {
                        if (Math.random() < 0.5) AggressionMode.VERY_AGGRESSIVE else AggressionMode.AGGRESSIVE
                    }
                    opponentScore > userScore + 4 -> {
                        if (Math.random() < 0.6) AggressionMode.DEFENSIVE else AggressionMode.NORMAL
                    }
                    else -> {
                        val rand = Math.random()
                        when {
                            rand < 0.25 -> AggressionMode.DEFENSIVE
                            rand < 0.70 -> AggressionMode.NORMAL
                            else -> AggressionMode.AGGRESSIVE
                        }
                    }
                }

                // Select computer raider (mostly raiders & all-rounders as criteria)
                val activeOpp = oppSquadState.filter { !it.isOut }
                if (activeOpp.isNotEmpty()) {
                    val prefersRoleRoll = (1..100).random()
                    val targetRole = when {
                        prefersRoleRoll <= 65 -> PlayerRole.RAIDER
                        prefersRoleRoll <= 95 -> PlayerRole.ALL_ROUNDER
                        else -> PlayerRole.DEFENDER
                    }

                    val chosenComputerRaider = activeOpp.firstOrNull { it.role == targetRole }
                        ?: activeOpp.firstOrNull { it.role == PlayerRole.RAIDER }
                        ?: activeOpp.firstOrNull { it.role == PlayerRole.ALL_ROUNDER }
                        ?: activeOpp.randomOrNull()

                    if (chosenComputerRaider != null) {
                        oppSquadState.forEach { it.isSelectedForRaid = false }
                        chosenComputerRaider.isSelectedForRaid = true
                        activeRaider = chosenComputerRaider
                        raidTimeRemaining = 15
                        ArenaSoundEngine.playWhistle(context)
                    }
                } else {
                    isYourTurn = true
                }
            }
        }
    }

    // --- VIEW INTERFACE ---
    Box(modifier = Modifier.fillMaxSize()) {
        if (matchEnded) {
            MatchSummaryScreen(
                yourTeam = yourTeam,
                opponentTeam = opponentTeam,
                userScore = userScore,
                opponentScore = opponentScore,
                yourSquad = yourSquadState,
                oppSquad = oppSquadState,
                matchLogs = logs.toList(),
                onHomeClick = onBack,
                onBackToTournamentClick = onBackToTournament,
                onBackToCareerClick = onBackToCareer
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
        // 1. TOP HEADER (Scores, Team names, Timers)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Your Team stats block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(0.35f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(yourActiveJerseyColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PlainWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = yourTeam.name.uppercase().split(" ").firstOrNull() ?: yourTeam.name,
                        color = PlainWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "$userScore",
                        color = PlainWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                }
            }

            // Central clocks
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.3f)
            ) {
                Text(
                    text = String.format("%02d:%02d", matchMinutes, matchSeconds),
                    color = PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp
                )
                Text(
                    text = if (activeRaider != null) "RAID: 00:${String.format("%02d", raidTimeRemaining)}" else "RAID: 00:15",
                    color = if (activeRaider != null) Color(0xFFE91E63) else Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            // Opponent block stats
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                modifier = Modifier.weight(0.35f)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = opponentTeam.name.uppercase().split(" ").firstOrNull() ?: opponentTeam.name,
                        color = PlainWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "$opponentScore",
                        color = PlainWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(opponentTeam.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PlainWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. STATUS CONTEXT BANNER
        Card(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2236)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        matchEnded -> "MATCH FINISHED! FINAL SCORE: $userScore - $opponentScore"
                        activeRaider == null && isYourTurn -> "Your turn to raid. Select a player."
                        activeRaider != null && isYourTurn -> "Your raid is active! Ticking..."
                        activeRaider == null && !isYourTurn -> "Opponent is picking their raider..."
                        activeRaider != null && !isYourTurn -> "Opponent is raiding your half!"
                        else -> ""
                    },
                    color = PlainWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. KABADDI COURT (Divided 7v7 player mat)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = courtMatColor),
            border = BorderStroke(1.5.dp, courtBorderColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw horizontal dashed center line
                    drawLine(
                        color = courtLineColor,
                        start = Offset(0f, size.height / 2f),
                        end = Offset(size.width, size.height / 2f),
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // UPPER HALF (Opponent Half of Court)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = opponentTeam.name.uppercase(),
                            color = PlainWhite.copy(alpha = 0.03f),
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            oppSquadState.forEach { player ->
                                PlayerCircleOnMat(
                                    player = player,
                                    isActiveRaider = activeRaider == player,
                                    accentColor = opponentTeam.primaryColor,
                                    onClick = {
                                        // Opponent mat clicks are passive
                                    }
                                )
                            }
                        }
                    }

                    // LOWER HALF (Your Team Half of Court)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = yourTeam.name.uppercase(),
                            color = PlainWhite.copy(alpha = 0.03f),
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            yourSquadState.forEach { player ->
                                PlayerCircleOnMat(
                                    player = player,
                                    isActiveRaider = activeRaider == player,
                                    accentColor = yourActiveJerseyColor,
                                    onClick = {
                                        if (isYourTurn && activeRaider == null) {
                                            if (!player.isOut) {
                                                activeRaider = player
                                                raidTimeRemaining = 15
                                                yourSquadState.forEach { it.isSelectedForRaid = false }
                                                player.isSelectedForRaid = true
                                                ArenaSoundEngine.playWhistle(context)
                                                ArenaSoundEngine.playStadiumAmbienceCue(context)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 4. STRATEGY SELECTION PANEL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left block: User controls (DEFENSIVE / NORMAL row and AGGRESSIVE / VERY_AGGRESSIVE row)
            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StrategyPill(
                        label = "Defensive",
                        isSelected = yourAggression == AggressionMode.DEFENSIVE,
                        onClick = { yourAggression = AggressionMode.DEFENSIVE },
                        modifier = Modifier.weight(1f)
                    )
                    StrategyPill(
                        label = "Normal",
                        isSelected = yourAggression == AggressionMode.NORMAL,
                        onClick = { yourAggression = AggressionMode.NORMAL },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StrategyPill(
                        label = "Aggressive",
                        isSelected = yourAggression == AggressionMode.AGGRESSIVE,
                        onClick = { yourAggression = AggressionMode.AGGRESSIVE },
                        modifier = Modifier.weight(1f)
                    )
                    StrategyPill(
                        label = "Very Aggressive",
                        isSelected = yourAggression == AggressionMode.VERY_AGGRESSIVE,
                        onClick = { yourAggression = AggressionMode.VERY_AGGRESSIVE },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Right block: Opponent AI status display & non-functional substitute representation
            Column(
                modifier = Modifier.weight(0.45f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2236)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "OPPONENT",
                            color = SlateMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = opponentAggression.displayName,
                            color = ChampionGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Button(
                    onClick = {
                        if (activeRaider == null && subsRemaining > 0) {
                            showSubstitutionDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeRaider != null) Color(0xFF1E283F).copy(alpha = 0.5f) else Color(0xFF1E283F),
                        disabledContainerColor = Color(0xFF131A2B),
                        disabledContentColor = SlateMuted
                    ),
                    enabled = activeRaider == null && subsRemaining > 0,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("btn_substitute_players")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (activeRaider == null && subsRemaining > 0) PlainWhite else SlateMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Substitute ($subsRemaining/5)",
                            color = if (activeRaider == null && subsRemaining > 0) PlainWhite else SlateMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 5. MATCH GRAPHIC LOGS / SPEED CONTROLLER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Controller Button (1x, 2x, 3x)
            Button(
                onClick = {
                    gameSpeed = when (gameSpeed) {
                        1 -> 2
                        2 -> 3
                        else -> 1
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2236)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(0.35f)
                    .height(38.dp)
                    .testTag("btn_simulation_speed")
            ) {
                Text(
                    text = "Speed: ${gameSpeed}x",
                    color = PlainWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Flat scrolling/trimmed single live kicker log ticker
            Card(
                modifier = Modifier.weight(0.65f).height(38.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1528)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF1E2C48))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val activeLog = if (commentaryEnabled) {
                        logs.firstOrNull() ?: "🏟️ Click starting players to initiate raid battles!"
                    } else {
                        "🔇 Live Commentary Muted (Settings)"
                    }
                    Text(
                        text = activeLog,
                        color = if (commentaryEnabled) PlainWhite.copy(0.9f) else SlateMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 6. BOTTOM PLAY/PAUSE, SCORECARD & HOME ACTION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isPaused = !isPaused },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.35f)
                    .height(48.dp)
                    .testTag("btn_game_play_pause")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = StadiumNavy,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPaused) "Play" else "Pause",
                        color = StadiumNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = { showScorecardDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283F)),
                border = BorderStroke(1.dp, ChampionGold.copy(0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.35f)
                    .height(48.dp)
                    .testTag("btn_game_scorecard_trigger")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ChampionGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scorecard",
                        color = PlainWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131A2D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.30f)
                    .height(48.dp)
                    .testTag("btn_game_home")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = PlainWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Home",
                        color = PlainWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    } // Closes Column
    } // Closes else block

    // Scorecard Dialog/Overlay
    if (showScorecardDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.85f))
                .clickable { showScorecardDialog = false }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clickable(enabled = false) { },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.5.dp, ChampionGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE MATCH SCORECARD",
                            color = ChampionGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        IconButton(
                            onClick = { showScorecardDialog = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = PlainWhite
                            )
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp), color = SlateMuted.copy(0.3f))
                    
                    ScorecardView(
                        yourSquad = yourSquadState,
                        oppSquad = oppSquadState,
                        yourTeamName = yourTeam.name,
                        oppTeamName = opponentTeam.name
                    )
                }
            }
        }
    }

    // Substitution Dialog overlay
    if (showSubstitutionDialog && subsRemaining > 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.85f))
                .clickable { 
                    selectedStarterToSub = null
                    selectedBenchedToSub = null
                    showSubstitutionDialog = false 
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clickable(enabled = false) { },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.5.dp, ChampionGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TEAM SUBSTITUTION",
                                color = ChampionGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "$subsRemaining of 5 substitutions left",
                                color = SlateMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        IconButton(
                            onClick = {
                                selectedStarterToSub = null
                                selectedBenchedToSub = null
                                showSubstitutionDialog = false
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = PlainWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateMuted.copy(0.2f))

                    // Selection Summary / Info Box
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283F)),
                        border = BorderStroke(1.dp, Color(0xFF2E395E))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text("SUB OUT (Starters)", color = ClayOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = selectedStarterToSub?.name ?: "Select on-court player",
                                    color = if (selectedStarterToSub != null) PlainWhite else Color.Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                if (selectedStarterToSub != null) {
                                    Text(
                                        text = "HP: ${selectedStarterToSub!!.health.toInt()}% | ${selectedStarterToSub!!.role}",
                                        color = SlateMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = ChampionGold,
                                modifier = Modifier.padding(horizontal = 8.dp).size(24.dp)
                            )

                            Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.End) {
                                Text("SUB IN (Bench)", color = Color(0xFF43C065), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = selectedBenchedToSub?.name ?: "Select benched player",
                                    color = if (selectedBenchedToSub != null) PlainWhite else Color.Gray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    textAlign = TextAlign.End
                                )
                                if (selectedBenchedToSub != null) {
                                    Text(
                                        text = "HP: ${selectedBenchedToSub!!.health.toInt()}% | ${selectedBenchedToSub!!.role}",
                                        color = SlateMuted,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }

                    // On-Court Starters Section vs Bench Section in Two columns
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // ON COURT STARTERS
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ON COURT (7)",
                                color = PlainWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(yourSquadState.size) { index ->
                                    val player = yourSquadState[index]
                                    val isSelected = selectedStarterToSub == player
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedStarterToSub = player },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFF263351) else Color(0xFF141C30)
                                        ),
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) ClayOrange else Color(0xFF232D42)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "#${player.jerseyNumber} ${player.name}",
                                                    color = PlainWhite,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (player.isOut) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFFF3D00), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("OUT", color = PlainWhite, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = player.role.name.replace("_", " "),
                                                    color = SlateMuted,
                                                    fontSize = 9.sp
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    val hpColor = when {
                                                        player.health > 75f -> Color(0xFF39FF14)
                                                        player.health > 45f -> Color(0xFFFFEA00)
                                                        else -> Color(0xFFFE019A)
                                                    }
                                                    Box(modifier = Modifier.size(6.dp).background(hpColor, CircleShape))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "${player.health.toInt()}%",
                                                        color = hpColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // BENCH PLAYERS
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BENCH",
                                color = PlainWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            if (yourBenchState.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No bench players", color = SlateMuted, fontSize = 11.sp)
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(yourBenchState.size) { index ->
                                        val player = yourBenchState[index]
                                        val isSelected = selectedBenchedToSub == player
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedBenchedToSub = player },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(0xFF1E3525) else Color(0xFF141C30)
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF43C065) else Color(0xFF232D42)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "#${player.jerseyNumber} ${player.name}",
                                                    color = PlainWhite,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = player.role.name.replace("_", " "),
                                                        color = SlateMuted,
                                                        fontSize = 9.sp
                                                    )
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        val hpColor = when {
                                                            player.health > 75f -> Color(0xFF39FF14)
                                                            player.health > 45f -> Color(0xFFFFEA00)
                                                            else -> Color(0xFFFE019A)
                                                        }
                                                        Box(modifier = Modifier.size(6.dp).background(hpColor, CircleShape))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = "${player.health.toInt()}%",
                                                            color = hpColor,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
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

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateMuted.copy(0.2f))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedStarterToSub = null
                                selectedBenchedToSub = null
                                showSubstitutionDialog = false
                            },
                            border = BorderStroke(1.dp, SlateMuted),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("Cancel", color = PlainWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        val canConfirm = selectedStarterToSub != null && selectedBenchedToSub != null
                        Button(
                            onClick = {
                                if (canConfirm) {
                                    val idx = yourSquadState.indexOf(selectedStarterToSub!!)
                                    if (idx != -1) {
                                        val starterPlayer = selectedStarterToSub!!
                                        val benchPlayer = selectedBenchedToSub!!
                                        
                                        // Swap
                                        yourSquadState[idx] = benchPlayer
                                        yourBenchState.remove(benchPlayer)
                                        yourBenchState.add(starterPlayer)
                                        
                                        // Make sure the newly added on-court player is NOT marked as out initially when coming fresh from bench
                                        benchPlayer.isOut = false
                                        
                                        // Log substitution
                                        logs.add(0, "🔄 [SUB] ${benchPlayer.name} (Bench) replaced ${starterPlayer.name}! ($subsRemaining substitutions left)")
                                        
                                        subsRemaining--
                                        
                                        // Reset & close
                                        selectedStarterToSub = null
                                        selectedBenchedToSub = null
                                        showSubstitutionDialog = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canConfirm) ChampionGold else Color(0x33FFB300),
                                contentColor = StadiumNavy
                            ),
                            enabled = canConfirm,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("btn_confirm_substitution")
                        ) {
                            Text("Confirm Sub", color = if (canConfirm) StadiumNavy else SlateMuted, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
} // Closes Box wrapper
} // Closes MatchArenaScreen function

@Composable
fun ScorecardView(
    yourSquad: List<LivePlayerState>,
    oppSquad: List<LivePlayerState>,
    yourTeamName: String,
    oppTeamName: String
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color(0xFF141C30), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedTab == 0) ChampionGold else Color.Transparent)
                    .clickable { selectedTab = 0 },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = yourTeamName.split(" ").firstOrNull() ?: yourTeamName,
                    color = if (selectedTab == 0) StadiumNavy else PlainWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedTab == 1) ChampionGold else Color.Transparent)
                    .clickable { selectedTab = 1 },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = oppTeamName.split(" ").firstOrNull() ?: oppTeamName,
                    color = if (selectedTab == 1) StadiumNavy else PlainWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        val activeSquad = if (selectedTab == 0) yourSquad else oppSquad
        
        val totalSuccessfulRaids = activeSquad.sumOf { it.successfulRaids }
        val totalUnsuccessfulRaids = activeSquad.sumOf { it.unsuccessfulRaids }
        val totalRaidPoints = activeSquad.sumOf { it.totalRaidPoints }
        val totalSuccessfulTackles = activeSquad.sumOf { it.successfulTackles }
        val totalUnsuccessfulTackles = activeSquad.sumOf { it.unsuccessfulTackles }
        val totalTacklePoints = activeSquad.sumOf { it.totalTacklePoints }
        val grandTotalPoints = activeSquad.sumOf { it.overallTotalPoints }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
            border = BorderStroke(1.dp, ChampionGold.copy(0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TEAM OVERALL TOTALS",
                    color = ChampionGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Raid Points: $totalRaidPoints", color = PlainWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Raids: $totalSuccessfulRaids S, $totalUnsuccessfulRaids U", color = SlateMuted, fontSize = 9.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Tackle Points: $totalTacklePoints", color = PlainWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Tackles: $totalSuccessfulTackles S, $totalUnsuccessfulTackles U", color = SlateMuted, fontSize = 9.sp)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SlateMuted.copy(0.2f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "GRAND TOTAL POINTS", color = PlainWhite, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    Text(text = "$grandTotalPoints Pts", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 240.dp)
        ) {
            items(activeSquad) { player ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (player.isOut) Color(0xFF0F1626) else Color(0xFF161E33)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF222C4A))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (player.role == PlayerRole.RAIDER) ClayOrange else Color(0xFF43C065)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = player.role.name.first().toString(),
                                        color = PlainWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = player.name,
                                    color = if (player.isOut) PlainWhite.copy(0.5f) else PlainWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "#${player.jerseyNumber}",
                                    color = ClayOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            if (player.isOut) {
                                Badge(containerColor = Color.Red.copy(0.2f), contentColor = Color.Red) {
                                    Text(text = "OUT", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Badge(containerColor = Color.Green.copy(0.15f), contentColor = Color(0xFF39FF14)) {
                                    Text(text = "ON COURT", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "RAIDS", color = ClayOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Points: ${player.totalRaidPoints}", color = PlainWhite, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(text = "S: ${player.successfulRaids} | U: ${player.unsuccessfulRaids}", color = SlateMuted, fontSize = 9.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "TACKLES", color = Color(0xFF43C065), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Points: ${player.totalTacklePoints}", color = PlainWhite, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                Text(text = "S: ${player.successfulTackles} | U: ${player.unsuccessfulTackles}", color = SlateMuted, fontSize = 9.sp)
                            }
                            Column(
                                modifier = Modifier.weight(0.8f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(text = "OVERALL", color = ChampionGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${player.overallTotalPoints} PTS", color = ChampionGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchSummaryScreen(
    yourTeam: PklTeam,
    opponentTeam: PklTeam,
    userScore: Int,
    opponentScore: Int,
    yourSquad: List<LivePlayerState>,
    oppSquad: List<LivePlayerState>,
    matchLogs: List<String> = emptyList(),
    onHomeClick: () -> Unit,
    onBackToTournamentClick: (() -> Unit)? = null,
    onBackToCareerClick: (() -> Unit)? = null
) {
    val allPlayers = yourSquad + oppSquad
    val playerOfTheMatch = allPlayers.maxByOrNull { it.overallTotalPoints }
    val raiderOfTheMatch = allPlayers.maxByOrNull { it.totalRaidPoints }
    val defenderOfTheMatch = allPlayers.maxByOrNull { it.totalTacklePoints }
    val highlights = remember(matchLogs) {
        val keyMoments = matchLogs.filter {
            it.contains("SUPER RAID") || it.contains("SUPER TACKLE") || it.contains("ALL OUT")
        }
        if (keyMoments.isNotEmpty()) keyMoments.take(4) else matchLogs.take(3)
    }
    val matchCoinsEarned = when {
        userScore > opponentScore -> 250
        userScore == opponentScore -> 140
        else -> 80
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MATCH SUMMARY",
            color = ClayOrange,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, ChampionGold.copy(0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val matchResultText = when {
                    userScore > opponentScore -> "${yourTeam.name.uppercase()} WINS!"
                    userScore < opponentScore -> "${opponentTeam.name.uppercase()} WINS!"
                    else -> "IT'S A DRAW!"
                }
                
                Text(
                    text = matchResultText,
                    color = ChampionGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = yourTeam.nickName.uppercase(),
                            color = PlainWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$userScore",
                            color = PlainWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                    }
                    
                    Text(
                        text = "VS",
                        color = ClayOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = opponentTeam.nickName.uppercase(),
                            color = PlainWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$opponentScore",
                            color = PlainWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
        
        if (playerOfTheMatch != null && playerOfTheMatch.overallTotalPoints > 0) {
            val isYourPlayer = yourSquad.contains(playerOfTheMatch)
            val teamName = if (isYourPlayer) yourTeam.name else opponentTeam.name
            val teamBg = if (isYourPlayer) yourTeam.primaryColor else opponentTeam.primaryColor

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2236)),
                border = BorderStroke(1.5.dp, ChampionGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = ChampionGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAYER OF THE MATCH",
                            color = ChampionGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = playerOfTheMatch.name.uppercase(),
                        color = PlainWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    
                    Text(
                        text = teamName,
                        color = teamBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TOTAL POINTS", color = ChampionGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${playerOfTheMatch.overallTotalPoints}", color = PlainWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "RAID POINTS", color = ClayOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${playerOfTheMatch.totalRaidPoints}", color = PlainWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TACKLE POINTS", color = Color(0xFF43C065), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${playerOfTheMatch.totalTacklePoints}", color = PlainWhite, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Raider & Defender of the Match + Coins Reward
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E33)),
                border = BorderStroke(1.dp, ClayOrange.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RAIDER OF MATCH", color = ClayOrange, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(raiderOfTheMatch?.name ?: "-", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    Text("${raiderOfTheMatch?.totalRaidPoints ?: 0} Raid Pts", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E33)),
                border = BorderStroke(1.dp, RaidCyan.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DEFENDER OF MATCH", color = RaidCyan, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(defenderOfTheMatch?.name ?: "-", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    Text("${defenderOfTheMatch?.totalTacklePoints ?: 0} Tackle Pts", color = Color(0xFF4ADE80), fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14532D).copy(alpha = 0.45f)),
            border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MATCH COINS EARNED", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("+$matchCoinsEarned COINS", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }

        if (highlights.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1327)),
                border = BorderStroke(1.dp, Color(0xFF1E283F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("MATCH HIGHLIGHTS REEL", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    highlights.forEach { moment ->
                        Text(text = "• $moment", color = PlainWhite.copy(alpha = 0.88f), fontSize = 11.sp)
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1327)),
            border = BorderStroke(1.dp, Color(0xFF1E283F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "DETAILED MATCH SCORECARD",
                    color = PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                ScorecardView(
                    yourSquad = yourSquad,
                    oppSquad = oppSquad,
                    yourTeamName = yourTeam.name,
                    oppTeamName = opponentTeam.name
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (onBackToCareerClick != null) {
            Button(
                onClick = onBackToCareerClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampionGold,
                    contentColor = AlwaysDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_summary_back_to_career")
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AlwaysDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BACK TO CAREER",
                    color = AlwaysDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (onBackToTournamentClick != null) {
            Button(
                onClick = onBackToTournamentClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampionGold,
                    contentColor = AlwaysDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_summary_back_to_tournament")
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AlwaysDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BACK TO TOURNAMENT",
                    color = AlwaysDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Button(
            onClick = onHomeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_summary_home")
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = AlwaysWhite,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (onBackToTournamentClick != null) "RETURN TO HOME" else "RETURN TO HOME",
                color = AlwaysWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

// ==========================================
// SCREEN 4: DETAILED GAME SETTINGS SCREEN
// ==========================================
@Composable
fun GameSettingsScreen(
    sharedPrefs: SharedPreferences,
    onResetAllData: () -> Unit = {},
    onBack: () -> Unit
) {
    var managerInput by remember { mutableStateOf(sharedPrefs.getString("user_manager_name", "Amit Team Manager") ?: "Amit Team Manager") }
    var bgmEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("settings_bgm_enabled", true)) }
    var sfxEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("settings_sfx_enabled", true)) }
    var commentaryEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("settings_commentary_enabled", true)) }
    var matchSpeed by remember { mutableStateOf(sharedPrefs.getFloat("settings_game_speed", 1.0f)) }
    var appLanguage by remember { mutableStateOf(sharedPrefs.getString("settings_app_language", "ENG") ?: "ENG") }
    var selectedThemeIndex by remember { mutableStateOf(sharedPrefs.getInt("settings_theme_index", 0)) }
    var isDarkMode by remember { mutableStateOf(sharedPrefs.getBoolean("settings_dark_mode", true)) }
    
    var localFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var showResetStatsDialog by remember { mutableStateOf(false) }
    var handbookLanguageTab by remember { mutableStateOf(if (appLanguage == "HINDI") 1 else 0) }

    val saveSettings = {
        sharedPrefs.edit().apply {
            putString("user_manager_name", managerInput.trim().ifEmpty { "Amit Team Manager" })
            putBoolean("settings_bgm_enabled", bgmEnabled)
            putBoolean("settings_sfx_enabled", sfxEnabled)
            putBoolean("settings_commentary_enabled", commentaryEnabled)
            putFloat("settings_game_speed", matchSpeed)
            putString("settings_app_language", appLanguage)
            putInt("settings_theme_index", selectedThemeIndex)
            putBoolean("settings_dark_mode", isDarkMode)
            apply()
        }
        currentThemeIndexState.value = selectedThemeIndex
        currentLanguageState.value = appLanguage
        currentDarkModeState.value = isDarkMode
        localFeedbackMessage = "Settings successfully saved! / सेटिंग्स सुरक्षित की गईं!"
    }

    LaunchedEffect(localFeedbackMessage) {
        if (localFeedbackMessage != null) {
            delay(2500)
            localFeedbackMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper App bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_close_settings")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PlainWhite)
            }

            Text(
                text = "PLAYBOOK & SETTINGS".loc(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = ChampionGold
            )

            IconButton(
                onClick = { saveSettings() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ClayOrange)
                    .testTag("btn_save_settings")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = AlwaysWhite)
            }
        }

        // Main Configurations Scrollable Slate
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (localFeedbackMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6))
                ) {
                    Text(
                        text = localFeedbackMessage!!,
                        color = PlainWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 0. GAME LANGUAGE SELECTOR
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🌐 APP LANGUAGE / खेल की भाषा",
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("ENG" to "English (EN)", "HINDI" to "हिन्दी (Hindi)").forEach { (langCode, label) ->
                            val isSelected = appLanguage == langCode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ClayOrange else StadiumNavy)
                                    .border(1.dp, if (isSelected) ChampionGold else SlateMuted.copy(0.25f), RoundedCornerShape(8.dp))
                                    .clickable { 
                                        appLanguage = langCode
                                        currentLanguageState.value = langCode
                                        handbookLanguageTab = if (langCode == "HINDI") 1 else 0
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) AlwaysWhite else PlainWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 0.1 APP THEME MODE SELECTOR
            Card(
                modifier = Modifier.fillMaxWidth().testTag("theme_mode_selector"),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (appLanguage == "HINDI") "🌓 ऐप थीम मोड (Dark/Light)" else "🌓 APP THEME MODE (Dark/Light)",
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(
                            true to (if (appLanguage == "HINDI") "डार्क मोड" else "Dark Mode"),
                            false to (if (appLanguage == "HINDI") "लाइट मोड (सफ़ेद)" else "Light Mode")
                        ).forEach { (isDarkOpt, label) ->
                            val isSelected = isDarkMode == isDarkOpt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ClayOrange else StadiumNavy)
                                    .border(1.dp, if (isSelected) ChampionGold else SlateMuted.copy(0.25f), RoundedCornerShape(8.dp))
                                    .clickable { 
                                        isDarkMode = isDarkOpt
                                        currentDarkModeState.value = isDarkOpt
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) AlwaysWhite else SlateMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Theme selection Card - Includes 10 Dynamic Sports themes
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🎨 CHOOSE GAME THEME / खेल का रंग और थीम",
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KABADDI_THEMES.forEachIndexed { index, theme ->
                            val isSelected = selectedThemeIndex == index
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ClayOrange.copy(0.12f) else StadiumNavy)
                                    .border(1.dp, if (isSelected) ClayOrange else SlateMuted.copy(0.15f), RoundedCornerShape(8.dp))
                                    .clickable { 
                                        selectedThemeIndex = index
                                        currentThemeIndexState.value = index
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Colored dots to preview colors
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(theme.clayOrange)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(theme.stadiumNavy)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(theme.championGold)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = if (appLanguage == "HINDI") theme.nameHi else theme.nameEn,
                                        color = if (isSelected) PlainWhite else SlateMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = ClayOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 1. MANAGER REGISTER & PROFILE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "👤 MANAGER DIRECTORY / प्रबन्धक प्रोफ़ाइल".loc(),
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    OutlinedTextField(
                        value = managerInput,
                        onValueChange = { managerInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_manager_name"),
                        label = { Text("Manager Name".loc(), color = SlateMuted) },
                        textStyle = LocalTextStyle.current.copy(color = PlainWhite, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ClayOrange,
                            unfocusedBorderColor = SlateMuted.copy(0.4f),
                            cursorColor = ClayOrange
                        ),
                        singleLine = true
                    )

                    Text(
                        text = "Changes the manager name shown on your main hub dashboard screen instantly.".loc(),
                        color = SlateMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // 2. SOUND CONTROLS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "🔊 SOUND & ENVIRONMENT CONTROLS".loc(),
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Stadium Atmosphere / BGM".loc(), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Rousing ambient background music".loc(), color = SlateMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = bgmEnabled,
                            onCheckedChange = { bgmEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ClayOrange, checkedTrackColor = ClayOrange.copy(0.4f))
                        )
                    }

                    HorizontalDivider(color = SlateMuted.copy(0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Whistles & Mat Sound Effects (SFX)".loc(), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Audible whistles, raids, and tackle bangs".loc(), color = SlateMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = sfxEnabled,
                            onCheckedChange = { sfxEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ClayOrange, checkedTrackColor = ClayOrange.copy(0.4f))
                        )
                    }

                    HorizontalDivider(color = SlateMuted.copy(0.1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Live Pro Commentary".loc(), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Bilingual 'Super Raid!' match event alerts".loc(), color = SlateMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = commentaryEnabled,
                            onCheckedChange = { commentaryEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ClayOrange, checkedTrackColor = ClayOrange.copy(0.4f))
                        )
                    }
                }
            }

            // 3. GAMEPLAY SPEED CONTROLLER
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "⚡ MATCH SIMULATION SPEED / गेमप्ले गति".loc(),
                        color = ChampionGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1.0f to "1.0x (Normal)", 1.5f to "1.5x (Swift)", 2.0f to "2.0x (Turbo)").forEach { (multiplier, label) ->
                            val isSelected = matchSpeed == multiplier
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ClayOrange else StadiumNavy)
                                    .border(1.dp, if (isSelected) ChampionGold else SlateMuted.copy(0.25f), RoundedCornerShape(8.dp))
                                    .clickable { matchSpeed = multiplier }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) AlwaysWhite else PlainWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Accelerates raider and defender decisions in the Match Arena simulation engine so you can finish matches faster.".loc(),
                        color = SlateMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // 4. BILINGUAL HANDBOOK & RULES EXPLAINER
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📕 OFFICIAL KABADDI PLAYBOOK".loc(),
                            color = ChampionGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )

                        // Handbook language selector
                        Row(
                            modifier = Modifier
                                .background(StadiumNavy, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            listOf("ENG", "हिन्दी").forEachIndexed { tabIndex, label ->
                                val isSelected = handbookLanguageTab == tabIndex
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) ClayOrange else Color.Transparent)
                                        .clickable { handbookLanguageTab = tabIndex }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) AlwaysWhite else PlainWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    if (handbookLanguageTab == 0) {
                        // English Handbook Items
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HandbookItem(
                                title = "Do-or-Die Raid (3rd Raid Rule)",
                                text = "If a raiding team completes two successive 'empty raids' (raids yielding 0 points), the third raid is a 'Do-or-Die' raid. The raider MUST score a point (touch or bonus) or they are declared OUT, awarding 1 point to the defense!"
                            )
                            HandbookItem(
                                title = "Super Tackle Active Benefit",
                                text = "When the defending team has 3 or fewer players active on the court, a successful capture of an opposition raider is declared a 'Super Tackle'. This earns the defense an extra BONUS point (2 points total instead of 1)!"
                            )
                            HandbookItem(
                                title = "The Lobby Area Safety Law",
                                text = "The yellow side strips of the court are the 'Lobbies'. During regular play, they are out of bounds. However, once a touch contact occurs between the raider and any defender, the lobbies activate as active playing arena."
                            )
                            HandbookItem(
                                title = "Full Squad All Out (Lona)",
                                text = "If a team manages to eliminate all 7 active players of the opposition, they score an 'All Out' (Lona). The team gets 2 bonus points, and all 7 eliminated players of the opponent team are revived back onto the court."
                            )
                        }
                    } else {
                        // Hindi Playbook Items
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HandbookItem(
                                title = "तीसरी रेड / डू-ऑर-डाई रेड (Do-or-Die)",
                                text = "यदि आक्रमणकारी टीम लगातार दो खाली रेड (बिना पॉइंट की रेड) करती है, तो तीसरी रेड 'डू-ऑर-डाई' रेड होती है। रेडर को पॉइंट (टच या बोनस) हासिल करना ही होगा, अन्यथा उसे आउट घोषित कर विरोधी टीम को 1 पॉइंट दिया जाएगा!"
                            )
                            HandbookItem(
                                title = "सुपर टैकल सक्रिय लाभ (Super Tackle)",
                                text = "जब बचाव करने वाली टीम के पास कोर्ट पर 3 या उससे कम खिलाड़ी सक्रिय होते हैं, और वे विरोधी रेडर को सफलतापूर्वक दबोच लेते हैं, तो उसे 'सुपर टैकल' कहा जाता है। इसके तहत डिफेंस को 1 के बजाय 2 पॉइंट मिलते हैं!"
                            )
                            HandbookItem(
                                title = "लॉबी क्षेत्र सक्रिय नियम (Lobby Rule)",
                                text = "पार्क के दोनों तरफ बनी पीली पट्टियों को 'लॉबी' कहा जाता है। सामान्यतः यह सीमा से बाहर होती हैं। लेकिन जैसे ही रेडर और किसी डिफेंडर के बीच संपर्क (टच) होता है, पूरी लॉबी खेल के मैदान का हिस्सा बन जाती है।"
                            )
                            HandbookItem(
                                title = "सर्वनाश / ऑल-आउट नियम (Lona - 2 अंक)",
                                text = "यदि कोई टीम विपक्षी टीम के सभी 7 खिलाड़ियों को आउट कर देती है, तो उसे 'ऑल-आउट' (लोना) बोनस मिलता है। इसके तहत टीम को 2 अतिरिक्त पॉइंट दिए जाते हैं और विपक्षी टीम के सभी खिलाड़ी जीवित हो जाते हैं।"
                            )
                        }
                    }
                }
            }

            // 5. RESET DATA / DANGER ZONE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MatSlate),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF991B1B).copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🚨 DANGER ZONE / डेटा रीसेट".loc(),
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "This action will permanently wipe all matches won/lost statistics, individual real-time player points leaderboard records, and reset manager name. It cannot be undone.".loc(),
                        color = SlateMuted,
                        fontSize = 10.sp
                    )

                    Button(
                        onClick = { showResetStatsDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7F1D1D),
                            contentColor = AlwaysWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_reset_all_data")
                    ) {
                        Text("RESET PROGRESS & PLAYER RECORDS / पूरी प्रगति साफ़ करें".loc(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AlwaysWhite)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save & Sync Bottom Row Controls
        Button(
            onClick = {
                saveSettings()
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_save_exit_settings"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("APPLY & RETURN TO HUBAREA / लागू करें और मुख्य सूची पर जाएं".loc(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = AlwaysWhite)
        }
    }

    // Confirmation Alert Dialog
    if (showResetStatsDialog) {
        AlertDialog(
            onDismissRequest = { showResetStatsDialog = false },
            containerColor = MatSlate,
            title = {
                Text(
                    text = "Confirm Full Reset?".loc(),
                    color = PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Are you absolutely sure you want to delete all persistent match entries and custom player scores? This will restart your Season 10 campaign.".loc(),
                    color = SlateMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharedPrefs.edit().putString("user_manager_name", "Amit Team Manager").apply()
                        GameProgressRepository.resetAllProgress(sharedPrefs)
                        onResetAllData()
                        managerInput = "Amit Team Manager"
                        localFeedbackMessage = "All historical data wiped cleanly! / सारा डेटा मिटा दिया गया है!"
                        showResetStatsDialog = false
                    }
                ) {
                    Text("YES, WIPE DATA".loc(), color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetStatsDialog = false }) {
                    Text("CANCEL".loc(), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun HandbookItem(title: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StadiumNavy, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = ClayOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Text(
            text = text,
            color = PlainWhite.copy(0.9f),
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

// ==========================================
// BILINGUAL GAME STRING LOCALIZATION ENGINE
// ==========================================
fun String.loc(): String {
    if (com.example.ui.theme.currentLanguageState.value == "HINDI") {
        return TranslationsHindi[this] ?: this
    }
    return this
}

val TranslationsHindi = mapOf(
    // Home Screen
    "KABADDI KINGS" to "कबड्डी किंग्स",
    "SEASON 10 ARENA" to "सीजन 10 एरिना",
    "ARENA MODES" to "एरिना मोड्स",
    "SELECT YOUR PATH" to "अपना मोड चुनें",
    "FRANCHISE & TRAINING" to "फ्रेंचाइजी और प्रशिक्षण",
    "BUILD YOUR DYNASTY" to "अपना साम्राज्य बनाएं",
    "Lineup 7" to "लाइनअप 7",
    "CAREER" to "करियर",
    "Seasons" to "सीज़न",
    "DRILLS" to "अभ्यास",
    "Practice" to "प्रैक्टिस",
    "DAILY REWARDS, MISSIONS & PRO SHOP" to "दैनिक इनाम, मिशन और प्रो शॉप",
    "SQUAD" to "स्क्वाड",
    "PLAYED" to "मैच",
    "WIN RATE" to "जीत दर",
    "PLAY NOW" to "अभी खेलें",
    "QUICK MATCH" to "क्विक मैच",
    "Quick Match" to "क्विक मैच",
    "Instant 7v7 Raid & Tackle Match" to "त्वरित 7v7 रेड और टैकल मैच",
    "1v1 Instant Raid & Tackle Simulator" to "त्वरित 1v1 रेड और टैकल सिम्युलेटर",
    "Instant Raid & Defense Simulator" to "त्वरित रेड और डिफेंस सिम्युलेटर",
    "TOURNAMENT" to "टूर्नामेंट मोड",
    "TOURNAMENT MODE" to "टूर्नामेंट मोड",
    "Tournaments" to "टूर्नामेंट्स",
    "PKL & World Cup" to "पीकेएल और वर्ल्ड कप",
    "Cup Season Pro 1" to "कप सीजन प्रो 1",
    "AUCTION" to "खिलाड़ी नीलामी",
    "Player Auctions" to "खिलाड़ी नीलामी",
    "Bid & Draft Pro Stars" to "प्रो स्टार्स पर बोली लगाएं",
    "Bid & Draft Raiders" to "रेडर्स की बोली लगाएं",
    "STATISTICS" to "आंकड़े और रिकॉर्ड",
    "Team Win Rate, Raid Ratio & Player Records" to "जीत दर, रेड अनुपात और खिलाड़ी रिकॉर्ड",
    "SETTINGS" to "सेटिंग्स",
    "Themes, Language, Audio & Official Rules" to "थीम, भाषा, ध्वनि और खेल के नियम",
    "SELECT TOURNAMENT TYPE" to "टूर्नामेंट प्रकार का चयन करें",
    "PRO KABADDI LEAGUE" to "प्रो कबड्डी लीग",
    "KABADDI WORLD CUP" to "कबड्डी वर्ल्ड कप",
    "CHAMPIONSHIP SQUAD" to "चैंपियनशिप की तैयारी",
    "START NEW TOURNAMENT" to "नया टूर्नामेंट शुरू करें",
    "CONTINUE TOURNAMENT" to "टूर्नामेंट जारी रखें",
    "SELECT YOUR TEAM" to "अपनी टीम चुनें",
    "DIFFICULTY & TIMING" to "कठिनाई और समय सीमा",
    "CREATE TOURNAMENT" to "टूर्नामेंट बनाएं",
    "BACK" to "पीछे जाएं",
    "SCHEDULE" to "शेड्यूल",
    "STANDINGS" to "स्थान तालिका",
    "PLAYER STATS" to "खिलाड़ी रिकॉर्ड",
    "PLAY MATCH" to "मैच खेलें",
    "AUTO-PLAY" to "ऑटो सिमुलेशन",
    "GRAND FINALE" to "भव्य फाइनल",
    "GRAND FINALE / भव्य फाइनल" to "भव्य फाइनल",
    "PLAYOFFS: SEMI-FINALS / सेमीफाइनल जंग" to "सेमीफाइनल जंग",
    "Top 4 qualify for Semifinals" to "शीर्ष 4 सेमीफाइनल के लिए क्वालीफाई करेंगे",
    "LIVE" to "लाइव",
    "LIVE AUCTION" to "लाइव नीलामी",
    "Bid elite Raiders" to "खिलाड़ियों की बोली लगाएं",
    "OPEN" to "सक्रिय",
    "STATISTICS & RECORDS" to "आंकड़े और रिकॉर्ड",
    "Inspect team win rate, raid ratio & roster" to "जीत दर, रेड अनुपात और टीम देखें",
    "GAME SETTINGS" to "खेल सेटिंग्स",
    "Modify graphics, sounds & read Rules" to "थीम, ध्वनि और गेम के नियम बदलें",

    // Profile Header & Stats Summary
    "TEAM MANAGER" to "टीम प्रबंधक",
    "SEASON 10 HUB" to "सीजन 10 हब",
    "HQ STATS" to "मुख्य आंकड़े",
    "MATED" to "खेले गए",
    "W/L RATIO" to "जीत / हार",
    "CHAMP" to "विजेता",

    // Quick Play Config
    "QUICK PLAY ARENA" to "क्विक प्ले एरिना",
    "SELECT RAID & TACKLE DIFFICULTY" to "रेड और टैकल कठिनाई चुनें",
    "MATCH SPEED MULTIPLIER" to "मैच की गति",
    "CHOOSE YOUR TEAM" to "अपनी टीम चुनें",
    "SELECT OPPONENT TEAM" to "विपक्षी टीम चुनें",
    "START QUICK SIMULATION" to "मैच शुरू करें",
    "Matches won with chosen configurations add to persistent records." to "चुने गए विकल्पों के साथ मैच जीतने पर रिकॉर्ड सुरक्षित होंगे।",

    // Team Selector
    "TAP TO SELECT" to "चुनने के लिए टैप करें",

    // Playbook & Settings
    "PLAYBOOK & SETTINGS" to "प्लेबुक और सेटिंग्स",
    "🌐 APP LANGUAGE / खेल की भाषा" to "🌐 खेल की भाषा (Language)",
    "🎨 CHOOSE GAME THEME / खेल का रंग और थीम" to "🎨 खेल का रंग और थीम (Theme)",
    "👤 MANAGER DIRECTORY / प्रबन्धक प्रोफ़ाइल" to "👤 प्रबंधक प्रोफ़ाइल",
    "Manager Name" to "प्रबंधक का नाम",
    "Changes the manager name shown on your main hub dashboard screen instantly." to "मुख्य स्क्रीन पर दिखने वाले प्रबंधक का नाम तुरंत बदलें।",
    "🔊 SOUND & ENVIRONMENT CONTROLS" to "🔊 ध्वनि नियंत्रण और विकल्प",
    "Stadium Atmosphere / BGM" to "स्टेडियम का माहौल / बैकग्राउंड संगीत",
    "Rousing ambient background music" to "उत्साहजनक पृष्ठभूमि संगीत",
    "Whistles & Mat Sound Effects (SFX)" to "मैट ध्वनि प्रभाव (SFX)",
    "Audible whistles, raids, and tackle bangs" to "सीटी, रेड और टैकल की आवाजें",
    "Live Pro Commentary" to "लाइव कमेंट्री",
    "Bilingual 'Super Raid!' match event alerts" to "मैच कमेंट्री और घोषणाएं",
    "⚡ MATCH SIMULATION SPEED / गेमप्ले गति" to "⚡ मैच सिम्युलेटर की गति",
    "Accelerates raider and defender decisions in the Match Arena simulation engine so you can finish matches faster." to "सिमुलेशन को तेज करें ताकि मैच जल्दी खत्म हो सकें।",
    "📕 OFFICIAL KABADDI PLAYBOOK" to "📕 आधिकारिक कबड्डी प्लेबुक (नियम)",
    "🚨 DANGER ZONE / डेटा रीसेट" to "🚨 डेंजर जोन / डेटा रीसेट",
    "This action will permanently wipe all matches won/lost statistics, individual real-time player points leaderboard records, and reset manager name. It cannot be undone." to "इससे आपकी सारी प्रगति, मैच रिकॉर्ड और आंकड़े हमेशा के लिए मिट जाएंगे।",
    "RESET PROGRESS & PLAYER RECORDS / पूरी प्रगति साफ़ करें" to "डेटा और आंकड़े रीसेट करें",
    "APPLY & RETURN TO HUBAREA / लागू करें और मुख्य सूची पर जाएं" to "लागू करें और मुख्य सूची पर जाएं",
    "Settings saved successfully! / सेटिंग्स सुरक्षित की गईं!" to "सेटिंग्स सफलतापूर्वक सुरक्षित की गईं!",

    // Stats
    "TEAM STATISTICS" to "टीम के आंकड़े",
    "🏆 PLAYER LEADERBOARDS" to "🏆 खिलाड़ी लीडरबोर्ड",
    "RAID STATISTICS" to "रेड आंकड़े",
    "TACKLE STATISTICS" to "टैकल आंकड़े",
    "WIN RATE" to "जीत की दर",
    "TOTAL REVENUE" to "कुल राजस्व",
    "HIGHEST MATCH SCORE" to "सर्वोच्च मैच स्कोर",
    "SHOW TOP 10 PLAYERS" to "शीर्ष 10 खिलाड़ी देखें",
    "SHOW ALL" to "सभी देखें",
    "RAIDERS" to "रेडर (आक्रमणकारी)",
    "DEFENDERS" to "डिफेंडर (रक्षक)",
    "ALL-ROUNDERS" to "ऑल-राउंडर",

    // Live Arena
    "LIVE STATS" to "लाइव आंकड़े",
    "UPCOMING RAID" to "आगामी रेड",
    "SUPER RAID" to "सुपर रेड",
    "SUPER TACKLE" to "सुपर टैकल",
    "ALL OUT" to "ऑल आउट",
    "SCORE BOARD" to "स्कोर बोर्ड",
    "BONUS" to "बोनस",
    "TOUCH" to "टच",
    "MATCH OVER" to "मैच समाप्त",
    "MATCH ARENA" to "मैच एरिना",

    // Tournament
    "TOURNAMENT LEADERSHIP" to "टूर्नामेंट लीडरशिप",
    "PLAY MATCH" to "मैच खेलें",
    "Cup Season Pro 1" to "कप सीजन प्रो 1",

    // Auction
    "PRO KABADDI AUCTION" to "प्रो कबड्डी नीलामी (ऑक्शन)",
    "ELITE PLAYERS AUCTION LIST" to "अभिजात वर्ग खिलाड़ी नीलामी",
    "YOUR BUDGET" to "आपका बजट",
    "CURRENT BID" to "वर्तमान बोली",
    "BID NOW" to "बोली लगाएं",
    "PASS" to "छोड़ें"
)

data class PlayerAggregatedStats(
    val name: String,
    val role: PlayerRole,
    val jerseyNumber: Int,
    val teamName: String,
    val successfulRaids: Int,
    val unsuccessfulRaids: Int,
    val totalRaidPoints: Int,
    val successfulTackles: Int,
    val unsuccessfulTackles: Int,
    val totalTacklePoints: Int,
    val overallTotalPoints: Int,
    val totalMatches: Int,
    val raidSuccessPercentage: Float,
    val tackleSuccessPercentage: Float
)

// ==========================================
// SCREEN 3: DETAILED LIVE TEAM STATISTICS
// ==========================================
@Composable
fun TeamStatisticsScreen(
    sharedPrefs: SharedPreferences,
    matchesPlayed: Int,
    matchesWon: Int,
    matchesLost: Int,
    matchesTied: Int,
    totalUserPoints: Int,
    totalOpponentPoints: Int,
    totalRaidPoints: Int,
    totalTacklePoints: Int,
    highestMatchScore: Int,
    onResetStats: () -> Unit,
    onBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    val allPlayersStats = remember(matchesPlayed) {
        PKL_SEASON_10_TEAMS.flatMap { team ->
            getPlayersForTeam(team.name).map { player ->
                val name = player.name
                val successfulRaids = sharedPrefs.getInt("player_${name}_successfulRaids", 0)
                val unsuccessfulRaids = sharedPrefs.getInt("player_${name}_unsuccessfulRaids", 0)
                val totalRaidPoints = sharedPrefs.getInt("player_${name}_totalRaidPoints", 0)
                val successfulTackles = sharedPrefs.getInt("player_${name}_successfulTackles", 0)
                val unsuccessfulTackles = sharedPrefs.getInt("player_${name}_unsuccessfulTackles", 0)
                val totalTacklePoints = sharedPrefs.getInt("player_${name}_totalTacklePoints", 0)
                val overallTotalPoints = sharedPrefs.getInt("player_${name}_overallTotalPoints", 0)
                val totalMatches = sharedPrefs.getInt("player_${name}_totalMatches", 0)

                val totalRaids = successfulRaids + unsuccessfulRaids
                val raidSuccessPercentage = if (totalRaids > 0) (successfulRaids.toFloat() / totalRaids * 100f) else 0f

                val totalTackles = successfulTackles + unsuccessfulTackles
                val tackleSuccessPercentage = if (totalTackles > 0) (successfulTackles.toFloat() / totalTackles * 100f) else 0f

                PlayerAggregatedStats(
                    name = name,
                    role = player.role,
                    jerseyNumber = player.jerseyNumber,
                    teamName = team.name,
                    successfulRaids = successfulRaids,
                    unsuccessfulRaids = unsuccessfulRaids,
                    totalRaidPoints = totalRaidPoints,
                    successfulTackles = successfulTackles,
                    unsuccessfulTackles = unsuccessfulTackles,
                    totalTacklePoints = totalTacklePoints,
                    overallTotalPoints = overallTotalPoints,
                    totalMatches = totalMatches,
                    raidSuccessPercentage = raidSuccessPercentage,
                    tackleSuccessPercentage = tackleSuccessPercentage
                )
            }
        }.sortedByDescending { it.overallTotalPoints }
    }

    val winRate = if (matchesPlayed > 0) {
        (matchesWon.toFloat() / matchesPlayed.toFloat() * 100f).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Upper App bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MatSlate)
                    .testTag("btn_stats_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PlainWhite
                )
            }

            Text(
                text = "TEAM STATISTICS",
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                color = ChampionGold
            )

            // Reset action button
            IconButton(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF3B1E2B)) // dark crimson
                    .testTag("btn_stats_reset_trigger")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Reset",
                    tint = Color(0xFFFF5252)
                )
            }
        }

        // 1. OVERALL WIN RATE DONUT CONTAINER (Drawn beautifully with Canvas and overlaid stats)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MatSlate),
            border = BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "CAMPAIGN PERFORMANCE",
                    color = ChampionGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Win Rate Arc / Ring
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 10.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val radius = diameter / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)

                            // Grey background track
                            drawCircle(
                                color = Color(0xFF131A2D),
                                radius = radius,
                                center = center,
                                style = Stroke(width = strokeWidth)
                            )

                            // Colorful Win Ratio display arc
                            val sweepAngle = (winRate.toFloat() / 100f) * 360f
                            drawArc(
                                color = if (matchesPlayed > 0) Color(0xFF10B981) else Color(0xFF334155),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$winRate%",
                                color = PlainWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "WIN RATE",
                                color = SlateMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            )
                        }
                    }

                    // Detailed Play Record Badge Layout
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        StatBadge(label = "Matches Played", value = "$matchesPlayed", color = PlainWhite, icon = Icons.Default.PlayArrow)
                        StatBadge(label = "Wins Recorded", value = "$matchesWon", color = Color(0xFF10B981), icon = Icons.Default.EmojiEvents)
                        StatBadge(label = "Losses Recorded", value = "$matchesLost", color = Color(0xFFFF5252), icon = Icons.Default.Cancel)
                        StatBadge(label = "Ties Drawn", value = "$matchesTied", color = SlateMuted, icon = Icons.Default.Minimize)
                    }
                }
            }
        }

        // 2. CORE PERFORMANCE GRID (Cards representing detailed statistics parameters)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatMetricCard(
                        title = "TOTAL RAID PTS",
                        value = "$totalRaidPoints",
                        subtitle = "Avg: ${if (matchesPlayed > 0) String.format("%.1f", totalRaidPoints.toFloat() / matchesPlayed) else "0.0"} / match",
                        accentColor = ClayOrange,
                        icon = Icons.Default.Bolt,
                        testTag = "card_total_raid_pts"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatMetricCard(
                        title = "TOTAL TACKLE PTS",
                        value = "$totalTacklePoints",
                        subtitle = "Avg: ${if (matchesPlayed > 0) String.format("%.1f", totalTacklePoints.toFloat() / matchesPlayed) else "0.0"} / match",
                        accentColor = Color(0xFF10B981),
                        icon = Icons.Default.Shield,
                        testTag = "card_total_tackle_pts"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatMetricCard(
                        title = "HIGHEST SCORE",
                        value = "$highestMatchScore",
                        subtitle = "Single Match Record",
                        accentColor = ChampionGold,
                        icon = Icons.Default.Star,
                        testTag = "card_highest_score"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    val averageScore = if (matchesPlayed > 0) {
                        String.format("%.1f", totalUserPoints.toFloat() / matchesPlayed)
                    } else {
                        "0.0"
                    }
                    StatMetricCard(
                        title = "AVG MATCH PTS",
                        value = averageScore,
                        subtitle = "Total: $totalUserPoints pts",
                        accentColor = RaidCyan,
                        icon = Icons.Default.Timeline,
                        testTag = "card_avg_score"
                    )
                }
            }
        }

        // 3. CATEGORY LEADERBOARD TABLE (Raider / Defender / All Rounder)
        Text(
            text = "🏆 PLAYER LEADERBOARDS",
            color = ChampionGold,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        var selectedRoleTab by remember { mutableStateOf(PlayerRole.RAIDER) }
        var showAllLeaderboardPlayers by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111827), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                PlayerRole.RAIDER to "RAIDERS",
                PlayerRole.DEFENDER to "DEFENDERS",
                PlayerRole.ALL_ROUNDER to "ALL-ROUNDERS"
            ).forEach { (role, label) ->
                val isSelected = selectedRoleTab == role
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ClayOrange else Color.Transparent)
                        .clickable { 
                            selectedRoleTab = role 
                            showAllLeaderboardPlayers = false
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) PlainWhite else SlateMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        val filteredPlayers = remember(allPlayersStats, selectedRoleTab) {
            allPlayersStats.filter { it.role == selectedRoleTab }
        }

        val visiblePlayers = if (showAllLeaderboardPlayers) filteredPlayers else filteredPlayers.take(10)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            visiblePlayers.forEachIndexed { index, player ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MatSlate),
                    border = BorderStroke(1.dp, if (index < 3 && player.overallTotalPoints > 0) ChampionGold.copy(alpha = 0.4f) else SlateMuted.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header row (Rank, Name, Team, Points)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Position Circle
                                val rankBg = when (index) {
                                    0 -> ChampionGold
                                    1 -> Color(0xFF94A3B8)
                                    2 -> Color(0xFFB45309)
                                    else -> Color(0xFF334155)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(rankBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = PlainWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Column {
                                    Text(
                                        text = player.name,
                                        color = PlainWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = player.teamName,
                                            color = ClayOrange,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "#${player.jerseyNumber}",
                                            color = SlateMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            // Dynamic Overall Points Summary
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${player.overallTotalPoints} PTS",
                                    color = ChampionGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${player.totalMatches} Matches",
                                    color = SlateMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        HorizontalDivider(color = SlateMuted.copy(alpha = 0.1f))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Raid stats
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "RAID STATISTICS",
                                        color = ClayOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 8.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Success Raids", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.successfulRaids}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Unsuccess Raids", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.unsuccessfulRaids}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Raid Points", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.totalRaidPoints}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Raid %", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = String.format("%.1f%%", player.raidSuccessPercentage), color = ClayOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Tackle stats
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "TACKLE STATISTICS",
                                        color = RaidCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 8.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Success Tackles", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.successfulTackles}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Unsuccess Tackles", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.unsuccessfulTackles}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Tackle Points", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = "${player.totalTacklePoints}", color = PlainWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Tackle %", color = SlateMuted, fontSize = 9.sp)
                                        Text(text = String.format("%.1f%%", player.tackleSuccessPercentage), color = RaidCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredPlayers.size > 10) {
                TextButton(
                    onClick = { showAllLeaderboardPlayers = !showAllLeaderboardPlayers },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (showAllLeaderboardPlayers) "SHOW TOP 10 PLAYERS" else "SHOW ALL ${filteredPlayers.size} ${selectedRoleTab.name}S",
                        color = ChampionGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Support Stats Information Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            border = BorderStroke(1.dp, SlateMuted.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ChampionGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "These statistics reflect all live matches completed on your local device. Work hard to boost your win rate!",
                    color = SlateMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // Return button CTE
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_stats_home_back"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ClayOrange,
                contentColor = AlwaysWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = AlwaysWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RETURN TO HQ",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = AlwaysWhite
            )
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xFF0F172A),
            tonalElevation = 6.dp,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "RESET STATISTICS?",
                    color = PlainWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently clear all matches played, win rate, total points, and individual player history statistics? This action cannot be undone.",
                    color = SlateMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetStats()
                        showResetDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_reset")
                ) {
                    Text(
                        text = "YES, RESET",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false },
                    modifier = Modifier.testTag("btn_cancel_reset")
                ) {
                    Text(
                        text = "CANCEL",
                        color = PlainWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        )
    }
}

@Composable
fun StatBadge(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .background(Color(0xFF131A2D), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = label,
            color = SlateMuted,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp
        )
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
        )
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MatSlate),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.loc(),
                    color = SlateMuted,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = value,
                color = PlainWhite,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Text(
                text = subtitle.loc(),
                color = SlateMuted,
                fontSize = 9.sp
            )
        }
    }
}

// ==========================================
// TOURNAMENT SYSTEM STRUCTS & HELPERS
// ==========================================

enum class TournamentStage {
    LEAGUE,
    SEMIFINALS,
    FINAL,
    FINISHED
}

data class TournamentMatch(
    val id: Int,
    val teamAName: String,
    val teamBName: String,
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val played: Boolean = false,
    val isAuto: Boolean = false
)

data class StandingRow(
    val teamName: String,
    val played: Int,
    val win: Int,
    val loss: Int,
    val tie: Int,
    val pointsScored: Int,
    val pointsConceded: Int,
    val points: Int,
    val pointDiff: Int
)

fun TournamentMatch.serialize(): String {
    return "$id;$teamAName;$teamBName;$scoreA;$scoreB;$played;$isAuto"
}

fun deserializeTournamentMatch(s: String): TournamentMatch {
    val parts = s.split(";")
    return TournamentMatch(
        id = parts[0].toInt(),
        teamAName = parts[1],
        teamBName = parts[2],
        scoreA = parts[3].toInt(),
        scoreB = parts[4].toInt(),
        played = parts[5].toBoolean(),
        isAuto = parts.getOrNull(6)?.toBoolean() ?: false
    )
}

fun saveTournamentToPrefs(
    sharedPrefs: SharedPreferences,
    type: String,
    yourTeamName: String,
    difficulty: String,
    timing: Int,
    stage: TournamentStage,
    matches: List<TournamentMatch>,
    semi1: TournamentMatch? = null,
    semi2: TournamentMatch? = null,
    finalMatch: TournamentMatch? = null,
    isCompleted: Boolean = false
) {
    val editor = sharedPrefs.edit()
    editor.putBoolean("tournament_active", true)
    editor.putString("tournament_type", type)
    editor.putString("tournament_your_team", yourTeamName)
    editor.putString("tournament_difficulty", difficulty)
    editor.putInt("tournament_timing", timing)
    editor.putString("tournament_stage", stage.name)
    editor.putString("tournament_matches_list", matches.joinToString("|") { it.serialize() })
    editor.putString("tournament_semi1", semi1?.serialize() ?: "")
    editor.putString("tournament_semi2", semi2?.serialize() ?: "")
    editor.putString("tournament_final", finalMatch?.serialize() ?: "")
    editor.putBoolean("tournament_is_completed", isCompleted)
    editor.apply()
}

fun loadTournamentMatchesFromPrefs(sharedPrefs: SharedPreferences): List<TournamentMatch> {
    val s = sharedPrefs.getString("tournament_matches_list", "") ?: ""
    if (s.isEmpty()) return emptyList()
    return s.split("|").filter { it.isNotEmpty() }.map { deserializeTournamentMatch(it) }
}

fun loadTournamentMatchFromPrefs(sharedPrefs: SharedPreferences, key: String): TournamentMatch? {
    val s = sharedPrefs.getString(key, "") ?: ""
    if (s.isEmpty()) return null
    return deserializeTournamentMatch(s)
}

fun saveTournamentMatchResult(
    sharedPrefs: SharedPreferences,
    matchId: Int,
    scoreA: Int,
    scoreB: Int,
    yourSquad: List<LivePlayerState>,
    yourBench: List<LivePlayerState>,
    oppSquad: List<LivePlayerState>
) {
    val tournMatchesStr = sharedPrefs.getString("tournament_matches_list", "") ?: ""
    val yourTeamName = sharedPrefs.getString("tournament_your_team", "") ?: ""
    
    val matches = tournMatchesStr.split("|").filter { it.isNotEmpty() }.map { s ->
        val m = deserializeTournamentMatch(s)
        if (m.id == matchId) {
            val isUserTeamA = m.teamAName == yourTeamName
            val finalScoreA = if (isUserTeamA) scoreA else scoreB
            val finalScoreB = if (isUserTeamA) scoreB else scoreA
            m.copy(scoreA = finalScoreA, scoreB = finalScoreB, played = true)
        } else {
            m
        }
    }
    
    val editor = sharedPrefs.edit()
    editor.putString("tournament_matches_list", matches.joinToString("|") { it.serialize() })
    
    if (matchId == 1001) {
        val s1 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi1")
        if (s1 != null) {
            val isUserTeamA = s1.teamAName == yourTeamName
            val finalScoreA = if (isUserTeamA) scoreA else scoreB
            val finalScoreB = if (isUserTeamA) scoreB else scoreA
            editor.putString("tournament_semi1", s1.copy(scoreA = finalScoreA, scoreB = finalScoreB, played = true).serialize())
        }
    } else if (matchId == 1002) {
        val s2 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi2")
        if (s2 != null) {
            val isUserTeamA = s2.teamAName == yourTeamName
            val finalScoreA = if (isUserTeamA) scoreA else scoreB
            val finalScoreB = if (isUserTeamA) scoreB else scoreA
            editor.putString("tournament_semi2", s2.copy(scoreA = finalScoreA, scoreB = finalScoreB, played = true).serialize())
        }
    } else if (matchId == 2001) {
        val fm = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_final")
        if (fm != null) {
            val isUserTeamA = fm.teamAName == yourTeamName
            val finalScoreA = if (isUserTeamA) scoreA else scoreB
            val finalScoreB = if (isUserTeamA) scoreB else scoreA
            editor.putString("tournament_final", fm.copy(scoreA = finalScoreA, scoreB = finalScoreB, played = true).serialize())
            editor.putBoolean("tournament_is_completed", true)
        }
    }
    
    val type = sharedPrefs.getString("tournament_type", "PKL") ?: "PKL"
    val combinedYour = yourSquad + yourBench
    val allPlayers = combinedYour + oppSquad
    
    allPlayers.forEach { p ->
        val keyPrefix = "t_stats_${type}_player_${p.name}"
        val curRaid = sharedPrefs.getInt("${keyPrefix}_raid_pts", 0)
        val curTackle = sharedPrefs.getInt("${keyPrefix}_tackle_pts", 0)
        val curTotal = sharedPrefs.getInt("${keyPrefix}_total_pts", 0)
        val curMatches = sharedPrefs.getInt("${keyPrefix}_matches", 0)
        
        editor.putInt("${keyPrefix}_raid_pts", curRaid + p.totalRaidPoints)
        editor.putInt("${keyPrefix}_tackle_pts", curTackle + p.totalTacklePoints)
        editor.putInt("${keyPrefix}_total_pts", curTotal + p.overallTotalPoints)
        editor.putInt("${keyPrefix}_matches", curMatches + 1)
    }
    
    editor.putInt("tournament_playing_match_id", -1)
    editor.apply()
    
    checkAndAdvanceTournamentStage(sharedPrefs)
}

fun simulateAndSavePlayerStatsForMatch(
    sharedPrefs: SharedPreferences, 
    type: String, 
    teamName: String, 
    teamScore: Int
) {
    val players = getPlayersForTeam(teamName)
    if (players.isEmpty()) return
    
    val totalRaidPoints = (teamScore * 0.65f).toInt().coerceAtLeast(1)
    val totalTacklePoints = (teamScore - totalRaidPoints).coerceAtLeast(1)
    
    val raidersAndARs = players.filter { it.role == PlayerRole.RAIDER || it.role == PlayerRole.ALL_ROUNDER }
    val defendersAndARs = players.filter { it.role == PlayerRole.DEFENDER || it.role == PlayerRole.ALL_ROUNDER }
    
    val raidPtsMap = mutableMapOf<String, Int>()
    var remainingRaidPts = totalRaidPoints
    if (raidersAndARs.isNotEmpty()) {
        val sortedR = raidersAndARs.sortedByDescending { it.raidingAbility }
        sortedR.forEachIndexed { idx, p ->
            val pts = if (idx == 0) {
                (remainingRaidPts * 0.5f).toInt().coerceIn(1, remainingRaidPts)
            } else if (idx == 1 && remainingRaidPts > 0) {
                (remainingRaidPts * 0.6f).toInt().coerceIn(0, remainingRaidPts)
            } else {
                remainingRaidPts
            }
            raidPtsMap[p.name] = pts
            remainingRaidPts -= pts
        }
    }
    
    val tacklePtsMap = mutableMapOf<String, Int>()
    var remainingTacklePts = totalTacklePoints
    if (defendersAndARs.isNotEmpty()) {
        val sortedD = defendersAndARs.sortedByDescending { it.defendingAbility }
        sortedD.forEachIndexed { idx, p ->
            val pts = if (idx == 0) {
                (remainingTacklePts * 0.45f).toInt().coerceIn(1, remainingTacklePts)
            } else if (idx == 1 && remainingTacklePts > 0) {
                (remainingTacklePts * 0.5f).toInt().coerceIn(0, remainingTacklePts)
            } else {
                remainingTacklePts
            }
            tacklePtsMap[p.name] = pts
            remainingTacklePts -= pts
        }
    }
    
    val editor = sharedPrefs.edit()
    players.forEach { p ->
        val rPts = raidPtsMap[p.name] ?: 0
        val tPts = tacklePtsMap[p.name] ?: 0
        val totPts = rPts + tPts
        
        val keyPrefix = "t_stats_${type}_player_${p.name}"
        val curRaid = sharedPrefs.getInt("${keyPrefix}_raid_pts", 0)
        val curTackle = sharedPrefs.getInt("${keyPrefix}_tackle_pts", 0)
        val curTotal = sharedPrefs.getInt("${keyPrefix}_total_pts", 0)
        val curMatches = sharedPrefs.getInt("${keyPrefix}_matches", 0)
        
        editor.putInt("${keyPrefix}_raid_pts", curRaid + rPts)
        editor.putInt("${keyPrefix}_tackle_pts", curTackle + tPts)
        editor.putInt("${keyPrefix}_total_pts", curTotal + totPts)
        editor.putInt("${keyPrefix}_matches", curMatches + 1)
    }
    editor.apply()
}

fun generateRoundRobinMatches(teams: List<PklTeam>): List<TournamentMatch> {
    val matches = mutableListOf<TournamentMatch>()
    var matchId = 1
    for (i in 0 until teams.size) {
        for (j in i + 1 until teams.size) {
            matches.add(
                TournamentMatch(
                    id = matchId++,
                    teamAName = teams[i].name,
                    teamBName = teams[j].name,
                    scoreA = 0,
                    scoreB = 0,
                    played = false
                )
            )
        }
    }
    return matches.shuffled()
}

fun calculateStandings(matches: List<TournamentMatch>, teams: List<PklTeam>): List<StandingRow> {
    val map = teams.associate { it.name to mutableListOf<TournamentMatch>() }.toMutableMap()
    matches.filter { it.played }.forEach { m ->
        map[m.teamAName]?.add(m)
        map[m.teamBName]?.add(m)
    }
    val rows = teams.map { team ->
        var played = 0
        var win = 0
        var loss = 0
        var tie = 0
        var pointsScored = 0
        var pointsConceded = 0
        var points = 0
        
        val teamMatches = map[team.name] ?: emptyList()
        teamMatches.forEach { m ->
            played++
            val isTeamA = m.teamAName == team.name
            val teamScore = if (isTeamA) m.scoreA else m.scoreB
            val oppScore = if (isTeamA) m.scoreB else m.scoreA
            
            pointsScored += teamScore
            pointsConceded += oppScore
            
            if (teamScore > oppScore) {
                win++
                points += 5
            } else if (teamScore == oppScore) {
                tie++
                points += 3
            } else {
                loss++
                val diff = oppScore - teamScore
                if (diff <= 3) {
                    points += 2
                } else {
                    points += 1
                }
            }
        }
        StandingRow(
            teamName = team.name,
            played = played,
            win = win,
            loss = loss,
            tie = tie,
            pointsScored = pointsScored,
            pointsConceded = pointsConceded,
            points = points,
            pointDiff = pointsScored - pointsConceded
        )
    }
    return rows.sortedWith(
        compareByDescending<StandingRow> { it.points }
            .thenByDescending { it.pointDiff }
            .thenByDescending { it.win }
    )
}

fun checkAndAdvanceTournamentStage(sharedPrefs: SharedPreferences) {
    val stageStr = sharedPrefs.getString("tournament_stage", "LEAGUE") ?: "LEAGUE"
    val stage = TournamentStage.valueOf(stageStr)
    val type = sharedPrefs.getString("tournament_type", "PKL") ?: "PKL"
    val pklTeams = if (type == "PKL") PKL_SEASON_10_TEAMS else WORLD_CUP_TEAMS
    
    if (stage == TournamentStage.LEAGUE) {
        val matches = loadTournamentMatchesFromPrefs(sharedPrefs)
        val allPlayed = matches.all { it.played }
        if (allPlayed && matches.isNotEmpty()) {
            val standings = calculateStandings(matches, pklTeams)
            if (standings.size >= 4) {
                val rank1 = standings[0].teamName
                val rank2 = standings[1].teamName
                val rank3 = standings[2].teamName
                val rank4 = standings[3].teamName
                
                val semi1 = TournamentMatch(1001, rank1, rank4, 0, 0, false)
                val semi2 = TournamentMatch(1002, rank2, rank3, 0, 0, false)
                
                sharedPrefs.edit()
                    .putString("tournament_stage", TournamentStage.SEMIFINALS.name)
                    .putString("tournament_semi1", semi1.serialize())
                    .putString("tournament_semi2", semi2.serialize())
                    .apply()
            }
        }
    } else if (stage == TournamentStage.SEMIFINALS) {
        val s1 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi1")
        val s2 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi2")
        if (s1 != null && s2 != null && s1.played && s2.played) {
            val win1 = if (s1.scoreA >= s1.scoreB) s1.teamAName else s1.teamBName
            val win2 = if (s2.scoreA >= s2.scoreB) s2.teamAName else s2.teamBName
            
            val finalMatch = TournamentMatch(2001, win1, win2, 0, 0, false)
            
            sharedPrefs.edit()
                .putString("tournament_stage", TournamentStage.FINAL.name)
                .putString("tournament_final", finalMatch.serialize())
                .apply()
        }
    } else if (stage == TournamentStage.FINAL) {
        val fm = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_final")
        if (fm != null && fm.played) {
            sharedPrefs.edit()
                .putString("tournament_stage", TournamentStage.FINISHED.name)
                .putBoolean("tournament_is_completed", true)
                .apply()
        }
    }
}

// ==========================================
// TOURNAMENT USER INTERFACE MODULES (COMPOSE)
// ==========================================

enum class TournamentSubScreen {
    WELCOME,
    NEW_OR_CONTINUE,
    SELECT_TEAM,
    CONFIG,
    DASHBOARD
}

@Composable
fun TournamentScreen(
    onBack: () -> Unit,
    onStartTournamentMatch: (
        yourTeam: PklTeam,
        opponentTeam: PklTeam,
        difficulty: String,
        timing: Int,
        players: List<KabaddiPlayer>
    ) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("kabaddi_kings_prefs", Context.MODE_PRIVATE) }
    
    var subScreen by remember { mutableStateOf(TournamentSubScreen.WELCOME) }
    var tournType by remember { mutableStateOf("PKL") } // "PKL" or "WC"
    var selectedTeam by remember { mutableStateOf<PklTeam?>(null) }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var selectedMatchTiming by remember { mutableStateOf(10) }
    
    // Live Tournament State Loading
    val isTournActive = sharedPrefs.getBoolean("tournament_active", false)
    val savedType = sharedPrefs.getString("tournament_type", "PKL") ?: "PKL"
    
    // Auto redirect to DASHBOARD if continue is chosen or layout is active
    LaunchedEffect(isTournActive) {
        if (isTournActive) {
            subScreen = TournamentSubScreen.DASHBOARD
            tournType = savedType
        }
    }

    when (subScreen) {
        TournamentSubScreen.WELCOME -> {
            TournamentWelcomeView(
                onBack = onBack,
                onSelectType = { type ->
                    tournType = type
                    // If there's an active tournament of this exact type, show NEW_OR_CONTINUE, else go directly to SELECT_TEAM!
                    val hasCurrent = sharedPrefs.getBoolean("tournament_active", false) && 
                                     sharedPrefs.getString("tournament_type", "") == type
                    if (hasCurrent) {
                        subScreen = TournamentSubScreen.NEW_OR_CONTINUE
                    } else {
                        subScreen = TournamentSubScreen.SELECT_TEAM
                    }
                }
            )
        }
        TournamentSubScreen.NEW_OR_CONTINUE -> {
            TournamentNewOrContinueView(
                tournType = tournType,
                onBack = { subScreen = TournamentSubScreen.WELCOME },
                onChooseNew = {
                    // Reset tournament prefs for a fresh run
                    sharedPrefs.edit()
                        .putBoolean("tournament_active", false)
                        .remove("tournament_type")
                        .remove("tournament_your_team")
                        .remove("tournament_matches_list")
                        .remove("tournament_stage")
                        .remove("tournament_semi1")
                        .remove("tournament_semi2")
                        .remove("tournament_final")
                        .remove("tournament_is_completed")
                        .apply()
                        
                    // Explicitly wipe tournament player stats keys to prevent carrying over
                    val editor = sharedPrefs.edit()
                    sharedPrefs.all.keys.filter { it.startsWith("t_stats_${tournType}_") }.forEach { key ->
                        editor.remove(key)
                    }
                    editor.apply()
                    
                    selectedTeam = null
                    subScreen = TournamentSubScreen.SELECT_TEAM
                },
                onChooseContinue = {
                    subScreen = TournamentSubScreen.DASHBOARD
                }
            )
        }
        TournamentSubScreen.SELECT_TEAM -> {
            TournamentTeamSelectView(
                tournType = tournType,
                onBack = { subScreen = TournamentSubScreen.WELCOME },
                onTeamSelected = { team ->
                    selectedTeam = team
                    subScreen = TournamentSubScreen.CONFIG
                }
            )
        }
        TournamentSubScreen.CONFIG -> {
            TournamentConfigView(
                selectedTeam = selectedTeam,
                difficulty = selectedDifficulty,
                timing = selectedMatchTiming,
                onDifficultyChange = { selectedDifficulty = it },
                onTimingChange = { selectedMatchTiming = it },
                onBack = { subScreen = TournamentSubScreen.SELECT_TEAM },
                onCreate = {
                    val team = selectedTeam
                    if (team != null) {
                        val teamList = if (tournType == "PKL") PKL_SEASON_10_TEAMS else WORLD_CUP_TEAMS
                        val matches = generateRoundRobinMatches(teamList)
                        
                        saveTournamentToPrefs(
                            sharedPrefs = sharedPrefs,
                            type = tournType,
                            yourTeamName = team.name,
                            difficulty = selectedDifficulty,
                            timing = selectedMatchTiming,
                            stage = TournamentStage.LEAGUE,
                            matches = matches
                        )
                        subScreen = TournamentSubScreen.DASHBOARD
                    }
                }
            )
        }
        TournamentSubScreen.DASHBOARD -> {
            TournamentDashboardView(
                tournType = tournType,
                sharedPrefs = sharedPrefs,
                onBackToWelcome = {
                    subScreen = TournamentSubScreen.WELCOME
                },
                onStartTournamentMatch = onStartTournamentMatch
            )
        }
    }
}

// -------------------------------------------------------------
// VIEW 1: WELCOME SCREEN (PRO KABADDI VS WORLD CUP SELECTION)
// -------------------------------------------------------------
@Composable
fun TournamentWelcomeView(
    onBack: () -> Unit,
    onSelectType: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1527))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Tighter padding to fit edge edge systems
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0x11FFC72C), CircleShape)
                    .border(2.dp, ChampionGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = ChampionGold,
                    modifier = Modifier.size(44.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            Text(
                text = "SELECT TOURNAMENT TYPE".loc(),
                color = PlainWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "टूर्नामेंट के स्तर और टीमों का चयन करें",
                color = SlateMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // OPTION 1: Pro Kabaddi League
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectType("PKL") }
                    .testTag("btn_tourn_pkl"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
                border = BorderStroke(1.dp, Color(0x66FF5C22)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0x22FF5C22), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PKL",
                            color = ClayOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "PRO KABADDI LEAGUE".loc(),
                            color = PlainWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Experience premier franchise battles with 12 real PKL teams",
                            color = SlateMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // OPTION 2: World Cup
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectType("WC") }
                    .testTag("btn_tourn_wc"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
                border = BorderStroke(1.dp, Color(0x6606B6D4)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0x2206B6D4), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = RaidCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "KABADDI WORLD CUP".loc(),
                            color = PlainWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Lead your nation against the world's elite in a global battle",
                            color = SlateMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2640), contentColor = PlainWhite),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("btn_welcome_back")
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "BACK / पीछे जाएं", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// -------------------------------------------------------------
// VIEW 2: NEW OR CONTINUE VIEW
// -------------------------------------------------------------
@Composable
fun TournamentNewOrContinueView(
    tournType: String,
    onBack: () -> Unit,
    onChooseNew: () -> Unit,
    onChooseContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1527))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = ChampionGold,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CHAMPIONSHIP SQUAD".loc(),
                color = PlainWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "Type Selected: ".loc() + if (tournType == "PKL") "Pro Kabaddi" else "World Cup",
                color = SlateMuted,
                fontSize = 11.sp
            )
            
            Spacer(modifier = Modifier.height(36.dp))
            
            Button(
                onClick = onChooseContinue,
                colors = ButtonDefaults.buttonColors(containerColor = ChampionGold, contentColor = AlwaysDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_continue_tourn")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = AlwaysDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "CONTINUE TOURNAMENT / जारी रखें", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            OutlinedButton(
                onClick = onChooseNew,
                border = BorderStroke(1.5.dp, ClayOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_new_tourn")
            ) {
                Text(text = "START NEW TOURNAMENT / नया खेलें", color = ClayOrange, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
        
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2640), contentColor = PlainWhite),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(text = "BACK", fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// VIEW 3: SELECT YOUR TEAM GIRD
// -------------------------------------------------------------
@Composable
fun TournamentTeamSelectView(
    tournType: String,
    onBack: () -> Unit,
    onTeamSelected: (PklTeam) -> Unit
) {
    val teamsList = if (tournType == "PKL") PKL_SEASON_10_TEAMS else WORLD_CUP_TEAMS
    var activeSelection by remember { mutableStateOf<PklTeam?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1527))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "SELECT YOUR TEAM".loc(),
                color = PlainWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "अपनी पसंदीदा कबड्डी टीम चुनें",
                color = SlateMuted,
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(teamsList) { team ->
                    val isSelected = activeSelection?.name == team.name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeSelection = team }
                            .testTag("team_card_${team.name.replace(" ", "_")}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) team.primaryColor.copy(0.12f) else Color(0xFF1E2640)
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) team.primaryColor else Color(0xFF2E395E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(team.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = team.name.take(1).uppercase(),
                                        color = AlwaysWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = team.name,
                                        color = PlainWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = team.nickName,
                                        color = SlateMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Star Count Indicator
                                repeat(team.championStars) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ChampionGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                if (team.championStars == 0) {
                                    repeat(3) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = SlateMuted.copy(0.2f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2640), contentColor = PlainWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(text = "BACK", fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = { activeSelection?.let { onTeamSelected(it) } },
                enabled = activeSelection != null,
                colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp)
                    .testTag("btn_select_team_confirm")
            ) {
                Text(text = "NEXT / अगला कदम", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 4: TOURNAMENT DIFFICULTY & TIMING SETTINGS CONFIG VIEW
// -------------------------------------------------------------
@Composable
fun TournamentConfigView(
    selectedTeam: PklTeam?,
    difficulty: String,
    timing: Int,
    onDifficultyChange: (String) -> Unit, // String key-value
    onTimingChange: (Int) -> Unit,
    onBack: () -> Unit,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1527))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DIFFICULTY & TIMING".loc(),
                color = PlainWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "Configure formatting settings for league tournament",
                color = SlateMuted,
                fontSize = 11.sp
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            if (selectedTeam != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(selectedTeam.primaryColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Selected Squad: ".loc() + selectedTeam.name,
                            color = PlainWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // DIFFICULTY SELECTOR CHIPS
            Text(text = "CHAMPIONSHIP DIFFICULTY / कठिनाई स्तर", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Easy", "Medium", "Hard", "Pro").forEach { diffOption ->
                    val isSel = diffOption == difficulty
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ClayOrange else Color(0xFF1E2640))
                            .clickable { onDifficultyChange(diffOption) }
                            .padding(vertical = 10.dp)
                            .testTag("chip_diff_${diffOption.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diffOption,
                            color = if (isSel) AlwaysWhite else SlateMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // TIMING SELECTOR CHIPS
            Text(text = "MATCH TIMING SESSIONS / मैच समय", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(5, 10, 15, 20).forEach { timeOption ->
                    val isSel = timeOption == timing
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ClayOrange else Color(0xFF1E2640))
                            .clickable { onTimingChange(timeOption) }
                            .padding(vertical = 10.dp)
                            .testTag("chip_time_${timeOption}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$timeOption Min",
                            color = if (isSel) AlwaysWhite else SlateMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2640), contentColor = PlainWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(text = "BACK / वापस", fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = ClayOrange, contentColor = AlwaysWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("btn_create_tournament_confirm")
            ) {
                Text(text = "CREATE TOURNAMENT", fontWeight = FontWeight.Black)
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 5: CORE TOURNAMENT DASHBOARD HUB (TABS INCLUDED)
// -------------------------------------------------------------
@Composable
fun TournamentDashboardView(
    tournType: String,
    sharedPrefs: SharedPreferences,
    onBackToWelcome: () -> Unit,
    onStartTournamentMatch: (
        yourTeam: PklTeam,
        opponentTeam: PklTeam,
        difficulty: String,
        timing: Int,
        players: List<KabaddiPlayer>
    ) -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0 = Schedule, 1 = Standings, 2 = Player Stats
    
    // Loaded Settings Values
    val yourTeamName = remember { sharedPrefs.getString("tournament_your_team", "") ?: "" }
    val difficulty = remember { sharedPrefs.getString("tournament_difficulty", "Medium") ?: "Medium" }
    val timing = remember { sharedPrefs.getInt("tournament_timing", 10) }
    
    val allTeams = if (tournType == "PKL") PKL_SEASON_10_TEAMS else WORLD_CUP_TEAMS
    val yourTeamObj = allTeams.find { it.name == yourTeamName } ?: allTeams[0]
    
    // Handle state transitions live
    var matchesList by remember { mutableStateOf<List<TournamentMatch>>(emptyList()) }
    var stageStr by remember { mutableStateOf("LEAGUE") }
    var tournSemi1 by remember { mutableStateOf<TournamentMatch?>(null) }
    var tournSemi2 by remember { mutableStateOf<TournamentMatch?>(null) }
    var tournFinalMatch by remember { mutableStateOf<TournamentMatch?>(null) }
    var isCompletedState by remember { mutableStateOf(false) }
    
    // Simulate Live Scorecard Dialog state
    var simulatedMatchForPopup by remember { mutableStateOf<TournamentMatch?>(null) }
    var showSimulatedPopup by remember { mutableStateOf(false) }
    
    // Complete Profile inspect Dialog state
    var selectedPlayerForProfile by remember { mutableStateOf<KabaddiPlayer?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val reloadTournamentData = {
        matchesList = loadTournamentMatchesFromPrefs(sharedPrefs)
        stageStr = sharedPrefs.getString("tournament_stage", "LEAGUE") ?: "LEAGUE"
        tournSemi1 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi1")
        tournSemi2 = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_semi2")
        tournFinalMatch = loadTournamentMatchFromPrefs(sharedPrefs, "tournament_final")
        isCompletedState = sharedPrefs.getBoolean("tournament_is_completed", false)
    }
    
    LaunchedEffect(Unit) {
        reloadTournamentData()
    }

    val stage = TournamentStage.valueOf(stageStr)

    // Compute Standings Row List
    val standingsRows = remember(matchesList, tournStageStateChangedKey(stage, tournSemi1, tournSemi2, tournFinalMatch)) {
        calculateStandings(matchesList, allTeams)
    }
    
    // Find User's Rank
    val userRank = remember(standingsRows) {
        val idx = standingsRows.indexOfFirst { it.teamName == yourTeamName }
        if (idx != -1) idx + 1 else 1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1527))
    ) {
        // TOP HEADER BADGE PANEL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(yourTeamObj.primaryColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (tournType == "PKL") "PRO KABADDI LEAGUE" else "WORLD CUP BRACKET",
                            color = ChampionGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        Text(
                            text = yourTeamName.uppercase(),
                            color = PlainWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "CURRENT RANK / रैंक", color = SlateMuted, fontSize = 8.sp)
                        Text(text = "#$userRank", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "FORMAT / कठिनाई", color = SlateMuted, fontSize = 8.sp)
                        Text(text = difficulty.uppercase(), color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
        
        // TAB BUTTONS STRIP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val tabTitles = listOf("SCHEDULE", "STANDINGS", "PLAYER STATS")
            val tabTitlesHi = listOf("शेड्यूल", "स्थान तालिका", "खिलाड़ी रिकॉर्ड")
            tabTitles.forEachIndexed { index, title ->
                val isSelected = activeTab == index
                val displayTitle = if (com.example.ui.theme.currentLanguageState.value == "HINDI") tabTitlesHi[index] else title
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = index }
                        .padding(vertical = 12.dp)
                        .testTag("tab_button_$index"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayTitle,
                        color = if (isSelected) ChampionGold else SlateMuted,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(3.dp)
                            .background(if (isSelected) ChampionGold else Color.Transparent, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
        
        // CORNER ACTIVE CONTENT AREA
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> {
                    // TAB 0: SCHEDULE & PLAY ACTIONS
                    ScheduleTabContent(
                        tournType = tournType,
                        yourTeamName = yourTeamName,
                        difficulty = difficulty,
                        timing = timing,
                        stage = stage,
                        matchesList = matchesList,
                        tournSemi1 = tournSemi1,
                        tournSemi2 = tournSemi2,
                        tournFinalMatch = tournFinalMatch,
                        isCompleted = isCompletedState,
                        sharedPrefs = sharedPrefs,
                        onSimulateMatchFinished = { match ->
                            simulatedMatchForPopup = match
                            showSimulatedPopup = true
                            reloadTournamentData()
                        },
                        onPlayClicked = { match ->
                            val isUserTeamA = match.teamAName == yourTeamName
                            val oppTeamName = if (isUserTeamA) match.teamBName else match.teamAName
                            val oppTeamObj = allTeams.find { it.name == oppTeamName } ?: allTeams[0]
                            
                            // Save active playing match ID before entering selecting layout
                            sharedPrefs.edit().putInt("tournament_playing_match_id", match.id).apply()
                            
                            val fullSquad = getPlayersForTeam(yourTeamName)
                            onStartTournamentMatch(
                                yourTeamObj,
                                oppTeamObj,
                                difficulty,
                                timing,
                                fullSquad.take(7) // Draft top 7 as initial choice
                            )
                        }
                    )
                }
                1 -> {
                    // TAB 1: STANDINGS GRID LIST
                    StandingsTabContent(
                        standingsRows = standingsRows,
                        yourTeamName = yourTeamName,
                        allTeams = allTeams
                    )
                }
                2 -> {
                    // TAB 2: PLAYER TOURNAMENT LEADERBOARD
                    PlayerStatsTabContent(
                        tournType = tournType,
                        sharedPrefs = sharedPrefs,
                        allTeams = allTeams,
                        onPlayerInspect = { player ->
                            selectedPlayerForProfile = player
                            showProfileDialog = true
                        }
                    )
                }
            }
        }
        
        // BOTTOM GLOBAL TOURNAMENT BACK BUTTON
        Button(
            onClick = onBackToWelcome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2640), contentColor = PlainWhite),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_dashboard_leave_lobby")
        ) {
            Text(text = "LEAVE CAMPAIGN LOBBY / बाहर जाएं", fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }

    // SIMULATED COMPLETED Match Scorecard Summary Dialog
    if (showSimulatedPopup && simulatedMatchForPopup != null) {
        val match = simulatedMatchForPopup!!
        AlertDialog(
            onDismissRequest = { showSimulatedPopup = false },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            confirmButton = {
                Button(
                    onClick = { showSimulatedPopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ClayOrange)
                ) {
                    Text(text = "CLOSE / बंद करें", color = AlwaysWhite, fontWeight = FontWeight.Black)
                }
            },
            title = {
                Text(
                    text = "SIMULATED SCORECARD".loc(),
                    color = ChampionGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val colA = allTeams.find { it.name == match.teamAName }?.primaryColor ?: Color.Gray
                            Box(modifier = Modifier.size(24.dp).background(colA, CircleShape))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = match.teamAName, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "${match.scoreA}", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 26.sp)
                        }
                        Text(text = "VS", color = SlateMuted, fontWeight = FontWeight.Black)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val colB = allTeams.find { it.name == match.teamBName }?.primaryColor ?: Color.Gray
                            Box(modifier = Modifier.size(24.dp).background(colB, CircleShape))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = match.teamBName, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "${match.scoreB}", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 26.sp)
                        }
                    }
                    
                    HorizontalDivider(color = SlateMuted.copy(alpha = 0.3f))
                    
                    Text(
                        text = "Real-time automated AI simulation successfully calculated. Cumulative player statistics updated across rosters.",
                        color = SlateMuted,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    // COMPLETE PLAYER inspect Profile Dialog
    if (showProfileDialog && selectedPlayerForProfile != null) {
        val player = selectedPlayerForProfile!!
        val keyPrefix = "t_stats_${tournType}_player_${player.name}"
        
        val raidPts = sharedPrefs.getInt("${keyPrefix}_raid_pts", 0)
        val tacklePts = sharedPrefs.getInt("${keyPrefix}_tackle_pts", 0)
        val matchesPlayed = sharedPrefs.getInt("${keyPrefix}_matches", 0)
        val overallPoints = sharedPrefs.getInt("${keyPrefix}_total_pts", 0)

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp),
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ClayOrange)
                ) {
                    Text(text = "DISMISS / बंद करें", color = AlwaysWhite, fontWeight = FontWeight.Black)
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = player.name,
                        color = PlainWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    val team = allTeams.find { t -> getPlayersForTeam(t.name).any { it.name == player.name } }
                    if (team != null) {
                        Text(
                            text = team.name.uppercase(),
                            color = team.primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Ratings progress bars
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "RAIDING ATTACK RATIO", color = SlateMuted, fontSize = 9.sp)
                            Text(text = "${player.raidingAbility}%", color = ClayOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { player.raidingAbility / 100f },
                            color = ClayOrange,
                            trackColor = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                        )
                    }
                    
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "TACKLE WALL RATIO", color = SlateMuted, fontSize = 9.sp)
                            Text(text = "${player.defendingAbility}%", color = RaidCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { player.defendingAbility / 100f },
                            color = RaidCyan,
                            trackColor = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                        )
                    }
                    
                    HorizontalDivider(color = SlateMuted.copy(0.2f))
                    
                    // Stats cards grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1527))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "MATCHES", color = SlateMuted, fontSize = 7.sp)
                                    Text(text = "$matchesPlayed", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1527))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "RAID PTS", color = SlateMuted, fontSize = 7.sp)
                                    Text(text = "$raidPts", color = ClayOrange, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1527))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "TACKLE PTS", color = SlateMuted, fontSize = 7.sp)
                                    Text(text = "$tacklePts", color = RaidCyan, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1527))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "OVERALL PTS", color = SlateMuted, fontSize = 7.sp)
                                    Text(text = "$overallPoints", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

// Check state changes live to reload standings Rows beautifully
fun tournStageStateChangedKey(
    stage: TournamentStage,
    s1: TournamentMatch?,
    s2: TournamentMatch?,
    fm: TournamentMatch?
): String {
    return "${stage.name}_${s1?.played}_${s2?.played}_${fm?.played}"
}


// -------------------------------------------------------------
// TAB COMPOSABLE 1: SCHEDULE LIST CONTENT RENDERER
// -------------------------------------------------------------
@Composable
fun ScheduleTabContent(
    tournType: String,
    yourTeamName: String,
    difficulty: String,
    timing: Int,
    stage: TournamentStage,
    matchesList: List<TournamentMatch>,
    tournSemi1: TournamentMatch?,
    tournSemi2: TournamentMatch?,
    tournFinalMatch: TournamentMatch?,
    isCompleted: Boolean,
    sharedPrefs: SharedPreferences,
    onSimulateMatchFinished: (TournamentMatch) -> Unit,
    onPlayClicked: (TournamentMatch) -> Unit
) {
    var filterSelection by remember { mutableStateOf(0) } // 0 = ALL, 1 = YOUR TEAM, 2 = COMPLETED
    val context = LocalContext.current
    
    val allTeams = if (tournType == "PKL") PKL_SEASON_10_TEAMS else WORLD_CUP_TEAMS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // FILTER PILLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("ALL MATCHES", "YOUR MATCHES", "COMPLETED")
            val filtersHi = listOf("सभी मैच", "मेरे मैच", "पूरे हुए")
            filters.forEachIndexed { idx, filterLabel ->
                val isS = filterSelection == idx
                val dLab = if (com.example.ui.theme.currentLanguageState.value == "HINDI") filtersHi[idx] else filterLabel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isS) ClayOrange else Color(0xFF1E2640))
                        .clickable { filterSelection = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dLab,
                        color = if (isS) AlwaysWhite else SlateMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }
        
        // Render Bracket first if in PLAYOFF stages
        if (stage == TournamentStage.SEMIFINALS || stage == TournamentStage.FINAL || stage == TournamentStage.FINISHED) {
            PlayoffBracketSection(
                tournType = tournType,
                yourTeamName = yourTeamName,
                stage = stage,
                semi1 = tournSemi1,
                semi2 = tournSemi2,
                finalMatch = tournFinalMatch,
                isCompleted = isCompleted,
                sharedPrefs = sharedPrefs,
                onSimulateMatchFinished = onSimulateMatchFinished,
                onPlayClicked = onPlayClicked,
                allTeams = allTeams
            )
        } else {
            // RENDER Group Stage matches list
            val filteredMatches = remember(matchesList, filterSelection, yourTeamName) {
                when (filterSelection) {
                    1 -> matchesList.filter { it.teamAName == yourTeamName || it.teamBName == yourTeamName }
                    2 -> matchesList.filter { it.played }
                    else -> matchesList
                }
            }
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMatches) { m ->
                    val isYourMatch = m.teamAName == yourTeamName || m.teamBName == yourTeamName
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
                        border = BorderStroke(
                            width = if (isYourMatch) 1.5.dp else 0.5.dp,
                            color = if (isYourMatch) ChampionGold else Color(0xFF2E395E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (m.played) Color(0xFF10B981) else Color(0xFF4B5563),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (m.played) "RESULT" else "LOBBY MATCH ${m.id}",
                                            color = AlwaysWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                    if (isYourMatch) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PLAYABLE / आपका मैच",
                                            color = ChampionGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 8.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                                
                                Text(
                                    text = if (m.played) "SUCCESSFUL" else "UPCOMING",
                                    color = if (m.played) Color(0xFF10B981) else SlateMuted,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Teams match scores Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(0.42f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val colA = allTeams.find { it.name == m.teamAName }?.primaryColor ?: Color.Gray
                                    Box(modifier = Modifier.size(12.dp).background(colA, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = m.teamAName,
                                        color = PlainWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                                
                                val scoreText = if (m.played) "${m.scoreA} - ${m.scoreB}" else "VS"
                                Box(
                                    modifier = Modifier
                                        .weight(0.16f)
                                        .background(Color(0xFF0D1527), RoundedCornerShape(6.dp))
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = scoreText,
                                        color = ChampionGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.weight(0.42f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = m.teamBName,
                                        color = PlainWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val colB = allTeams.find { it.name == m.teamBName }?.primaryColor ?: Color.Gray
                                    Box(modifier = Modifier.size(12.dp).background(colB, CircleShape))
                                }
                            }
                            
                            // Match CTA actions
                            if (!m.played) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (isYourMatch) {
                                        Button(
                                            onClick = { onPlayClicked(m) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClayOrange),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("match_play_btn_${m.id}")
                                        ) {
                                            Text(text = "PLAY MATCH / खेलें", fontWeight = FontWeight.Black, fontSize = 10.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                // Run live simulated engine
                                                val starA = allTeams.find { it.name == m.teamAName }?.championStars ?: 0
                                                val starB = allTeams.find { it.name == m.teamBName }?.championStars ?: 0
                                                val weightA = starA * 2
                                                val weightB = starB * 2
                                                
                                                val scoreA = (22..44).random() + weightA
                                                var scoreB = (22..44).random() + weightB
                                                if (scoreA == scoreB) {
                                                    scoreB += if ((0..1).random() == 0) 1 else -1
                                                }
                                                
                                                val simulatedCompleted = m.copy(scoreA = scoreA, scoreB = scoreB, played = true)
                                                
                                                // Update local DB strings representation inside SharedPreferences
                                                val savedMatches = loadTournamentMatchesFromPrefs(sharedPrefs)
                                                val updatedMatches = savedMatches.map { sm ->
                                                    if (sm.id == m.id) simulatedCompleted else sm
                                                }
                                                sharedPrefs.edit()
                                                    .putString("tournament_matches_list", updatedMatches.joinToString("|") { it.serialize() })
                                                    .apply()
                                                    
                                                // Distribute and save individual player stats
                                                simulateAndSavePlayerStatsForMatch(sharedPrefs, tournType, m.teamAName, scoreA)
                                                simulateAndSavePlayerStatsForMatch(sharedPrefs, tournType, m.teamBName, scoreB)
                                                
                                                checkAndAdvanceTournamentStage(sharedPrefs)
                                                
                                                onSimulateMatchFinished(simulatedCompleted)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131A2D)),
                                            border = BorderStroke(1.dp, Color(0xFF2E395E)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                                .testTag("match_auto_btn_${m.id}")
                                        ) {
                                            Text(text = "AUTO-PLAY / सिमुलेट", color = SlateMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Playoff stage rendering
@Composable
fun PlayoffBracketSection(
    tournType: String,
    yourTeamName: String,
    stage: TournamentStage,
    semi1: TournamentMatch?,
    semi2: TournamentMatch?,
    finalMatch: TournamentMatch?,
    isCompleted: Boolean,
    sharedPrefs: SharedPreferences,
    onSimulateMatchFinished: (TournamentMatch) -> Unit,
    onPlayClicked: (TournamentMatch) -> Unit,
    allTeams: List<PklTeam>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // CHAMPION TROPHY CELEB CARD IF FINISHED!
        if (stage == TournamentStage.FINISHED && finalMatch != null && finalMatch.played) {
            val champTeamName = if (finalMatch.scoreA >= finalMatch.scoreB) finalMatch.teamAName else finalMatch.teamBName
            val isUserChamp = champTeamName == yourTeamName
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("tourn_winner_trophy_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F38)),
                border = BorderStroke(2.dp, ChampionGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = ChampionGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isUserChamp) "CONGRATULATIONS CHAMPIONS!" else "TOURNAMENT COMPLETED",
                        color = ChampionGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "$champTeamName WINS THE CHAMPIONSHIP CROWN!",
                        color = PlainWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "नया चैंपियन: ${champTeamName.loc()}! आपने पूरी मेहनत से टूर्नामेंट सीजन रिकॉर्ड पूरा किया।",
                        color = SlateMuted,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
        
        // STAGE LABLE
        Text(
            text = if (stage == TournamentStage.SEMIFINALS) "PLAYOFFS: SEMI-FINALS / सेमीफाइनल जंग" else "GRAND FINALE / भव्य फाइनल",
            color = PlainWhite,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        // SEMIFINALS SECTION
        if (semi1 != null && semi2 != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "SEMI FINAL 1", color = SlateMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                PlayoffMatchCard(
                    match = semi1,
                    yourTeamName = yourTeamName,
                    tournType = tournType,
                    sharedPrefs = sharedPrefs,
                    onSimulateMatchFinished = onSimulateMatchFinished,
                    onPlayClicked = onPlayClicked,
                    allTeams = allTeams,
                    keyStr = "tournament_semi1"
                )
                
                Text(
                    text = "SEMI FINAL 2",
                    color = SlateMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
                PlayoffMatchCard(
                    match = semi2,
                    yourTeamName = yourTeamName,
                    tournType = tournType,
                    sharedPrefs = sharedPrefs,
                    onSimulateMatchFinished = onSimulateMatchFinished,
                    onPlayClicked = onPlayClicked,
                    allTeams = allTeams,
                    keyStr = "tournament_semi2"
                )
            }
        }
        
        // GRAND FINALE SECTION
        if (finalMatch != null) {
            HorizontalDivider(color = SlateMuted.copy(0.2f), modifier = Modifier.padding(vertical = 12.dp))
            Text(text = "GRAND FINIALE / भव्य फाइनल ममुकाबला", color = ChampionGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
            PlayoffMatchCard(
                match = finalMatch,
                yourTeamName = yourTeamName,
                tournType = tournType,
                sharedPrefs = sharedPrefs,
                onSimulateMatchFinished = onSimulateMatchFinished,
                onPlayClicked = onPlayClicked,
                allTeams = allTeams,
                keyStr = "tournament_final"
            )
        }
    }
}

@Composable
fun PlayoffMatchCard(
    match: TournamentMatch,
    yourTeamName: String,
    tournType: String,
    sharedPrefs: SharedPreferences,
    onSimulateMatchFinished: (TournamentMatch) -> Unit,
    onPlayClicked: (TournamentMatch) -> Unit,
    allTeams: List<PklTeam>,
    keyStr: String
) {
    val isYourMatch = match.teamAName == yourTeamName || match.teamBName == yourTeamName
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2640)),
        border = BorderStroke(
            width = if (isYourMatch) 1.5.dp else 0.5.dp,
            color = if (isYourMatch) ChampionGold else Color(0xFF2E395E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (match.played) Color(0xFF10B981) else Color(0xFFFF9800), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (match.played) "RESULT / परिणाम" else "LIVE FIGHT / लाइव मुकाबला",
                        color = if (match.played) Color(0xFF10B981) else Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(0.42f), verticalAlignment = Alignment.CenterVertically) {
                    val colA = allTeams.find { it.name == match.teamAName }?.primaryColor ?: Color.Gray
                    Box(modifier = Modifier.size(12.dp).background(colA, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = match.teamAName, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                }
                
                val scoreText = if (match.played) "${match.scoreA} - ${match.scoreB}" else "VS"
                Box(
                    modifier = Modifier
                        .weight(0.16f)
                        .background(Color(0xFF0D1527), RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = scoreText, color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
                
                Row(modifier = Modifier.weight(0.42f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                    Text(text = match.teamBName, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                    Spacer(modifier = Modifier.width(8.dp))
                    val colB = allTeams.find { it.name == match.teamBName }?.primaryColor ?: Color.Gray
                    Box(modifier = Modifier.size(12.dp).background(colB, CircleShape))
                }
            }
            
            if (!match.played) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isYourMatch) {
                        Button(
                            onClick = { onPlayClicked(match) },
                            colors = ButtonDefaults.buttonColors(containerColor = ClayOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("play_knockout_btn")
                        ) {
                            Text(text = "PLAY / मुकाबला खेलें", fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                val scoreA = (18..38).random()
                                var scoreB = (18..38).random()
                                if (scoreA == scoreB) {
                                    scoreB += if ((0..1).random() == 0) 1 else -1
                                }
                                val simCompleted = match.copy(scoreA = scoreA, scoreB = scoreB, played = true)
                                sharedPrefs.edit()
                                    .putString(keyStr, simCompleted.serialize())
                                    .apply()
                                
                                simulateAndSavePlayerStatsForMatch(sharedPrefs, tournType, match.teamAName, scoreA)
                                simulateAndSavePlayerStatsForMatch(sharedPrefs, tournType, match.teamBName, scoreB)
                                
                                checkAndAdvanceTournamentStage(sharedPrefs)
                                onSimulateMatchFinished(simCompleted)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131A2D)),
                            border = BorderStroke(1.dp, Color(0xFF2E395E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("auto_knockout_btn")
                        ) {
                            Text(text = "AUTO-PLAY / सिमुलेट", color = SlateMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB COMPOSABLE 2: STANDINGS BOARD (WITH POINTS MATH LOGIC)
// -------------------------------------------------------------
@Composable
fun StandingsTabContent(
    standingsRows: List<StandingRow>,
    yourTeamName: String,
    allTeams: List<PklTeam>
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .testTag("standings_table_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF10162A)),
        border = BorderStroke(1.dp, Color(0xFF1E283F)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            // Legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Top 4 qualify for Semifinals / सेमीफ़ाइनल क्वालिफ़िकेशन",
                    color = SlateMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2640), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "#", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(20.dp))
                Text(text = "TEAM", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.weight(1f))
                Text(text = "PL", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                Text(text = "W", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                Text(text = "L", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                Text(text = "T", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                Text(text = "PD", color = PlainWhite, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                Text(text = "PTS", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(standingsRows.size) { idx ->
                    val row = standingsRows[idx]
                    val isYourTeam = row.teamName == yourTeamName
                    val qualifyQual = idx < 4
                    val colorIndicator = if (qualifyQual) Color(0xFF10B981) else Color.Transparent
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isYourTeam) yourTeamObjRowBackground(allTeams, yourTeamName) else Color.Transparent
                            )
                            .border(
                                width = if (isYourTeam) 1.5.dp else 0.dp,
                                color = if (isYourTeam) ChampionGold else Color.Transparent,
                                shape = RoundedCornerShape(0.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left rank qualify indicator
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .border(width = 3.dp, color = colorIndicator, shape = RoundedCornerShape(1.5.dp))
                        ) {
                            Text(text = "${idx + 1}", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            val col = allTeams.find { it.name == row.teamName }?.primaryColor ?: Color.Gray
                            Box(modifier = Modifier.size(8.dp).background(col, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isYourTeam) "${row.teamName.uppercase()} (YOU)" else row.teamName,
                                color = if (isYourTeam) ChampionGold else PlainWhite,
                                fontWeight = if (isYourTeam) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                        
                        Text(text = "${row.played}", color = PlainWhite, fontSize = 11.sp, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.win}", color = PlainWhite, fontSize = 11.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.loss}", color = PlainWhite, fontSize = 11.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.tie}", color = PlainWhite, fontSize = 11.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        
                        val diffSign = if (row.pointDiff > 0) "+${row.pointDiff}" else "${row.pointDiff}"
                        Text(
                            text = diffSign,
                            color = if (row.pointDiff >= 0) Color(0xFF10B981) else Color(0xFFFF5252),
                            fontSize = 11.sp,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "${row.points}", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                    }
                    HorizontalDivider(color = SlateMuted.copy(0.1f))
                }
            }
        }
    }
}

fun yourTeamObjRowBackground(allTeams: List<PklTeam>, yourTeamName: String): Color {
    val t = allTeams.find { it.name == yourTeamName }
    return t?.primaryColor?.copy(0.16f) ?: Color(0xFF1E2640)
}


// -------------------------------------------------------------
// TAB COMPOSABLE 3: PLAYER LEADERBOARDS & STATS TABS
// -------------------------------------------------------------
@Composable
fun PlayerStatsTabContent(
    tournType: String,
    sharedPrefs: SharedPreferences,
    allTeams: List<PklTeam>,
    onPlayerInspect: (KabaddiPlayer) -> Unit
) {
    var statsCategorySelection by remember { mutableStateOf(0) } // 0 = RAIDERS, 1 = DEFENDERS, 2 = ALL-ROUNDERS

    // Memoize full rosters
    val allTournPlayers = remember(tournType) {
        allTeams.flatMap { getPlayersForTeam(it.name) }
    }

    val filteredPlayers = remember(statsCategorySelection, allTournPlayers) {
        when (statsCategorySelection) {
            0 -> allTournPlayers.filter { it.role == PlayerRole.RAIDER || it.role == PlayerRole.ALL_ROUNDER }
            1 -> allTournPlayers.filter { it.role == PlayerRole.DEFENDER || it.role == PlayerRole.ALL_ROUNDER }
            else -> allTournPlayers
        }
    }

    // Read and Sort Descending
    val sortedItems = remember(filteredPlayers, statsCategorySelection, tournType) {
        filteredPlayers.map { p ->
            val keyPrefix = "t_stats_${tournType}_player_${p.name}"
            val raidPts = sharedPrefs.getInt("${keyPrefix}_raid_pts", 0)
            val tacklePts = sharedPrefs.getInt("${keyPrefix}_tackle_pts", 0)
            val overallPoints = sharedPrefs.getInt("${keyPrefix}_total_pts", 0)
            val matches = sharedPrefs.getInt("${keyPrefix}_matches", 0)
            
            // Package into temporary storage mapping
            TournPlayerStatRow(p, raidPts, tacklePts, overallPoints, matches)
        }.sortedByDescending { item ->
            when (statsCategorySelection) {
                0 -> item.raidPts
                1 -> item.tacklePts
                else -> item.overallPoints
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // SEGMENTED STAT CATEGORY STRIP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val cats = listOf("TOP RAIDERS", "TOP DEFENDERS", "ALL-ROUNDERS")
            val catsHi = listOf("रेडर्स", "डिफेंडर्स", "ऑल-राउंडर्स")
            cats.forEachIndexed { idx, catLabel ->
                val isS = statsCategorySelection == idx
                val dLab = if (com.example.ui.theme.currentLanguageState.value == "HINDI") catsHi[idx] else catLabel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isS) ClayOrange else Color(0xFF1E2640))
                        .clickable { statsCategorySelection = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dLab,
                        color = if (isS) AlwaysWhite else SlateMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF10162A)),
            border = BorderStroke(1.dp, Color(0xFF1E283F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E2640), RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "RANK", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.width(36.dp))
                    Text(text = "PLAYER NAME", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.weight(1f))
                    Text(text = "TEAM", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.width(64.dp))
                    Text(
                        text = when (statsCategorySelection) {
                            0 -> "RAID PTS"
                            1 -> "TACKLE PTS"
                            else -> "TOTAL PTS"
                        },
                        color = ChampionGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.End
                    )
                }
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(sortedItems.size) { idx ->
                        val item = sortedItems[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlayerInspect(item.player) }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "#${idx + 1}", color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(36.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.player.name, color = PlainWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = item.player.role.name, color = SlateMuted, fontSize = 8.sp)
                            }
                            
                            val team = allTeams.find { t -> getPlayersForTeam(t.name).any { it.name == item.player.name } }
                            Text(
                                text = team?.name?.uppercase()?.split(" ")?.firstOrNull() ?: "TEAM",
                                color = team?.primaryColor ?: SlateMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.width(64.dp),
                                maxLines = 1
                            )
                            
                            val valueText = when (statsCategorySelection) {
                                0 -> "${item.raidPts}"
                                1 -> "${item.tacklePts}"
                                else -> "${item.overallPoints}"
                            }
                            Text(
                                text = valueText,
                                color = ChampionGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier.width(60.dp),
                                textAlign = TextAlign.End
                            )
                        }
                        HorizontalDivider(color = SlateMuted.copy(0.1f))
                    }
                }
            }
        }
    }
}

data class TournPlayerStatRow(
    val player: KabaddiPlayer,
    val raidPts: Int,
    val tacklePts: Int,
    val overallPoints: Int,
    val matchesPlayed: Int
)

