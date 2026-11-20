# Build status — IMPORTANT

This package updates Gradle and Fabric metadata to the Minecraft 26.2 versions shown on the Fabric developer page (Loader 0.20.1, API 0.161.0+26.2, Loom 1.19-SNAPSHOT, Java 25).

**It is NOT a verified working mod.** The Java source still uses pre-26.2 Yarn-style names (for example `MinecraftClient`, `DrawContext`, `Text`) and the previous GitHub build reported 100 compilation errors. Those source errors have NOT been fixed in this package. Do not expect a green build from this ZIP alone.

No new GitHub repository is required. Replace existing repository files with the updated files. Keep the Actions workflow using Java 25.
