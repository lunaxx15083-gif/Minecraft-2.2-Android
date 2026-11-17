# Voxel PE — incremental prototype update

This patch adds a world-selection screen, a create-world dialog, persistent world metadata, and seed-based starter terrain. It deliberately contains **only changed/new source and configuration files**; it does not include or duplicate the texture assets.

## ARM compatibility

The current prototype contains no native `.so` code. It uses Android Java APIs and OpenGL ES 1.0, so the APK is not restricted by an `abiFilters` setting and can run on supported Android ARM devices without shipping separate native binaries.

- Android 2.2 (API 8): legacy 32-bit ARM devices such as ARMv6/ARMv7, provided the device has a compatible Dalvik runtime and OpenGL ES 1.0 driver.
- Modern 32-bit ARM: supported by the Java-only app on compatible Android versions.
- ARM64 (arm64-v8a): supported by the Java-only app on modern Android versions. Android 2.2 itself cannot run on ARM64-era devices as a 64-bit OS; the OS/API level determines runtime compatibility.

This is **not** a claim that one Android 2.2 installation supports every ARM generation. Android 2.2 is a 32-bit operating system. No native ARM binaries are included, so there is currently no NDK ABI matrix to build. If native engine code is added later, separate builds will be needed: `armeabi`/ARMv6 for legacy API-8 devices (using an older compatible NDK toolchain), `armeabi-v7a` for 32-bit ARMv7, and `arm64-v8a` for modern 64-bit ARM (minimum Android API 21).

## Current prototype features

- World list stored in the app's private files directory.
- Create a world with a name, optional numeric seed, and Survival/Creative selection.
- Seed-based starter terrain generation.
- OpenGL ES 1.0 rendering and simple touch controls.

World creation currently stores world metadata and generates a simple terrain preview/game scene. It is not yet a full Minecraft-equivalent engine: block editing, chunks, inventory, crafting, mobs, saving modified blocks, multiplayer, and survival mechanics remain future work.

## Build notes

The project uses Android Gradle Plugin 8.2.2 and compile SDK 34 for modern build environments while keeping `minSdkVersion 8` in the manifest. Very old Android 2.2 devices cannot build this project locally; build the APK on a modern computer and install/test it on real legacy hardware. Actual API-8 installation must be verified with a built APK and device testing.
