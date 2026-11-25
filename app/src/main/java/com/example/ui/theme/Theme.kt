package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark Theme for sports arena feel
  dynamicColor: Boolean = false, // Disable dynamic colors to ensure stability and brand styling consistency
  content: @Composable () -> Unit,
) {
  val colorScheme = darkColorScheme(
    primary = ClayOrange,
    secondary = ChampionGold,
    tertiary = RaidCyan,
    background = StadiumNavy,
    surface = MatSlate,
    onPrimary = PlainWhite,
    onSecondary = StadiumNavy,
    onBackground = PlainWhite,
    onSurface = PlainWhite
  )

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
