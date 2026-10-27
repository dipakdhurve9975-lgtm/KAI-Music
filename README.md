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

## Source and license

KAI is a modified fork of the original [Echo Music](https://github.com/EchoMusicApp/Echo-Music) source. The original contributor credits and third-party notices below are retained. The source is provided under [GPL-3.0](LICENSE); bundled components retain their respective notices and licenses. The repository distributes application code and its APK, not users' libraries, cached lyrics, accounts, or music files.

## Contributors

Without the support of this incredible open-source community, none of this would be possible. The original Echo Music contributors are credited below; KAI retains their source notices and GPL license.

<!-- readme: contributors -start -->
<table>
<tr><td align="center"><a href="https://github.com/iad1tya"><img src="https://avatars.githubusercontent.com/u/147871321?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/mdakashhossain1"><img src="https://avatars.githubusercontent.com/u/85729564?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Roshan-aa11"><img src="https://avatars.githubusercontent.com/u/192568043?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/gerry02"><img src="https://avatars.githubusercontent.com/u/33490327?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/VardhmanSurana"><img src="https://avatars.githubusercontent.com/u/100058534?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Rajendra0309"><img src="https://avatars.githubusercontent.com/u/103703747?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/pranav6004"><img src="https://avatars.githubusercontent.com/u/156088278?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Mickael81"><img src="https://avatars.githubusercontent.com/u/184148670?v=4" width="60" height="60" /></a></td></tr>
<tr><td align="center"><a href="https://github.com/soumya-99"><img src="https://avatars.githubusercontent.com/u/59480692?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/kaunkrishna"><img src="https://avatars.githubusercontent.com/u/270065466?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Berserkr2k"><img src="https://avatars.githubusercontent.com/u/127702589?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/andrewvalletta"><img src="https://avatars.githubusercontent.com/u/137303110?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/bivekk51"><img src="https://avatars.githubusercontent.com/u/115235822?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/DennyHo0917"><img src="https://avatars.githubusercontent.com/u/149746199?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Felixkw12"><img src="https://avatars.githubusercontent.com/u/151852644?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/pranavxdevops"><img src="https://avatars.githubusercontent.com/u/110167539?v=4" width="60" height="60" /></a></td></tr>
<tr><td align="center"><a href="https://github.com/TanvirSdq"><img src="https://avatars.githubusercontent.com/u/112406045?v=4" width="60" height="60" /></a></td><td align="center"><a href="https://github.com/Thibaultjaigu"><img src="https://avatars.githubusercontent.com/u/84420566?v=4" width="60" height="60" /></a></td></tr>
</table>
<!-- readme: contributors -end -->


---

## Special Thanks

Echo Music stands on the shoulders of several excellent open-source projects. Sincere thanks to:

| Project                                                                                                                   | Description                                         |
| :------------------------------------------------------------------------------------------------------------------------ | :-------------------------------------------------- |
| **[Compose Floating Tab Bar](https://github.com/elyesmansour/compose-floating-tab-bar)** | Existing Apache-2.0 vendored collapsing tab/accessory layout, extended with a shared expanded KAI liquid navigation slot while retaining its optional compact presentation |
| **[Backdrop](https://github.com/Kyant0/backdrop)** | Existing vendored Apache-2.0 backdrop rendering, reused for KAI's player selector lens with a separately recorded player background, limited refraction and opaque fallback |
| **[Plus Jakarta Sans](https://github.com/tokotype/PlusJakartaSans)** | Bundled font used as KAI's default typography; SIL Open Font License included in `app/src/main/assets/licenses/plus-jakarta-sans-OFL.txt` |
| **[Metrolist](https://github.com/MetrolistGroup/Metrolist)** & **[Vivi Music](https://github.com/vivizzz007/vivi-music)** | Foundational inspiration and architecture reference |
| **[ArchiveTune](https://github.com/koiverse/ArchiveTune)**                                                                | Material You UI inspiration                         |
| **[Better Lyrics](https://better-lyrics.boidu.dev/)**                                                                     | Lyrics enhancement and synchronization              |
| **[SimpMusic](https://github.com/maxrave-dev/SimpMusic)**                                                                 | Lyrics implementation reference                     |
| **[Music Recognizer](https://github.com/aleksey-saenko/MusicRecognizer)**                                                 | Audio recognition (Echo Find)                       |
| **[BravePipe](https://github.com/bravepipeproject/BravePipe)**                                                            | Decryption handling and backup playback engine      |

---
