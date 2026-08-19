#!/usr/bin/env python3
from pathlib import Path
import re

path = Path('casezero/src/main/java/com/casezero/lastwitness/GameView.java')
s = path.read_text()

# Keep the mature mystery gameplay, but replace the old near-black visual layer.
s = s.replace('setLayerType(View.LAYER_TYPE_SOFTWARE,null)', 'setLayerType(View.LAYER_TYPE_HARDWARE,null)')
s = s.replace('"#11161D"', '"#15544F"')
s = s.replace('"#151B22"', '"#1D756C"')
s = s.replace('"#2A3540"', '"#6BC6B4"')
s = s.replace('"#3B4653"', '"#98DCCF"')
s = s.replace('"#8E3030"', '"#EC695B"')
s = s.replace('"#07090E"', '"#EAF1D8"')

# Full-screen bright illustrated rooms behind menus.
old = '@Override protected void onDraw(Canvas c){super.onDraw(c);hits.clear();color("#EAF1D8");c.drawRect(0,0,getWidth(),getHeight(),p);drawRain(c);switch(screen)'
new = '@Override protected void onDraw(Canvas c){super.onDraw(c);hits.clear();drawVisualBackground(c);switch(screen)'
if old not in s:
    raise SystemExit('onDraw anchor not found')
s = s.replace(old, new)

anchor = '    private void drawRain(Canvas c)'
helper = '''    private void drawVisualBackground(Canvas c){
        if(screen==SCENE){color("#E9F1D8");c.drawRect(0,0,getWidth(),getHeight(),p);return;}
        if(screen==TITLE||screen==SETUP||screen==HUB||screen==SETTINGS){
            ArtRenderer.drawOffice(c,getWidth(),getHeight());
            p.setColor(Color.argb(screen==TITLE?32:72,10,72,67));c.drawRect(0,0,getWidth(),getHeight(),p);
        }else{
            ArtRenderer.drawArchive(c,getWidth(),getHeight());
            p.setColor(Color.argb(82,10,69,65));c.drawRect(0,0,getWidth(),getHeight(),p);
        }
    }

'''
if anchor not in s:
    raise SystemExit('rain anchor not found')
s = s.replace(anchor, helper + anchor)

# Remove the old tiny primitive office diorama; the whole title screen is now the room art.
s, n = re.subn(r'    private void drawOfficeDiorama\(Canvas c,float cx,float cy,float s\)\{.*?\}\n',
               '    private void drawOfficeDiorama(Canvas c,float cx,float cy,float s){}\n', s, count=1)
if n != 1:
    raise SystemExit('office diorama method not found')

# Replace the old dark geometric crime scene with five bright, case-specific illustrated rooms.
s, n = re.subn(r'    private void drawRoom\(Canvas c,float l,float t,float r,float b\)\{.*?\}\n',
'''    private void drawRoom(Canvas c,float l,float t,float r,float b){
        String style=caseIndex==1?"loft":caseIndex==2?"garage":caseIndex==3?"hotel":caseIndex==4?"observatory":"apartment";
        ArtRenderer.drawRoom(c,new RectF(l,t,r,b),style,caseIndex);
    }
''', s, count=1)
if n != 1:
    raise SystemExit('room method not found')

# Brighter card surfaces and warmer primary controls.
s = s.replace('primary?"#D69B3B":"#1D756C"', 'primary?"#EF835F":"#1D756C"')
s = s.replace('primary?"#101217":"#F1E7D3"', 'primary?"#FFF9E8":"#FFF9E8"')

path.write_text(s)
print('Applied bright/cozy visual overhaul:', path)
