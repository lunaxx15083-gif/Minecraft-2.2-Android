# Voxel PE — Android 2.2 starter project

The uploaded archive has been inspected. It contains an APK-style package, not the original editable source project.

## Imported resources
Textures and GUI resources from the supplied archive are included under `app/src/main/assets/minecraft/` (including `terrain.png`, `gui/`, `mob/`, `font/`, and `particles.png`). PNG drawable resources are also copied into `app/src/main/res/drawable/`.

The archive's `classes.dex` is compiled Dalvik bytecode and `lib/armeabi-v7a/libminecraftpe.so` is a compiled native library. Neither is the original Java/C++ source code. The project therefore cannot directly recover the original game logic from this ZIP.

## Prototype status
This is an original, minimal OpenGL ES 1.0 voxel prototype with Android `minSdkVersion 8`, basic movement/look controls, and imported texture assets. The current renderer is not yet wired to draw the imported Minecraft texture atlas. This is not a complete Minecraft PE feature clone: inventory, crafting, mobs, multiplayer, world saving, survival systems, and other gameplay systems remain to be implemented.

## Build
Open the project in Android Studio with a compatible Android Gradle Plugin and SDK. `minSdkVersion 8` targets Android 2.2, but modern build tools may require compatibility adjustments.
