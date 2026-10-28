# KAI releases

The distributed application is currently a **Universal GMS debug preview**. No stable release exists. A README-only change never creates an APK release.

## Existing downloads

The latest public preview is [1.4.7](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/tag/kai-preview-1.4.7). Its [APK](https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/download/kai-preview-1.4.7/KAI-universal-gms-preview.apk) and attached matching source ZIP remain available. Historical releases are not overwritten. Their automatic GitHub source archives contain documentation-only tag contents; use the attached KAI source ZIP.

## One-time setup after source publication approval

1. Publish the reviewed full source branch; retain origin. A separate local `upstream` remote points at Echo Music for inspection only. Do not merge it blindly.
2. Configure the **kai-release** repository environment, preferably with required reviewers and tag restrictions. Workflow files alone do not configure these protections. No account settings were changed by this task.
3. For previews, store the **existing** published-preview debug keystore as `KAI_PREVIEW_KEYSTORE_BASE64` in that environment. Set `KAI_PREVIEW_SIGNER_SHA256` to its verified public certificate digest. The workflow refuses missing or mismatching keys and does not generate a replacement. Never publish the keystore or send it in chat.
4. For production releases only, use the existing production configuration: `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, and `KAI_RELEASE_SIGNER_SHA256`. There is no approved production identity configured by this task. Do not create one implicitly. The release application ID is `echo.music.iad1tya`, distinct from installed previews.
5. Set `KAI_PREVIOUS_VERSION_CODE` to the highest approved published code (currently 168). Update it after each publication. Stable releases additionally require `KAI_STABLE_RELEASES_ENABLED=true` **and** a version-matched `docs/release-approval.json` with reviewed status and no unresolved blockers. Leave disabled until asset/native/proprietary issues and runtime checks are resolved.
6. Optional service credentials are not required to compile. Do not embed confidential server/OAuth credentials into a redistributable APK; client-side strings are recoverable. Last.fm integration needs an appropriate maintainer-owned setup, not inherited credentials.

After keys, fingerprints and protections are configured, set repository variable `KAI_RELEASE_AUTOMATION_ENABLED=true`. Until then the draft-release job is deliberately skipped, including tag pushes. A source push never uploads private signing keys or enables release signing by itself.

## Prepare a release

1. Increase versionName/versionCode without changing IDs/storage keys. Write factual `docs/release-notes/X.Y.Z.md`, update modification notices and run validation.
2. Commit all intended public source. Review the source leak scan and dependency/license inventory. Confirm actual device testing and report limitations.
3. After owner approval, create an annotated `kai-preview-X.Y.Z` tag on that exact source commit, or `kai-vX.Y.Z` for a genuinely approved production release. Push that tag, or manually dispatch **Prepare approved KAI release** with an existing tag. The source commit must be reachable from main. Do not retag old releases.
4. The workflow checks tag/version, scans tracked source, runs release-tool and Android unit tests, builds Universal GMS, validates APK identity/ABIs/versionCode and checks its signer against the approved fingerprint. It archives the same committed source via `git archive` and writes metadata/checksums. Any failed step stops release creation.
5. The workflow creates a **draft only**, refusing existing releases. Download its APK, test launch/navigation/playback/About/links and the actual lyric timeline on a device. Compare signatures to the installed app. Review APK, source, checksums, source commit and licenses. Draft creation is not runtime verification or legal certification.
6. Publish manually only after review. A stable draft is created with latest=false; when publishing an approved stable build, select **Set as latest release**. Previews must remain prereleases.

## Download behavior

Stable releases contain both a descriptive `KAI-X.Y.Z-universal-gms-release.apk` and **KAI-latest.apk**. Once a real latest stable release contains that asset, the permanent download becomes:

`https://github.com/dipakdhurve9975-lgtm/KAI-Music/releases/latest/download/KAI-latest.apk`

This is a future conditional destination, not a currently working stable download. Do not promote a preview to satisfy the URL. GitHub's latest endpoint excludes prereleases/drafts; see [GitHub linking docs](https://docs.github.com/en/repositories/releasing-projects-on-github/linking-to-releases).

The refreshed **contributors.yml** workflow now handles download links instead of rewriting contributor acknowledgments. On publication or manual dispatch, it queries the releases API, checks actual nonempty uploaded KAI assets, selects the highest semantic-version compatible preview, and advertises a stable latest link only when its asset exists in GitHub's actual latest stable release. It updates only the marked README download section. This also works while the project has previews only. No scraper, hosting service or server is needed.

The automation needs repository Actions enabled and contents-write permission for its own token; branch protection can require an approved PR instead. If a bot push is rejected, the workflow fails visibly; maintainers can run `python scripts/release_tools.py downloads` and submit the README change through the normal review path. Nothing automatically publishes an APK.

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

Hosted CI then compiled Android source but failed validateSigning because the fresh runner had no debug.keystore. Validation now creates a disposable two-day CI-only debug key when absent; its artifact is explicitly not for distribution. The owner-signed installed/published APK and release signing workflow keep the existing certificate and never use this CI key. The follow-up hosted run must complete before claiming a hosted pass; local 206 Android plus 14 tooling tests remain passed.
