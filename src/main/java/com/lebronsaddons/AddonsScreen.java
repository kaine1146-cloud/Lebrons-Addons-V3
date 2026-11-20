package com.lebronsaddons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** Dark, rounded settings dashboard based on the supplied design reference. */
public class AddonsScreen extends Screen {
    private final Screen previous;
    private int tab = 0;
    private int left, top, panelW, panelH, sideW;
    private static final String[] SKY = {"Default Day", "Sunset", "Northern Lights", "Starry Night", "Cotton Candy", "Stormy"};
    private static final String[] COLOURS = {"Green", "Blue", "Pink", "Gold", "Purple"};

    public AddonsScreen(Screen previous) { super(Component.literal("Lebron's Addons")); this.previous=previous; }
    private static Component label(String s) { return Component.literal(s); }
    private void button(String text, int x, int y, int w, int h, Runnable action) {
        addRenderableWidget(Button.builder(label(text), b -> action.run()).bounds(x,y,w,h).build());
    }
    private void saveRebuild() { LebronsAddons.config.save(); rebuildWidgets(); }

    @Override protected void init() {
        panelW=Math.min(520,width-24);
        panelH=Math.min(310,height-24);
        left=(width-panelW)/2; top=(height-panelH)/2;
        sideW=Math.min(146, panelW/3);
        int x=left+12, content=left+sideW+16, right=left+panelW-14;
        int contentW=Math.max(110,right-content);
        button((tab==0?"● ":"♫ ")+"Spotify HUD",x,top+50,sideW-24,23,()->{tab=0;rebuildWidgets();});
        button((tab==1?"● ":"☀ ")+"Sky Themes",x,top+81,sideW-24,23,()->{tab=1;rebuildWidgets();});
        button((tab==2?"● ":"⚙ ")+"Settings",x,top+112,sideW-24,23,()->{tab=2;rebuildWidgets();});
        button("Done",x,top+panelH-34,sideW-24,22,this::onClose);
        if(tab==0) {
            button("Spotify HUD: "+(LebronsAddons.config.showHud?"ON":"OFF"),content,top+55,contentW,23,()->{LebronsAddons.config.showHud=!LebronsAddons.config.showHud;saveRebuild();});
            button("Connect Spotify",content,top+85,contentW,23,LebronsAddons::connectSpotify);
            button("Move / Resize HUD",content,top+115,contentW,23,()->minecraft.gui.setScreen(new HudPositionScreen(this)));
            int cardW=Math.max(35,(contentW-12)/3);
            String[] lyrics={"No lyrics","In front","In sky"};
            for(int i=0;i<3;i++) { final int selected=i;
                button((LebronsAddons.config.lyricsMode==i?"● ":"○ ")+lyrics[i],content+i*(cardW+6),top+194,cardW,26,()->{LebronsAddons.config.lyricsMode=selected;saveRebuild();});
            }
        } else if(tab==1) {
            int gap=8, cardW=(contentW-gap*2)/3;
            for(int i=0;i<6;i++) { final int selected=i;
                button((LebronsAddons.config.skyTheme==i?"● ":"○ ")+SKY[i],content+(i%3)*(cardW+gap),top+114+(i/3)*77,cardW,24,()->{LebronsAddons.config.skyTheme=selected;saveRebuild();});
            }
        } else {
            button("Background blur: "+(LebronsAddons.config.backgroundBlur?"ON":"OFF"),content,top+52,contentW,22,()->{LebronsAddons.config.backgroundBlur=!LebronsAddons.config.backgroundBlur;saveRebuild();});
            button("Menu sounds: "+(LebronsAddons.config.menuSounds?"ON":"OFF"),content,top+83,contentW,22,()->{LebronsAddons.config.menuSounds=!LebronsAddons.config.menuSounds;saveRebuild();});
            button("Volume: "+LebronsAddons.config.menuVolume+"%",content,top+114,contentW,22,()->{LebronsAddons.config.menuVolume=(LebronsAddons.config.menuVolume+10)%110;saveRebuild();});
            button("Menu colour: "+COLOURS[Math.floorMod(LebronsAddons.config.menuColour,5)],content,top+145,contentW,22,()->{LebronsAddons.config.menuColour++;saveRebuild();});
            button("Christmas mode: "+(LebronsAddons.config.christmasMode?"ON":"OFF"),content,top+176,contentW,22,()->{LebronsAddons.config.christmasMode=!LebronsAddons.config.christmasMode;saveRebuild();});
        }
    }
    private static void round(GuiGraphicsExtractor g,int x,int y,int w,int h,int color) {
        if(w<=0||h<=0)return;
        g.fill(x+7,y,x+w-7,y+h,color);
        g.fill(x+3,y+3,x+w-3,y+h-3,color);
        g.fill(x,y+7,x+w,y+h-7,color);
    }
    private void text(GuiGraphicsExtractor g,String s,int x,int y,int color) {g.text(font,s,x,y,color,false);}
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta) {
        round(g,left,top,panelW,panelH,0xF8151719);
        g.fill(left+sideW,top+5,left+sideW+1,top+panelH-5,0xFF303437);
        text(g,"Lebron's Addons",left+13,top+18,0xFFFFFFFF);
        int content=left+sideW+16, contentW=panelW-sideW-30;
        text(g,new String[]{"Spotify HUD","Sky Themes","Settings"}[tab],content,top+18,0xFFFFFFFF);
        int green=LebronsAddons.accent();
        g.fill(left+8,top+43+tab*31,left+11,top+75+tab*31,green);
        if(tab==0) {
            text(g,"NOW PLAYING BOX",content,top+42,0xFF999E9F);
            text(g,"LYRICS",content,top+153,0xFF999E9F);
            // Illustrated preview strips for the three lyric choices.
            int cw=Math.max(35,(contentW-12)/3);
            for(int i=0;i<3;i++) {
                int bx=content+i*(cw+6),by=top+165;
                round(g,bx,by,cw,25,0xFF304D68);
                g.fill(bx+5,by+4,bx+cw-5,by+16,0xFF71B5F4);
                g.fill(bx+5,by+16,bx+cw-5,by+21,0xFF54945A);
                if(i==1)g.fill(bx+8,by+12,bx+cw-8,by+15,0xFFFFFFFF);
                if(i==2)g.fill(bx+8,by+7,bx+cw-8,by+10,0xFFFFFFFF);
            }
            String status=SpotifyService.status;
            text(g,font.plainSubstrByWidth(status,contentW),content,top+234,0xFFB4B7B8);
            if(LebronsAddons.config.spotifyClientId.isBlank())
                text(g,font.plainSubstrByWidth("Spotify: add Client ID to config",contentW),content,top+252,0xFFFFCB82);
        } else if(tab==1) {
            text(g,"Theme previews (visual effect not active yet)",content,top+42,0xFFB1B5B6);
            for(int i=0;i<6;i++) {
                int cw=(contentW-16)/3;
                int bx=content+(i%3)*(cw+8),by=top+70+(i/3)*77;
                int[] sky={0xFF7ABCF9,0xFFDF7967,0xFF102D45,0xFF141A39,0xFFF3B6D6,0xFF4D5666};
                round(g,bx,by,cw,40,sky[i]);
                g.fill(bx+4,by+30,bx+cw-4,by+36,0xFF4A8058);
                if(i==0||i==1||i==4)g.fill(bx+cw-20,by+8,bx+cw-13,by+15,0xFFFFE8A0);
                if(i==2)g.fill(bx+7,by+11,bx+cw-7,by+17,0xFF2DCD94);
                if(i==3)g.fill(bx+13,by+8,bx+15,by+10,0xFFFFFFFF);
            }
            text(g,"Selected: "+SKY[Math.floorMod(LebronsAddons.config.skyTheme,6)],content,top+240,0xFFB4B7B8);
        } else {
            text(g,"PERSONALISE YOUR MENU",content,top+40,0xFF999E9F);
            text(g,"Green / Blue / Pink / Gold / Purple",content,top+218,0xFFB4B7B8);
        }
        super.extractRenderState(g,mx,my,delta);
    }
    @Override public void onClose() { LebronsAddons.config.save(); if(minecraft!=null)minecraft.gui.setScreen(previous); }
}
