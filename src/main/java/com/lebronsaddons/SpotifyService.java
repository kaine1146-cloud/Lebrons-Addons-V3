package com.lebronsaddons;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public final class SpotifyService {
    public record Track(String title, String artist, String album, String artworkUrl, long positionMs, long durationMs, boolean playing, long updatedAt) {}
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final SecureRandom RNG = new SecureRandom();
    public static volatile Track current;
    public static volatile String status = "Not connected";
    private static volatile String accessToken = "";
    private static volatile long expiry = 0;
    private static volatile boolean busy = false;
    private static volatile long lastPoll = 0;
    private static volatile String verifier = "";
    private static volatile String state = "";
    private static final String REDIRECT = "http://127.0.0.1:8765/callback";

    private static String form(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    private static String random() { byte[] b = new byte[32]; RNG.nextBytes(b); return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    public static String authorizationUrl(Config config) throws Exception {
        if (config.spotifyClientId.isBlank()) throw new IllegalStateException("Set spotifyClientId in config first");
        verifier = random(); state = random();
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII));
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        return "https://accounts.spotify.com/authorize?response_type=code&client_id=" + form(config.spotifyClientId)
            + "&redirect_uri=" + form(REDIRECT) + "&scope=" + form("user-read-currently-playing user-read-playback-state")
            + "&code_challenge_method=S256&code_challenge=" + form(challenge) + "&state=" + form(state);
    }
    public static boolean verifyState(String s) { return !state.isBlank() && state.equals(s); }
    public static void exchangeCode(Config cfg, String code) {
        CompletableFuture.runAsync(() -> {
            try { tokenRequest(cfg, "grant_type=authorization_code&code=" + form(code) + "&redirect_uri=" + form(REDIRECT) + "&code_verifier=" + form(verifier)); }
            catch (Exception e) { status = "Spotify login failed: " + e.getMessage(); }
        });
    }
    private static void tokenRequest(Config cfg, String params) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.spotify.com/api/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString("client_id=" + form(cfg.spotifyClientId) + "&" + params)).build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String detail = "";
            try {
                JsonObject err = JsonParser.parseString(response.body()).getAsJsonObject();
                if (err.has("error_description")) detail = err.get("error_description").getAsString();
                else if (err.has("error")) detail = err.get("error").toString();
            } catch (Exception ignored) {}
            throw new IllegalStateException("Token HTTP " + response.statusCode() + (detail.isBlank() ? "" : ": " + detail));
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        accessToken = json.get("access_token").getAsString();
        expiry = System.currentTimeMillis() + json.get("expires_in").getAsLong() * 1000 - 60000;
        if (json.has("refresh_token")) { cfg.spotifyRefreshToken = json.get("refresh_token").getAsString(); cfg.save(); }
        status = "Spotify connected";
    }
    public static void tick(Config cfg) {
        long now = System.currentTimeMillis();
        if (busy || now - lastPoll < 3500 || cfg.spotifyClientId.isBlank()) return;
        lastPoll = now; busy = true;
        CompletableFuture.runAsync(() -> {
            try {
                if (accessToken.isBlank() || System.currentTimeMillis() > expiry) {
                    if (cfg.spotifyRefreshToken.isBlank()) { status = "Connect Spotify in settings"; return; }
                    tokenRequest(cfg, "grant_type=refresh_token&refresh_token=" + form(cfg.spotifyRefreshToken));
                }
                HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.spotify.com/v1/me/player/currently-playing"))
                    .header("Authorization", "Bearer " + accessToken).GET().build();
                HttpResponse<String> response = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204) { current = null; status = "Nothing playing"; return; }
                if (response.statusCode() == 401) { accessToken = ""; expiry = 0; status = "Spotify session expired; reconnect"; return; }
                if (response.statusCode() == 403) { current = null; status = "Spotify access denied (check app access / account)"; return; }
                if (response.statusCode() == 429) { status = "Spotify rate limited; try later"; lastPoll = System.currentTimeMillis() + 20000; return; }
                if (response.statusCode() != 200) { status = "Spotify HTTP " + response.statusCode(); return; }
                JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
                if (!data.has("item") || data.get("item").isJsonNull()) { current = null; return; }
                JsonObject item = data.getAsJsonObject("item");
                String title = item.get("name").getAsString();
                String artist = item.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
                JsonObject album = item.getAsJsonObject("album");
                String artwork = album.getAsJsonArray("images").isEmpty() ? "" : album.getAsJsonArray("images").get(0).getAsJsonObject().get("url").getAsString();
                current = new Track(title, artist, album.get("name").getAsString(), artwork,
                    (data.has("progress_ms") && !data.get("progress_ms").isJsonNull() ? data.get("progress_ms").getAsLong() : 0), item.get("duration_ms").getAsLong(),
                    data.get("is_playing").getAsBoolean(), System.currentTimeMillis());
                status = "Spotify detected";
            } catch (Exception e) { status = "Spotify error: " + e.getClass().getSimpleName() + (e.getMessage() == null ? "" : " - " + e.getMessage()); }
            finally { busy = false; }
        });
    }
}
