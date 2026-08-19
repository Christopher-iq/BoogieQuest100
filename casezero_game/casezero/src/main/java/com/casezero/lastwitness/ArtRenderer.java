package com.casezero.lastwitness;

import android.graphics.*;
import java.util.Random;

/** Rich, bright isometric-style room artwork drawn at runtime. */
final class ArtRenderer {
    private static final Paint P = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint S = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final int NAVY = Color.rgb(25,49,66);
    private static final int TEAL = Color.rgb(32,139,131);
    private static final int TEAL2 = Color.rgb(61,177,159);
    private static final int CREAM = Color.rgb(250,239,211);
    private static final int WOOD = Color.rgb(164,101,55);
    private static final int WOOD2 = Color.rgb(115,73,44);
    private static final int GOLD = Color.rgb(250,184,62);
    private static final int CORAL = Color.rgb(234,102,84);
    private static final int BLUE = Color.rgb(72,139,196);
    private static final int GREEN = Color.rgb(85,167,94);

    private static void fill(int c){ P.setStyle(Paint.Style.FILL); P.setColor(c); P.setShader(null); }
    private static void stroke(int c,float w){ S.setStyle(Paint.Style.STROKE); S.setStrokeWidth(w); S.setColor(c); S.setShader(null); }
    private static void rect(Canvas c,float l,float t,float r,float b,int col){ fill(col); c.drawRect(l,t,r,b,P); }
    private static void round(Canvas c,float l,float t,float r,float b,float rad,int col){ fill(col); c.drawRoundRect(l,t,r,b,rad,rad,P); }
    private static void line(Canvas c,float x1,float y1,float x2,float y2,int col,float w){ stroke(col,w); c.drawLine(x1,y1,x2,y2,S); }
    private static void text(Canvas c,String s,float x,float y,float size,int col,Paint.Align a,boolean bold){ fill(col); P.setTextSize(size); P.setTextAlign(a); P.setTypeface(Typeface.create(Typeface.SANS_SERIF,bold?Typeface.BOLD:Typeface.NORMAL)); c.drawText(s,x,y,P); }

    static void drawOffice(Canvas canvas, int viewW, int viewH){
        canvas.save();
        float sx=viewW/1080f, sy=viewH/1920f;
        canvas.scale(sx,sy);
        drawOfficeDesign(canvas,0,0,1080,1920);
        canvas.restore();
    }

    static void drawArchive(Canvas canvas, int viewW, int viewH){
        canvas.save();
        float sx=viewW/1080f, sy=viewH/1920f;
        canvas.scale(sx,sy);
        drawArchiveDesign(canvas,0,0,1080,1920);
        canvas.restore();
    }

    static void drawRoom(Canvas canvas, RectF physicalDest, String style, int caseIndex){
        canvas.save();
        canvas.clipRect(physicalDest);
        canvas.translate(physicalDest.left, physicalDest.top);
        float sx=physicalDest.width()/1000f, sy=physicalDest.height()/1180f;
        canvas.scale(sx,sy);
        if("garage".equals(style)) drawGarage(canvas,0,0,1000,1180);
        else if("hotel".equals(style)) drawHotel(canvas,0,0,1000,1180);
        else if("observatory".equals(style)) drawObservatory(canvas,0,0,1000,1180);
        else if("loft".equals(style)) drawLoft(canvas,0,0,1000,1180);
        else drawCrimeApartment(canvas,0,0,1000,1180);
        canvas.restore();
    }

    private static void drawOfficeDesign(Canvas c,float x,float y,float w,float h){
        rect(c,x,y,x+w,y+h,Color.rgb(239,230,202));
        LinearGradient wall=new LinearGradient(0,0,w,h*.65f,Color.rgb(255,246,219),Color.rgb(232,235,205),Shader.TileMode.CLAMP);
        P.setShader(wall); c.drawRect(x,y,x+w,y+h*.67f,P); P.setShader(null);
        rect(c,x,y+h*.54f,x+w,y+h*.70f,Color.rgb(35,118,112));
        LinearGradient floor=new LinearGradient(0,h*.66f,w,h,Color.rgb(206,141,83),Color.rgb(137,84,52),Shader.TileMode.CLAMP);
        P.setShader(floor); c.drawRect(x,y+h*.66f,x+w,y+h,P); P.setShader(null);
        for(int i=0;i<18;i++) line(c,x,y+h*.66f+i*39,x+w,y+h*.66f+i*39,Color.argb(75,86,55,34),2);
        for(int i=0;i<12;i++) line(c,x+i*105,y+h*.66f,x+i*105,y+h,Color.argb(45,92,55,31),2);
        round(c,100,160,455,710,18,WOOD2); rect(c,124,185,431,685,Color.rgb(106,177,204));
        LinearGradient sky=new LinearGradient(120,190,420,680,Color.rgb(121,205,234),Color.rgb(250,197,119),Shader.TileMode.CLAMP); P.setShader(sky); c.drawRect(128,190,427,680,P); P.setShader(null);
        for(int i=0;i<9;i++){ float cx=145+i*34; float bh=40+(i%4)*34; rect(c,cx,680-bh,cx+22,680,Color.rgb(65,83,101)); for(int j=0;j<3;j++) rect(c,cx+5,690-bh+j*14,cx+9,696-bh+j*14,Color.rgb(255,221,111)); }
        line(c,278,190,278,680,Color.rgb(113,72,43),10); line(c,128,430,427,430,Color.rgb(113,72,43),10);
        round(c,78,155,125,720,18,Color.rgb(255,218,118)); round(c,430,155,477,720,18,Color.rgb(255,218,118));
        round(c,555,150,1015,590,18,WOOD2); round(c,578,175,992,565,12,Color.rgb(214,166,104));
        for(int i=0;i<10;i++){ float px=605+(i%4)*95, py=205+(i/4)*112; round(c,px,py,px+72,py+82,4,i%3==0?Color.rgb(249,235,178):Color.rgb(238,220,188)); line(c,px+36,py+8,px+36,py+70,Color.argb(60,76,62,51),1); }
        for(int i=0;i<7;i++){ float x1=630+(i%4)*92, y1=245+(i%3)*105; float x2=680+((i+2)%4)*92,y2=255+((i+1)%3)*105; line(c,x1,y1,x2,y2,CORAL,4); }
        text(c,"ACTIVE CASE",785,215,22,NAVY,Paint.Align.CENTER,true);
        round(c,72,780,330,1470,16,WOOD2); for(int r=0;r<5;r++) rect(c,88,825+r*125,315,840+r*125,Color.rgb(94,59,38));
        int[] bookCols={Color.rgb(41,125,122),Color.rgb(205,98,73),Color.rgb(58,83,122),Color.rgb(231,170,67),Color.rgb(83,126,70)};
        for(int r=0;r<5;r++) for(int b=0;b<6;b++){ float bx=98+b*34, by=752+(r+1)*125; round(c,bx,by,bx+25,by+72,3,bookCols[(r+b)%bookCols.length]); }
        plant(c,205,760,1.05f);
        round(c,360,1245,970,1710,38,Color.rgb(238,188,123));
        round(c,392,1300,940,1585,20,WOOD); rect(c,420,1585,470,1770,WOOD2); rect(c,855,1585,905,1770,WOOD2);
        round(c,650,1350,830,1460,8,Color.rgb(43,63,70)); round(c,663,1362,817,1443,5,Color.rgb(99,172,181));
        round(c,495,1342,630,1460,7,Color.rgb(250,242,210)); for(int i=0;i<5;i++) line(c,515,1370+i*15,610,1370+i*15,Color.argb(110,74,64,55),2);
        line(c,450,1395,450,1320,Color.rgb(85,96,61),9); round(c,405,1280,495,1335,18,Color.rgb(67,153,92));
        round(c,840,1350,900,1418,15,Color.rgb(71,151,170));
        round(c,80,1540,330,1815,42,Color.rgb(72,159,162)); round(c,110,1510,305,1610,40,Color.rgb(78,170,173)); round(c,125,1580,285,1660,18,Color.rgb(241,163,96));
        round(c,45,1480,100,1810,25,Color.rgb(58,136,140)); round(c,280,1510,350,1810,25,Color.rgb(58,136,140));
        round(c,395,1730,620,1900,38,Color.rgb(229,124,92)); round(c,420,1700,600,1780,34,Color.rgb(235,137,102));
        plant(c,1010,1080,.9f); plant(c,75,650,.75f); plant(c,945,735,.7f);
        round(c,710,690,930,760,12,Color.rgb(116,80,50)); text(c,"CASE ZERO AGENCY",820,737,22,CREAM,Paint.Align.CENTER,true);
        lamp(c,355,1050,1f); lamp(c,920,1020,.8f);
    }

    private static void drawArchiveDesign(Canvas c,float x,float y,float w,float h){
        rect(c,0,0,w,h,Color.rgb(246,235,207)); rect(c,0,0,w,760,Color.rgb(38,112,104)); rect(c,0,760,w,h,Color.rgb(190,124,72));
        for(int i=0;i<18;i++) line(c,0,780+i*62,w,780+i*62,Color.argb(50,82,52,32),2);
        round(c,95,130,365,650,16,WOOD2); rect(c,120,160,340,620,Color.rgb(138,211,226)); line(c,230,160,230,620,WOOD2,9); line(c,120,390,340,390,WOOD2,9);
        for(int col=0;col<2;col++) for(int row=0;row<5;row++) cabinet(c,60+col*175,760+row*130,155,110,(col+row)%2==0?Color.rgb(43,131,124):Color.rgb(93,125,74));
        for(int col=0;col<3;col++) for(int row=0;row<5;row++) cabinet(c,575+col*150,790+row*125,132,105,Color.rgb(86,112,76));
        round(c,440,130,1005,675,18,WOOD2); round(c,462,155,983,650,12,Color.rgb(228,201,147)); text(c,"ACTIVE CASES",720,205,22,NAVY,Paint.Align.CENTER,true);
        for(int i=0;i<8;i++){ float px=490+(i%4)*115, py=245+(i/4)*160; round(c,px,py,px+92,py+105,5,Color.rgb(248,239,210)); }
        for(int i=0;i<6;i++){ line(c,535+i*50,300+(i%2)*120,650+i*40,420+((i+1)%2)*80,CORAL,4); }
        round(c,260,930,790,1365,28,Color.rgb(222,166,102)); round(c,295,965,755,1280,16,WOOD); rect(c,330,1280,375,1510,WOOD2); rect(c,675,1280,720,1510,WOOD2);
        for(int i=0;i<8;i++){ float px=325+(i%4)*102, py=1000+(i/4)*130; round(c,px,py,px+82,py+98,5,Color.rgb(248,236,205)); }
        round(c,345,1450,910,1780,25,WOOD); round(c,390,1490,865,1670,12,Color.rgb(207,144,82)); text(c,"TRUTH • PATIENCE • PERSISTENCE",630,1535,20,CREAM,Paint.Align.CENTER,true);
        plant(c,900,720,.85f); plant(c,385,710,.7f); lamp(c,200,1430,.85f); lamp(c,830,1390,.8f);
    }

    private static void drawCrimeApartment(Canvas c,float x,float y,float w,float h){
        roomShell(c,Color.rgb(249,241,220),Color.rgb(42,119,114),Color.rgb(191,126,73)); window(c,65,120,335,500,true);
        round(c,70,560,370,810,34,Color.rgb(69,139,193)); round(c,92,525,345,620,35,Color.rgb(74,151,207)); round(c,110,620,210,700,16,Color.rgb(242,160,83));
        round(c,565,190,900,475,16,WOOD); round(c,600,225,865,410,10,Color.rgb(177,109,61)); round(c,585,75,935,180,10,Color.rgb(218,181,111)); for(int i=0;i<5;i++) round(c,605+i*60,95,650+i*60,150,4,Color.rgb(249,237,203));
        round(c,300,705,690,920,18,WOOD); rect(c,345,920,385,1030,WOOD2); rect(c,605,920,645,1030,WOOD2);
        stroke(Color.rgb(247,245,227),10); Path body=new Path(); body.moveTo(720,650); body.cubicTo(755,615,805,620,825,655); body.cubicTo(845,690,815,725,780,735); body.lineTo(855,845); body.lineTo(820,875); body.lineTo(760,800); body.lineTo(705,915); body.lineTo(665,895); body.lineTo(720,760); body.lineTo(650,800); body.lineTo(625,765); body.close(); c.drawPath(body,S);
        evidenceTent(c,250,955,"1"); evidenceTent(c,705,940,"2"); evidenceTent(c,875,790,"3"); round(c,520,850,570,905,8,Color.rgb(225,239,243)); round(c,735,905,805,930,5,Color.rgb(92,79,64)); plant(c,930,560,.85f); lamp(c,65,820,.75f);
    }

    private static void drawLoft(Canvas c,float x,float y,float w,float h){ roomShell(c,Color.rgb(246,237,211),Color.rgb(31,125,118),Color.rgb(174,113,68)); window(c,70,120,390,520,false); round(c,90,590,420,820,40,Color.rgb(225,124,91)); round(c,560,160,920,485,20,WOOD); round(c,600,200,885,405,10,Color.rgb(74,145,151)); evidenceTent(c,520,900,"1"); evidenceTent(c,740,820,"2"); evidenceTent(c,285,945,"3"); round(c,350,650,670,875,22,Color.rgb(210,151,85)); plant(c,930,630,.9f); plant(c,150,520,.7f); }

    private static void drawGarage(Canvas c,float x,float y,float w,float h){
        roomShell(c,Color.rgb(231,235,225),Color.rgb(54,121,116),Color.rgb(152,112,82)); for(int i=0;i<7;i++) cabinet(c,50+i*130,180,112,105,i%2==0?Color.rgb(60,143,135):Color.rgb(209,143,75));
        Path car=new Path(); car.moveTo(170,760); car.lineTo(300,585); car.lineTo(700,585); car.lineTo(855,745); car.lineTo(800,880); car.lineTo(215,880); car.close(); fill(Color.rgb(65,129,179)); c.drawPath(car,P);
        round(c,315,620,675,720,18,Color.rgb(139,207,222)); fill(Color.rgb(44,54,59)); c.drawCircle(300,870,70,P); c.drawCircle(735,870,70,P); fill(Color.rgb(225,181,84)); c.drawCircle(300,870,32,P); c.drawCircle(735,870,32,P); evidenceTent(c,135,960,"1"); evidenceTent(c,515,980,"2"); evidenceTent(c,865,925,"3");
    }

    private static void drawHotel(Canvas c,float x,float y,float w,float h){ roomShell(c,Color.rgb(252,231,214),Color.rgb(83,113,119),Color.rgb(171,103,75)); window(c,610,120,930,470,false); round(c,85,465,610,835,28,Color.rgb(218,142,105)); round(c,110,405,585,550,26,Color.rgb(245,218,180)); round(c,100,150,520,355,18,Color.rgb(123,80,56)); round(c,130,180,490,330,10,Color.rgb(225,190,140)); round(c,690,555,930,820,20,WOOD); lamp(c,850,495,.7f); evidenceTent(c,250,920,"1"); evidenceTent(c,620,920,"2"); evidenceTent(c,850,890,"3"); plant(c,930,500,.7f); }

    private static void drawObservatory(Canvas c,float x,float y,float w,float h){
        roomShell(c,Color.rgb(223,239,235),Color.rgb(28,109,111),Color.rgb(115,99,91)); round(c,60,95,420,500,26,Color.rgb(27,58,86)); Random r=new Random(4); for(int i=0;i<60;i++){ float px=85+r.nextFloat()*310, py=120+r.nextFloat()*350; fill(i%8==0?GOLD:Color.rgb(238,247,255)); c.drawCircle(px,py,i%8==0?3:1.5f,P); }
        round(c,510,160,930,470,20,Color.rgb(51,76,80)); for(int i=0;i<4;i++){ round(c,545+i*90,205,610+i*90,300,8,Color.rgb(91,188,179)); } round(c,245,635,790,930,35,Color.rgb(58,121,126)); round(c,310,680,730,830,16,Color.rgb(37,68,82)); evidenceTent(c,210,965,"1"); evidenceTent(c,510,980,"2"); evidenceTent(c,825,945,"3"); plant(c,930,610,.75f);
    }

    private static void roomShell(Canvas c,int wall,int accent,int floor){ rect(c,0,0,1000,1180,wall); rect(c,0,390,1000,570,accent); rect(c,0,570,1000,1180,floor); for(int i=0;i<13;i++) line(c,0,590+i*46,1000,590+i*46,Color.argb(50,79,51,34),2); for(int i=0;i<11;i++) line(c,60+i*100,570,60+i*100,1180,Color.argb(28,79,51,34),2); }
    private static void window(Canvas c,float l,float t,float r,float b,boolean daylight){ round(c,l,t,r,b,16,WOOD2); int sky=daylight?Color.rgb(125,205,230):Color.rgb(80,110,160); rect(c,l+20,t+20,r-20,b-20,sky); line(c,(l+r)/2,t+20,(l+r)/2,b-20,WOOD2,8); line(c,l+20,(t+b)/2,r-20,(t+b)/2,WOOD2,8); if(daylight){ fill(Color.argb(115,255,235,135)); Path ray=new Path(); ray.moveTo(r-30,t+35); ray.lineTo(r+210,b+270); ray.lineTo(r-110,b+270); ray.close(); c.drawPath(ray,P); } }
    private static void evidenceTent(Canvas c,float cx,float cy,String n){ fill(GOLD); Path pth=new Path(); pth.moveTo(cx-35,cy+35); pth.lineTo(cx,cy-35); pth.lineTo(cx+35,cy+35); pth.close(); c.drawPath(pth,P); text(c,n,cx,cy+18,28,NAVY,Paint.Align.CENTER,true); }
    private static void cabinet(Canvas c,float x,float y,float w,float h,int col){ round(c,x,y,x+w,y+h,9,col); line(c,x+12,y+h*.52f,x+w-12,y+h*.52f,Color.argb(70,255,255,255),2); round(c,x+w*.42f,y+h*.39f,x+w*.58f,y+h*.51f,4,GOLD); }
    private static void lamp(Canvas c,float x,float y,float sc){ fill(Color.argb(35,255,219,102)); c.drawCircle(x,y,120*sc,P); line(c,x,y,x,y+95*sc,Color.rgb(87,104,65),10*sc); fill(Color.rgb(66,158,95)); Path sh=new Path(); sh.moveTo(x-60*sc,y-15*sc); sh.lineTo(x+60*sc,y-15*sc); sh.lineTo(x+40*sc,y-65*sc); sh.lineTo(x-40*sc,y-65*sc); sh.close(); c.drawPath(sh,P); fill(GOLD); c.drawCircle(x,y-25*sc,9*sc,P); }
    private static void plant(Canvas c,float x,float y,float sc){ round(c,x-35*sc,y,x+35*sc,y+65*sc,12*sc,Color.rgb(183,115,61)); for(int i=0;i<9;i++){ double a=i*.78; float px=x+(float)Math.cos(a)*38*sc, py=y-25*sc+(float)Math.sin(a)*28*sc-(i%3)*10*sc; fill(i%2==0?Color.rgb(72,160,83):Color.rgb(98,185,91)); c.drawOval(px-18*sc,py-32*sc,px+18*sc,py+18*sc,P); } }
}
