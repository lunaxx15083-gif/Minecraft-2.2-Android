# Minecraft 2.2 Android / Voxel PE

[![Android CI](https://github.com/lunaxx15083-gif/Minecraft-2.2-Android/actions/workflows/android.yml/badge.svg)](https://github.com/lunaxx15083-gif/Minecraft-2.2-Android/actions/workflows/android.yml)

Prototype Android project. The badge above links to the GitHub Actions build workflow.

## Build

Push to `main` or `master`, open a pull request targeting either branch, or run **Android CI** manually from the Actions tab. The workflow uses JDK 17 and Gradle 8.2.1 and builds `:app:assembleDebug`.

The repository does not include a Gradle wrapper, so CI installs and invokes the pinned Gradle version directly.

## Compatibility note

The app declares Android API 8 as its minimum SDK and uses Java/OpenGL ES 1.0 APIs. This does not guarantee that every device or ARM processor will run it: actual installation and rendering must be tested on hardware. The current Java-only app has no native ABI-specific `.so` libraries.
