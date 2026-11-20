package com.lebronsaddons;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
public class HudPositionScreen extends Screen {
    private final Screen previous;
    public HudPositionScreen(Screen previous) { super(Component.literal("Move Spotify HUD")); this.previous = previous; }
    @Override protected void init() {
        int cx=width/2;
        addRenderableWidget(Button.builder(Component.literal("Up"), b -> move(0,-5)).bounds(cx-35,height-130,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Left"), b -> move(-5,0)).bounds(cx-110,height-105,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Right"), b -> move(5,0)).bounds(cx+40,height-105,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Down"), b -> move(0,5)).bounds(cx-35,height-80,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("Size -"), b -> resize(-10)).bounds(cx-110,height-52,100,20).build());
        addRenderableWidget(Button.builder(Component.literal("Size +"), b -> resize(10)).bounds(cx+10,height-52,100,20).build());
        addRenderableWidget(Button.builder(Component.literal("Save position"), b -> onClose()).bounds(10,height-30,130,20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset"), b -> { LebronsAddons.config.hudX=14; LebronsAddons.config.hudY=14; LebronsAddons.config.save(); }).bounds(width-80,10,70,20).build());
    }
    private void resize(int delta) {
        LebronsAddons.config.hudScale=Math.max(60,Math.min(180,LebronsAddons.config.hudScale+delta));
        LebronsAddons.config.hudX=Math.max(0,Math.min(width-LebronsAddons.hudWidth(),LebronsAddons.config.hudX));
        LebronsAddons.config.hudY=Math.max(0,Math.min(height-LebronsAddons.hudHeight(),LebronsAddons.config.hudY));
        LebronsAddons.config.save();
    }
    private void move(int dx,int dy){LebronsAddons.config.hudX=Math.max(0,Math.min(width-LebronsAddons.hudWidth(),LebronsAddons.config.hudX+dx));LebronsAddons.config.hudY=Math.max(0,Math.min(height-LebronsAddons.hudHeight(),LebronsAddons.config.hudY+dy));LebronsAddons.config.save();}
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float delta){
        super.extractRenderState(g,mx,my,delta);
        g.text(font,"Move with arrows; resize with Size +/- ("+LebronsAddons.config.hudScale+"%)",width/2-145,20,0xFFFFFFFF,true);
        LebronsAddons.renderHud(g);
    }
    @Override public void onClose(){LebronsAddons.config.save();minecraft.gui.setScreen(previous);}
}
