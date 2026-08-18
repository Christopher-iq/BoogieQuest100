package com.boogie.quest.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.boogie.quest.model.PuzzleSpec
import com.boogie.quest.ui.theme.*
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun PuzzleHost(puzzle:PuzzleSpec,onAttempt:()->Unit,onCorrect:()->Unit,onWrong:()->Unit){
    VisualImagePuzzle(seed=puzzle.hashCode(),onAttempt=onAttempt,onCorrect=onCorrect,onWrong=onWrong)
}

private fun shuffled(size:Int,seed:Int):List<Int>{
    val a=(1 until size*size).toMutableList().apply{add(0)}
    var blank=a.lastIndex
    val rnd=Random(seed)
    var previous=-1
    repeat(90){
        val r=blank/size;val c=blank%size
        val candidates=mutableListOf<Int>()
        if(r>0)candidates+=blank-size;if(r<size-1)candidates+=blank+size;if(c>0)candidates+=blank-1;if(c<size-1)candidates+=blank+1
        val options=candidates.filter{it!=previous}.ifEmpty{candidates}
        val next=options[rnd.nextInt(options.size)]
        a[blank]=a[next];a[next]=0;previous=blank;blank=next
    }
    return a
}

@Composable
private fun VisualImagePuzzle(seed:Int,onAttempt:()->Unit,onCorrect:()->Unit,onWrong:()->Unit){
    val size=3
    val tiles=remember(seed){mutableStateListOf<Int>().also{it.addAll(shuffled(size,seed))}}
    var moves by remember(seed){mutableIntStateOf(0)}
    val solved=tiles.withIndex().all{(i,v)->if(i==tiles.lastIndex)v==0 else v==i+1}
    val pulse by rememberInfiniteTransition(label="glow").animateFloat(.86f,1f,infiniteRepeatable(tween(1200,easing=EaseInOutSine),RepeatMode.Reverse),label="pulse")

    Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(14.dp)){
        Surface(color=Color.White.copy(alpha=.045f),shape=RoundedCornerShape(26.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.10f))){
            Box(Modifier.padding(10.dp)){
                LazyVerticalGrid(
                    GridCells.Fixed(size),
                    Modifier.size(300.dp),
                    userScrollEnabled=false,
                    horizontalArrangement=Arrangement.spacedBy(5.dp),
                    verticalArrangement=Arrangement.spacedBy(5.dp)
                ){
                    items(tiles.size){idx->
                        val value=tiles[idx]
                        val blank=tiles.indexOf(0)
                        val adjacent=abs(idx/size-blank/size)+abs(idx%size-blank%size)==1
                        if(value==0){
                            Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha=.025f)))
                        }else{
                            ArtTile(value,Modifier.aspectRatio(1f).clickable(enabled=adjacent){
                                onAttempt();tiles[blank]=value;tiles[idx]=0;moves++
                                val done=tiles.withIndex().all{(i,v)->if(i==tiles.lastIndex)v==0 else v==i+1}
                                if(done)onCorrect()
                            }.graphicsLayer(scaleX=if(adjacent)pulse else .98f,scaleY=if(adjacent)pulse else .98f))
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
            Text("MOVES  $moves",color=Muted,fontSize=11.sp,fontWeight=FontWeight.Black)
            Text(if(solved)"COMPLETE ♡"else"Tap a piece beside the empty space",color=if(solved)Mint else Blush,fontSize=11.sp,fontWeight=FontWeight.Bold)
        }
        Surface(color=Panel2.copy(alpha=.70f),shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.07f))){
            Text("Each level uses a different abstract romantic artwork. Later builds can replace these with personal photos without changing the puzzle engine.",Modifier.padding(12.dp),color=Muted,fontSize=10.sp,lineHeight=14.sp,textAlign=TextAlign.Center)
        }
    }
}

@Composable
private fun ArtTile(value:Int,modifier:Modifier=Modifier){
    val palettes=listOf(
        listOf(Color(0xFFFF6FB6),Color(0xFF8D70FF)),
        listOf(Color(0xFFFFD26B),Color(0xFFFF7AAE)),
        listOf(Color(0xFF8EF0C4),Color(0xFF7A6CFF)),
        listOf(Color(0xFFFF9FD0),Color(0xFFFFC85C))
    )
    val pair=palettes[(value-1)%palettes.size]
    Box(modifier.clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(pair)),contentAlignment=Alignment.Center){
        val glyph=when(value){1->"♡";2->"✦";3->"❀";4->"◈";5->"☾";6->"✿";7->"◇";else->"♛"}
        Text(glyph,color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black)
        Text(value.toString(),Modifier.align(Alignment.BottomEnd).padding(8.dp),color=Color.White.copy(alpha=.72f),fontSize=9.sp,fontWeight=FontWeight.Black)
    }
}
