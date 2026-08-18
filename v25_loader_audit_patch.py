from pathlib import Path
import json, re, urllib.request

APP=Path('bibleapp')
kt=APP/'app/src/main/java/com/biblequest/pro/MainActivity.kt'
s=kt.read_text(encoding='utf-8')

# --- Build an exact Open Translation Bible chapter manifest from the repository tree. ---
langs=['en-GB','hi-IN','es-ES','pt-BR','fr-FR','de-DE','ar-EG','zh-CN','id-ID','ru-RU','it-IT','ja-JP','ko-KR','fa-IR','he-IL','ml-IN','nl-NL','sv-SE','da-DK','fi-FI','nb-NO','sw-TZ','so-SO','is-IS']
expected=[50,40,27,36,34,24,21,4,31,24,22,25,29,36,10,13,10,42,150,31,12,8,66,52,5,48,12,14,3,9,1,4,7,3,3,3,2,14,4,28,16,24,21,28,16,16,13,6,6,4,4,5,3,6,4,3,1,13,5,5,3,5,1,1,1,22]
req=urllib.request.Request(
    'https://api.github.com/repos/OpenTranslationBible/open-bible/git/trees/main?recursive=1',
    headers={'Accept':'application/vnd.github+json','User-Agent':'BibleQuestPro-CI'}
)
with urllib.request.urlopen(req,timeout=60) as r:
    tree=json.load(r)
if tree.get('truncated'):
    raise SystemExit('Open Translation Bible tree response was truncated; refusing to build an incomplete manifest')

manifest={lang:{} for lang in langs}
pat=re.compile(r'^lang/([^/]+)/([0-9]{2})\.[^/]+/json/.*-([0-9]{2,3})\.json$',re.I)
for item in tree.get('tree',[]):
    if item.get('type')!='blob':
        continue
    path=item.get('path','')
    m=pat.match(path)
    if not m:
        continue
    locale,book_s,chapter_s=m.groups()
    if locale not in manifest:
        continue
    book=int(book_s); chapter=int(chapter_s)
    if 1 <= book <= 66:
        manifest[locale].setdefault(str(book),{})[str(chapter)]=path

# Coverage audit: exact 66-book / 1,189-chapter expectation for each supported language.
report=[]
for locale in langs:
    missing=[]
    found=0
    for book_no,max_ch in enumerate(expected,1):
        bm=manifest[locale].get(str(book_no),{})
        for ch in range(1,max_ch+1):
            if str(ch) in bm:
                found+=1
            else:
                missing.append(f'{book_no}:{ch}')
    report.append((locale,found,len(missing),missing[:12]))
    print(f'{locale}: {found}/1189 chapters; missing={len(missing)}' + (f' sample={missing[:12]}' if missing else ''))

# English and Hindi are core product languages: never ship incomplete core coverage.
for locale,found,missing_count,sample in report:
    if locale in ('en-GB','hi-IN') and missing_count:
        raise SystemExit(f'Core locale {locale} is incomplete: {missing_count} missing, sample={sample}')

assets=APP/'app/src/main/assets'
assets.mkdir(parents=True,exist_ok=True)
(assets/'bible_manifest.json').write_text(json.dumps(manifest,ensure_ascii=False,separators=(',',':')),encoding='utf-8')
(assets/'bible_manifest_audit.txt').write_text('\n'.join(f'{l}\t{f}/1189\tmissing={m}\t{sample}' for l,f,m,sample in report)+'\n',encoding='utf-8')

# --- Patch runtime loader to use the exact bundled manifest instead of guessing filenames. ---
if 'class ScriptureRepository {' in s:
    s=s.replace('class ScriptureRepository {','class ScriptureRepository(private val context: android.content.Context) {',1)
elif 'class ScriptureRepository(private val context:' not in s:
    raise SystemExit('ScriptureRepository declaration not found')

repo_anchor='''class ScriptureRepository(private val context: android.content.Context) {\n    private val bookCache=mutableMapOf<String,List<String>>()\n    private val chapterCache=mutableMapOf<String,ChapterData>()\n'''
repo_insert='''class ScriptureRepository(private val context: android.content.Context) {\n    private val bookCache=mutableMapOf<String,List<String>>()\n    private val chapterCache=mutableMapOf<String,ChapterData>()\n    private val manifest: JSONObject by lazy {\n        val raw=context.assets.open("bible_manifest.json").bufferedReader(Charsets.UTF_8).use{it.readText()}\n        JSONObject(raw)\n    }\n    private val diskCacheDir by lazy { java.io.File(context.filesDir,"scripture_cache").apply{mkdirs()} }\n\n    private fun manifestPath(locale:String,bookIndex:Int,chapter:Int):String? {\n        val lang=manifest.optJSONObject(locale) ?: return null\n        val book=lang.optJSONObject((bookIndex+1).toString()) ?: return null\n        return book.optString(chapter.toString()).takeIf{it.isNotBlank()}\n    }\n\n    private fun encodedRawUrl(relativePath:String):String {\n        val encoded=relativePath.split('/').joinToString("/"){ Uri.encode(it) }\n        return "https://raw.githubusercontent.com/OpenTranslationBible/open-bible/main/$encoded"\n    }\n'''
if repo_anchor in s:
    s=s.replace(repo_anchor,repo_insert,1)
elif 'private fun manifestPath(' not in s:
    raise SystemExit('Repository field anchor not found')

# Replace the v2.3 filename-guessing block with exact manifest resolution + persistent disk cache.
start='''        val folders=bookFolders(locale)\n        val folder=folders.getOrNull(bookIndex) ?: error("Book not available for $locale")\n        val display=folder.substringAfter('.',folder)\n        val chapterTag=chapter.toString().padStart(2,'0')\n        val slug=display.lowercase(java.util.Locale.ROOT).trim().replace(Regex("\\\\s+"), "-")\n        val file="$slug-$chapterTag.json"\n        val path="lang/$locale/${Uri.encode(folder)}/json/${Uri.encode(file)}"\n        val directUrl="https://raw.githubusercontent.com/OpenTranslationBible/open-bible/main/$path"\n        val rawChapter = try {\n            get(directUrl)\n        } catch (e: Exception) {\n            val dirApi="https://api.github.com/repos/OpenTranslationBible/open-bible/contents/lang/$locale/${Uri.encode(folder)}/json"\n            val listing=JSONArray(get(dirApi))\n            val suffix="-$chapterTag.json"\n            val downloadUrl=(0 until listing.length()).mapNotNull { i ->\n                val item=listing.getJSONObject(i)\n                val name=item.optString("name")\n                if(item.optString("type")=="file" && name.endsWith(suffix, ignoreCase=true))\n                    item.optString("download_url").takeIf { it.isNotBlank() } else null\n            }.firstOrNull() ?: throw e\n            get(downloadUrl)\n        }\n        val obj=JSONObject(rawChapter)\n'''
replacement='''        val relativePath=manifestPath(locale,bookIndex,chapter)\n            ?: error("This chapter is not available in the selected Scripture language.")\n        val display=relativePath.substringAfter('.').substringBefore("/json/")\n        val cacheFile=java.io.File(diskCacheDir,"${locale}_${bookIndex+1}_${chapter}.json")\n        val rawChapter = if(cacheFile.exists() && cacheFile.length()>10L) {\n            cacheFile.readText(Charsets.UTF_8)\n        } else {\n            val downloaded=get(encodedRawUrl(relativePath))\n            runCatching{ cacheFile.writeText(downloaded,Charsets.UTF_8) }\n            downloaded\n        }\n        val obj=JSONObject(rawChapter)\n'''
if start in s:
    s=s.replace(start,replacement,1)
elif 'val relativePath=manifestPath(locale,bookIndex,chapter)' not in s:
    raise SystemExit('v2.3 loader block not found')

# Repository now needs Context.
s=s.replace('val repo=remember{ScriptureRepository()}','val repo=remember{ScriptureRepository(context)}',1)

# Improve error copy so it does not blame connectivity for a translation availability problem.
s=s.replace('"network" to "Couldn’t load this chapter. Check your connection and try again."','"network" to "Couldn’t load this chapter. Try again, or switch Scripture language if this translation is unavailable."')
s=s.replace('"network" to "यह अध्याय लोड नहीं हो सका। कनेक्शन जाँचें और फिर प्रयास करें।"','"network" to "यह अध्याय लोड नहीं हो सका। फिर प्रयास करें, या यह अनुवाद उपलब्ध न हो तो भाषा बदलें।"')

kt.write_text(s,encoding='utf-8')

# Version bump.
gp=APP/'app/build.gradle.kts'
g=gp.read_text(encoding='utf-8')
g=g.replace('versionCode = 4','versionCode = 5').replace('versionName = "2.4.0"','versionName = "2.5.0"')
gp.write_text(g,encoding='utf-8')
print('v2.5 exact-manifest loader + chapter cache patch applied')
