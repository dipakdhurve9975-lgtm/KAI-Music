# KAI 1.4.10 update-popup and About follow-up

Version 1.4.10/code171 retains the existing preview package, signing key, database and 1.4.9 lyric fix.

## Update popup audience

- Fresh preview installs check compatible preview releases by default. Production builds default to stable-only. Explicit saved opt-outs and dismissed version choices remain respected.
- The existing app-launch/foreground host shows the real release-notes popup when a compatible newer numeric versionCode is found. Successful checks refresh hourly and failures back off thirty minutes; manual checks bypass the interval.
- A channel marker invalidates old stable-check cache when the preview policy/channel changes. It never clears music data or settings.
- Download links cannot identify/contact anonymous downloaders or retrofit code into old installed APKs. Existing 1.4.9 users must enable Include preview releases once; <=1.4.8 needs a manual compatible update. No email, remote push campaign or silent installation was introduced.

## About

The developer link group in About now orders Instagram as KAI owner → original Echo creator (@iad1tya, explicitly attributed) → friend. It reuses the existing link row and validated destination. The duplicate Instagram action in the earlier creator panel is removed; original GitHub/source/contributor credits remain. Welcome and README do not get the creator Instagram action.

## Verification

Universal GMS debug/public APK build passed. App tests: 193 passed; retained core 17, lyrics 17 and YouLyPlus 7 also have zero failures/errors/skips. Release tooling: 15 passed. Total 249. Added tests cover preview defaults, saved choices, first/channel-changed cache invalidation, hourly throttling and independent Instagram destinations.

## Published release and phone verification (29 October 2026)

The owner approved publication and the real phone update test. [KAI 1.4.10 Preview](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/tag/kai-preview-1.4.10) is public at exact source/tag commit `8cdb16f7bcfe33a7771acadd90433c9c5f1c596b`. All eight release assets match their local size and SHA-256. Anonymous APK/source/metadata requests returned HTTP 200; the downloaded update manifest equals the local release manifest. README automation updated the download button at the top to 1.4.10 and kept Instagram out of README.

- APK SHA-256: `0aec8fc50fde2573f3312e21db52bbd648481ff236b89aaad3b6745b109b7d17`, 136,839,255 bytes. Local final copy: workspace `KAI-final-debug.apk`; release bundle: `kai-checks/releases/1.4.10`.
- The existing 1.4.9 app showed the real 1.4.10 popup after enabling its preview channel and checking. Download was started through KAI; its completed cached APK matches the published hash. This proves the real popup/download path, without a fabricated release.
- The connected A001 now reports installed 1.4.10/code171, installed by `com.google.android.packageinstaller` at 04:36:16. The Android confirmation screens themselves were not observed during the UI gap; no claim of independently operating every permission/installer prompt is made. No ADB installation or uninstall was used for this upgrade.
- The installed About screen was inspected: owner Instagram, attributed `@iad1tya · Echo Music creator`, then friend, in that order. All 148 pre-upgrade song IDs and the favourite remain. No populated playlists existed in this baseline, so populated-playlist migration was not exercised.
- [Android validation 40784300429](https://github.com/dipakdhurve9975-lgtm/KAI-Music/actions/runs/40784300429) and [README refresh 40784726270](https://github.com/dipakdhurve9975-lgtm/KAI-Music/actions/runs/40784726270) passed. The duplicate tag-triggered preparation 40784309789 was deliberately cancelled because this same version was already being published through the owner-approved, locally validated artifact path; Android validation was not cancelled.
- The existing preview signing identity/environment review remain configured, the previous-version guard now records code171, and stable publishing remains disabled. Published assets/tags were not overwritten.

The checkpoint, screenshots and phone data snapshots remain private. This release retains the 1.4.9 lyric repair; the earlier English/Devanagari/seek/pause/runtime results are recorded in its report and were not all repeated for this update-policy/About-only follow-up. Full legacy lint, hosted signing end-to-end, broader device regression and the existing stable-readiness/license limits remain open. This is a preview, not a stable certification or a remote push campaign to anonymous downloaders.
