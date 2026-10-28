# Build KAI Music

KAI is an independent modification of Echo Music. GPL-covered source is under GPL-3.0; see LICENSE, NOTICE.md and docs/COMPLIANCE_REPORT.md.

## Requirements

JDK 21, Android SDK platform `android-37.0`, build-tools 36.0.0 and NDK 28.2.13676358. The project wrapper uses Gradle 9.3.1. Minimum device version is Android 8.0 (API 26).

Clone `https://github.com/dipakdhurve9975-lgtm/KAI-Music.git` after the prepared full-source changes are published, or extract the matching KAI-Music-source ZIP attached to a preview release. Historical tag-generated GitHub ZIPs contain documentation only and are not the application's source.

Set ANDROID_HOME, or create an untracked local.properties containing sdk.dir with your SDK path. Do not commit this machine-specific file. Firebase configuration is optional; without your own app/google-services.json the related plugins are not applied.

## Build and test

```sh
chmod +x gradlew
./gradlew :app:assembleUniversalGmsDebug -PkaiPublicBuild=true -PFLOW_NEURO_API_KEY= --max-workers=2 --console=plain
./gradlew :app:testUniversalGmsDebugUnitTest :core:testDebugUnitTest :lyrics:testDebugUnitTest :youlyplus:testDebugUnitTest
python3 -m unittest discover -s scripts/tests -v
```

Use gradlew.bat on Windows. The APK is under app/build/outputs/apk/universalGms/debug/. GMS includes Cast-related dependencies; only Universal GMS is verified/distributed here. Other ABI and FOSS flavors are configured but are not currently published or certified.

Debug signing uses your existing ~/.android/debug.keystore, with application ID echo.music.iad1tya.debug. A fresh developer signing key cannot update an APK signed by the maintainer. Do not replace keys to work around a mismatch or uninstall without backing up user data.

## Optional service configuration

Public source contains no private credentials. Last.fm configuration uses LASTFM_API_KEY and LASTFM_SECRET from untracked local.properties or environment variables, defaulting to empty. GitHub OAuth uses GH_CLIENT_ID/GH_CLIENT_SECRET similarly; the Flow integration accepts Gradle properties. These are optional integration inputs, not prerequisites to compile. Do not embed confidential server credentials in distributed clients. Values compiled into APKs can be recovered; use an appropriate service architecture and maintainer-owned configuration. Do not reuse upstream developer credentials.

## Release signing

Do not generate or change production signing keys without owner approval. The existing release Gradle configuration accepts root keystore.jks (or its existing local fallback) and STORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD environment variables. Release ID echo.music.iad1tya differs from the debug preview. See docs/RELEASING.md for fingerprint validation, draft releases and stable-review gates.

Original setup instructions are preserved for historical reference in docs/upstream/SETUP-original.md; those commands may describe upstream behavior rather than the current KAI build.

## Track upstream without replacing KAI

The local publication checkout keeps origin pointing to KAI and adds a separate upstream fetch remote. In a fresh clone, optionally run `git remote add upstream https://github.com/EchoMusicApp/Echo-Music.git`. Inspect/review upstream changes before merging; do not replace the modified KAI tree or overwrite origin.

## Fresh debug signing setup

If Android Studio has not already created ~/.android/debug.keystore, create your own **development-only** key before assemble. Never overwrite an existing key:

```sh
mkdir -p "$HOME/.android"
if [ ! -f "$HOME/.android/debug.keystore" ]; then
  keytool -genkeypair -keystore "$HOME/.android/debug.keystore" -storepass android -keypass android -alias androiddebugkey -dname 'CN=Android Debug,O=Android,C=US' -keyalg RSA -keysize 2048 -validity 10000
fi
```

On Windows, use Android Studio's debug keystore or the equivalent keytool command with `%USERPROFILE%/.android/debug.keystore`, only if that file is absent. Your key will differ from the published preview and cannot update it. Release automation requires the existing approved maintainer key; it never creates a replacement.
