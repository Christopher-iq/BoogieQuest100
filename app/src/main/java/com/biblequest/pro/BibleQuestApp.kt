package com.biblequest.pro

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlin.math.*
import kotlin.random.Random

private val Bg=Color(0xFF091224); private val Panel=Color(0xB31B2940); private val Panel2=Color(0xFF203653)
private val Gold=Color(0xFFF5C451); private val Sky=Color(0xFF48C7FF); private val Mint=Color(0xFF6CE7B5)
private val Ink=Color(0xFFFFFAEC); private val Muted=Color(0xFFB7C6DB); private val Bad=Color(0xFFFF7284)

enum class Screen { HOME, CATEGORIES, QUIZ, RESULT, REWARDS, SETTINGS }
data class Player(var xp:Int=0,var coins:Int=80,var streak:Int=1,var correct:Int=0,var answered:Int=0)

@Composable
fun BibleQuestApp(){
 var screen by remember{ mutableStateOf(Screen.HOME) }
 var hindi by remember{ mutableStateOf(false) }
 var sound by remember{ mutableStateOf(true) }
 var music by remember{ mutableStateOf(true) }
 var player by remember{ mutableStateOf(Player()) }
 var category by remember{ mutableStateOf("All") }
 var questions by remember{ mutableStateOf(emptyList<Question>()) }
 var score by remember{ mutableIntStateOf(0) }
 var quizTitle by remember{ mutableStateOf("Quick Quest") }
 val tone=remember{ToneGenerator(AudioManager.STREAM_MUSIC,32)}
 DisposableEffect(Unit){onDispose{tone.release()}}
 fun begin(cat:String,count:Int,title:String){
   category=cat; quizTitle=title
   val base=if(cat=="All")QuestionBank.all else QuestionBank.all.filter{it.category==cat}
   questions=base.shuffled(Random(System.nanoTime())).take(min(count,base.size))
   score=0; screen=Screen.QUIZ
 }
 Box(Modifier.fillMaxSize().background(Bg)){
   Aura()
   AnimatedContent(screen,transitionSpec={
     (fadeIn(tween(260))+slideInHorizontally(tween(320)){it/10}) togetherWith
     (fadeOut(tween(180))+slideOutHorizontally(tween(250)){-it/12})
   },label="nav"){s->
     when(s){
       Screen.HOME->Home(hindi,player,{screen=Screen.CATEGORIES},{begin("All",10,if(hindi)"त्वरित क्विज़" else "Quick Quest")},{begin("All",7,if(hindi)"दैनिक चुनौती" else "Daily Challenge")},{screen=Screen.REWARDS},{screen=Screen.SETTINGS})
       Screen.CATEGORIES->Categories(hindi,{screen=Screen.HOME}){begin(it,10,it)}
       Screen.QUIZ->Quiz(hindi,quizTitle,questions,sound,tone,{delta->
          score+=delta; player=player.copy(xp=player.xp+delta*10,coins=player.coins+delta*2,correct=player.correct+delta,answered=player.answered+1)
       },{player=player.copy(answered=player.answered+1)},{screen=Screen.RESULT})
       Screen.RESULT->Result(hindi,score,questions.size,player,{screen=Screen.HOME},{begin(category,10,quizTitle)})
       Screen.REWARDS->Rewards(hindi,player,{screen=Screen.HOME})
       Screen.SETTINGS->Settings(hindi,sound,music,{hindi=!hindi},{sound=!sound},{music=!music},{screen=Screen.HOME})
     }
   }
 }
}

@Composable private fun Aura(){
 val inf=rememberInfiniteTransition(label="a"); val d by inf.animateFloat(-30f,30f,infiniteRepeatable(tween(7000,easing=EaseInOutSine),RepeatMode.Reverse),label="d")
 Canvas(Modifier.fillMaxSize()){drawCircle(Sky.copy(.08f),size.minDimension*.38f,Offset(size.width*.05f+d,size.height*.18f));drawCircle(Gold.copy(.07f),size.minDimension*.32f,Offset(size.width*.92f-d,size.height*.62f));drawCircle(Color(0xFF7E67FF).copy(.10f),size.minDimension*.28f,Offset(size.width*.35f,size.height*.98f+d))}
}
@Composable private fun Top(title:String,coins:Int,onBack:(()->Unit)?=null,onGear:(()->Unit)?=null){
 Row(Modifier.statusBarsPadding().padding(14.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Panel).padding(12.dp),verticalAlignment=Alignment.CenterVertically){
  if(onBack!=null){IconButton(onBack){Icon(Icons.Rounded.ArrowBack,null,tint=Ink)}}
  Column(Modifier.weight(1f)){Text("BIBLE QUEST PRO",fontSize=9.sp,letterSpacing=2.sp,color=Gold,fontWeight=FontWeight.Black);Text(title,color=Ink,fontWeight=FontWeight.Bold,fontSize=20.sp)}
  Surface(color=Panel2,shape=RoundedCornerShape(18.dp)){Text("✦ $coins",Modifier.padding(horizontal=12.dp,vertical=8.dp),color=Gold,fontWeight=FontWeight.Black)}
  if(onGear!=null)IconButton(onGear){Icon(Icons.Rounded.Settings,null,tint=Ink)}
 }
}
@Composable private fun Glass(mod:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){Surface(mod,color=Panel,shape=RoundedCornerShape(26.dp),border=BorderStroke(1.dp,Color.White.copy(.09f)),shadowElevation=12.dp){Column(Modifier.padding(18.dp),content=content)}}
@Composable private fun HeroButton(text:String,icon:String,onClick:()->Unit){Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Gold,Sky))).clickable(onClick=onClick).padding(16.dp),contentAlignment=Alignment.Center){Text("$icon  $text",color=Bg,fontWeight=FontWeight.Black,fontSize=16.sp)}}

@Composable private fun Home(hi:Boolean,p:Player,onExplore:()->Unit,onQuick:()->Unit,onDaily:()->Unit,onRewards:()->Unit,onSettings:()->Unit){
 Column(Modifier.fillMaxSize()){Top(if(hi)"वचन सीखें • खेलें • बढ़ें" else "Learn • Play • Grow",p.coins,onGear=onSettings)
 LazyColumn(contentPadding=PaddingValues(18.dp,8.dp,18.dp,30.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
  item{Glass(Modifier.fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(78.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Gold,Sky))),contentAlignment=Alignment.Center){Text("✝",fontSize=42.sp,color=Bg,fontWeight=FontWeight.Black)};Spacer(Modifier.width(14.dp));Column{Text(if(hi)"आज का वचन" else "VERSE OF THE DAY",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black,letterSpacing=1.5.sp);Text(if(hi)"“तेरा वचन मेरे पाँव के लिए दीपक है।”" else "“Your word is a lamp for my feet.”",color=Ink,fontSize=18.sp,fontWeight=FontWeight.Bold);Text(if(hi)"भजन 119:105" else "Psalm 119:105",color=Muted,fontSize=12.sp)}}}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Stat("${p.xp}","XP",Modifier.weight(1f));Stat("🔥 ${p.streak}",if(hi)"श्रृंखला" else "STREAK",Modifier.weight(1f));Stat("${p.correct}/${max(1,p.answered)}",if(hi)"सही" else "RIGHT",Modifier.weight(1f))}}
  item{HeroButton(if(hi)"त्वरित क्विज़" else "QUICK QUEST","⚡",onQuick)}
  item{Glass(Modifier.fillMaxWidth()){Text(if(hi)"आज की चुनौती" else "DAILY CHALLENGE",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black,letterSpacing=1.6.sp);Spacer(Modifier.height(6.dp));Text(if(hi)"7 प्रश्न • बोनस XP • दैनिक इनाम" else "7 questions • bonus XP • daily reward",color=Ink,fontWeight=FontWeight.Bold,fontSize=18.sp);Spacer(Modifier.height(12.dp));OutlinedButton(onClick=onDaily,modifier=Modifier.fillMaxWidth()){Text(if(hi)"शुरू करें" else "PLAY TODAY",color=Sky,fontWeight=FontWeight.Black)}}}
  item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){Tile("📚",if(hi)"विषय" else "TOPICS",onExplore,Modifier.weight(1f));Tile("🏆",if(hi)"इनाम" else "REWARDS",onRewards,Modifier.weight(1f))}}
  item{Glass(Modifier.fillMaxWidth()){Text(if(hi)"सीखने का तरीका" else "LEARNING LOOP",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text(if(hi)"उत्तर दें → तुरंत कारण समझें → अध्याय देखें → XP और सिक्के कमाएँ।" else "Answer → understand why → see the chapter reference → earn XP and coins.",color=Muted,lineHeight=21.sp)}}
 }}
}
@Composable private fun Stat(v:String,l:String,m:Modifier){Glass(m){Text(v,color=Ink,fontSize=20.sp,fontWeight=FontWeight.Black);Text(l,color=Muted,fontSize=9.sp,fontWeight=FontWeight.Bold)}}
@Composable private fun Tile(icon:String,label:String,on:()->Unit,m:Modifier){Surface(m.clickable(onClick=on),color=Panel2,shape=RoundedCornerShape(22.dp),border=BorderStroke(1.dp,Color.White.copy(.08f))){Column(Modifier.padding(18.dp)){Text(icon,fontSize=28.sp);Spacer(Modifier.height(8.dp));Text(label,color=Ink,fontWeight=FontWeight.Black)}}}

@Composable private fun Categories(hi:Boolean,back:()->Unit,start:(String)->Unit){Column(Modifier.fillMaxSize()){Top(if(hi)"विषय चुनें" else "Choose a topic",0,onBack=back);LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(QuestionBank.categories){cat->Surface(Modifier.fillMaxWidth().clickable{start(cat)},color=Panel,shape=RoundedCornerShape(20.dp),border=BorderStroke(1.dp,Color.White.copy(.08f))){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text(categoryIcon(cat),fontSize=25.sp);Spacer(Modifier.width(14.dp));Column(Modifier.weight(1f)){Text(cat,color=Ink,fontWeight=FontWeight.Black);Text("${if(cat=="All")QuestionBank.all.size else QuestionBank.all.count{it.category==cat}} ${if(hi)"प्रश्न" else "questions"}",color=Muted,fontSize=11.sp)};Icon(Icons.Rounded.ChevronRight,null,tint=Sky)}}}}}}
private fun categoryIcon(c:String)=when(c){"Gospels"->"✝";"Acts"->"🔥";"Kings"->"♛";"Prophets"->"📜";"Wisdom"->"💡";"Women"->"🌸";"Revelation"->"✨";else->"📖"}

@Composable private fun Quiz(hi:Boolean,title:String,questions:List<Question>,sound:Boolean,tone:ToneGenerator,onCorrect:(Int)->Unit,onWrong:()->Unit,onDone:()->Unit){
 var index by remember(questions){mutableIntStateOf(0)}; var picked by remember(questions){mutableIntStateOf(-1)}; var reveal by remember(questions){mutableStateOf(false)}
 if(questions.isEmpty()){LaunchedEffect(Unit){onDone()};return}
 val q=questions[index]; val opts=if(hi)q.optionsHi else q.optionsEn
 Column(Modifier.fillMaxSize()){Top(title,0);LinearProgressIndicator(progress={ (index+1f)/questions.size },modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp),color=Gold,trackColor=Panel2)
 Column(Modifier.padding(18.dp).fillMaxSize()){
   Glass(Modifier.fillMaxWidth().weight(1f)){
     val scrollState = rememberScrollState()
     LaunchedEffect(reveal) {
       if (reveal) scrollState.animateScrollTo(scrollState.maxValue)
       else scrollState.scrollTo(0)
     }
     Column(
       Modifier.fillMaxWidth().verticalScroll(scrollState)
     ){
       Row{
         Surface(color=Sky.copy(.12f),shape=RoundedCornerShape(12.dp)){
           Text(q.category,Modifier.padding(9.dp),color=Sky,fontSize=10.sp,fontWeight=FontWeight.Black)
         }
         Spacer(Modifier.weight(1f))
         Text("${index+1}/${questions.size}",color=Muted,fontWeight=FontWeight.Bold)
       }
       Spacer(Modifier.height(18.dp))
       Text(if(hi)q.hi else q.en,color=Ink,fontSize=24.sp,lineHeight=31.sp,fontWeight=FontWeight.Black)
       Spacer(Modifier.height(18.dp))
       opts.forEachIndexed{i,o->
         val good=reveal&&i==q.correct
         val bad=reveal&&i==picked&&i!=q.correct
         Surface(
           Modifier.fillMaxWidth().padding(vertical=5.dp).clickable(enabled=!reveal){
             picked=i;reveal=true
             if(i==q.correct){
               onCorrect(1)
               if(sound)tone.startTone(ToneGenerator.TONE_PROP_ACK,90)
             }else{
               onWrong()
               if(sound)tone.startTone(ToneGenerator.TONE_PROP_NACK,90)
             }
           },
           color=when{good->Mint.copy(.16f);bad->Bad.copy(.14f);else->Panel2},
           shape=RoundedCornerShape(18.dp),
           border=BorderStroke(1.dp,when{good->Mint;bad->Bad;else->Color.White.copy(.07f)})
         ){
           Row(Modifier.padding(15.dp)){
             Text("${'A'+i}.",color=if(good)Mint else Sky,fontWeight=FontWeight.Black)
             Spacer(Modifier.width(10.dp))
             Text(o,color=Ink,fontWeight=FontWeight.Bold)
           }
         }
       }
       AnimatedVisibility(reveal){
         Column{
           Spacer(Modifier.height(15.dp))
           Surface(
             color=Gold.copy(.09f),
             shape=RoundedCornerShape(18.dp),
             border=BorderStroke(1.dp,Gold.copy(.28f))
           ){
             Column(Modifier.padding(14.dp)){
               Text(if(hi)"क्यों?" else "WHY?",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black)
               Text(if(hi)q.explainHi else q.explainEn,color=Ink,lineHeight=20.sp)
               Spacer(Modifier.height(7.dp))
               Text("📖 ${if(hi)q.refHi else q.refEn}",color=Sky,fontWeight=FontWeight.Black,fontSize=12.sp)
             }
           }
           Spacer(Modifier.height(12.dp))
           HeroButton(
             if(index==questions.lastIndex){if(hi)"परिणाम देखें" else "SEE RESULTS"}
             else{if(hi)"अगला प्रश्न" else "NEXT QUESTION"},
             "→"
           ){
             if(index==questions.lastIndex) onDone()
             else{
               index++;picked=-1;reveal=false
             }
           }
           Spacer(Modifier.height(12.dp))
         }
       }
     }
   }
 }}}
}

@Composable private fun Result(hi:Boolean,score:Int,total:Int,p:Player,home:()->Unit,again:()->Unit){Column(Modifier.fillMaxSize()){Top(if(hi)"क्विज़ पूर्ण" else "Quest Complete",p.coins);Column(Modifier.padding(18.dp),horizontalAlignment=Alignment.CenterHorizontally){Spacer(Modifier.height(35.dp));Box(Modifier.size(130.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Gold,Sky))),contentAlignment=Alignment.Center){Text(if(score>=total*.8)"🏆" else "📖",fontSize=58.sp)};Spacer(Modifier.height(20.dp));Text("$score / $total",color=Ink,fontSize=44.sp,fontWeight=FontWeight.Black);Text(if(hi)"सही उत्तर" else "correct answers",color=Muted);Spacer(Modifier.height(18.dp));Glass(Modifier.fillMaxWidth()){Text(if(hi)"इनाम" else "REWARDS EARNED",color=Gold,fontWeight=FontWeight.Black,fontSize=10.sp);Text("+${score*10} XP    +${score*2} ✦",color=Ink,fontSize=23.sp,fontWeight=FontWeight.Black)};Spacer(Modifier.height(14.dp));HeroButton(if(hi)"फिर से खेलें" else "PLAY AGAIN","↻",again);Spacer(Modifier.height(10.dp));OutlinedButton(onClick=home,modifier=Modifier.fillMaxWidth()){Text(if(hi)"होम" else "BACK HOME",color=Sky)}}}}
@Composable private fun Rewards(hi:Boolean,p:Player,back:()->Unit){Column(Modifier.fillMaxSize()){Top(if(hi)"इनाम" else "Rewards",p.coins,onBack=back);LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Glass(Modifier.fillMaxWidth()){Text(if(hi)"आपका स्तर" else "YOUR LEVEL",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text("${p.xp/100+1}",color=Ink,fontSize=42.sp,fontWeight=FontWeight.Black);LinearProgressIndicator(progress={(p.xp%100)/100f},modifier=Modifier.fillMaxWidth(),color=Sky,trackColor=Panel2)}};items(listOf("📖 First Steps" to 1,"🔥 7-Day Faithful" to 7,"🏆 Bible Scholar" to 20,"✝ Gospel Master" to 40,"👑 Scripture Champion" to 80)){(name,need)->Glass(Modifier.fillMaxWidth()){Row{Text(name,color=Ink,fontWeight=FontWeight.Black,modifier=Modifier.weight(1f));Text(if(p.correct>=need)"✓" else "${p.correct}/$need",color=if(p.correct>=need)Mint else Muted)}}}}}}
@Composable private fun Settings(hi:Boolean,sound:Boolean,music:Boolean,toggleLang:()->Unit,toggleSound:()->Unit,toggleMusic:()->Unit,back:()->Unit){Column(Modifier.fillMaxSize()){Top(if(hi)"सेटिंग्स" else "Settings",0,onBack=back);Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Text(if(hi)"भाषा: हिन्दी" else "Language: English",color=Ink,fontWeight=FontWeight.Black,modifier=Modifier.weight(1f));Button(onClick=toggleLang){Text(if(hi)"EN" else "हिंदी")}}};Glass(Modifier.fillMaxWidth()){Row{Text(if(hi)"ध्वनि प्रभाव" else "Sound effects",color=Ink,modifier=Modifier.weight(1f));Switch(sound,{toggleSound()})};HorizontalDivider(color=Color.White.copy(.06f),modifier=Modifier.padding(vertical=9.dp));Row{Text(if(hi)"पृष्ठभूमि संगीत" else "Background music",color=Ink,modifier=Modifier.weight(1f));Switch(music,{toggleMusic()})}};Text(if(hi)"यह ऐप प्रश्नों के बाद बाइबल संदर्भ और सीखने योग्य व्याख्या दिखाता है।" else "This app shows Bible references and learning explanations after every answer.",color=Muted,lineHeight=20.sp)}}}
