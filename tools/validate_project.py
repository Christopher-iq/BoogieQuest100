from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
required = [
    root/'app/src/main/java/com/boogie/quest/data/LevelCatalog.kt',
    root/'app/src/main/java/com/boogie/quest/ui/components/PuzzleHost.kt',
    root/'app/src/main/java/com/boogie/quest/ui/screens/GameScreen.kt',
    root/'app/src/main/AndroidManifest.xml',
]
missing=[str(p) for p in required if not p.exists()]
if missing:
    raise SystemExit('Missing required files: '+', '.join(missing))
text=(root/'app/src/main/java/com/boogie/quest/data/LevelCatalog.kt').read_text()
if '(1..100)' not in text:
    raise SystemExit('100-level generator not found')
print('BoogieQuest validation passed: 100-level catalog and core gameplay files present.')
