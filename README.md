# Lebron's Addons — v0.4 (source prototype)

This is a **source-code prototype**, not a compiled or verified Fabric mod. It continues the user's v0.3 project and uses the six provided PNG mockups as design references.

## Source features
- **L** keybind opens the three-tab menu; pause-menu shortcut is included as a mixin.
- Spotify Web API authorization-code PKCE login and token refresh; playback polling displays track name, artist, album artwork, progress and duration.
- New in v0.4: album artwork downloaded asynchronously from Spotify's image CDN, decoded and registered as a Minecraft texture. A fallback icon appears until loaded.
- HUD position can be dragged and saved, HUD visibility can be toggled.
- Local `.lrc` synchronized lyrics; two HUD placements, one near the bottom and one near the upper third of the screen. Neither is an actual 3D world projection.
- Six sky-theme **selectors**; these do not yet change the Minecraft sky.
- Saved menu colour, Christmas Mode (directly below Menu Colour), menu-sounds, volume and background-blur preferences. Christmas Mode recolours the HUD and menu accents. Sound and volume preferences are not yet wired to custom sound playback.

## Critical limitations
The previous project uses provisional Minecraft `26.2`, Yarn, Fabric Loader, and Fabric API dependency coordinates. **They are not confirmed to exist or be compatible.** Minecraft 26.2 and its mapping/API availability must be verified. There is no Gradle wrapper and the code has not been compiled or run in Minecraft. APIs such as `drawTexture`, `NativeImageBackedTexture`, and GUI callbacks may require migration for the target version.

The menu is a functional prototype, **not a pixel-perfect match** for the six supplied mockups. No live sky shaders, automatic licensed lyrics provider, 3D lyrics, custom sound effects, or tested blur effect have been implemented. Do not publish this as a finished mod.

## Spotify setup
1. Register your own Spotify developer app (if Spotify currently permits it), with callback `http://127.0.0.1:8765/callback`.
2. Set `spotifyClientId` in `.minecraft/config/lebronsaddons.json` after launching once. **Never put a Spotify client secret in the mod.**
3. Press L, then **Connect Spotify**, and authorize in your browser. Spotify API permissions and account eligibility can affect playback retrieval.
4. The refresh token is saved locally in the config; do not share that config publicly.

Spotify's playback API does not supply synchronized lyrics. For locally sourced lyrics, add a legally obtained `.lrc` file under `.minecraft/config/lebronsaddons/lyrics/Artist - Song.lrc`.

## Building
Once valid Fabric version coordinates are identified and the source has been updated to the correct API/mappings, install the matching JDK and Gradle, run `gradle build`, and install the resulting `.jar` in the Modrinth App profile for that exact Minecraft/Fabric version. A ZIP of source files is **not** an installable mod.
