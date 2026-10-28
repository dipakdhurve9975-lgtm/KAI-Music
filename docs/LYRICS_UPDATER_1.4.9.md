# KAI 1.4.9 verification — 29 October 2026

## Publication and configuration update

The owner subsequently approved publication. [KAI 1.4.9 Preview](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/tag/kai-preview-1.4.9) is public and installed. All eight assets matched local hashes/sizes; anonymous APK/source/metadata checks returned HTTP 200. README automation successfully updated the top download to 1.4.9. The exact release source/tag is `4ad38803774dde7f325ce6335759c00fa5e74ce8`; later documentation does not change that APK.

Future preview automation is now configured: the existing matching preview key is stored only as an encrypted secret in the `kai-release` environment, the owner is its required reviewer, certificate/previous-code170 variables are configured, and preview automation is enabled. No replacement key was generated or committed. Stable automation remains disabled. A future genuine release is still needed to exercise the complete hosted signing/newer-version installer path. [Android CI for the released source](https://github.com/dipakdhurve9975-lgtm/KAI-Music/actions/runs/40780153006) subsequently completed successfully; the hosted pass was verified before handoff.

The remaining text records the candidate implementation before that approval.

KAI 1.4.9 (170) is built and installed on the connected A001. It is a Universal GMS debug preview candidate. New APK publication requires the owner's final review under phase 40 of the attached brief. README download-position and Instagram-removal changes are already public.

## Lyrics: actual cause and repair

Device diagnostics reproduced visible=true, lifecycle=RESUMED, advancing=true, STATE_READY and no seek preview. Actual player positions advanced from 51134ms to 86355ms while the lyric sample stayed at 46050ms. An earlier reproduction froze at 11435ms while transport passed 20 seconds.

Original Echo Music reads the live player position inside its lyric coroutine, adds the saved offset, calculates the active timestamp index and scrolls on line changes. KAI extracted sampling into a lifecycle-aware clock. Its retained local `sample` callback captured the initial false visibility before asynchronous lyrics became timed. Updating the playback-state gate did not refresh that captured visibility.

`rememberLyricsClockSampler` explicitly retains current State holders for enabled/resumed state, preview, live position provider and consumer. The clock samples immediately when visibility changes. Genuine timestamps, original index/scroll behavior, providers, offsets, original-script text, fonts and five presentation styles remain. No synthetic timing or unrelated whole-file replacement was used.

Relevant files:

- `app/src/main/kotlin/com/music/echo/ui/component/LyricsPlaybackClock.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/LyricsClockSampler.kt`
- `app/src/test/kotlin/echo/music/iad1tya/ui/component/LyricsClockSamplerTest.kt`

The original supplied Echo source and official repository were inspected as the regression reference.

## Phone verification

- English: Shape of You advanced through real timestamps 9731, 12105, 14915, 17088, 19533, 22315, 24665 and beyond. Rendered active lines and scroll positions changed accordingly.
- Devanagari/Sanskrit with Romanized secondary text: A Modern Mantra advanced at 85600ms and 97330ms. Shaped glyphs and scrolling were inspected. This is not a claim that every Hindi provider was tested.
- Pause held the transport at 2:33. A paused seek immediately selected index 62 at 153947ms. Backward seek, resume and Normal → Lyrics switching continued following playback.
- On foreground return, lyrics caught up from the previous 115484ms sample to 171019ms and continued. Track changes were also observed.
- Final installation used `adb install -r`. Package/version 170 were confirmed. All 137 previous song IDs and the saved favourite survived; playback testing added three entries. No playlists existed in the baseline, so a populated-playlist upgrade was not manually established. No uninstall or storage clearing occurred.

## KAI-only updater

One Hilt-scoped manager owns asynchronous Ktor checks/downloads. Successful checks are throttled for six hours and retries for thirty minutes; manual checks bypass the throttle. Dismissed versions are saved. Network/API failures leave the app usable.

Only KAI GitHub Releases are queried. Stable is the default; previews require opt-in. Selection uses numeric versionCode and rejects drafts, foreign URLs, equal/older versions and incompatible package/signer/Android/ABI metadata. Exact repository asset URLs and HTTPS GitHub asset redirects are validated. No access token is embedded.

The optional dialog displays actual release notes and installed/available versions. Downloads require a tap, stream to private updater cache, and provide size/progress/cancel/retry. Actual APK byte count, SHA-256, package, newer versionCode, minSDK, ABI set and current signer are checked before installation. The installed signing certificate is an additional authenticity anchor; a checksum from the same server is not independent authentication. Key rotation is intentionally rejected pending a separately reviewed migration.

A narrow, non-exported FileProvider grants Android temporary access to the updater cache. Unknown-source permission and installation confirmation remain user-controlled. Permission state refreshes after returning from Android Settings. Operations are serialized and repeated taps do not create duplicate transfers/install jobs. Legacy Echo workers remain no-ops.

The phone rendered Update Settings and completed a live manual check, correctly reporting no newer compatible release. Preview opt-in was saved to disk, survived leaving/reopening Settings, and was restored to stable-only after testing. A future real in-app download/installer round trip requires an approved newer public release; none was fabricated for this test.

The old initializer also reset Force Opus/Cronet choices on every version change. It now initializes absent defaults once and preserves explicit saved choices. Two regression tests cover this. Database schema, package/storage identifiers and signing identity remain unchanged.

Main components are `updates/KaiUpdatePolicy`, `KaiUpdateRepository`, `KaiUpdateManager`, `KaiApkVerifier`, `KaiUpdateUi` and `KaiUpdateFileProvider`, plus the existing Update Settings/initializer integrations. New strings and a scoped provider-path XML accompany them. The only added dependency is test-only Ktor MockEngine.

## Release workflow and compatibility

`prepare_release.py` generates `release-metadata.json` from the inspected signed APK and exact committed source: schema, application ID, numeric code, name/channel, minSDK, actual ABIs, variant, APK filename/hash, signer hash and source commit. Metadata, matching source and build scripts join the checksummed release bundle.

The existing workflow creates drafts by default, refuses existing releases, and requires the existing signing key/fingerprint and automation gate. After first-publication approval and repository/environment configuration, an explicitly approved manual dispatch with `publish_release=true` can validate/upload/publish in one run. Ordinary commits and tag-triggered preparations do not publish APKs. README automation updates its top download block after a real release is published.

Existing 1.4.8 has no KAI updater and needs one manual compatible installation of 1.4.9. Preview users must opt into previews for future preview updates. The separate production application ID cannot silently replace the debug preview. Hosted preview signing setup was completed after approval as recorded above; never generate a replacement key as a substitute.

## About and README

The README download section is directly below its introduction. Creator/project GitHub credits remain; Instagram is only inside About. KAI owner/friend/support links, Echo Music/Aditya/contributor credits, original notices and GPL text remain. No new fonts, vinyl renderer, playback service or navigation framework were introduced.

Original project: [Echo Music](https://github.com/EchoMusicApp/Echo-Music). Original creator: [Aditya](https://github.com/iad1tya). The creator Instagram remains verified and unchanged in `KaiProject.ORIGINAL_INSTAGRAM`.

Until approval/publication, the working public download remains 1.4.8. No future or nonexistent stable URL is advertised.

## Results and artifacts

- Final Universal GMS public/debug build: **BUILD SUCCESSFUL**.
- Android tests: **230 passed**, zero failures/errors/skips: app 189, core 17, lyrics 17, YouLyPlus 7.
- Release-tool tests: **15 passed**. Total: **245**.
- Modified release workflow passes actionlint. Full legacy Android lint was not rerun; inherited compiler/D8 metadata warnings remain.
- Identity: `echo.music.iad1tya.debug`, version 1.4.9/code 170, minSDK 26, targetSDK 36, ARM64/ARMv7/x86/x86_64.
- Signer SHA-256: `1885a38ee90bee0d2b4af586c971ef270842397a190b23982c20e2fe3a94d52a`, matching distributed 1.4.8.
- Final local APK: workspace root `KAI-final-debug.apk`; Gradle output `app/build/outputs/apk/universalGms/debug/app-universal-gms-debug.apk`.
- Local bundle: `kai-checks/releases/1.4.9`. Its generated checksums and metadata record actual artifact hashes/source revision.
- No hosted CI pass is claimed for unpublished 1.4.9 source. Baseline 1.4.8 infrastructure CI run 40734457934 passed.

## Remaining limits

This is not a stable-release or blanket security/legal clearance. Existing artwork/native FFmpeg/GMS/transitive-license reviews, production signing decisions, broader device regression, Cast and every provider/style/locale remain open. Original GPL/notices and corresponding source/build scripts are retained. A populated-playlist upgrade and a future real in-app installer upgrade were not manually tested in this run.
