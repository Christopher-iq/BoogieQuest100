package com.casezero.lastwitness;

import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.view.*;
import java.util.*;

public final class GameView extends View {
    private static final int TITLE=0, SETUP=1, CASES=2, BRIEF=3, SCENE=4, CLUE=5, HUB=6, SUSPECTS=7, INTERROGATE=8, BOARD=9, TIMELINE=10, ACCUSE=11, ENDING=12, SETTINGS=13;
    private final MainActivity ctx;
    private final Paint p=new Paint(3), stroke=new Paint(3);
    private final ArrayList<Hit> hits=new ArrayList<>();
    private final SharedPreferences prefs;
    private final GameData.CaseFile[] cases=GameData.all();
    private int screen=TITLE, caseIndex=0, selectedEvidence=0, selectedSuspect=0, unlocked=1;
    private String detective="Detective", gender="MALE", assist="INVESTIGATOR", toast="";
    private long toastUntil=0, start=System.currentTimeMillis();
    private final HashSet<Integer> found=new HashSet<>(), examined=new HashSet<>(), boardDone=new HashSet<>();
    private int timelineProgress=0, interrogationDepth=0, trust=50, stress=20;
    private int accuseSuspect=-1, accuseMotive=-1, accuseMethod=-1;
    private float scenePanX=0, scenePanY=0, sceneZoom=1f, downX, downY;
    private boolean dragging=false, musicOn=true, sfxOn=true, hapticsOn=true;
    private MediaPlayer ambient;
    private SoundPool sounds;
    private int sndClick,sndEvidence,sndSolved,sndFail;

    private static final class Hit{
        final RectF r; final String a; final int v;
        Hit(float l,float t,float rr,float b,String a,int v){r=new RectF(l,t,rr,b);this.a=a;this.v=v;}
    }

    public GameView(MainActivity c){
        super(c); ctx=c; setLayerType(View.LAYER_TYPE_SOFTWARE,null); setFocusable(true);
        prefs=c.getSharedPreferences("case_zero_save",Context.MODE_PRIVATE);
        detective=prefs.getString("detective","Detective"); gender=prefs.getString("gender","MALE");
        assist=prefs.getString("assist","INVESTIGATOR"); unlocked=prefs.getInt("unlocked",1);
        musicOn=prefs.getBoolean("music",true); sfxOn=prefs.getBoolean("sfx",true); hapticsOn=prefs.getBoolean("haptics",true);
        stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(dp(1.2f)); initAudio();
    }

    private void initAudio(){
        try{
            AudioAttributes aa=new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            sounds=new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(aa).build();
            sndClick=sounds.load(ctx,R.raw.ui_click,1); sndEvidence=sounds.load(ctx,R.raw.evidence_sting,1); sndSolved=sounds.load(ctx,R.raw.case_solved,1); sndFail=sounds.load(ctx,R.raw.soft_fail,1);
            ambient=MediaPlayer.create(ctx,R.raw.noir_ambient); if(ambient!=null){ambient.setLooping(true);ambient.setVolume(.34f,.34f); if(musicOn) ambient.start();}
        }catch(Exception ignored){}
    }
    public void resumeAudio(){try{if(musicOn&&ambient!=null&&!ambient.isPlaying())ambient.start();}catch(Exception ignored){}}
    public void pauseAudio(){try{if(ambient!=null&&ambient.isPlaying())ambient.pause();}catch(Exception ignored){}}
    public void setDetectiveName(String s){detective=s; prefs.edit().putString("detective",s).apply();invalidate();}

    private float dp(float v){return v*getResources().getDisplayMetrics().density;}
    private int C(String h){return Color.parseColor(h);}
    private void color(String h){p.setColor(C(h));p.setStyle(Paint.Style.FILL);}
    private void text(float size,String h,Paint.Align align){p.setTextSize(dp(size));p.setColor(C(h));p.setTextAlign(align);p.setTypeface(Typeface.create("sans",Typeface.NORMAL));}
    private void bold(float size,String h,Paint.Align align){text(size,h,align);p.setTypeface(Typeface.create("sans",Typeface.BOLD));}
    private void rect(Canvas c,float l,float t,float r,float b,String fill,float rad){color(fill);c.drawRoundRect(l,t,r,b,dp(rad),dp(rad),p);}
    private void line(Canvas c,float x1,float y1,float x2,float y2,String col,float w){stroke.setColor(C(col));stroke.setStrokeWidth(dp(w));c.drawLine(x1,y1,x2,y2,stroke);}
    private void label(Canvas c,String s,float x,float y,float size,String col,Paint.Align a){text(size,col,a);c.drawText(s,x,y,p);}
    private void title(Canvas c,String s,float x,float y,float size,String col){bold(size,col,Paint.Align.LEFT);c.drawText(s,x,y,p);}
    private void wrap(Canvas c,String s,float x,float y,float max,float size,String col,int maxLines){
        text(size,col,Paint.Align.LEFT);String[] ws=s.split(" ");StringBuilder ln=new StringBuilder();int n=0;float yy=y;
        for(String w:ws){String q=ln.length()==0?w:ln+" "+w;if(p.measureText(q)>max&&ln.length()>0){c.drawText(ln.toString(),x,yy,p);yy+=dp(size*1.34f);ln=new StringBuilder(w);if(++n>=maxLines)return;}else ln=new StringBuilder(q);} if(n<maxLines&&ln.length()>0)c.drawText(ln.toString(),x,yy,p);
    }
    private void button(Canvas c,String s,float l,float t,float r,float b,String action,int v,boolean primary){
        rect(c,l,t,r,b,primary?"#D69B3B":"#151B22",12); if(!primary){stroke.setColor(C("#3B4653"));stroke.setStrokeWidth(dp(1));c.drawRoundRect(l,t,r,b,dp(12),dp(12),stroke);} bold(13,primary?"#101217":"#F1E7D3",Paint.Align.CENTER);c.drawText(s,(l+r)/2,(t+b)/2+dp(4.5f),p);hits.add(new Hit(l,t,r,b,action,v));
    }
    private void top(Canvas c,String eyebrow,String heading){
        float w=getWidth(); label(c,eyebrow.toUpperCase(Locale.US),dp(20),dp(38),10,"#D69B3B",Paint.Align.LEFT);title(c,heading,dp(20),dp(69),24,"#F1E7D3");line(c,dp(20),dp(82),w-dp(20),dp(82),"#25303A",1);
        if(screen!=TITLE&&screen!=SETUP){button(c,"‹",w-dp(62),dp(24),w-dp(20),dp(66),"back",0,false);}
    }
    private void panel(Canvas c,float l,float t,float r,float b){rect(c,l,t,r,b,"#11161D",16);stroke.setColor(C("#2A3540"));stroke.setStrokeWidth(dp(1));c.drawRoundRect(l,t,r,b,dp(16),dp(16),stroke);}

    @Override protected void onDraw(Canvas c){super.onDraw(c);hits.clear();color("#07090E");c.drawRect(0,0,getWidth(),getHeight(),p);drawRain(c);switch(screen){case TITLE:drawTitle(c);break;case SETUP:drawSetup(c);break;case CASES:drawCases(c);break;case BRIEF:drawBrief(c);break;case SCENE:drawScene(c);break;case CLUE:drawClue(c);break;case HUB:drawHub(c);break;case SUSPECTS:drawSuspects(c);break;case INTERROGATE:drawInterrogate(c);break;case BOARD:drawBoard(c);break;case TIMELINE:drawTimeline(c);break;case ACCUSE:drawAccuse(c);break;case ENDING:drawEnding(c);break;case SETTINGS:drawSettings(c);break;}drawToast(c);postInvalidateDelayed(33);}

    private void drawRain(Canvas c){long now=System.currentTimeMillis()-start;stroke.setStrokeWidth(dp(.7f));stroke.setColor(C("#16232E"));for(int i=0;i<24;i++){float x=(i*137+(now/14)%311)%Math.max(1,getWidth());float y=(i*197+(now/9)%Math.max(1,getHeight()+120))-60;c.drawLine(x,y,x-dp(6),y+dp(19),stroke);}}

    private void drawTitle(Canvas c){float w=getWidth(),h=getHeight();
        label(c,"A GREYBRIDGE DETECTIVE STORY",w/2,dp(92),11,"#D69B3B",Paint.Align.CENTER);bold(48,"#F1E7D3",Paint.Align.CENTER);c.drawText("CASE",w/2,dp(160),p);bold(58,"#8E3030",Paint.Align.CENTER);c.drawText("ZERO",w/2,dp(218),p);label(c,"THE LAST WITNESS",w/2,dp(251),15,"#D8CBB5",Paint.Align.CENTER);
        float cy=h*.42f;drawOfficeDiorama(c,w/2,cy,w*.78f);
        button(c,"CONTINUE",dp(36),h-dp(220),w-dp(36),h-dp(164),unlocked>1?"cases":"setup",0,true);
        button(c,"NEW INVESTIGATION",dp(36),h-dp(151),w-dp(36),h-dp(97),"setup",0,false);
        button(c,"SETTINGS",dp(36),h-dp(84),w-dp(36),h-dp(32),"settings",0,false);
        label(c,"HEADPHONES RECOMMENDED",w/2,h-dp(8),9,"#6F7D89",Paint.Align.CENTER);
    }
    private void drawOfficeDiorama(Canvas c,float cx,float cy,float s){float x=cx-s/2,y=cy-s*.27f;Path floor=new Path();floor.moveTo(cx,y);floor.lineTo(x+s,y+s*.26f);floor.lineTo(cx,y+s*.52f);floor.lineTo(x,y+s*.26f);floor.close();color("#2B221D");c.drawPath(floor,p);Path wall=new Path();wall.moveTo(x,y-s*.22f);wall.lineTo(cx,y);wall.lineTo(cx,y+s*.52f);wall.lineTo(x,y+s*.26f);wall.close();color("#132129");c.drawPath(wall,p);Path wall2=new Path();wall2.moveTo(x+s,y-s*.22f);wall2.lineTo(cx,y);wall2.lineTo(cx,y+s*.52f);wall2.lineTo(x+s,y+s*.26f);wall2.close();color("#10191F");c.drawPath(wall2,p);rect(c,cx-dp(85),cy-dp(10),cx+dp(72),cy+dp(62),"#6B4427",4);rect(c,cx-dp(55),cy-dp(82),cx+dp(58),cy-dp(20),"#4A3527",3);for(int i=0;i<4;i++){rect(c,cx-dp(45)+i*dp(25),cy-dp(73)+(i%2)*dp(18),cx-dp(29)+i*dp(25),cy-dp(57)+(i%2)*dp(18),"#CDBB97",2);}line(c,cx-dp(35),cy-dp(60),cx+dp(35),cy-dp(40),"#8E3030",1.3f);color("#D69B3B");c.drawCircle(cx+dp(45),cy+dp(12),dp(8),p);}

    private void drawSetup(Canvas c){top(c,"PROFILE","Choose your detective");float w=getWidth();panel(c,dp(20),dp(104),w-dp(20),dp(235));label(c,"DETECTIVE NAME",dp(38),dp(132),10,"#8493A0",Paint.Align.LEFT);title(c,detective,dp(38),dp(171),24,"#F1E7D3");button(c,"EDIT NAME",dp(38),dp(186),w-dp(38),dp(222),"name",0,false);
        label(c,"IDENTITY",dp(24),dp(276),10,"#D69B3B",Paint.Align.LEFT);button(c,"MALE",dp(24),dp(292),w/2-dp(7),dp(350),"gender",0,gender.equals("MALE"));button(c,"FEMALE",w/2+dp(7),dp(292),w-dp(24),dp(350),"gender",1,gender.equals("FEMALE"));
        label(c,"ASSISTANCE",dp(24),dp(390),10,"#D69B3B",Paint.Align.LEFT);String[] m={"DETECTIVE","INVESTIGATOR","STORY"};for(int i=0;i<3;i++)button(c,m[i],dp(24),dp(408)+i*dp(62),w-dp(24),dp(458)+i*dp(62),"assist",i,assist.equals(m[i]));
        wrap(c,"Detective gives minimal guidance. Investigator offers subtle nudges. Story mode keeps every deduction accessible without changing the culprit.",dp(26),dp(610),w-dp(52),12,"#8D9AA5",5);button(c,"BEGIN CASE FILE",dp(24),getHeight()-dp(82),w-dp(24),getHeight()-dp(26),"cases",0,true);
    }

    private void drawCases(Canvas c){top(c,"SEASON ONE","Case files");float w=getWidth();float y=dp(108);for(int i=0;i<cases.length;i++){GameData.CaseFile f=cases[i];boolean open=i<unlocked;panel(c,dp(20),y,w-dp(20),y+dp(112));label(c,f.id,dp(36),y+dp(25),9,open?"#D69B3B":"#59646D",Paint.Align.LEFT);title(c,f.title,dp(36),y+dp(54),17,open?"#F1E7D3":"#65717A");wrap(c,open?f.subtitle:"LOCKED — solve the previous case",dp(36),y+dp(78),w-dp(88),11,"#87949F",2);if(open)hits.add(new Hit(dp(20),y,w-dp(20),y+dp(112),"openCase",i));y+=dp(124);} }

    private void drawBrief(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,f.id,f.title);float w=getWidth();panel(c,dp(20),dp(108),w-dp(20),dp(204));label(c,"VICTIM / SUBJECT",dp(38),dp(136),9,"#87949F",Paint.Align.LEFT);title(c,f.victim,dp(38),dp(171),23,"#F1E7D3");panel(c,dp(20),dp(220),w-dp(20),dp(510));label(c,"INITIAL BRIEFING",dp(38),dp(250),10,"#D69B3B",Paint.Align.LEFT);wrap(c,f.brief,dp(38),dp(286),w-dp(76),15,"#D8CBB5",11);label(c,"Every clue can be reopened. No energy. No forced waiting.",dp(38),dp(478),10,"#6F7D89",Paint.Align.LEFT);button(c,"ENTER CRIME SCENE",dp(24),getHeight()-dp(82),w-dp(24),getHeight()-dp(26),"scene",0,true);}

    private void drawScene(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,f.id,"Crime scene");float w=getWidth(),h=getHeight();label(c,"DRAG TO PAN • TAP GOLD MARKERS",dp(20),dp(104),9,"#73818D",Paint.Align.LEFT);float L=dp(18),T=dp(120),R=w-dp(18),B=h-dp(128);panel(c,L,T,R,B);c.save();c.clipRect(L,T,R,B);c.translate(scenePanX,scenePanY);c.scale(sceneZoom,sceneZoom,w/2,(T+B)/2);drawRoom(c,L,T,R,B);for(int i=0;i<f.evidence.length;i++){float[] q=spot(i,L,T,R,B);boolean have=found.contains(i);color(have?"#377B63":"#D69B3B");c.drawCircle(q[0],q[1],dp(have?7:10),p);stroke.setColor(C(have?"#8BD0B4":"#FFE0A1"));stroke.setStrokeWidth(dp(2));c.drawCircle(q[0],q[1],dp(15),stroke);if(!dragging)hits.add(new Hit(q[0]-dp(24),q[1]-dp(24),q[0]+dp(24),q[1]+dp(24),"clue",i));}c.restore();label(c,"EVIDENCE "+found.size()+" / "+f.evidence.length,dp(24),h-dp(94),11,"#D69B3B",Paint.Align.LEFT);button(c,"DETECTIVE HUB",dp(24),h-dp(78),w-dp(24),h-dp(24),"hub",0,found.size()>=3);}
    private void drawRoom(Canvas c,float l,float t,float r,float b){float cx=(l+r)/2,cy=(t+b)/2;Path floor=new Path();floor.moveTo(cx,t+dp(30));floor.lineTo(r-dp(18),cy);floor.lineTo(cx,b-dp(20));floor.lineTo(l+dp(18),cy);floor.close();color("#362B25");c.drawPath(floor,p);Path wl=new Path();wl.moveTo(l+dp(18),t+dp(5));wl.lineTo(cx,t+dp(30));wl.lineTo(cx,b-dp(20));wl.lineTo(l+dp(18),cy);wl.close();color("#16242B");c.drawPath(wl,p);Path wr=new Path();wr.moveTo(r-dp(18),t+dp(5));wr.lineTo(cx,t+dp(30));wr.lineTo(cx,b-dp(20));wr.lineTo(r-dp(18),cy);wr.close();color("#101A21");c.drawPath(wr,p);rect(c,cx-dp(94),cy-dp(35),cx+dp(70),cy+dp(44),"#5B3B2A",5);rect(c,l+dp(58),cy+dp(36),l+dp(160),cy+dp(92),"#26343A",12);rect(c,r-dp(132),cy-dp(116),r-dp(62),cy-dp(8),"#442D26",4);rect(c,cx-dp(32),t+dp(74),cx+dp(67),t+dp(137),"#252A2D",4);color("#8E3030");c.drawOval(cx-dp(36),cy+dp(75),cx+dp(8),cy+dp(91),p);line(c,cx-dp(8),cy+dp(61),cx+dp(27),cy+dp(95),"#B9B2A5",3);}
    private float[] spot(int i,float l,float t,float r,float b){float[][] n={{.32f,.43f},{.62f,.50f},{.78f,.34f},{.48f,.62f},{.68f,.72f},{.23f,.67f},{.52f,.29f},{.81f,.58f},{.38f,.77f},{.17f,.40f}};float[] q=n[i%n.length];return new float[]{l+(r-l)*q[0],t+(b-t)*q[1]};}

    private void drawClue(Canvas c){GameData.CaseFile f=cases[caseIndex];String[] e=parts(f.evidence[selectedEvidence]);top(c,"EVIDENCE "+(selectedEvidence+1),e[0]);float w=getWidth();panel(c,dp(24),dp(118),w-dp(24),dp(360));drawEvidenceIcon(c,w/2,dp(230),selectedEvidence);label(c,examined.contains(selectedEvidence)?"EXAMINED":"OBSERVED",dp(32),dp(398),10,examined.contains(selectedEvidence)?"#72BE9E":"#D69B3B",Paint.Align.LEFT);wrap(c,e[1],dp(32),dp(430),w-dp(64),15,"#E6DCCA",8);if(examined.contains(selectedEvidence))wrap(c,insight(selectedEvidence),dp(32),dp(554),w-dp(64),13,"#8FC8B0",6);button(c,examined.contains(selectedEvidence)?"RETURN TO SCENE":"EXAMINE CLOSELY",dp(24),getHeight()-dp(82),w-dp(24),getHeight()-dp(26),examined.contains(selectedEvidence)?"scene":"examine",selectedEvidence,true);}
    private void drawEvidenceIcon(Canvas c,float x,float y,int i){color("#D69B3B");c.drawCircle(x,y,dp(54),p);color("#121820");if(i%3==0){stroke.setColor(C("#121820"));stroke.setStrokeWidth(dp(7));c.drawCircle(x,y,dp(29),stroke);line(c,x-dp(27),y+dp(25),x+dp(28),y-dp(24),"#121820",4);}else if(i%3==1){c.drawRect(x-dp(34),y-dp(42),x+dp(34),y+dp(42),p);color("#D69B3B");for(int k=0;k<3;k++)c.drawRect(x-dp(22),y-dp(23)+k*dp(18),x+dp(22),y-dp(18)+k*dp(18),p);}else{Path q=new Path();q.moveTo(x,y-dp(44));q.lineTo(x+dp(38),y+dp(31));q.lineTo(x-dp(38),y+dp(31));q.close();c.drawPath(q,p);}}
    private String insight(int i){String[] v={"The visible time or sequence may have been deliberately manipulated.","Trace residue gives this object a second purpose beyond what it first appears to be.","Mechanical wear proves somebody interacted with this after the scene was staged.","Metadata is more reliable than the story someone told you.","The gap is too precise to be accidental.","This material links the method to a specific access route.","The ordinary explanation does not account for the chemistry.","This location recurs across the wider investigation.","Follow the money before you follow the confession.","Environmental transfer places someone where they deny being."};return v[i%v.length];}

    private void drawHub(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,f.id,"Detective hub");float w=getWidth();wrap(c,"Build the case instead of clearing filler puzzles. Revisit evidence whenever you need the original context.",dp(24),dp(118),w-dp(48),12,"#8C99A4",3);String[] names={"EVIDENCE LOG","FORENSIC REVIEW","SUSPECTS","EVIDENCE BOARD","TIMELINE","FINAL ACCUSATION"};String[] acts={"evidenceLog","forensic","suspects","board","timeline","accuse"};for(int i=0;i<6;i++){float y=dp(180)+i*dp(70);button(c,names[i],dp(24),y,w-dp(24),y+dp(54),acts[i],0,i==5&&boardDone.size()>=2&&timelineProgress>=3);}label(c,"CASE PROGRESS",dp(26),getHeight()-dp(105),9,"#D69B3B",Paint.Align.LEFT);label(c,found.size()+" clues • "+boardDone.size()+" deductions • "+timelineProgress+" timeline links",dp(26),getHeight()-dp(80),11,"#9BA7B0",Paint.Align.LEFT);}

    private void drawSuspects(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,f.id,"Suspects");float w=getWidth(),y=dp(112);for(int i=0;i<f.suspects.length;i++){String[] s=parts(f.suspects[i]);panel(c,dp(20),y,w-dp(20),y+dp(115));drawPortrait(c,dp(72),y+dp(57),i);title(c,s[0],dp(126),y+dp(38),17,"#F1E7D3");label(c,s[1],dp(126),y+dp(60),10,"#D69B3B",Paint.Align.LEFT);wrap(c,s[2],dp(126),y+dp(83),w-dp(154),11,"#8E9AA4",2);hits.add(new Hit(dp(20),y,w-dp(20),y+dp(115),"interrogate",i));y+=dp(128);}}
    private void drawPortrait(Canvas c,float x,float y,int i){color(i%2==0?"#273640":"#372D33");c.drawCircle(x,y,dp(39),p);color("#B38A6E");c.drawCircle(x,y-dp(9),dp(15),p);Path b=new Path();b.moveTo(x-dp(25),y+dp(31));b.quadTo(x,y+dp(4),x+dp(25),y+dp(31));b.close();color(i%2==0?"#171C21":"#211B20");c.drawPath(b,p);}

    private void drawInterrogate(Canvas c){GameData.CaseFile f=cases[caseIndex];String[] s=parts(f.suspects[selectedSuspect]);top(c,"INTERROGATION",s[0]);float w=getWidth();drawPortrait(c,w/2,dp(165),selectedSuspect);label(c,"TRUST "+trust+"%",dp(30),dp(233),10,"#72BE9E",Paint.Align.LEFT);label(c,"STRESS "+stress+"%",w-dp(30),dp(233),10,"#D36B62",Paint.Align.RIGHT);panel(c,dp(22),dp(257),w-dp(22),dp(392));String quote=interrogationDepth==0?"I already told patrol everything. I wasn't there.":interrogationDepth==1?"Why are you asking about that exact time?":interrogationDepth==2?"You think a camera gap proves I killed someone?":"Fine. Ask your question. But show me what you actually have.";wrap(c,"“"+quote+"”",dp(40),dp(298),w-dp(80),16,"#E9DFC9",5);
        String[] opts={"ASK ABOUT THE TIMELINE","PRESENT A KEY CLUE","CHALLENGE THE ALIBI","REMAIN SILENT"};for(int i=0;i<4;i++)button(c,opts[i],dp(24),dp(425)+i*dp(66),w-dp(24),dp(477)+i*dp(66),"question",i,i==1&&found.size()>=5);button(c,"END INTERVIEW",dp(24),getHeight()-dp(72),w-dp(24),getHeight()-dp(22),"suspects",0,false);}

    private void drawBoard(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,"DEDUCTION BOARD","Connect the facts");float w=getWidth();wrap(c,"A deduction unlocks when you have examined enough evidence. The game never marks a random guess as detective work.",dp(22),dp(112),w-dp(44),11,"#8996A1",3);for(int i=0;i<f.deductions.length;i++){float y=dp(190)+i*dp(150);boolean done=boardDone.contains(i);panel(c,dp(22),y,w-dp(22),y+dp(128));label(c,"THREAD 0"+(i+1),dp(38),y+dp(27),9,done?"#72BE9E":"#D69B3B",Paint.Align.LEFT);String[] d=parts(f.deductions[i]);title(c,d[0],dp(38),y+dp(56),16,"#F1E7D3");wrap(c,done?d[1]:"Select this thread after examining the connected evidence.",dp(38),y+dp(83),w-dp(76),11,done?"#A9CDBD":"#85919A",3);if(!done)button(c,"DEDUCE",w-dp(124),y+dp(18),w-dp(36),y+dp(57),"deduce",i,examined.size()>=Math.min(found.size(),3+i));} }

    private void drawTimeline(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,"RECONSTRUCTION","Timeline");float w=getWidth(),x=dp(54),y=dp(128);line(c,x,y,x,getHeight()-dp(130),"#495764",2);for(int i=0;i<f.timeline.length;i++){String[] t=parts(f.timeline[i]);boolean built=i<timelineProgress;color(built?"#D69B3B":"#39444E");c.drawCircle(x,y+i*dp(82),dp(8),p);label(c,t[0],dp(82),y+i*dp(82)+dp(3),11,built?"#D69B3B":"#697681",Paint.Align.LEFT);wrap(c,t[1],dp(142),y+i*dp(82)-dp(6),w-dp(166),11,built?"#E2D7C3":"#697681",2);}if(timelineProgress<f.timeline.length)button(c,"PLACE NEXT EVENT",dp(24),getHeight()-dp(82),w-dp(24),getHeight()-dp(26),"timelineNext",0,found.size()>=Math.min(f.evidence.length,timelineProgress+3));else button(c,"TIMELINE COMPLETE",dp(24),getHeight()-dp(82),w-dp(24),getHeight()-dp(26),"hub",0,true);}

    private void drawAccuse(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,"FINAL DEDUCTION","Make your accusation");float w=getWidth();label(c,"WHO?",dp(24),dp(112),9,"#D69B3B",Paint.Align.LEFT);for(int i=0;i<f.suspects.length;i++){String n=parts(f.suspects[i])[0];button(c,n,dp(24),dp(128)+i*dp(50),w-dp(24),dp(169)+i*dp(50),"accWho",i,accuseSuspect==i);}label(c,"WHY?",dp(24),dp(344),9,"#D69B3B",Paint.Align.LEFT);String[] motives={f.motive,"A personal argument that escalated","An insurance payment and revenge"};for(int i=0;i<3;i++)button(c,i==0?"FOLLOW THE MONEY / SECRET":"ALTERNATIVE THEORY "+i,dp(24),dp(360)+i*dp(50),w-dp(24),dp(401)+i*dp(50),"accMotive",i,accuseMotive==i);label(c,"HOW?",dp(24),dp(530),9,"#D69B3B",Paint.Align.LEFT);String[] meth={"METHOD SUPPORTED BY EVIDENCE","SPONTANEOUS ATTACK","ACCIDENT COVER-UP"};for(int i=0;i<3;i++)button(c,meth[i],dp(24),dp(546)+i*dp(50),w-dp(24),dp(587)+i*dp(50),"accMethod",i,accuseMethod==i);button(c,"CONFIRM ACCUSATION",dp(24),getHeight()-dp(78),w-dp(24),getHeight()-dp(24),"confirm",0,accuseSuspect>=0&&accuseMotive>=0&&accuseMethod>=0);}

    private void drawEnding(Canvas c){GameData.CaseFile f=cases[caseIndex];top(c,"CASE RESOLUTION",f.title);float w=getWidth();boolean correct=parts(f.suspects[Math.max(0,accuseSuspect)])[0].equals(f.killer)&&accuseMotive==0&&accuseMethod==0;panel(c,dp(22),dp(112),w-dp(22),dp(235));label(c,correct?"TRUTH RECONSTRUCTED":"CASE COMPROMISED",w/2,dp(148),11,correct?"#72BE9E":"#D36B62",Paint.Align.CENTER);bold(25,"#F1E7D3",Paint.Align.CENTER);c.drawText(correct?f.killer:"Your theory does not fit all the evidence",w/2,dp(188),p);label(c,correct?"Accused with motive and method":"You can reopen the case and try again",w/2,dp(216),10,"#8F9BA5",Paint.Align.CENTER);if(correct){label(c,"METHOD",dp(28),dp(278),9,"#D69B3B",Paint.Align.LEFT);wrap(c,f.method,dp(28),dp(307),w-dp(56),13,"#DDD2BE",7);label(c,"AFTERMATH",dp(28),dp(440),9,"#D69B3B",Paint.Align.LEFT);wrap(c,f.reveal,dp(28),dp(470),w-dp(56),14,"#E9DFC9",8);if(caseIndex==4){label(c,"THE LAST WITNESS WAS YOU.",w/2,dp(635),14,"#8E3030",Paint.Align.CENTER);}}
        button(c,correct?(caseIndex<4?"UNLOCK NEXT CASE":"RETURN TO CASE FILES"):"REOPEN INVESTIGATION",dp(24),getHeight()-dp(80),w-dp(24),getHeight()-dp(24),correct?"finish":"brief",0,true);}

    private void drawSettings(Canvas c){top(c,"SYSTEM","Settings");float w=getWidth();button(c,"MUSIC  "+(musicOn?"ON":"OFF"),dp(24),dp(140),w-dp(24),dp(200),"toggleMusic",0,musicOn);button(c,"SFX  "+(sfxOn?"ON":"OFF"),dp(24),dp(216),w-dp(24),dp(276),"toggleSfx",0,sfxOn);button(c,"HAPTICS  "+(hapticsOn?"ON":"OFF"),dp(24),dp(292),w-dp(24),dp(352),"toggleHaptics",0,hapticsOn);wrap(c,"Designed for portrait play with high-contrast text, large touch targets, autosave, offline case data and lossless PCM audio assets.",dp(28),dp(408),w-dp(56),13,"#8996A1",6);button(c,"RETURN",dp(24),getHeight()-dp(80),w-dp(24),getHeight()-dp(24),"title",0,false);}

    private void evidenceLog(){if(found.isEmpty()){say("No evidence collected yet.");return;}selectedEvidence=found.iterator().next();screen=CLUE;}
    private void forensic(){for(Integer i:found)examined.add(i);say("Forensic review updated every collected clue.");screen=HUB;}
    private String[] parts(String s){return s.split("\\|",-1);}
    private void say(String s){toast=s;toastUntil=System.currentTimeMillis()+2400;invalidate();}
    private void drawToast(Canvas c){if(System.currentTimeMillis()>toastUntil||toast.length()==0)return;float w=getWidth();rect(c,dp(24),getHeight()-dp(132),w-dp(24),getHeight()-dp(88),"#252E37",12);label(c,toast,w/2,getHeight()-dp(104),11,"#F1E7D3",Paint.Align.CENTER);}
    private void click(){if(sfxOn&&sounds!=null)try{sounds.play(sndClick,.45f,.45f,1,0,1);}catch(Exception ignored){}if(hapticsOn)try{performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);}catch(Exception ignored){}}
    private void evidenceSound(){if(sfxOn&&sounds!=null)try{sounds.play(sndEvidence,.65f,.65f,1,0,1);}catch(Exception ignored){}if(hapticsOn)try{performHapticFeedback(HapticFeedbackConstants.CONFIRM);}catch(Exception ignored){}}
    private void solvedSound(boolean ok){if(sfxOn&&sounds!=null)try{sounds.play(ok?sndSolved:sndFail,.65f,.65f,1,0,1);}catch(Exception ignored){}}
    private void resetCase(){found.clear();examined.clear();boardDone.clear();timelineProgress=0;interrogationDepth=0;trust=50;stress=20;accuseSuspect=accuseMotive=accuseMethod=-1;scenePanX=scenePanY=0;sceneZoom=1;}

    private void act(String a,int v){click();switch(a){
        case "title":screen=TITLE;break;case "setup":screen=SETUP;break;case "name":ctx.promptDetectiveName(detective);return;
        case "gender":gender=v==0?"MALE":"FEMALE";prefs.edit().putString("gender",gender).apply();break;
        case "assist":assist=new String[]{"DETECTIVE","INVESTIGATOR","STORY"}[v];prefs.edit().putString("assist",assist).apply();break;
        case "cases":screen=CASES;break;case "openCase":caseIndex=v;resetCase();screen=BRIEF;break;case "brief":screen=BRIEF;break;
        case "scene":screen=SCENE;break;case "clue":selectedEvidence=v;if(found.add(v))evidenceSound();screen=CLUE;break;case "examine":examined.add(v);evidenceSound();say("New inference added to the case file.");break;
        case "hub":screen=HUB;break;case "evidenceLog":evidenceLog();break;case "forensic":forensic();break;case "suspects":screen=SUSPECTS;break;
        case "interrogate":selectedSuspect=v;interrogationDepth=0;trust=50;stress=20;screen=INTERROGATE;break;
        case "question":interrogationDepth=Math.min(3,interrogationDepth+1);if(v==0){trust+=4;stress+=3;}else if(v==1&&found.size()>=4){trust-=4;stress+=18;evidenceSound();}else if(v==2){trust-=8;stress+=12;}else{trust+=2;stress+=6;}trust=Math.max(0,Math.min(100,trust));stress=Math.max(0,Math.min(100,stress));break;
        case "board":screen=BOARD;break;case "deduce":if(examined.size()>=Math.min(found.size(),3+v)){boardDone.add(v);evidenceSound();say(parts(cases[caseIndex].deductions[v])[1]);}else say("Examine more connected evidence first.");break;
        case "timeline":screen=TIMELINE;break;case "timelineNext":if(timelineProgress<cases[caseIndex].timeline.length&&found.size()>=Math.min(cases[caseIndex].evidence.length,timelineProgress+3)){timelineProgress++;evidenceSound();}else say("You need another clue before placing that event.");break;
        case "accuse":if(boardDone.size()<2||timelineProgress<3){say("Build at least two deductions and three timeline links first.");screen=HUB;}else screen=ACCUSE;break;
        case "accWho":accuseSuspect=v;break;case "accMotive":accuseMotive=v;break;case "accMethod":accuseMethod=v;break;
        case "confirm":if(accuseSuspect<0||accuseMotive<0||accuseMethod<0){say("Complete Who, Why and How.");break;}boolean ok=parts(cases[caseIndex].suspects[accuseSuspect])[0].equals(cases[caseIndex].killer)&&accuseMotive==0&&accuseMethod==0;solvedSound(ok);screen=ENDING;break;
        case "finish":boolean correct=parts(cases[caseIndex].suspects[Math.max(0,accuseSuspect)])[0].equals(cases[caseIndex].killer)&&accuseMotive==0&&accuseMethod==0;if(correct&&caseIndex<4){unlocked=Math.max(unlocked,caseIndex+2);prefs.edit().putInt("unlocked",unlocked).apply();}screen=CASES;break;
        case "settings":screen=SETTINGS;break;case "toggleMusic":musicOn=!musicOn;prefs.edit().putBoolean("music",musicOn).apply();if(musicOn)resumeAudio();else pauseAudio();break;
        case "toggleSfx":sfxOn=!sfxOn;prefs.edit().putBoolean("sfx",sfxOn).apply();break;case "toggleHaptics":hapticsOn=!hapticsOn;prefs.edit().putBoolean("haptics",hapticsOn).apply();break;
        case "back":if(screen==BRIEF||screen==SETTINGS)screen=CASES;else if(screen==SCENE||screen==SUSPECTS||screen==BOARD||screen==TIMELINE||screen==ACCUSE)screen=HUB;else if(screen==CLUE)screen=SCENE;else if(screen==INTERROGATE)screen=SUSPECTS;else screen=TITLE;break;
    }invalidate();}

    @Override public boolean onTouchEvent(MotionEvent e){float x=e.getX(),y=e.getY();if(e.getAction()==MotionEvent.ACTION_DOWN){downX=x;downY=y;dragging=false;return true;}if(e.getAction()==MotionEvent.ACTION_MOVE&&screen==SCENE){float dx=x-downX,dy=y-downY;if(Math.abs(dx)+Math.abs(dy)>dp(7)){dragging=true;scenePanX+=dx;scenePanY+=dy;downX=x;downY=y;invalidate();}return true;}if(e.getAction()==MotionEvent.ACTION_UP){if(screen==SCENE&&dragging){dragging=false;return true;}for(int i=hits.size()-1;i>=0;i--){Hit h=hits.get(i);if(h.r.contains(x,y)){act(h.a,h.v);return true;}}}return true;}
}
