# KAI source and distribution review â€” 28 October 2026

This is an engineering review, not a guarantee of legal compliance. The outstanding items below prevent treating this work as a fully cleared stable distribution.

## Identity and license

KAI is an independent modification of Echo Music 1.4.4. The original archive has no Git history; differences and dated KAI checkpoint inventories establish modifications, not authorship of all inherited code. Local LICENSE is byte-identical to the supplied original GNU GPL v3 text. Original copyright/source headers and the original contributor/third-party acknowledgments in NOTICE.md are retained. GPL-covered KAI modifications remain under GPL-3.0 with no additional noncommercial or use restrictions. Separate third-party licenses are not relicensed as GPL.

About and README distinguish original creator Aditya, original contributors and independent maintainer @dipakdhurve9975-lgtm. Creator Instagram ownership was verified from his GitHub profile and original About source; direct Instagram access was throttled. The original project/source links remain separate from KAI source and downloads.

GPL sections 4â€“6 address retention of notices, marking modified works and corresponding source when conveying binaries. This review uses the actual [upstream license text](https://github.com/EchoMusicApp/Echo-Music/blob/main/LICENSE); [LICENSE](../LICENSE) is supplied unmodified. It does not decide every dependency or service-terms question.

## Corrections prepared

- Full modified application source, wrapper, module/build scripts and resource assets are prepared for Git rather than a documentation-only main branch. Changes are locally reviewable before pushing.
- Dated file-level notices are added to changed Kotlin/Gradle/XML source where the format permits comments. MODIFICATIONS.md inventories other modified/binary resources and known KAI changes. Existing copyright/license headers remain. The notice date is the documentation date, not a fabricated original edit date.
- GPL and missing Apache/font license texts are bundled offline and linked from About. Existing original notices/contributor lists remain in NOTICE.md. Extracted available dependency notices and POM evidence are included separately; missing evidence is not silently treated as permission.
- The inherited `google_sans_flex.ttf` explicitly said it was not open-source. It has been replaced with the official Google Fonts OFL edition, retaining its Android resource name and existing font setting. Outfit, BBH and Google Sans Flex OFL notices accompany the existing Plus Jakarta Sans OFL. Source URLs and hashes are recorded in font-license-evidence.json. The other existing `sans_flex.ttf` embeds OFL wording and remains unchanged.
- Inherited hardcoded Last.fm credentials are removed from the build script. No private API credentials, owner tokens, private signing material, local properties, Firebase project JSON, QA databases or recordings are included in the prepared public source. Last.fm's optional service integration requires appropriate maintainer-owned configuration. Source scanning reports paths/types, never secret values. One exact inherited public YouTube web-client identifier and a synthetic example.org negative-test URL are documented narrow scan exceptions; no owner/server credential is exempted.
- New release tooling creates APK/source/checksum metadata from one committed revision, validates the signer and keeps preview/stable identities distinct. Historical releases are preserved. No silent APK downloads/installations or automatic upstream update feed are introduced.

## Historical binary/source traceability

The public 1.4.7 APK SHA-256 is `8ba1dceb7aba75e2600bff12fd13529f96f9392a5d4c03d69954c0be6536fea5`; its attached source ZIP SHA-256 is `e8a7bbdb1ddf2f7893e95ba5ca95d8a29ea6c4b58ca8621113dab149dceb28f5`.

Historical preview tags point to documentation revisions, so their automatically generated GitHub source ZIP/tarball is **not** matching application source. README/release guidance explicitly directs users to attached `KAI-Music-source-*.zip`. These attachments contain source/build inputs and omit confidential configuration and signing keys. Exact bit-for-bit reproducibility of historical binaries has not been established: dependency resolution, optional private build settings and signing affect output. The new workflow records source SHA, tag, variant, version, signer and all artifact checksums, but this is traceability, not a claim of reproducible builds.

## Unresolved obligations and release blockers

1. **Artwork and optional icon variants.** Inherited photographs, album/artist imagery, character/community icons and illustrative PNG/WebP assets lack a complete permission/provenance manifest. GPL source licensing does not automatically cover them. Do not infer permission from inclusion upstream. Preserve functionality/source during this audit; obtain evidence or approved replacements before a cleared stable distribution.
2. **Native code and bundled tools.** FFmpegKit audio, youtubedl Android/FFmpeg/aria2 and extractor dependencies require exact artifact license/source review. Determine LGPL/GPL variants, corresponding source, build/configuration changes and any relinking obligations. Upstream links alone are not a complete corresponding-source package. No certification is made that the existing ZIP satisfies all native obligations.
3. **GMS and transitive dependencies.** Resolve compatibility/distribution terms for Google Play Services/Cast and other non-GPL components, plus every unresolved entry in dependency-inventory.json. A Gradle FOSS flavor exists but has not been audited or distributed here; its name is not a license determination. Shazam integration is local HTTP-client source, not evidence that Apple SDK binaries are bundled.
4. **Credential hygiene after previous distribution.** Hardcoded inherited credentials existed in previous public source/APKs. This task stops including them in new build configuration; rotation/revocation is the credential owner's responsibility and cannot erase past public copies. Do not publish private server/client secrets in Android BuildConfig. Optional integrations need a reviewed configuration.
5. **Complete source verification.** Before any new publication, compare committed source inputs to the built artifact, refresh dependency notices/inventory, inspect exclusions and confirm no required generated source/build instructions are omitted. Private signing keys need not be shipped; missing service credentials are documented. A matching source package is not established merely by renaming an archive.
6. **Runtime and release identity.** No ADB device was attached at the initial audit; the owner subsequently connected one for final checks. About wrapping, themes, navigation, external browser launch and playback/lyrics need actual device checks. Last task's live lyric progression was explicitly deferred. Production package/signing is a separate update chain from the current debug preview and requires owner confirmation.

`release-approval.json` deliberately records these blockers and stable approval false. No new APK, metadata setting, push or release is published before the user's final review. Do not change that record to true without resolving the applicable issues.

## Verification evidence

Build/test, source scan, link validation, full file inventory and candidate APK identity are recorded in the final local review report. CI workflow files have not executed on GitHub until this change is approved and pushed; local workflow lint/tests are not a hosted signing/release test.

## Dependency inventory result

Resolved Universal GMS debug runtime: 296 artifacts, 201 with direct cached POM declarations and 95 without a direct declaration in the inspected cache (parents or other sources may provide one). Extracted 28 unique available notice/license documents, including FFmpeg package notices. Exact artifact hashes and POM declarations are recorded; native/source/relinking/asset issues remain unresolved.

## Link verification

HEAD requests on 28 October 2026 returned HTTP 200 for the KAI repository/releases, current 1.4.7 APK and matching source attachment, original repository, creator GitHub, original contributors, upstream GPL text and creator Instagram. Instagram was initially throttled in browser retrieval but later responded to the direct HTTP check; ownership evidence remains the creator-controlled cross-links. KAI main currently lacks LICENSE until the prepared source change is pushed; About supplies GPL locally so that viewer does not depend on a future URL. No nonexistent stable APK URL is presented as a current download.

Public distribution builds use `-PkaiPublicBuild=true -PFLOW_NEURO_API_KEY=` and empty private OAuth/Last.fm inputs. This deliberately ignores any local google-services.json and excludes its generated Firebase/Crashlytics project configuration from the APK. Private local integration setup is preserved; the matching public source needs no Firebase project file.
