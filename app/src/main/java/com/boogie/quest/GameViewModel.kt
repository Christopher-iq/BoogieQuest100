package com.boogie.quest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.boogie.quest.data.LevelCatalog
import com.boogie.quest.data.ProgressStore
import com.boogie.quest.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GameUiState(
    val screen: AppScreen = AppScreen.HOME,
    val progress: Progress = Progress(),
    val currentLevelId: Int = 1,
    val attempts: Int = 0,
    val hintUsed: Boolean = false,
    val lastEarnedStars: Int = 0,
    val showLevelComplete: Boolean = false
) {
    val currentLevel: Level get() = LevelCatalog.level(currentLevelId)
}

class GameViewModel(app: Application) : AndroidViewModel(app) {
    private val store = ProgressStore(app)
    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            store.progress.collect { p -> _state.update { it.copy(progress = p) } }
        }
    }

    fun openMap() = _state.update { it.copy(screen = AppScreen.MAP) }
    fun openHome() = _state.update { it.copy(screen = AppScreen.HOME) }
    fun openSettings() = _state.update { it.copy(screen = AppScreen.SETTINGS) }

    fun continueGame() {
        val id = _state.value.progress.unlockedLevel.coerceIn(1, 100)
        startLevel(id)
    }

    fun startLevel(id: Int) {
        if (id > _state.value.progress.unlockedLevel) return
        _state.update { it.copy(screen = AppScreen.GAME, currentLevelId = id, attempts = 0, hintUsed = false, showLevelComplete = false) }
    }

    fun registerAttempt() = _state.update { it.copy(attempts = it.attempts + 1) }

    fun revealHint() {
        if (_state.value.hintUsed) return
        _state.update { it.copy(hintUsed = true) }
        viewModelScope.launch { store.useHint() }
    }

    fun completeLevel() {
        val s = _state.value
        val stars = when {
            s.hintUsed -> 2
            s.attempts <= 1 -> 3
            s.attempts <= 3 -> 2
            else -> 1
        }
        viewModelScope.launch { store.complete(s.currentLevelId, stars) }
        _state.update { it.copy(lastEarnedStars = stars, showLevelComplete = true) }
    }

    fun nextLevel() {
        val id = _state.value.currentLevelId
        if (id >= 100) _state.update { it.copy(screen = AppScreen.FINALE, showLevelComplete = false) }
        else startLevel(id + 1)
    }

    fun backToMap() = _state.update { it.copy(screen = AppScreen.MAP, showLevelComplete = false) }

    fun toggleSound() {
        val value = !_state.value.progress.soundEnabled
        viewModelScope.launch { store.setSound(value) }
    }
    fun toggleHaptics() {
        val value = !_state.value.progress.hapticsEnabled
        viewModelScope.launch { store.setHaptics(value) }
    }
    fun resetProgress() {
        viewModelScope.launch { store.reset() }
        _state.value = GameUiState(screen = AppScreen.HOME)
    }
}
