# Spotify setup — Lebron's Addons

The mod already uses Spotify Authorization Code + PKCE. It cannot connect without a Spotify app Client ID, and it cannot automatically create that ID for you.

1. If eligible, create an app at https://developer.spotify.com/dashboard.
2. Add the exact redirect URI `http://127.0.0.1:8765/callback` to the app settings.
3. Copy **Client ID**, not Client Secret.
4. Close Minecraft. Open your Modrinth instance's folder, then `config/lebronsaddons.json`.
5. Set `"spotifyClientId": "YOUR_CLIENT_ID_HERE"` (keep quotation marks), save, then relaunch Minecraft.
6. Click **Connect Spotify** and approve the permissions in your browser.
7. Start playing a song on Spotify. The HUD polls the currently playing endpoint about every 3.5 seconds.

Do not share your refresh token from the config file or a Spotify Client Secret. Spotify developer API access and endpoints may be restricted for some accounts/apps; a successful mod build cannot override Spotify restrictions.

## Troubleshooting
- `Set spotifyClientId in config first`: Client ID missing.
- `Spotify HTTP 403`: Spotify denied API access; check app eligibility and permissions.
- `Nothing playing`: Start Spotify playback and check the authorized account.
- `Token HTTP 400`: Check Client ID and registered redirect URI, then reconnect.
