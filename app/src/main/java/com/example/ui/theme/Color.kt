package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

// Observable States
val currentThemeIndexState = mutableStateOf(0)
val currentLanguageState = mutableStateOf("ENG")
val currentDarkModeState = mutableStateOf(true) // true = Dark Mode, false = White/Light Mode

val AlwaysWhite = Color(0xFFF8FAFC)
val AlwaysDark = Color(0xFF0D1527)

// Theme specifications
data class KabaddiTheme(
    val nameEn: String,
    val nameHi: String,
    val clayOrange: Color,
    val championGold: Color,
    val stadiumNavy: Color,
    val matSlate: Color,
    val raidCyan: Color,
    val defenderBlue: Color,
    val goldGlow: Color = Color(0xFFFCD34D),
    val plainWhite: Color = Color(0xFFF8FAFC),
    val slateMuted: Color = Color(0xFF94A3B8)
)

val KABADDI_THEMES = listOf(
    // 0. Traditional Clay (Default)
    KabaddiTheme(
        nameEn = "Traditional Orange-Clay (Default)",
        nameHi = "पारंपरिक लाल मैट (डिफ़ॉल्ट)",
        clayOrange = Color(0xFFFF5C22),
        championGold = Color(0xFFFFC72C),
        stadiumNavy = Color(0xFF0D1527),
        matSlate = Color(0xFF1E2640),
        raidCyan = Color(0xFF06B6D4),
        defenderBlue = Color(0xFF2563EB)
    ),
    // 1. Championship Gold
    KabaddiTheme(
        nameEn = "Royal Golden (Championship Pro)",
        nameHi = "रॉयल गोल्डन विनर",
        clayOrange = Color(0xFFD97706),
        championGold = Color(0xFFF59E0B),
        stadiumNavy = Color(0xFF111827),
        matSlate = Color(0xFF1F2937),
        raidCyan = Color(0xFFFBBF24),
        defenderBlue = Color(0xFFB45309)
    ),
    // 2. Jungle Green
    KabaddiTheme(
        nameEn = "Jungle Warriors (Green Raid)",
        nameHi = "जंगल वारियर्स (हरा हमला)",
        clayOrange = Color(0xFF10B981),
        championGold = Color(0xFF34D399),
        stadiumNavy = Color(0xFF064E3B),
        matSlate = Color(0xFF0F5A47),
        raidCyan = Color(0xFF059669),
        defenderBlue = Color(0xFF047857)
    ),
    // 3. Frozen Blizzard
    KabaddiTheme(
        nameEn = "Ice Blizzard (Frost Defense)",
        nameHi = "बर्फानी बवंडर (नीली दीवार)",
        clayOrange = Color(0xFF38BDF8),
        championGold = Color(0xFF7DD3FC),
        stadiumNavy = Color(0xFF0F172A),
        matSlate = Color(0xFF1E293B),
        raidCyan = Color(0xFF0EA5E9),
        defenderBlue = Color(0xFF0284C7)
    ),
    // 4. Synthwave Future
    KabaddiTheme(
        nameEn = "Future Cyberpunk (Neon Aura)",
        nameHi = "साइबरपंक नियॉन मैट",
        clayOrange = Color(0xFFEC4899),
        championGold = Color(0xFFF472B6),
        stadiumNavy = Color(0xFF03001C),
        matSlate = Color(0xFF301E67),
        raidCyan = Color(0xFF5B8FB9),
        defenderBlue = Color(0xFFB6EADA)
    ),
    // 5. Crimson Dynasty
    KabaddiTheme(
        nameEn = "Crimson Empire (Vanguard Field)",
        nameHi = "क्रिमसन एम्पायर (लाल युद्ध भूमि)",
        clayOrange = Color(0xFFEF4444),
        championGold = Color(0xFFF87171),
        stadiumNavy = Color(0xFF1A0505),
        matSlate = Color(0xFF2D1010),
        raidCyan = Color(0xFFDC2626),
        defenderBlue = Color(0xFF991B1B)
    ),
    // 6. Monochrome Shadow
    KabaddiTheme(
        nameEn = "Silent Obsidian (Shadow Edition)",
        nameHi = "शांत ब्लैक (शैडो एडिशन)",
        clayOrange = Color(0xFFF1F5F9),
        championGold = Color(0xFF94A3B8),
        stadiumNavy = Color(0xFF0F172A),
        matSlate = Color(0xFF1E293B),
        raidCyan = Color(0xFFCBD5E1),
        defenderBlue = Color(0xFF475569)
    ),
    // 7. Desert Sand
    KabaddiTheme(
        nameEn = "Thar Desert Clay (Desert Sun)",
        nameHi = "थार मरुस्थल (मिट्टी का किला)",
        clayOrange = Color(0xFFCA8A04),
        championGold = Color(0xFFFACC15),
        stadiumNavy = Color(0xFF451A03),
        matSlate = Color(0xFF78350F),
        raidCyan = Color(0xFFD97706),
        defenderBlue = Color(0xFF9A3412)
    ),
    // 8. Laser Matrix
    KabaddiTheme(
        nameEn = "Electric Volt Grid (Laser Net)",
        nameHi = "इलेक्ट्रिक वोल्टेज ग्रिड",
        clayOrange = Color(0xFF84CC16),
        championGold = Color(0xFFA3E635),
        stadiumNavy = Color(0xFF02170D),
        matSlate = Color(0xFF0B2F1C),
        raidCyan = Color(0xFF22C55E),
        defenderBlue = Color(0xFF15803D)
    ),
    // 9. Royal Sapphire
    KabaddiTheme(
        nameEn = "Imperial Sapphire (Dynasty Crest)",
        nameHi = "शाही नीलम विरासत (राजवंश)",
        clayOrange = Color(0xFFFF7E00),
        championGold = Color(0xFFF59E0B),
        stadiumNavy = Color(0xFF1E1B4B),
        matSlate = Color(0xFF2E2A72),
        raidCyan = Color(0xFF6366F1),
        defenderBlue = Color(0xFF4338CA)
    )
)

// Dynamic Color Property Getters
val ClayOrange: Color get() = KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.clayOrange
val ChampionGold: Color get() = KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.championGold
val StadiumNavy: Color get() = if (currentDarkModeState.value) KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.stadiumNavy else Color(0xFFF1F5F9)
val MatSlate: Color get() = if (currentDarkModeState.value) KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.matSlate else Color(0xFFFFFFFF)
val RaidCyan: Color get() = KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.raidCyan
val DefenderBlue: Color get() = KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.defenderBlue
val GoldGlow: Color get() = KABADDI_THEMES.getOrElse(currentThemeIndexState.value) { KABADDI_THEMES[0] }.goldGlow
val PlainWhite: Color get() = if (currentDarkModeState.value) Color(0xFFF8FAFC) else Color(0xFF0F172A)
val SlateMuted: Color get() = if (currentDarkModeState.value) Color(0xFF94A3B8) else Color(0xFF536471)

