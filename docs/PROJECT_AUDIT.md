# KAI project audit — 28 October 2026

## Findings before implementation

KAI is a modified Echo Music 1.4.4 source snapshot, not an application written from scratch. The original downloaded `Echo-Music-main.zip` has no Git metadata. Its LICENSE is byte-identical to the local GPL v3 license. A comparison before this task found 214 changed existing files; added KAI files and checkpoint inventories provide further evidence. Differences do not establish individual authorship of every line. Upstream main has continued changing and must not replace this checkout.

The app is 1.4.7 (168). Its 23 Gradle modules include app, core, playback, lyrics, six lyric providers, InnerTube, recognition, video/canvas, USB/audio DSP and domain utilities. Compose/Material 3 screens use Hilt, ViewModels, Room and DataStore. Media3/PlayerConnection owns playback. About is a LazyColumn using shared KAI header/community components. Navigation routes are retained in NavigationBuilder. KAI adds vinyl, liquid controls, Ambient Normal/Vinyl, five lyric presentations, typography, motion, home/dock polish and updater retirement. Inherited streaming, library, imports and recognition are not original KAI inventions.

Build: JDK 21, Gradle 9.3.1, AGP 9.0.0, compile SDK 37.0, target 36, minimum 26, NDK 28.2.13676358. ABI flavors: universal, arm64, armeabi, x86, x86_64. Service flavors: GMS and FOSS. Only Universal GMS debug has been distributed and verified here. FOSS configuration is not certification that every dependency is free software. Debug application ID is `echo.music.iad1tya.debug`; release is `echo.music.iad1tya`. Existing preferences, database and signing identities must remain intact.

The editing folder has no Git history. The publication checkout has origin `dipakdhurve9975-lgtm/KAI-Music`, default branch main, initially at 70976f7. Only README and two assets are tracked at its tip. Full modified source is supplied in versioned release ZIPs. Existing tags point to documentation commits, not actual source revisions: the attached source ZIP is essential for historical builds. All four public releases are previews. The 1.4.7 APK/source exist; APK SHA-256 is `8ba1dceb7aba75e2600bff12fd13529f96f9392a5d4c03d69954c0be6536fea5`. No stable release exists; do not advertise a stable latest-asset link yet.

Inherited CI has no release uploader, builds a FOSS fallback despite the Universal GMS rule, and generates ephemeral debug identities. The contributors workflow expects absent README markers. Dormant changelog/commit routes retain upstream release URLs, although live startup update checks are retired.

## Attribution evidence

- [Original repository](https://github.com/EchoMusicApp/Echo-Music) declares GPL-3.0 and credits contributors.
- [Aditya's GitHub](https://github.com/iad1tya) names Aditya, pins Echo Music and links `https://instagram.com/iad1tya`.
- [Personal site](https://iad1tya.cyou/) identifies Echo Music as his project. Original About source uses the same Instagram. Ownership is verified from these first-party links; Instagram throttled direct fetching, so availability is not asserted.
- Upstream inspected at `de8a2733b71c40d2fdb2cb721232738f07ab870b`. Contributors API lists Aditya and 17 other human contributors, plus two bots. Preserve their notices; do not attribute KAI changes to them.

## Plan and risks

1. Private checkpoint completed before edits. Preserve KAI features and technical identities.
2. Extend About with separate origins, original creator, maintainer and offline licenses. Retain owner/friend links. No network fetch on entry.
3. Restore GPL/third-party documentation and dated modification notices. Replace an inherited font explicitly marked non-open-source with its official OFL edition, preserving resource IDs. Include missing font/Apache licenses. Flag opaque artwork/native/proprietary obligations without certifying them.
4. Prepare sanitized source on a local review branch in the existing publication checkout. Keep origin; add a separate upstream fetch remote. No history rewrite or secrets/keys/QA data.
5. Preserve working preview downloads. Generate download sections using the releases API. Future stable builds use `KAI-latest.apk`; no stable link until that asset exists.
6. Replace inherited CI with tests/build and explicit tag/manual draft releases validating versions, signatures, source revision, metadata and checksums. No automatic OTA.
7. Build Universal GMS debug, run tests, inspect diff and secret scan, and provide review before push, metadata updates or publication.

No ADB device was attached at audit time. Runtime checks require a device; compilation is not runtime evidence. Live lyric progression remains pending from 1.4.7.
