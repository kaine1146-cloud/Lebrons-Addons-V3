package com.lebronsaddons;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** Local .lrc lyrics only; Spotify's playback API does not provide lyrics. */
public final class LyricsService {
    private record Line(long ms,String text) {}
    private static final Pattern TIMESTAMP=Pattern.compile("\\[(\\d+):(\\d{2})(?:\\.(\\d{1,3}))?\\]");
    private static String cachedKey="";
    private static List<Line> lines=List.of();
    public static String[] current(SpotifyService.Track track,long elapsed) {
        if(track==null)return new String[]{"","",""};
        String key=track.artist()+" - "+track.title();
        if(!key.equals(cachedKey)){
            cachedKey=key;
            String safe=key.replaceAll("[\\\\/:*?\"<>|]","_");
            Path file=FabricLoader.getInstance().getConfigDir().resolve("lebronsaddons/lyrics").resolve(safe+".lrc");
            ArrayList<Line> parsed=new ArrayList<>();
            try {
                for(String row:Files.readAllLines(file)){
                    Matcher matcher=TIMESTAMP.matcher(row);
                    while(matcher.find()){
                        long millis=(Long.parseLong(matcher.group(1))*60+Long.parseLong(matcher.group(2)))*1000;
                        if(matcher.group(3)!=null)millis+=Long.parseLong((matcher.group(3)+"00").substring(0,3));
                        String lyric=row.substring(matcher.end()).trim();
                        if(!lyric.isBlank())parsed.add(new Line(millis,lyric));
                    }
                }
            }catch(Exception ignored){}
            parsed.sort(Comparator.comparingLong(Line::ms));lines=List.copyOf(parsed);
        }
        int idx=-1;
        for(int i=0;i<lines.size();i++){if(lines.get(i).ms<=elapsed)idx=i;else break;}
        return new String[]{idx>0?lines.get(idx-1).text:"",idx>=0?lines.get(idx).text:"",idx+1<lines.size()?lines.get(idx+1).text:""};
    }
}
