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

The checkpoint is private. Matching source, APK, signer/version checks and publication review follow the existing release process. Phone popup/download/About results are recorded in the final handoff after the real newer release is available; no simulated update is claimed as a live result. Existing stable-readiness/license/hosted-signing end-to-end limits remain separate from this focused change.
