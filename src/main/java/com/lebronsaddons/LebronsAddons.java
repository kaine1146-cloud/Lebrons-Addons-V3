package com.lebronsaddons;

import com.sun.net.httpserver.HttpServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.client.KeyMapping;

import net.minecraft.network.chat.Component;

import java.awt.Desktop;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LebronsAddons implements ClientModInitializer {
    public static Config config;
    private static KeyMapping menuKey;
    private static boolean serverStarted = false;
    @Override public void onInitializeClient() {
        config = Config.load();
        menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.lebronsaddons.menu", 15, KeyMapping.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) client.gui.setScreen(new AddonsScreen(client.gui.screen()));
            SpotifyService.tick(config);
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("lebronsaddons", "spotify_hud"), (context, tickCounter) -> {renderHud(context);renderLyrics(context);});
    }
    private static int color() { return config.christmasMode ? 0xFFDA3A36 : new int[]{0xFF18D96D,0xFF419BFF,0xFFFF59A6,0xFFFFC43C,0xFFA687FA}[Math.floorMod(config.menuColour,5)]; }
    private static String time(long ms) { long s = Math.max(0,ms)/1000; return (s/60)+":"+String.format("%02d",s%60); }
    public static int hudWidth() { return Math.round(206 * Math.max(60, Math.min(180, config.hudScale)) / 100f); }
    public static int hudHeight() { return Math.round(68 * Math.max(60, Math.min(180, config.hudScale)) / 100f); }
    // All positions are scaled together, so changing the size doesn't scramble the layout.
    public static void renderHud(GuiGraphicsExtractor c) {
        if (!config.showHud) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.screen() != null && !(mc.gui.screen() instanceof HudPositionScreen)) return;
        int x = config.hudX, y = config.hudY;
        float scale = Math.max(60, Math.min(180, config.hudScale)) / 100f;
        int w = hudWidth(), h = hudHeight();
        int bg = config.christmasMode ? 0xF01A3026 : 0xEF181B20;
        // Soft rounded corners made from rectangles; no additional rendering dependency.
        c.fill(x+6,y,x+w-6,y+h,bg);
        c.fill(x+2,y+2,x+w-2,y+h-2,bg);
        c.fill(x,y+6,x+w,y+h-6,bg);
        int accent = config.christmasMode ? 0xFF19D267 : color();
        int ax = x + Math.round(8*scale), ay = y + Math.round(9*scale);
        int art = Math.round(48*scale);
        c.fill(ax+4,ay,ax+art-4,ay+art,0xFF8655B7);
        c.fill(ax,ay+4,ax+art,ay+art-4,0xFF8655B7);
        c.text(mc.font,"♫",ax+Math.round(17*scale),ay+Math.round(18*scale),0xFFFFFFFF,true);
        SpotifyService.Track track = SpotifyService.current;
        String title = track == null ? "No song playing" : track.title();
        String artist = track == null ? SpotifyService.status : track.artist();
        int tx=x+Math.round(64*scale);
        int maxText=Math.max(45,Math.round(132*scale));
        c.text(mc.font,mc.font.plainSubstrByWidth(title,maxText),tx,y+Math.round(10*scale),0xFFFFFFFF,true);
        c.text(mc.font,mc.font.plainSubstrByWidth(artist,maxText),tx,y+Math.round(25*scale),0xFFB8B8B8,false);
        long pos=track==null?0:track.positionMs()+(track.playing()?System.currentTimeMillis()-track.updatedAt():0);
        long duration=track==null?0:track.durationMs();
        int barX=tx+Math.round(29*scale), barY=y+Math.round(51*scale), barW=Math.round(91*scale);
        c.text(mc.font,time(pos),tx,y+Math.round(46*scale),0xFFCCCCCC,false);
        c.fill(barX,barY,barX+barW,barY+Math.max(2,Math.round(3*scale)),0xFF54585B);
        if(duration>0) c.fill(barX,barY,barX+(int)(barW*(double)Math.min(pos,duration)/duration),barY+Math.max(2,Math.round(3*scale)),accent);
        c.text(mc.font,time(duration),barX+barW+Math.round(4*scale),y+Math.round(46*scale),0xFFCCCCCC,false);
        if(config.christmasMode)c.text(mc.font,"*",x+w-Math.round(13*scale),y+Math.round(5*scale),0xFFFFD66E,false);
    }
    private static void renderLyrics(GuiGraphicsExtractor c) {
        if(config.lyricsMode==0 || SpotifyService.current==null)return;
        Minecraft mc=Minecraft.getInstance();
        if(mc.gui.screen()!=null)return;
        SpotifyService.Track track=SpotifyService.current;
        long pos=track.positionMs()+(track.playing()?System.currentTimeMillis()-track.updatedAt():0);
        String[] lines=LyricsService.current(track,pos);
        int cx=mc.getWindow().getGuiScaledWidth()/2;
        int y=config.lyricsMode==2?mc.getWindow().getGuiScaledHeight()/3:mc.getWindow().getGuiScaledHeight()-85;
        if(!lines[0].isEmpty())c.centeredText(mc.font,Component.literal(mc.font.plainSubstrByWidth(lines[0],360)),cx,y-15,0xFFBBBBBB);
        if(!lines[1].isEmpty())c.centeredText(mc.font,Component.literal(mc.font.plainSubstrByWidth(lines[1],360)),cx,y,0xFFFFFFFF);
        if(!lines[2].isEmpty())c.centeredText(mc.font,Component.literal(mc.font.plainSubstrByWidth(lines[2],360)),cx,y+15,0xFFBBBBBB);
    }
    public static void connectSpotify() {
        try {
            if (!serverStarted) {
                HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",8765),0);
                server.createContext("/callback", exchange -> {
                    Map<String,String> params=Arrays.stream((exchange.getRequestURI().getRawQuery()==null?"":exchange.getRequestURI().getRawQuery()).split("&"))
                        .map(p->p.split("=",2)).filter(p->p.length==2)
                        .collect(Collectors.toMap(p->p[0],p->java.net.URLDecoder.decode(p[1],StandardCharsets.UTF_8),(a,b)->a));
                    boolean ok=SpotifyService.verifyState(params.getOrDefault("state","")) && params.containsKey("code");
                    String body=ok?"Spotify connected. You can return to Minecraft.":"Spotify authorization failed. Return to Minecraft and try again.";
                    byte[] bytes=body.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type","text/plain; charset=utf-8");
                    exchange.sendResponseHeaders(ok?200:400,bytes.length);
                    try(var output=exchange.getResponseBody()){output.write(bytes);}
                    if(ok) SpotifyService.exchangeCode(config,params.get("code"));
                });
                server.start(); serverStarted=true;
            }
            URI uri=URI.create(SpotifyService.authorizationUrl(config));
            if(Desktop.isDesktopSupported()) Desktop.getDesktop().browse(uri);
            else SpotifyService.status="Browser unavailable; open the Spotify login URL manually";
        } catch(Exception e) { SpotifyService.status="Connect error: "+e.getMessage(); }
    }
    public static int accent() { return color(); }
}
