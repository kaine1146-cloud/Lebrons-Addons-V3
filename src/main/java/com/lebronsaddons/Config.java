package com.lebronsaddons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lebronsaddons.json");
    public boolean christmasMode = false;
    public boolean showHud = true;
    public boolean menuSounds = true;
    public boolean backgroundBlur = true;
    public int menuColour = 0;
    public int hudX = 14, hudY = 14;
    public int hudScale = 100; // 60 to 180 percent
    public int lyricsMode = 0; // 0 off, 1 front, 2 sky
    public int skyTheme = 0; // default, sunset, aurora, stars, cotton candy, stormy
    public int menuVolume = 70;
    public String spotifyClientId = "";
    public String spotifyRefreshToken = "";
    public static Config load() {
        try { if (Files.exists(FILE)) return GSON.fromJson(Files.readString(FILE), Config.class); }
        catch (Exception e) { System.err.println("Lebron's Addons config read failed: " + e); }
        return new Config();
    }
    public void save() {
        try { Files.createDirectories(FILE.getParent()); Files.writeString(FILE, GSON.toJson(this)); }
        catch (Exception e) { System.err.println("Lebron's Addons config save failed: " + e); }
    }
}
