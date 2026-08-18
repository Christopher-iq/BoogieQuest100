from pathlib import Path
import wave, math, struct

p=Path('bibleapp/app/src/main/java/com/biblequest/pro/MainActivity.kt')
s=p.read_text(encoding='utf-8')

# Compile import.
marker='import androidx.compose.ui.graphics.*\n'
needed='import androidx.compose.ui.geometry.Offset\n'
if needed not in s:
    if marker not in s:
        raise SystemExit('graphics import marker not found')
    s=s.replace(marker,marker+needed,1)
if 'import android.media.MediaPlayer' not in s:
    s=s.replace('import android.media.ToneGenerator\n','import android.media.ToneGenerator\nimport android.media.MediaPlayer\n',1)

# Robust multilingual chapter file lookup.
old='''        val display=folder.substringAfter('.',folder)\n        val file="$display-${chapter.toString().padStart(2,'0')}.json"\n        val path="lang/$locale/${Uri.encode(folder)}/json/${Uri.encode(file)}"\n        val url="https://raw.githubusercontent.com/OpenTranslationBible/open-bible/main/$path"\n        val obj=JSONObject(get(url))\n'''
new='''        val display=folder.substringAfter('.',folder)\n        val chapterTag=chapter.toString().padStart(2,'0')\n        val slug=display.lowercase(java.util.Locale.ROOT).trim().replace(Regex("\\\\s+"), "-")\n        val file="$slug-$chapterTag.json"\n        val path="lang/$locale/${Uri.encode(folder)}/json/${Uri.encode(file)}"\n        val directUrl="https://raw.githubusercontent.com/OpenTranslationBible/open-bible/main/$path"\n        val rawChapter = try {\n            get(directUrl)\n        } catch (e: Exception) {\n            val dirApi="https://api.github.com/repos/OpenTranslationBible/open-bible/contents/lang/$locale/${Uri.encode(folder)}/json"\n            val listing=JSONArray(get(dirApi))\n            val suffix="-$chapterTag.json"\n            val downloadUrl=(0 until listing.length()).mapNotNull { i ->\n                val item=listing.getJSONObject(i)\n                val name=item.optString("name")\n                if(item.optString("type")=="file" && name.endsWith(suffix, ignoreCase=true))\n                    item.optString("download_url").takeIf { it.isNotBlank() } else null\n            }.firstOrNull() ?: throw e\n            get(downloadUrl)\n        }\n        val obj=JSONObject(rawChapter)\n'''
if old in s:
    s=s.replace(old,new,1)

# Real retry instead of navigating back.
old_retry='''    var qs by remember{mutableStateOf(emptyList<AutoQ>())}\n    LaunchedEffect(l.code,book.number,chapter){\n'''
new_retry='''    var qs by remember{mutableStateOf(emptyList<AutoQ>())}\n    var retryKey by remember{mutableIntStateOf(0)}\n    LaunchedEffect(l.code,book.number,chapter,retryKey){\n'''
if old_retry in s:
    s=s.replace(old_retry,new_retry,1)
s=s.replace('Primary(I18n.t(l.code,"retry"),{back()},Modifier.fillMaxWidth())','Primary(I18n.t(l.code,"retry"),{retryKey++},Modifier.fillMaxWidth())',1)
s=s.replace('"network" to "Internet is needed the first time a chapter is opened."','"network" to "Couldn’t load this chapter. Check your connection and try again."')
s=s.replace('"network" to "किसी अध्याय को पहली बार खोलने के लिए इंटरनेट चाहिए।"','"network" to "यह अध्याय लोड नहीं हो सका। कनेक्शन जाँचें और फिर प्रयास करें।"')

# Preserve current index when the translated set refreshes.
s=s.replace('var idx by remember(qs){mutableIntStateOf(0)}','var idx by remember{mutableIntStateOf(0)}')

# Real louder looping background music + volume control.
old_audio='''    var music by remember{mutableStateOf(true)}\n    var sfx by remember{mutableStateOf(true)}\n    val repo=remember{ScriptureRepository()}\n    val tone=remember{ToneGenerator(AudioManager.STREAM_MUSIC,30)}\n    DisposableEffect(Unit){onDispose{tone.release()}}\n'''
new_audio='''    var music by remember{mutableStateOf(true)}\n    var musicVolume by remember{mutableFloatStateOf(.76f)}\n    var sfx by remember{mutableStateOf(true)}\n    val repo=remember{ScriptureRepository()}\n    val tone=remember{ToneGenerator(AudioManager.STREAM_MUSIC,55)}\n    val context=LocalContext.current\n    val bgm=remember { MediaPlayer.create(context,R.raw.bible_ambient).apply { isLooping=true } }\n    LaunchedEffect(music,musicVolume){\n        bgm.setVolume(musicVolume,musicVolume)\n        if(music){ if(!bgm.isPlaying) bgm.start() } else if(bgm.isPlaying) bgm.pause()\n    }\n    DisposableEffect(Unit){onDispose{tone.release();bgm.release()}}\n'''
if old_audio not in s:
    raise SystemExit('audio state block not found')
s=s.replace(old_audio,new_audio,1)

old_call='Page.SETTINGS->Settings(lang,music,sfx,{music=!music},{sfx=!sfx},{page=Page.HOME})'
new_call='Page.SETTINGS->Settings(lang,music,musicVolume,sfx,{music=!music},{musicVolume=it},{sfx=!sfx},{page=Page.HOME})'
if old_call not in s:
    raise SystemExit('settings call not found')
s=s.replace(old_call,new_call,1)

old_sig='@Composable private fun Settings(l:Lang,music:Boolean,sfx:Boolean,toggleMusic:()->Unit,toggleSfx:()->Unit,back:()->Unit){'
new_sig='@Composable private fun Settings(l:Lang,music:Boolean,musicVolume:Float,sfx:Boolean,toggleMusic:()->Unit,setMusicVolume:(Float)->Unit,toggleSfx:()->Unit,back:()->Unit){'
if old_sig not in s:
    raise SystemExit('settings signature not found')
s=s.replace(old_sig,new_sig,1)

old_settings='''Glass(Modifier.fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Text("Background music",color=INK,modifier=Modifier.weight(1f));Switch(music,{toggleMusic()})};HorizontalDivider(color=Color.White.copy(.06f),modifier=Modifier.padding(vertical=10.dp));Row(verticalAlignment=Alignment.CenterVertically){Text("Sound effects",color=INK,modifier=Modifier.weight(1f));Switch(sfx,{toggleSfx()})}}'''
new_settings='''Glass(Modifier.fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Text("Background music",color=INK,modifier=Modifier.weight(1f));Switch(music,{toggleMusic()})};Spacer(Modifier.height(10.dp));Row(verticalAlignment=Alignment.CenterVertically){Text("Music volume",color=MUTED,fontSize=12.sp,modifier=Modifier.weight(1f));Text("${(musicVolume*100).toInt()}%",color=GOLD,fontWeight=FontWeight.Black)};Slider(value=musicVolume,onValueChange=setMusicVolume,valueRange=.15f..1f,enabled=music);HorizontalDivider(color=Color.White.copy(.06f),modifier=Modifier.padding(vertical=10.dp));Row(verticalAlignment=Alignment.CenterVertically){Text("Sound effects",color=INK,modifier=Modifier.weight(1f));Switch(sfx,{toggleSfx()})}}'''
if old_settings not in s:
    raise SystemExit('settings UI block not found')
s=s.replace(old_settings,new_settings,1)

# Floating language switcher on every quiz page.
tail=new_call+'\n            }\n        }\n    }\n}\n\n@Composable private fun Ambient()'
if tail not in s:
    raise SystemExit('root tail anchor not found')
if 'fun startMixed' in s:
    reroute='if(page==Page.CURATED && newLang.code !in listOf("en-GB","hi-IN")){ lang=newLang; startMixed(curatedQuestions.size.coerceAtLeast(10)) } else { lang=newLang }'
else:
    reroute='lang=newLang'
replacement=new_call+'''\n            }\n        }\n        if(page==Page.CHAPTER_QUIZ || page==Page.CURATED || page==Page.MIXED){\n            QuizLanguageSwitcher(lang){ newLang -> '''+reroute+''' }\n        }\n    }\n}\n\n@Composable private fun Ambient()'''
s=s.replace(tail,replacement,1)

component_marker='@Composable private fun Glass(mod:Modifier=Modifier,content:@Composable ColumnScope.()->Unit)'
if component_marker not in s:
    raise SystemExit('component marker not found')
language_ui=r'''@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun QuizLanguageSwitcher(current:Lang,onSelect:(Lang)->Unit){
    var open by remember{mutableStateOf(false)}
    Box(Modifier.fillMaxSize().padding(top=84.dp,end=14.dp),contentAlignment=Alignment.TopEnd){
        SmallFloatingActionButton(
            onClick={open=true},
            containerColor=Color(0xEE203653),
            contentColor=INK,
            shape=RoundedCornerShape(16.dp)
        ){ Text("🌐",fontSize=19.sp) }
    }
    if(open){
        ModalBottomSheet(
            onDismissRequest={open=false},
            containerColor=Color(0xFF102238),
            contentColor=INK
        ){
            Column(Modifier.fillMaxWidth().padding(horizontal=18.dp)){
                Text("Change quiz language",color=INK,fontSize=22.sp,fontWeight=FontWeight.Black)
                Text("Switch without leaving the quiz.",color=MUTED,fontSize=12.sp)
                Spacer(Modifier.height(12.dp))
                LazyColumn(Modifier.fillMaxWidth().heightIn(max=500.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
                    items(BibleData.languages){language->
                        Surface(
                            Modifier.fillMaxWidth().clickable{onSelect(language);open=false},
                            color=if(language.code==current.code)SKY.copy(.14f)else PANEL2,
                            shape=RoundedCornerShape(16.dp),
                            border=BorderStroke(1.dp,if(language.code==current.code)SKY.copy(.45f)else Color.White.copy(.05f))
                        ){
                            Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
                                Text(language.nativeName,color=INK,fontWeight=FontWeight.Black,modifier=Modifier.weight(1f))
                                Text(language.englishName,color=MUTED,fontSize=11.sp)
                                if(language.code==current.code){Spacer(Modifier.width(8.dp));Text("✓",color=MINT,fontWeight=FontWeight.Black)}
                            }
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
            }
        }
    }
}

'''
s=s.replace(component_marker,language_ui+component_marker,1)

p.write_text(s,encoding='utf-8')

# Generate an original ambient loop locally during CI.
raw=Path('bibleapp/app/src/main/res/raw')
raw.mkdir(parents=True,exist_ok=True)
out=raw/'bible_ambient.wav'
sr=22050
duration=24.0
frames=int(sr*duration)
chords=[(220.00,277.18,329.63),(196.00,246.94,293.66),(174.61,220.00,261.63),(196.00,246.94,329.63)]
with wave.open(str(out),'w') as wf:
    wf.setnchannels(1); wf.setsampwidth(2); wf.setframerate(sr)
    for i in range(frames):
        t=i/sr
        chord=chords[int(t//6)%len(chords)]
        local=t%6
        fade=min(1.0,local/0.45,(6-local)/0.55)
        body=sum(math.sin(2*math.pi*f*t) for f in chord)/3.0
        bass=0.33*math.sin(2*math.pi*(chord[0]/2)*t)
        shimmer=0.25*math.sin(2*math.pi*(chord[1]*2)*t)
        sample=(0.32*body+0.18*bass+0.05*shimmer)*fade
        sample=max(-0.82,min(0.82,sample))
        wf.writeframesraw(struct.pack('<h',int(sample*32767)))

# Version bump.
gp=Path('bibleapp/app/build.gradle.kts')
g=gp.read_text(encoding='utf-8')
g=g.replace('versionCode = 2','versionCode = 3').replace('versionName = "2.0.0"','versionName = "2.3.0"')
gp.write_text(g,encoding='utf-8')
print('v2.3 patch complete:',out,out.stat().st_size)
