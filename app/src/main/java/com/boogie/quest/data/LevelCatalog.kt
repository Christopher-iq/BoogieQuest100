package com.boogie.quest.data

import com.boogie.quest.model.*
import com.boogie.quest.model.PuzzleSpec.*

object LevelCatalog {
    private val chapterNames = listOf(
        Triple("First Spark","Warm-up logic & observation","✦"),
        Triple("Pattern Garden","Sequences & memory","❀"),
        Triple("Memory Moon","Recall & ordering","☾"),
        Triple("Cipher Alley","Codes & number logic","◇"),
        Triple("Logic Lab","Deduction & tricky rules","⌘"),
        Triple("Neon Circuit","Lights, paths & tiles","⚡"),
        Triple("Clock Temple","Time & ordered thinking","◷"),
        Triple("Mirror Maze","Reverse logic & recall","∞"),
        Triple("Queen's Trial","Hard mixed challenges","♛"),
        Triple("Heart Chamber","Final expert run","♡")
    )

    val all: List<Level> = (1..100).map { id ->
        val chapter = (id - 1) / 10 + 1
        val difficulty = ((id - 1) / 20 + 1).coerceAtMost(5)
        val n = id + chapter
        val puzzle: PuzzleSpec = when (id % 8) {
            1 -> Choice("Which number completes the pattern: $n, ${n+2}, ${n+4}, ?", listOf("${n+5}","${n+6}","${n+7}","${n+8}"), 1, "The gap stays +2.")
            2 -> Input("A code is made by doubling $n and adding 3. What is the code?", (n*2+3).toString(), "Use 2 × $n + 3.")
            3 -> Memory("Match all hidden pairs.", if (difficulty < 3) listOf("♡","✦","☾","◇") else listOf("♡","✦","☾","◇","⚡","♛"), "Use corners and rows as landmarks.")
            4 -> TapOrder("Tap the values from smallest to largest.", listOf(n+13,n-1,n+7,n+2,n+18,n+5), true, "Locate the minimum first.")
            5 -> Simon("Watch the glow and repeat it.", List(4+difficulty) { i -> (id+i*3)%4 }, 4, "Chunk the sequence into pairs.")
            6 -> Lights("Turn every light OFF. A tap flips neighbours too.", if (difficulty >= 4) 4 else 3, if (difficulty >= 4) listOf(0,3,5,6,10,15) else listOf(0,4,8), "Solve row by row instead of tapping randomly.")
            7 -> Slide("Restore the numbered tiles in order.", 3, 8+difficulty*4, 1000+id, "Solve the top row first and protect it.")
            else -> GridPath("Tap the numbers in order without a mistake.", if (difficulty >= 4) 4 else 3, if (difficulty >= 4) 12 else 8, 2000+id, "Scan for the next two numbers before tapping.")
        }
        Level(id, chapter, titleFor(id), difficulty, puzzle)
    }

    val chapters: List<Chapter> = chapterNames.mapIndexed { index, meta ->
        val id = index + 1
        Chapter(id, meta.first, meta.second, meta.third, all.filter { it.chapter == id })
    }

    fun level(id: Int): Level = all[(id.coerceIn(1,100))-1]

    private fun titleFor(id: Int): String {
        val names = listOf("Spark","Odd Door","Number Lock","Tiny Order","Memory Bloom","Clock Kiss","Pattern Pulse","Pathfinder","Color Echo","Shuffle Boss")
        val chapter = (id - 1) / 10 + 1
        val pos = (id - 1) % 10
        return if (id == 100) "Final Heart" else "${names[pos]} ${if(chapter == 1) "" else "• $chapter"}".trim()
    }
}
