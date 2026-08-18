package com.boogie.quest.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.boogie.quest.model.Progress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "boogie_progress")

class ProgressStore(private val context: Context) {
    private val unlockedKey = intPreferencesKey("unlocked")
    private val starsKey = stringPreferencesKey("stars")
    private val hintsKey = intPreferencesKey("hints")
    private val soundKey = booleanPreferencesKey("sound")
    private val hapticsKey = booleanPreferencesKey("haptics")

    val progress: Flow<Progress> = context.dataStore.data.map { prefs ->
        Progress(
            unlockedLevel = prefs[unlockedKey] ?: 1,
            starsByLevel = decodeStars(prefs[starsKey].orEmpty()),
            hintsUsed = prefs[hintsKey] ?: 0,
            soundEnabled = prefs[soundKey] ?: true,
            hapticsEnabled = prefs[hapticsKey] ?: true
        )
    }

    suspend fun complete(levelId: Int, stars: Int) {
        context.dataStore.edit { prefs ->
            val current = decodeStars(prefs[starsKey].orEmpty()).toMutableMap()
            current[levelId] = maxOf(current[levelId] ?: 0, stars.coerceIn(1, 3))
            prefs[starsKey] = encodeStars(current)
            prefs[unlockedKey] = maxOf(prefs[unlockedKey] ?: 1, (levelId + 1).coerceAtMost(100))
        }
    }

    suspend fun useHint() = context.dataStore.edit { it[hintsKey] = (it[hintsKey] ?: 0) + 1 }
    suspend fun setSound(enabled: Boolean) = context.dataStore.edit { it[soundKey] = enabled }
    suspend fun setHaptics(enabled: Boolean) = context.dataStore.edit { it[hapticsKey] = enabled }

    suspend fun reset() = context.dataStore.edit { prefs ->
        prefs.clear()
    }

    private fun encodeStars(map: Map<Int, Int>) = map.entries.sortedBy { it.key }.joinToString(",") { "${it.key}:${it.value}" }
    private fun decodeStars(raw: String): Map<Int, Int> = raw.split(',').mapNotNull { token ->
        val p = token.split(':')
        if (p.size != 2) null else p[0].toIntOrNull()?.let { id -> p[1].toIntOrNull()?.let { id to it } }
    }.toMap()
}
