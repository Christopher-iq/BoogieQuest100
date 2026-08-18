package com.boogie.quest.model

sealed interface PuzzleSpec {
    data class Choice(val prompt: String, val options: List<String>, val answer: Int, val hint: String) : PuzzleSpec
    data class Input(val prompt: String, val answer: String, val hint: String, val keyboardNumeric: Boolean = true) : PuzzleSpec
    data class Memory(val prompt: String, val symbols: List<String>, val hint: String) : PuzzleSpec
    data class TapOrder(val prompt: String, val values: List<Int>, val ascending: Boolean = true, val hint: String) : PuzzleSpec
    data class Simon(val prompt: String, val sequence: List<Int>, val paletteSize: Int, val hint: String) : PuzzleSpec
    data class Lights(val prompt: String, val size: Int, val seedMoves: List<Int>, val hint: String) : PuzzleSpec
    data class Slide(val prompt: String, val size: Int, val scrambleMoves: Int, val seed: Int, val hint: String) : PuzzleSpec
    data class GridPath(val prompt: String, val size: Int, val count: Int, val seed: Int, val hint: String) : PuzzleSpec
}

data class Level(
    val id: Int,
    val chapter: Int,
    val title: String,
    val difficulty: Int,
    val puzzle: PuzzleSpec
)

data class Chapter(
    val id: Int,
    val title: String,
    val subtitle: String,
    val glyph: String,
    val levels: List<Level>
)

data class Progress(
    val unlockedLevel: Int = 1,
    val starsByLevel: Map<Int, Int> = emptyMap(),
    val hintsUsed: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
) {
    val totalStars: Int get() = starsByLevel.values.sum()
    val completed: Int get() = starsByLevel.size
}

enum class AppScreen { HOME, MAP, GAME, FINALE, SETTINGS }
