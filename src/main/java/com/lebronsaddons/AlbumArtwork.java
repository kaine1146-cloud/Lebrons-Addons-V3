package com.lebronsaddons;
import net.minecraft.resources.Identifier;
/** Album art loading needs migration to the Minecraft 26.2 GPU texture API.
 * Spotify titles, artists, progress and lyrics still work without album artwork. */
public final class AlbumArtwork {
    private AlbumArtwork() {}
    public static Identifier get(String url) { return null; }
}
