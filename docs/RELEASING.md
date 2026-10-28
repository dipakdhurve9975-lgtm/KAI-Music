# KAI releases

## Current publication/configuration — 29 October 2026

[KAI 1.4.9 Preview](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/tag/kai-preview-1.4.9) is now approved, public and installed. Its exact source is tag commit 4ad3880; APK/source/metadata links and all eight asset digests were verified. The top README download points to it.

The existing matching preview key is now stored as an encrypted `kai-release` environment secret. The owner is the required environment reviewer; preview certificate, previous code170 and automation-enabled variables are configured. Stable publishing is explicitly disabled. Future approved tags/manual dispatches can use the configured preview workflow; its complete hosted signing path still needs a genuine future release run. Do not rerun current published tags or replace the signing key. The setup steps below remain recovery/reference instructions, and the 1.4.8 paragraph describes the pre-candidate baseline.

The distributed application is currently a **Universal GMS debug preview**. No stable release exists. A README-only change never creates an APK release.

## Existing downloads

The latest public preview before the 1.4.9 candidate is [1.4.8](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/tag/kai-preview-1.4.8). Its [APK](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/download/kai-preview-1.4.8/KAI-universal-gms-preview.apk) and attached matching source ZIP remain available. Historical releases are not overwritten. Their automatic GitHub source archives contain documentation-only tag contents; use the attached KAI source ZIP.

## One-time setup after source publication approval

1. Publish the reviewed full source branch; retain origin. A separate local `upstream` remote points at Echo Music for inspection only. Do not merge it blindly.
2. Configure the **kai-release** repository environment, preferably with required reviewers and tag restrictions. Workflow files alone do not configure these protections. No account settings were changed by this task.
3. For previews, store the **existing** published-preview debug keystore as `KAI_PREVIEW_KEYSTORE_BASE64` in that environment. Set `KAI_PREVIEW_SIGNER_SHA256` to its verified public certificate digest. The workflow refuses missing or mismatching keys and does not generate a replacement. Never publish the keystore or send it in chat.
4. For production releases only, use the existing production configuration: `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, and `KAI_RELEASE_SIGNER_SHA256`. There is no approved production identity configured by this task. Do not create one implicitly. The release application ID is `echo.music.iad1tya`, distinct from installed previews.
5. Set `KAI_PREVIOUS_VERSION_CODE` to the highest approved published code (currently 169 before approval of 1.4.9). Update it after each publication. Stable releases additionally require `KAI_STABLE_RELEASES_ENABLED=true` **and** a version-matched `docs/release-approval.json` with reviewed status and no unresolved blockers. Leave disabled until asset/native/proprietary issues and runtime checks are resolved.
6. Optional service credentials are not required to compile. Do not embed confidential server/OAuth credentials into a redistributable APK; client-side strings are recoverable. Last.fm integration needs an appropriate maintainer-owned setup, not inherited credentials.

After keys, fingerprints and protections are configured, set repository variable `KAI_RELEASE_AUTOMATION_ENABLED=true`. Until then the draft-release job is deliberately skipped, including tag pushes. A source push never uploads private signing keys or enables release signing by itself.

## Prepare a release

1. Increase versionName/versionCode without changing IDs/storage keys. Write factual `docs/release-notes/X.Y.Z.md`, update modification notices and run validation.
2. Commit all intended public source. Review the source leak scan and dependency/license inventory. Confirm actual device testing and report limitations.
3. After owner approval, create an annotated `kai-preview-X.Y.Z` tag on that exact source commit, or `kai-vX.Y.Z` for a genuinely approved production release. Push that tag, or manually dispatch **Prepare approved KAI release** with an existing tag. The source commit must be reachable from main. Do not retag old releases.
4. The workflow checks tag/version, scans tracked source, runs release-tool and Android unit tests, builds Universal GMS, validates APK identity/ABIs/versionCode and checks its signer against the approved fingerprint. It archives the same committed source via `git archive` and writes metadata/checksums. Any failed step stops release creation.
5. The workflow creates a **draft by default**, refusing existing releases. Download its APK, test launch/navigation/playback/About/links and the actual lyric timeline on a device. Compare signatures to the installed app. Review APK, source, checksums, source commit and licenses. Draft creation is not runtime verification or legal certification.
6. Publish manually only after review. A stable draft is created with latest=false; when publishing an approved stable build, select **Set as latest release**. Previews must remain prereleases.

## Download behavior

Stable releases contain both a descriptive `KAI-X.Y.Z-universal-gms-release.apk` and **KAI-latest.apk**. Once a real latest stable release contains that asset, the permanent download becomes:

`https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/latest/download/KAI-latest.apk`

This is a future conditional destination, not a currently working stable download. Do not promote a preview to satisfy the URL. GitHub's latest endpoint excludes prereleases/drafts; see [GitHub linking docs](https://docs.github.com/en/repositories/releasing-projects-on-github/linking-to-releases).

The refreshed **contributors.yml** workflow now handles download links instead of rewriting contributor acknowledgments. On publication or manual dispatch, it queries the releases API, checks actual nonempty uploaded KAI assets, selects the highest semantic-version compatible preview, and advertises a stable latest link only when its asset exists in GitHub's actual latest stable release. It updates only the marked README download section. This also works while the project has previews only. No scraper, hosting service or server is needed.

The automation needs repository Actions enabled and contents-write permission for its own token; branch protection can require an approved PR instead. If a bot push is rejected, the workflow fails visibly; maintainers can run `python scripts/release_tools.py downloads` and submit the README change through the normal review path. Ordinary commits and tag-triggered draft preparation never publish an APK. An explicitly approved manual dispatch can publish after validation.

## Regenerate third-party evidence

```sh
./gradlew -I scripts/dependency_inventory.init.gradle kaiRuntimeArtifacts
python3 scripts/dependency_licenses.py
```

Review and commit the resulting public inventory/notices before tagging. Private cache paths stay in ignored build output. POM license declarations and extracted notice files are evidence, not a complete legal determination. Resolve unknown native/transitive/asset obligations; see COMPLIANCE_REPORT.md.

## Variant boundaries

Universal/arm64/armeabi/x86/x86_64 and GMS/FOSS flavors exist in Gradle. Only **universalGmsDebug** is the public preview and required verification variant. GMS enables Cast and includes Google dependencies. FOSS is not currently distributed or verified and is not claimed to have a fully audited free-software dependency set. The production release configuration enables shrinking and requires the approved production signing identity. No APK is universally compatible simply because it is named universal.

Public distribution builds use `-PkaiPublicBuild=true -PFLOW_NEURO_API_KEY=` and empty private OAuth/Last.fm inputs. This deliberately ignores any local google-services.json and excludes its generated Firebase/Crashlytics project configuration from the APK. Private local integration setup is preserved; the matching public source needs no Firebase project file.

CI follow-up: the first hosted run at source tag 7f0efaa failed before compilation because sdkmanager was absent from PATH. Main now uses the verified preinstalled SDK manager at ANDROID_HOME/cmdline-tools/latest/bin explicitly, accepts required SDK licenses with exit-status validation, enables the SDK preview channel and uses supported Node 24 actions. The setup-android default requested a retired tools package and was removed. Android source/APK are unchanged. Hosted success is not claimed until the follow-up run completes. Inherited Dependabot configuration opened version-update PRs on source restoration; new version-update PRs are now opt-in (limit 0), and existing PRs/branches were not deleted.

Hosted CI then compiled Android source but failed validateSigning because the fresh runner had no debug.keystore. Validation now creates a disposable two-day CI-only debug key when absent; its artifact is explicitly not for distribution. The owner-signed installed/published APK and release signing workflow keep the existing certificate and never use this CI key. The follow-up hosted run [40734457934](https://github.com/dipakdhurve9975-lgtm/KAI-Music/actions/runs/40734457934) passed for 1.4.8 infrastructure commit 1431571. This does not establish hosted status for an unpublished 1.4.9 source commit.


## KAI updater metadata and approved publication (1.4.9)

`prepare_release.py` now emits `release-metadata.json` from the inspected signed APK: schema version 1, application ID, numeric versionCode, version name, channel, minSDK, actual ABI set, variant, APK alias, SHA-256, current signer certificate digest and exact source commit. The metadata joins the release checksums and matching source archive. Never hand-edit a future version/checksum or reuse an incompatible signer.

The app queries only this KAI repository, compares numeric version codes, validates exact repository asset URLs and restricts HTTPS redirects to GitHub asset hosts. Stable is the default; previews require explicit opt-in. The installed package/signer must match. The app streams an explicitly requested download to private cache, verifies actual APK identity/size/checksum/ABI/signature, then asks Android to open installation. Android permission and installation approval remain manual. A checksum from GitHub is integrity evidence; authenticity is additionally anchored to the installed signing certificate. No silent installer, upstream Echo feed or destructive data migration is involved.

Published 1.4.8 has no KAI updater, so users must install 1.4.9 manually once. The current debug preview package cannot be replaced by the separate production package. Opt into previews to receive compatible future preview updates; never uninstall to work around an unexpected signature mismatch.

After the owner has reviewed and approved the first new release and configured the existing keystore, certificate variables, automation gate and protected `kai-release` environment, a manual workflow dispatch with `publish_release=true` can create and publish that new approved tag in one successful run. Default dispatches and tag pushes still create drafts only. Do not dispatch publication before that approval. A stable dispatch additionally retains the existing reviewed-license/runtime gate. The first 1.4.9 candidate remains local until its explicit publication approval.
