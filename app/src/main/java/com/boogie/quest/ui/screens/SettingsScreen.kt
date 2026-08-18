package com.boogie.quest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boogie.quest.GameUiState
import com.boogie.quest.GameViewModel
import com.boogie.quest.ui.components.*
import com.boogie.quest.ui.theme.*

@Composable fun SettingsScreen(state:GameUiState,vm:GameViewModel){Column(Modifier.fillMaxSize().statusBarsPadding()){TopBar("Settings",state.progress.totalStars,onBack=vm::openHome);Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){GlassCard(Modifier.fillMaxWidth()){SettingRow("Sound feedback",state.progress.soundEnabled,vm::toggleSound);HorizontalDivider(color=Ink.copy(alpha=.07f),modifier=Modifier.padding(vertical=10.dp));SettingRow("Haptics",state.progress.hapticsEnabled,vm::toggleHaptics)};GlassCard(Modifier.fillMaxWidth()){Text("PROGRESS",color=Muted,fontSize=10.sp,letterSpacing=1.6.sp,fontWeight=FontWeight.Black);Spacer(Modifier.height(8.dp));Text("${state.progress.completed} / 100 levels",color=Ink,fontSize=24.sp,fontWeight=FontWeight.Black);Text("${state.progress.totalStars} / 300 stars",color=Gold,fontWeight=FontWeight.Bold);Spacer(Modifier.height(16.dp));OutlinedButton(onClick=vm::resetProgress,modifier=Modifier.fillMaxWidth()){Text("Reset adventure",color=Blush)}}}}}
@Composable private fun SettingRow(title:String,checked:Boolean,onToggle:()->Unit){Row{Text(title,color=Ink,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f));Switch(checked=checked,onCheckedChange={onToggle()})}}
