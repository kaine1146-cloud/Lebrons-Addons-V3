# Lebron's Addons — visual update (source, unverified)

- Redesigned settings dashboard using rounded dark panels, green/accent highlight, sidebar, theme thumbnails and lyric previews based on the supplied six design images. Vanilla Minecraft buttons remain the interactive controls; fully custom animated buttons require a separate widget implementation.
- Moved pause menu button down 25 GUI pixels.
- Added HUD size controls (60–180%) to Move / Resize HUD screen. The HUD uses scaled coordinates and a compact rounded design.
- Fixed keybinding import to `net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper` (the import used in the successful build).

## Still requires work
- Spotify requires the user's own Spotify developer app Client ID, set in `config/lebronsaddons.json` as `spotifyClientId`. Spotify's redirect URI must be configured as `http://127.0.0.1:8765/callback` in the app settings. Restart the game after changing the file. Never share tokens or credentials.
- Sky themes currently provide selection and previews, **not a world sky renderer**. Implementing actual world sky effects requires an appropriate 26.2 world rendering hook and testing with shaders.
- Album artwork is still a placeholder.
- Menu sounds and blur settings remain stored preferences; this update does not implement new audio/blur effects.
- This source has not been compiled in this environment. Test in GitHub Actions before installing a new JAR.
