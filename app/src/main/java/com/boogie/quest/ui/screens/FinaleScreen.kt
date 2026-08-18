package com.boogie.quest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.GameViewModel
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable fun FinaleScreen(state:GameUiState,vm:GameViewModel){Column(Modifier.fillMaxSize().statusBarsPadding()){TopBar("Adventure Complete",state.progress.totalStars,onBack=vm::openMap);Column(Modifier.fillMaxSize().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Box(Modifier.size(156.dp).clip(RoundedCornerShape(48.dp)).background(Brush.linearGradient(listOf(Rose,Violet))),contentAlignment=Alignment.Center){Text("♡",fontSize=76.sp,color=Ink,fontWeight=FontWeight.Black)};Spacer(Modifier.height(28.dp));Text("PUZZLE QUEEN",color=Rose,fontSize=11.sp,letterSpacing=2.5.sp,fontWeight=FontWeight.Black);Text("Boogie cleared\nall 100 levels",color=Ink,fontSize=38.sp,lineHeight=42.sp,textAlign=TextAlign.Center,fontWeight=FontWeight.Black);Spacer(Modifier.height(12.dp));Text("This little puzzle world was made just to make you smile ♡",color=Muted,textAlign=TextAlign.Center,lineHeight=21.sp);Spacer(Modifier.height(24.dp));GlassCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Final stars",color=Muted);Text("★ ${state.progress.totalStars} / 300",color=Gold,fontWeight=FontWeight.Black)};Spacer(Modifier.height(8.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Secret answer",color=Muted);Text("Boogie ♡",color=Blush,fontWeight=FontWeight.Black)}};Spacer(Modifier.height(20.dp));GradientButton("BACK TO PUZZLE WORLD",vm::openMap,Modifier.fillMaxWidth())}}}
