package com.boogie.quest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.GameViewModel
import com.boogie.quest.data.LevelCatalog
import com.boogie.quest.model.Chapter
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable
fun MapScreen(state: GameUiState, vm: GameViewModel) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        TopBar("Puzzle World", state.progress.totalStars, onBack = vm::openHome)
        LazyColumn(
            Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(LevelCatalog.chapters) { chapter -> ChapterCard(chapter, state, vm) }
        }
    }
}

@Composable
private fun ChapterCard(chapter: Chapter, state: GameUiState, vm: GameViewModel) {
    val unlocked = chapter.levels.first().id <= state.progress.unlockedLevel
    GlassCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = if (unlocked) Rose.copy(alpha = .13f) else Color.White.copy(alpha=.04f)) {
                Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) { Text(chapter.glyph, fontSize = 24.sp, color = if (unlocked) Blush else Muted) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("CHAPTER ${chapter.id}", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Black)
                Text(chapter.title, color = if (unlocked) Ink else Muted, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Text(chapter.subtitle, color = Muted, fontSize = 11.sp)
            }
            Text(if (unlocked) "" else "⌘", color = Muted)
        }
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in chapter.levels.chunked(5)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { level ->
                        val canPlay = level.id <= state.progress.unlockedLevel
                        val stars = state.progress.starsByLevel[level.id] ?: 0
                        Surface(
                            modifier = Modifier.weight(1f).aspectRatio(1f).clickable(enabled = canPlay) { vm.startLevel(level.id) },
                            shape = CircleShape,
                            color = when { !canPlay -> Color.White.copy(alpha=.035f); stars > 0 -> Violet.copy(alpha=.23f); level.id == state.progress.unlockedLevel -> Rose.copy(alpha=.22f); else -> Panel2 },
                            border = BorderStroke(1.dp, if (canPlay) Color.White.copy(alpha=.10f) else Color.White.copy(alpha=.03f))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(if (canPlay) level.id.toString() else "•", color = if (canPlay) Ink else Muted, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                if (stars > 0) Text("★".repeat(stars), color = Gold, fontSize = 8.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
