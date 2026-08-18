package com.boogie.quest.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Night = Color(0xFF0B0711)
val Night2 = Color(0xFF15101F)
val Panel = Color(0xFF21182B)
val Panel2 = Color(0xFF2B2037)
val Rose = Color(0xFFFF83B8)
val Blush = Color(0xFFFFB4D3)
val Violet = Color(0xFF9A82FF)
val Gold = Color(0xFFFFD483)
val Mint = Color(0xFF88F0C0)
val Danger = Color(0xFFFF8993)
val Ink = Color(0xFFFFF7FC)
val Muted = Color(0xFFCDBDCE)

private val scheme = darkColorScheme(
    primary = Rose,
    onPrimary = Color(0xFF2C1020),
    secondary = Violet,
    tertiary = Gold,
    background = Night,
    surface = Night2,
    surfaceVariant = Panel,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Muted,
    error = Danger
)

@Composable
fun BoogieTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = Typography(), content = content)
}
