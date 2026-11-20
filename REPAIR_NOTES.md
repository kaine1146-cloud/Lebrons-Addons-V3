# 26.2 migration attempt — UNVERIFIED

Updated the mixed Yarn/Mojang UI code to 26.2-style names and APIs. Fixed the pause-screen mixin target, migrated GUI drawing to GuiGraphicsExtractor, moved screen access to Minecraft.gui, and replaced the obsolete HUD callback. Spotify OAuth and polling are retained.

**Temporary limitations:** Album cover art is a placeholder until its GPU texture path is migrated. HUD positioning uses arrow buttons rather than dragging. Sky theme selection remains a saved setting; the original project did not implement actual sky rendering. Key binding uses SDL scancode 15 (L), subject to in-game testing.

**Not tested:** This environment has Java 21 and cannot run the required Java 25 Fabric build. Run GitHub Actions, inspect the FIRST compiler error if the build fails, and fix remaining 26.2 API differences before installing. Do not describe this ZIP as a confirmed working build.
