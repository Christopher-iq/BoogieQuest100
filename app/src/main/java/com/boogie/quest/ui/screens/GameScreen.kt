package com.boogie.quest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.model.PuzzleSpec
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable
fun GameScreen(state: GameUiState,onAttempt:()->Unit,onCorrect:()->Unit,onWrong:()->Unit,onHint:()->Unit,onBack:()->Unit,onNext:()->Unit){
    val level=state.currentLevel
    val hint=when(val p=level.puzzle){is PuzzleSpec.Choice->p.hint;is PuzzleSpec.Input->p.hint;is PuzzleSpec.Memory->p.hint;is PuzzleSpec.TapOrder->p.hint;is PuzzleSpec.Simon->p.hint;is PuzzleSpec.Lights->p.hint;is PuzzleSpec.Slide->p.hint;is PuzzleSpec.GridPath->p.hint}
    Column(Modifier.fillMaxSize().statusBarsPadding()){
        TopBar("Level ${level.id} • ${level.title}",state.progress.totalStars,onBack=onBack)
        Column(Modifier.fillMaxSize().padding(18.dp)){
            GlassCard(Modifier.fillMaxWidth().weight(1f)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Surface(color=Rose.copy(alpha=.12f),shape=RoundedCornerShape(14.dp)){Text("${level.chapter}.${((level.id-1)%10)+1}",Modifier.padding(horizontal=10.dp,vertical=7.dp),color=Blush,fontSize=11.sp,fontWeight=FontWeight.Black)}
                    Spacer(Modifier.width(10.dp));Column{Text("DIFFICULTY",color=Muted,fontSize=9.sp,letterSpacing=1.5.sp,fontWeight=FontWeight.Black);DifficultyDots(level.difficulty)}
                }
                Spacer(Modifier.height(18.dp))
                Text(prompt(level.puzzle),color=Ink,fontSize=21.sp,lineHeight=28.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth().weight(1f),contentAlignment=Alignment.Center){PuzzleHost(level.puzzle,onAttempt,onCorrect,onWrong)}
                AnimatedVisibility(state.hintUsed){Column{Spacer(Modifier.height(10.dp));HintCard(hint)}}
                if(!state.hintUsed) TextButton(onClick=onHint,modifier=Modifier.align(Alignment.CenterHorizontally)){Text("♡ NEED A HINT?",color=Blush,fontWeight=FontWeight.Black)}
            }
        }
    }
    if(state.showLevelComplete) LevelCompleteOverlay(state.lastEarnedStars,level.id,onNext)
}

private fun prompt(p:PuzzleSpec)=when(p){is PuzzleSpec.Choice->p.prompt;is PuzzleSpec.Input->p.prompt;is PuzzleSpec.Memory->p.prompt;is PuzzleSpec.TapOrder->p.prompt;is PuzzleSpec.Simon->p.prompt;is PuzzleSpec.Lights->p.prompt;is PuzzleSpec.Slide->p.prompt;is PuzzleSpec.GridPath->p.prompt}

@Composable private fun LevelCompleteOverlay(stars:Int,levelId:Int,onNext:()->Unit){
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha=.68f)),contentAlignment=Alignment.Center){
        Surface(Modifier.padding(26.dp).fillMaxWidth(),color=Panel,shape=RoundedCornerShape(32.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.10f)),shadowElevation=24.dp){
            Column(Modifier.padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally){
                Text("✓",color=Mint,fontSize=42.sp,fontWeight=FontWeight.Black);Spacer(Modifier.height(10.dp));Text(if(levelId==100)"SECRET DOOR OPEN"else"LEVEL CLEAR",color=Muted,fontSize=10.sp,letterSpacing=2.sp,fontWeight=FontWeight.Black);Text(if(levelId==100)"You made it, Boogie ♡"else"Beautiful solve",color=Ink,fontSize=28.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center);Spacer(Modifier.height(10.dp));StarRow(stars,3,28);Spacer(Modifier.height(22.dp));GradientButton(if(levelId==100)"OPEN THE FINALE →"else"NEXT LEVEL →",onNext,Modifier.fillMaxWidth())
            }
        }
    }
}
