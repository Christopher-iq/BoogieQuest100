package com.boogie.quest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.ui.theme.*

@Composable
fun TopBar(title: String, stars: Int, onBack: (() -> Unit)? = null, action: (() -> Unit)? = null, actionGlyph: String = "⚙") {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)).background(Panel.copy(alpha = .78f)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            MiniButton("←", onBack)
            Spacer(Modifier.width(10.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("BOOGIE QUEST", fontSize = 10.sp, letterSpacing = 2.sp, color = Muted, fontWeight = FontWeight.Black)
            Text(title, fontSize = 20.sp, color = Ink, fontWeight = FontWeight.Bold)
        }
        Surface(color = Panel2, shape = RoundedCornerShape(18.dp)) {
            Text("★ $stars", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Gold, fontWeight = FontWeight.Black)
        }
        if (action != null) {
            Spacer(Modifier.width(8.dp))
            MiniButton(actionGlyph, action)
        }
    }
}

@Composable
fun MiniButton(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(42.dp).clickable(onClick = onClick),
        color = Panel2,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = .08f))
    ) { Box(contentAlignment = Alignment.Center) { Text(label, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold) } }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val alpha = if (enabled) 1f else .45f
    Box(
        modifier.clip(RoundedCornerShape(18.dp))
            .graphicsLayer(alpha = alpha)
            .background(Brush.linearGradient(listOf(Blush, Violet)))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = Color(0xFF291326), fontWeight = FontWeight.Black, letterSpacing = .4.sp) }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier,
        color = Panel.copy(alpha = .78f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = .08f)),
        shadowElevation = 10.dp
    ) { Column(Modifier.padding(20.dp), content = content) }
}

@Composable
fun SectionLabel(title: String, subtitle: String? = null) {
    Column {
        Text(title, color = Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
        if (subtitle != null) Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun StarRow(stars: Int, max: Int = 3, size: Int = 14) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(max) { Text(if (it < stars) "★" else "☆", color = Gold, fontSize = size.sp) }
    }
}

@Composable
fun DifficultyDots(value: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { idx ->
            Box(Modifier.size(5.dp).clip(RoundedCornerShape(50)).background(if (idx < value) Rose else Color.White.copy(alpha = .10f)))
        }
    }
}

@Composable
fun HintCard(hint: String) {
    Surface(color = Rose.copy(alpha = .10f), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Rose.copy(alpha = .24f))) {
        Text("♡  $hint", Modifier.fillMaxWidth().padding(14.dp), color = Blush, fontSize = 13.sp, lineHeight = 19.sp)
    }
}

@Composable
fun CenterMessage(title: String, body: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Ink, fontWeight = FontWeight.Black, fontSize = 28.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(body, color = Muted, textAlign = TextAlign.Center, lineHeight = 21.sp)
    }
}
