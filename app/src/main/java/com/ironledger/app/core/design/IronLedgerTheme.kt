package com.ironledger.app.core.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.ThemeMode

object IronColors {
    val Black = Color(0xFF010203)
    val Amoled = Color(0xFF000000)
    val Graphite = Color(0xFF0D1117)
    val Graphite2 = Color(0xFF161B22)
    val Titanium = Color(0xFF8B949E)
    val Silver = Color(0xFFC9D1D9)
    val Muted = Color(0xFF484F58)
    val Emerald = Color(0xFF238636)
    val EmeraldBright = Color(0xFF3FB950)
    val Blue = Color(0xFF388BFD)
    val Gold = Color(0xFFD29922)
    val TitaniumRed = Color(0xFFF85149)
    val Warning = Color(0xFFDB6D28)
    val Negative = Color(0xFFF85149)
    
    // Exact colors from reference image
    val CardBg = Color(0xFF0B1014)
    val CardBorder = Color(0xFF1C2128)
    val AccentGreen = Color(0xFF1DB954)
}

@Composable
fun IronLedgerTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED,
    accentColor: AccentColor = AccentColor.EMERALD,
    content: @Composable () -> Unit
) {
    val accent = when (accentColor) {
        AccentColor.EMERALD -> IronColors.Emerald
        AccentColor.BLUE -> IronColors.Blue
        AccentColor.GOLD -> IronColors.Gold
        AccentColor.TITANIUM_RED -> IronColors.TitaniumRed
    }
    val background = when (themeMode) {
        ThemeMode.DARK -> IronColors.Black
        ThemeMode.AMOLED -> IronColors.Amoled
        ThemeMode.GLASS -> IronColors.Graphite
    }

    MaterialTheme(
        colorScheme = ironColorScheme(background, accent),
        typography = ironTypography(),
        content = content
    )
}

@Composable
private fun ironColorScheme(background: Color, accent: Color): ColorScheme {
    isSystemInDarkTheme()
    return darkColorScheme(
        primary = accent,
        onPrimary = IronColors.Black,
        primaryContainer = accent.copy(alpha = 0.18f),
        onPrimaryContainer = IronColors.Silver,
        secondary = IronColors.Gold,
        onSecondary = IronColors.Black,
        background = background,
        onBackground = IronColors.Silver,
        surface = IronColors.Graphite,
        onSurface = IronColors.Silver,
        surfaceVariant = IronColors.Graphite2,
        onSurfaceVariant = IronColors.Titanium,
        outline = IronColors.Titanium.copy(alpha = 0.28f),
        error = IronColors.Negative,
        onError = IronColors.Black
    )
}

private fun ironTypography() = androidx.compose.material3.Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 42.sp, lineHeight = 48.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp)
)
