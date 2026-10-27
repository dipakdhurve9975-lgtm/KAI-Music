# KAI Music

KAI is an Android music player with a clean interface, a shared Vinyl / Normal / Lyrics player, immersive Ambient Normal / Vinyl, local-library features and synchronized lyrics when real timing is available.

## Download

[**Download the KAI preview APK**](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/download/kai-preview-2026.10.27/KAI-universal-gms-preview.apk)

[All releases and release notes](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases)

Download the `.apk` asset on Android, open it and permit installation from the browser/file manager when Android asks. This initial distribution is a **debug-signed preview** of the complete Universal GMS build, not a production-signed store release. It includes arm64-v8a, armeabi-v7a, x86 and x86_64. The minimum Android version is 8.0 (API 26).

## Features

- Music playback, queues, background audio, downloads and existing local collections.
- Vinyl, artwork and lyrics presentations using one audio session.
- Ambient artwork/vinyl with remembered presentation.
- Five lyric styles; original text, optional Romanization/translation, and per-song timing offset/reset.
- Immediate root navigation with a draggable selector; Home dock hide/reveal while scrolling.
- Original K/music-note branding and lifecycle-aware spiral loading.

Lyrics depend on the available provider data. Untimed text is shown honestly as static lyrics; KAI does not invent line or word timestamps. Existing provider options remain available in settings and the lyrics menu.

## Build from source

Use JDK 21 and Android SDK 37 with NDK 28.2.13676358. Set `ANDROID_HOME` to your SDK location or use a local `local.properties` based on the supplied template.

```sh
git clone https://github.com/dipakdhurve9975-lgtm/KAI-Music.git
cd KAI-Music
./gradlew :app:assembleUniversalGmsDebug
```

The APK is generated under `app/build/outputs/apk/universalGms/debug/`. On Windows, use `gradlew.bat`. Personal signing keys, local SDK paths and Firebase configuration are excluded from this public repository; Google-services plugins are applied only when the optional configuration exists. Production signing requires the owner's own private key.

## Developer and support

- [GitHub — dipakdhurve9975-lgtm](https://github.com/dipakdhurve9975-lgtm)
- [Instagram — @dipak.s.dhurve](https://www.instagram.com/dipak.s.dhurve/)
- [Buy Me a Coffee — deku2666](https://buymeacoffee.com/deku2666)
- UPI: `8999575447@apl`

## License

[GPL-3.0](LICENSE). Source and third-party notices are available in [NOTICE.md](NOTICE.md).
