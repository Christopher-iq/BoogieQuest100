package com.boogie.quest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable
fun GameScreen(state:GameUiState,onAttempt:()->Unit,onCorrect:()->Unit,onWrong:()->Unit,onHint:()->Unit,onBack:()->Unit,onNext:()->Unit){
    val level=state.currentLevel
    Column(Modifier.fillMaxSize().statusBarsPadding()){
        TopBar("Level ${level.id} • ${level.title}",state.progress.totalStars,onBack=onBack)
        Column(Modifier.fillMaxSize().padding(18.dp)){
            GlassCard(Modifier.fillMaxWidth().weight(1f)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Surface(color=Rose.copy(alpha=.14f),shape=RoundedCornerShape(14.dp)){Text("${level.chapter}.${((level.id-1)%10)+1}",Modifier.padding(horizontal=10.dp,vertical=7.dp),color=Blush,fontSize=11.sp,fontWeight=FontWeight.Black)}
                    Spacer(Modifier.width(10.dp))
                    Column{Text("IMAGE PUZZLE",color=Muted,fontSize=9.sp,letterSpacing=1.5.sp,fontWeight=FontWeight.Black);DifficultyDots(level.difficulty)}
                    Spacer(Modifier.weight(1f));Text("★ ${state.progress.starsByLevel[level.id]?:0}",color=Gold,fontWeight=FontWeight.Black)
                }
                Spacer(Modifier.height(16.dp))
                Text("Restore the picture",color=Ink,fontSize=24.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                Text("Move the broken pieces until the artwork is complete.",color=Muted,fontSize=12.sp,textAlign=TextAlign.Center,modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(18.dp))
                Box(Modifier.fillMaxWidth().weight(1f),contentAlignment=Alignment.Center){PuzzleHost(level.puzzle,onAttempt,onCorrect,onWrong)}
            }
        }
    }
    if(state.showLevelComplete) LevelCompleteOverlay(state.lastEarnedStars,level.id,onNext)
}

@Composable private fun LevelCompleteOverlay(stars:Int,levelId:Int,onNext:()->Unit){
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha=.72f)),contentAlignment=Alignment.Center){
        Surface(Modifier.padding(26.dp).fillMaxWidth(),color=Panel,shape=RoundedCornerShape(32.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.12f)),shadowElevation=24.dp){
            Column(Modifier.padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally){
                Text("✦",color=Mint,fontSize=46.sp,fontWeight=FontWeight.Black);Spacer(Modifier.height(8.dp))
                Text(if(levelId==100)"FINAL ARTWORK COMPLETE"else"PICTURE COMPLETE",color=Muted,fontSize=10.sp,letterSpacing=2.sp,fontWeight=FontWeight.Black)
                Text(if(levelId==100)"You made it, Boogie ♡"else"Beautifully restored",color=Ink,fontSize=28.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center)
                Spacer(Modifier.height(12.dp));StarRow(stars,3,28);Spacer(Modifier.height(22.dp))
                GradientButton(if(levelId==100)"OPEN THE FINALE →"else"NEXT PUZZLE →",onNext,Modifier.fillMaxWidth())
            }
        }
    }
}
