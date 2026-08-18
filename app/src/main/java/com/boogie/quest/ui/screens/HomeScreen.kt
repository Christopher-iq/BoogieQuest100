package com.boogie.quest.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.GameViewModel
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable
fun HomeScreen(state: GameUiState, vm: GameViewModel) {
    val infinite = rememberInfiniteTransition(label = "heart")
    val bob by infinite.animateFloat(0f, -9f, infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse), label = "bob")

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        TopBar("For my favorite girl ♡", state.progress.totalStars, action = vm::openSettings)
        Column(
            Modifier.fillMaxSize().padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier.size(132.dp).graphicsLayer(translationY = bob)
                    .clip(RoundedCornerShape(38.dp))
                    .background(Brush.linearGradient(listOf(Rose, Violet))),
                contentAlignment = Alignment.Center
            ) { Text("♡", color = Ink, fontSize = 62.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.height(30.dp))
            Text("A 100-level world\nmade for Boogie", color = Ink, fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text("Logic • memory • patterns • circuits • sliding boards • secrets", color = Muted, textAlign = TextAlign.Center, lineHeight = 21.sp)
            Spacer(Modifier.height(28.dp))
            GradientButton(if (state.progress.completed == 0) "START ADVENTURE  →" else "CONTINUE  •  LEVEL ${state.progress.unlockedLevel}", vm::continueGame, Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = vm::openMap) { Text("Explore all chapters", color = Blush, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(26.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("100", "LEVELS", Modifier.weight(1f))
                StatCard("${state.progress.totalStars}", "STARS", Modifier.weight(1f))
                StatCard("${state.progress.completed}%", "CLEARED", Modifier.weight(1f))
            }
        }
    }
}

@Composable private fun StatCard(value: String, label: String, modifier: Modifier) {
    GlassCard(modifier) {
        Text(value, color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Black)
        Text(label, color = Muted, fontSize = 9.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold)
    }
}
