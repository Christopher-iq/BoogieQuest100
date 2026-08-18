package com.boogie.quest.ui

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boogie.quest.GameViewModel
import com.boogie.quest.model.AppScreen
import com.boogie.quest.ui.screens.*
import com.boogie.quest.ui.theme.*

@Composable
fun BoogieApp(vm: GameViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val tone = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 35) }
    DisposableEffect(Unit) { onDispose { tone.release() } }

    fun feedback(success: Boolean) {
        if (state.progress.hapticsEnabled) {
            haptic.performHapticFeedback(if (success) androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress else androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
        }
        if (state.progress.soundEnabled) tone.startTone(if (success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK, 90)
    }

    Box(Modifier.fillMaxSize().background(Night)) {
        AmbientBackground()
        AnimatedContent(
            targetState = state.screen,
            transitionSpec = {
                (fadeIn(tween(280)) + slideInHorizontally(tween(320)) { it / 10 }) togetherWith
                    (fadeOut(tween(180)) + slideOutHorizontally(tween(260)) { -it / 12 })
            }, label = "screen"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> HomeScreen(state, vm)
                AppScreen.MAP -> MapScreen(state, vm)
                AppScreen.SETTINGS -> SettingsScreen(state, vm)
                AppScreen.FINALE -> FinaleScreen(state, vm)
                AppScreen.GAME -> GameScreen(
                    state = state,
                    onAttempt = vm::registerAttempt,
                    onCorrect = { feedback(true); vm.completeLevel() },
                    onWrong = { feedback(false) },
                    onHint = vm::revealHint,
                    onBack = vm::backToMap,
                    onNext = vm::nextLevel
                )
            }
        }
    }
}

@Composable
private fun AmbientBackground() {
    val infinite = rememberInfiniteTransition(label = "ambient")
    val drift by infinite.animateFloat(
        initialValue = -24f, targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(7000, easing = EaseInOutSine), RepeatMode.Reverse), label = "drift"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(Rose.copy(alpha = .11f), radius = size.minDimension * .34f, center = Offset(size.width * .05f + drift, size.height * .22f))
        drawCircle(Violet.copy(alpha = .10f), radius = size.minDimension * .40f, center = Offset(size.width * .98f - drift, size.height * .65f))
        drawCircle(Blush.copy(alpha = .05f), radius = size.minDimension * .28f, center = Offset(size.width * .42f, size.height * .98f + drift))
    }
}
