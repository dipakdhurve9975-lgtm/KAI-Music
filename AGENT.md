# AGENT.md - KAI

## Attribution and release review (28 October 2026)

KAI 1.4.8 (169) is a local preview candidate, not a published stable build. About separates original Echo Music/Aditya/contributors from independent KAI development; preserve owner/friend/support links. Long acknowledgments expand, links wrap, and GPL/bundled notices load locally on IO without network work. The restricted inherited Google Sans Flex binary is replaced at its existing resource ID with the official OFL edition; all bundled font notices remain. Hardcoded inherited Last.fm credentials were removed; optional integration needs appropriate maintainer-owned configuration.

Updates now exposes only an explicit KAI releases browser action; startup release requests, badges, updater workers and notifications stay retired. Legacy changelog/commit destinations refer to KAI, never the upstream APK feed. Package IDs, DB/storage keys, audio and lyric behavior are unchanged by this task.

The current publication task supersedes the earlier README-only distribution preference. Prepare full sanitized source, GPL/notices, origins, dependency evidence and reliable draft-release infrastructure; no push, remote metadata update or new APK publication before the user reviews the concrete result. Preserve historical releases. Origin remains KAI; upstream is a separate inspection remote. scripts/release_tools.py and prepare_release.py validate tags, exact source, APK/channel/ABIs/versionCode and signer. Workflows create drafts only; no silent publication or generated replacement signing key. Current public previews retain versioned links; KAI-latest.apk is advertised only after it exists in a genuine latest stable release. docs/RELEASING.md and COMPLIANCE_REPORT.md record setup and unresolved asset/native/GMS obligations; never claim guaranteed legal compliance or final runtime verification.


## Lyrics typography and provider fidelity (28 October 2026)

Phone reproduction found the lyric position frozen at 96541ms while transport advanced. LyricsPlaybackClock now follows the shared PlayerConnection playing/ready state and samples the live connection.player position every 16ms only while visible/resumed/playing, independent of the composition animation clock. Paused seeks remain event driven; Cast interpolation uses actual receiver samples. Do not restore a separate mutable localPlaying latch or captured player-position source.

Explicit Refetch bypasses the process cache and fetches on IO before opening a Room transaction. Retain text on empty/offline/failure results, guard against concurrent user edits, and never hold the Room query executor while awaiting network. The existing Refetch action can repair old provider output without clearing the music library.

Player presentation work uses a derived visibility policy (sheet progress above halfway), not exact expanded-anchor equality. A nearly expanded sheet must retain the lyric clock and transport polling after drags/resizes; collapsed/dismissed sheets stop them. Keep gesture/anchor semantics themselves unchanged.

KAI 1.4.7 (168) exposes the existing five-style dialog as Lyrics style & font in Player, Lyrics and Appearance menus. App-local kaiLyricsTypeface preserves Follow theme by default and offers System, bundled Jakarta/Outfit and platform Editorial serif, retaining Android script fallback. Shared KaiLyricsLine and Classic use the selected family; soft glow is a bounded static text shadow and works across the five presentations. Classic line-only glow no longer rebuilds animated gradients/text every audio frame. The old LYRICS_V2 preference uses the safe shared Kinetic renderer to preserve original script/spacing.

Enhanced LRC accepts whole seconds and 1/2/3 fractional digits consistently with normal LRC, including blank timed gaps and syllables without invented spaces. YouLyPlus aligns genuine syllable markers to exact original line text, falling back to line timing if fragments cannot match. It prefers genuinely timed mirror results over a faster plain response, within the existing timeouts, and rejects explicit duration/edit mismatches (slowed/sped-up/etc.) when metadata exists. Do not fabricate missing timing or retime a different recording. Cached manual edits are preserved; Refetch remains available for old provider output.

## Retired inherited updater (28 October 2026)

KAI 1.4.6 (167) removes startup update/changelog requests, prompts and notifications from the inherited release feed. Both `settings/update` and saved `update` routes show the same static Coming soon row; Settings never reads an old update badge. `initializeKaiUpdatePlaceholder` runs legacy settings/transport migration off the main thread, clears only notification IDs 1001/5678 and cancels only unique/tagged `update_download` work. Preserve the old worker class name as a no-op for persisted WorkManager entries. There is no app-release network client or automatic install flow; future owner-approved KAI updates require a deliberate implementation. Do not restore upstream release checks or observeForever. Playback/download notification IDs, package, database and music preferences stay compatible.

## Navigation and lyrics reliability (27 October 2026)

Root-to-root navigation is immediate; detail routes use a restrained 140ms fade. The shared liquid control starts its tap settle before dispatching navigation and retains its in-flight release spring. Existing entry ownership, singleTop and save/restore remain intact. The full player's 200ms transport polling is limited to its expanded state so tab changes do not compete with hidden player recomposition.

Lyrics distinguishes actual progression from zero/constant placeholder timestamps. Untimed lyrics are readable static text with a compact timing-unavailable label and no fake highlight/seek. The parser accepts whole seconds, 1/2/3-digit fractions and timed blank gaps; stable sorting and upper-bound lookup use the real playback timeline plus the saved per-song offset, with no implicit 300ms lead. Scroll follow uses one cancellable effect and is suspended only by actual user scrolling. The clipped, finite lyrics viewport is separate from the song information/controls; short/high-font layouts scroll the bounded controls region. Debug-only KaiLyricsSync logs record position/index/timestamps/offset/target without lyric text.

LyricsHelper is process-scoped, shares in-flight/cache results, honors provider enablement/order and keys search options by track/duration/metadata. One bounded session attempt may upgrade automatically fetched untimed lyrics with genuinely timed existing-provider data, retaining text on failures or concurrent user edits. YouLyPlus leaves missing timing as plain text; LRCLib automatic matching requires title/artist eligibility as well as duration. Keep the existing per-song ±3s offset range/reset and compatible values; all adjustment buttons now use 100ms.


## Home dock motion and selector settle correction (27 October 2026)

Final scope, evidence, APK identity and remaining runtime checks are recorded in `KAI_INTERACTION_HANDOFF.md`; file inventory is `KAI_INTERACTION_CHANGED_FILES.md`. The dock completion build passed 158 tests in 26 suites and was installed over the existing app. Do not treat a launch behind keyguard as visual verification.

Home's navigation bar and mini-player translate together after 12dp of consumed downward content motion and return on upward scrolling. The shared 180ms hide / 220ms reveal preserves content insets, never consumes the scroll, resets on navigation and player expansion, and snaps with reduced motion. Expanded Now Playing and landscape rails are never translated. Direction hysteresis prevents tiny reversals from flickering the dock.

The liquid selector retains an in-flight spring when navigation confirms its existing release target, avoiding a second zero-velocity animation. Layout, RTL and reduced-motion changes still force a fresh settle; different programmatic selections retarget normally.


## Completion pass (27 October 2026)

- Ambient Normal preserves the landscape artwork/lyrics layout. Ambient Vinyl uses the same `VinylPlayerView` and audio connection, with retained rotation, current artwork, title/artist and optional lyrics. App-local `AmbientPresentationKey` defaults to NORMAL and remembers later choices. The small in-screen selector, transport, Back and settings share inactivity behavior; taps reveal controls, paused/touch-exploration states retain them. Settings reuse the lyrics presentation chooser. Respect original orientation/window state across configuration recreation and exit; guest/Cast transport uses PlayerConnection. Background/video work is lifecycle and reduced-motion gated.
- Vinyl uses cached fine grooves, a dark stepped edge, stationary reflection/spindle and a refined bent tonearm. Keep its component API and 33⅓RPM frame-delta rotation; do not create an Ambient-specific renderer or audio session.
- Home distinguishes initial loading, refresh, pagination and request errors, retaining real cached shelves during refresh. Request flags must be atomic/serialized and cleared in finally blocks. Stable section identities/order prevent refresh jumps. Library groups existing collections compactly and preserves all routes, visibility switches, imports, sorting and playlist actions. Content insets already include the dock; add only the actual floating-action reserve.
- `kai_mark.xml` is the original connected K/music-note vector, reused by shared branding and launcher assets with a burgundy adaptive background and monochrome support. MainActivity alone uses the native `Theme.Kai.Starting` splash with the existing runtime theme afterward. Package/database/storage identities remain unchanged.
- `KaiSpiralLoading` is for actual indeterminate work only: a cached spiral path with graphics-layer rotation, progress semantics and lifecycle/reduced-motion gating. Preserve determinate progress and pull-to-refresh gestures. Never leave an invisible loading animation composed after completion. `rememberKaiAtmosphereProgress` similarly pauses decorative work without rebuilding text/artwork each frame.

## KAI interaction polish (26 October 2026)

The subsequent correction brief also applies the liquid interaction to the main five-cell navigation. `FloatingNavigationToolbar` uses fixed geometry, the shared drag core and the existing More sheet. Navigation-entry owners in `KaiNavigationPolicy`/MainActivity keep Search results, details, Back and restoration aligned with the selected tab; Recognition keeps the dock visible. Root callbacks inspect the live destination and use singleTop/save/restore instead of trusting a stale selected boolean. Content insets reserve actual bar margin and mini-player gap. The optional collapsing navigation retains its compact accessory and uses the same liquid control when expanded. Fresh preferences enable bounded glass with restrained refraction and no chromatic aberration; saved switches remain respected. `KaiMiniPlayerSurface` shares a single backdrop pass, quiet border and fallback, using24dp corners distinct from the navigation capsule. Explicit alternate mini-player backgrounds remain available.

`KAI_POLISH_AUDIT.md` records the reviewed phone recordings, safe source/APK checkpoint and ordered implementation. `KaiLiquidSegmentedControl` uses one layer-translated indicator, horizontal touch-slop arbitration and a single release commit; cancellation restores the external selection. Logical/physical coordinates support RTL. Tap/Tab accessibility and programmatic selection remain available. Mild spring/press/stretch effects respect the shared reduced-motion preference. Glass samples an explicit player-background backdrop only when expanded, supported and enabled; cached highlights and opaque contrast-preserving fallback avoid full-screen effects.

Lyrics presentations use separate `KaiPresentationPreferences` keys, retaining legacy animation preferences. Classic Flow preserves the scrolling path; Floating, Cinematic, Kinetic and Minimal Focus share `KaiLyricsLine`, original strings, translations and script fallback. Auto persists the current track/style and rotates only among line-safe styles at song boundaries. Genuine word spans are validated against the original text and grapheme boundaries; Romanized primary text uses line motion. `LyricsPlaybackClock` follows local/Cast progress and paused seeks, with one visible/resumed clock and no paused frame loop. Motion reads State in layers; only real word reveal changes text. The default player gradient uses cached, muted `KaiAlbumAtmosphere` fields; motion stops while paused, hidden, backgrounded or reduced motion is enabled.

## KAI premium redesign (26 October 2026)

The user's KAI brief supersedes the older Nothing OS and dynamic-first visual guidance below. Public display text is **KAI**; technical `echo.music.iad1tya` identifiers, service endpoints, database v48, stored keys and legal source attribution remain compatible.

- `ui/theme/KaiDesign.kt` centralizes spacing (4/8/12/16/24/32/48dp), artwork/card/pill shapes (16/24dp/50%), rose/plum and warm light palettes, feedback/transition durations (140/260ms), and a single theme-installed system reduced-motion observer. Use `MaterialTheme` color roles and typography with these tokens. Reserve glass for navigation/player surfaces, with supported-device checks and opaque fallback.
- Default typography uses the already bundled Plus Jakarta Sans font. Preserve saved font, theme, background, slider and player-style choices. KAI's `DefaultThemeColor` (#D85C78) and legacy `SystemThemeColor` (#ED5564) are distinct. Artwork-derived theme remains an explicit Appearance option. Absent preferences use KAI colors, non-wavy controls and 28sp fade lyrics.
- Shared `KaiScreenHeader`/`KaiEmptyState`, settings groups/rows, artwork cards, filters and list items give Home, Library, Search and Settings their common hierarchy. Use real ViewModel results; retain routes, imports, sorts, filters and stable item keys. Avoid per-row entrance animation or glass.
- `PlayerChrome.kt` supplies the contextual Now Playing header and restrained transport crossfade. `MiniPlayerProgress.kt` polls local/Cast progress only while RESUMED and exposes State for drawing rather than recomposing artwork. Both mini-player styles open the existing sheet and route transport through PlayerConnection, respecting Listen Together restrictions. Controls have descriptive accessibility labels and 48dp targets.
- Vinyl/Normal/Lyrics share one playback engine and session mode selection. Lyrics retains providers, timestamps, offsets, translations and seeking, with lifecycle-paused updates, reduced-motion scrolling, readable foreground colors and explicit loading/empty states.
- `branding/KaiLinks.kt` is the single configuration point for future public/community/payment links. Empty/invalid destinations are disabled as Coming soon. About/Welcome share brand/link components and actual BuildConfig version/channel. Update notifications open About when the website is unconfigured. Keep public account links separate from required technical update APIs and legal attribution.
- Final verification, file inventory, limitations and artifact paths are recorded in `KAI_PREMIUM_IMPLEMENTATION.md` and `KAI_PREMIUM_CHANGED_FILES.md`. Do not claim phone screenshots or runtime checks without an attached device/AVD.

## KAI branding and Vinyl player (25 October 2026)

- The display name is **KAI**. Localized UI text, default adaptive launcher icon and generic app marks use KAI. Existing `echo.music.iad1tya` packages, preference/database keys, provider authorities, legacy link handlers and upstream service configuration remain compatible. Do not globally rename technical identifiers as a branding change. Share actions now emit canonical YouTube Music links.
- `ui/player/VinylPlayer.kt` adds `VinylPlayerView`, `PlayerModeSelector`, and lifecycle-aware rotation. `VinylMotion.kt` contains frame-delta rotation math (200 degrees/second = 33 1/3 RPM). The drawing is original Compose Canvas code inspired by the user's QQ Music/MD Vinyl screenshots; no reference assets or code are copied.
- `BottomSheetPlayer` owns a saveable `PlayerDisplayMode` (VINYL/NORMAL/LYRICS). Vinyl is the default when opening a new player session. Track changes preserve the current choice. Normal reuses Thumbnail; Lyrics reuses InlineLyricsView. The selector only changes presentation, never queues/audio. Queue hides its old duplicate lyrics toggle for this player.
- Rotation reads actual Media3 `isPlaying` (or Cast's playing state), waits for the tonearm to settle, freezes at its current angle when paused/buffering/suppressed, and runs only while Vinyl is expanded. `repeatOnLifecycle(RESUMED)` cancels/restarts frame work directly, so background time cannot advance the record. Android animator scale zero disables rotation. Listeners/observers are removed on disposal. The disc angle is read in graphicsLayer, not in artwork composition.
- Design: brushed theme-colored square plinth, black grooved disc, circular live-artwork label with theme fallback and centered spindle, separately drawn metallic tonearm. Both orientations share the same visual and rotation state; compact portrait controls and landscape controls can scroll. Existing artwork-derived player backgrounds and transport controls are reused.
- New Kai generic mark is `drawable/kai_mark.xml`; default/static adaptive icons and generic UI imagery reference it. `kai_tv_banner.xml` supplies Android TV branding. Other optional community icon choices remain available.
- Build declarations currently take precedence over older notes below: app compile SDK **37**, target **36**, min **26**, NDK **28.2.13676358**, JDK **21**, Gradle **9.3.1**; both **foss** and **gms** flavors exist. Use Universal GMS for verification. Full build/runtime verification status is recorded in `KAI_IMPLEMENTATION.md`.

Context file for AI agents (Antigravity, Claude Code, etc.) working in this repo.
Keep this file up to date as the project evolves — it's the fastest way to give
the agent full context without re-scanning the whole codebase every session.

## Development Rules & Guidelines

> **Maintenance rule:** whenever you add or change a feature, structure,
> module, build config, or convention in this repo, **update this file in
> the same PR/commit**. This file is only useful if it stays accurate —
> stale context is worse than no context, because the agent will act on it
> confidently and be wrong. Treat an out-of-date `AGENT.md` as a bug.

> **Dynamic Design Pattern rule:** Whenever you make ANY changes or updates to the UI, layout, or components, you **must automatically document** the new or updated design patterns directly in this `AGENT.md` file (and `DESIGN.md` if applicable) during the same session. This ensures that the AI's context regarding the project's visual aesthetic is dynamically kept up-to-date, so future tasks follow the newly introduced patterns accurately.

> **Attribution rule:** if a feature is ported from, adapted from, or
> inspired by another open-source project (even partially — a UI pattern,
> an algorithm, a parsing approach, a whole file), you **must** add or
> update an entry in `README.md`'s **Special Thanks** section in the same
> commit. Follow the existing table format there:
> `| **[Project Name](repo URL)** | What was taken/inspired, described specifically |`.
> Be specific in the description — not "inspiration" alone, but what part
> (e.g. "Material You UI inspiration", "Lyrics implementation reference",
> "Decryption handling and backup playback engine"), matching how existing
> entries like Metrolist, ArchiveTune, and SimpMusic are described. If code
> is copied or closely adapted rather than just conceptually inspired, say
> so plainly in the description, and check the source project's license is
> compatible before merging. Never port/adapt code without adding this
> credit — this applies even for small snippets, not just whole features.

> **Upcoming Update rule:** whenever you add a new feature, fix a bug, or merge
> a PR, you **must** update the `upcomingupdate.json` file in the root
> directory. Add your changes to the appropriate array (`features`, `bug_fixes`,
> or `contributors`) so that they are tracked for the next weekly release.

> **Do not push without explicit instruction.** Making code changes
> (editing files, committing locally) is fine whenever asked, but **never
> run `git push` — or otherwise publish changes to GitHub — unless I
> explicitly tell you to push** in that message (e.g. "push this",
> "push it", "commit and push"). Finishing a change, fixing a bug, or being
> asked to "commit" is not by itself permission to push. If you're unsure
> whether "push" was actually meant, stop and ask rather than assuming.
> This applies to every session, not just the current one.

> **Pre-push checklist — run through this every time before pushing:**
>
> 1. **Remove all temporary files.** Before committing and pushing, delete any
>    scratch/debug/automation scripts that were created during the session.
>    Common culprits:
>    - One-off Python/shell scripts (`fix_*.py`, `fix_*.sh`, `patch_*.py`, etc.)
>    - Debug log files (`build.log`, `*.log` — already in `.gitignore` but
>      double-check nothing slipped through)
>    - Temporary data files (`.json`, `.txt` scratch files created during
>      investigation)
>    Check with `git status` and `git ls-files | grep -E '\.(py|sh|log)'`
>    before staging. If a script is genuinely reusable project tooling, move
>    it to `scripts/` with a clear name and docstring; otherwise delete it.
> 2. **Verify the build is clean.** Run
>    `./gradlew :app:compileUniversalGmsDebugKotlin` and confirm
>    `BUILD SUCCESSFUL` with no errors before committing.
> 3. **Write a proper commit message.** Title + body in Conventional Commits
>    format (see "Commit message format" section below).
> 4. **Update `AGENT.md` and docs** if module structure, conventions, or
>    architecture changed — stale context is a bug.

> **Build & Installation rule:** During development, if you are asked to assemble or install the app, or if you are doing so autonomously for testing, **always use the Universal GMS variant** (e.g., `./gradlew assembleUniversalGmsDebug` or `./gradlew installUniversalGmsDebug`).

### UI rule: Nothing OS 5.0 Design (over Material You)

All UI work — new screens, new components, and edits to existing ones —
**must** follow the **Nothing OS 5.0 design** language (clean and minimal) rather than blindly
applying standard Material Design 3 guidelines or Material You.

We maintain a dedicated **[DESIGN.md](DESIGN.md)** file which serves as the central
hub for all design decisions, component usage, and style choices in the Echo Music codebase.

**Key Requirements:**
- **Clean and Minimal:** Emphasize the Nothing OS 5.0 aesthetic. Focus on high-contrast, typography-driven layouts, monochrome or minimal color palettes, and distinct geometry.
- **Avoid standard M3 containers:** Do not use standard M3 containers or generic Material You bouncy/rounded components. Replace them with stark, clean Nothing-style UI.
- **Do not refactor to strict M3:** Do not replace existing minimal components with `LargeTopAppBar` or standard M3 cards.

If a new feature needs a UI pattern not covered in `DESIGN.md`, create a minimal, Nothing-OS-inspired pattern rather than pulling from the official Material 3 guidelines.

### Conventions worth following

- Kotlin official style: `val` over `var`, data classes for simple holders,
  sealed classes/interfaces for UI/playback state.
- New dependency versions go in `gradle/libs.versions.toml`
  (`[versions]` + `[libraries]`), referenced via `libs.xxx` — don't hardcode
  version strings in module `build.gradle.kts` files except for one-off
  Google/Firebase classpath deps in the root `build.gradle.kts`.
- New cross-cutting integrations (lyrics source, canvas provider, metadata
  source) → new Gradle module, not a package inside `:app`.
- Feature flags / build-time toggles go through `buildConfigField` in
  `app/build.gradle.kts` (see `CAST_AVAILABLE`, `IS_NIGHTLY` patterns).
- Room schema changes require a migration in `db/` and the schema JSON is
  version-controlled under `app/schemas/` — don't skip migrations.
- **Networking:** Use Ktor for all new network requests. Retrofit is maintained for legacy endpoints but should not be used for new features.

### Things to double check before assuming

- Exact current contents of `com/music/echo/echomusic/` (contains core app-level initializers/application class) and `:unison` (cross-cutting/shared utilities and common base classes) — check source before editing.

## What this app is

Echo Music is a native **Android** music streaming client (Kotlin + Jetpack
Compose) that streams from YouTube Music's catalog ad-free, and layers on
offline downloads, real-time synced lyrics, music recognition ("Echo Find"),
podcasts, local media playback, Spotify import, "Listen Together" (synced
group listening), Discord Rich Presence, and more. It ships as a single
**GMS** build variant (Google Cast + Firebase enabled) — the previous FOSS
(no-Google-services) flavor has been removed. See "Build variants" below.

Package/namespace: `echo.music.iad1tya` (app module). Application ID matches.

## Tech stack

- **Language:** Kotlin (JVM target 21), Gradle Kotlin DSL (`.kts`)
- **UI:** Jetpack Compose (Material 3, `material3 = 1.5.0-alpha18`), adaptive
  layouts (`androidx.compose.material3.adaptive`), `haze` for blur effects,
  `materialKolor` for Material-You-style dynamic color, Coil 3 for image
  loading, Lottie for animations, `shimmer` for loading placeholders,
  `smoothCorner` for custom shapes.
- **Architecture:** MVVM — ViewModels (`viewmodels/`, 30 files) + Compose
  screens (`ui/screens/`) + Repository-ish data layer under `data/`/`db/`.
  Hilt (`dagger.hilt`) is used throughout for DI (see `di/AppModule.kt`,
  `di/NetworkModule.kt`).
- **Persistence:** Room (`db/entities/`, `db/daos/`), DataStore Preferences
  (`utils/dataStore`).
- **Playback:** Media3 / ExoPlayer (`media3`, `media3-session`, `media3-hls`,
  `media3-ui`, `media3-okhttp`) — core logic in `playback/` (`MusicService.kt`,
  `PlayerConnection.kt`, `ExoDownloadService.kt`, `ChunkingDataSource.kt`,
  `SleepTimer.kt`, `audio/`, `queues/`). FFmpegKit is used for audio export.
- **Networking:** Ktor client + Retrofit (both present — Retrofit mainly for
  legacy/simple calls, Ktor for newer code), OkHttp, Jsoup for scraping,
  kotlinx.serialization + Gson.
- **Other:** Protobuf (lite, for message serialization), Guava +
  coroutines-guava, WorkManager, Firebase Analytics/Crashlytics
  (**GMS flavor only**), Google Drive API (GMS flavor, for backup/sync),
  Play Services Cast (GMS flavor).
- **Build system:** Gradle version catalog at `gradle/libs.versions.toml`
  (always add new deps here, not inline). AGP `9.0.0`, Kotlin `2.3.10`,
  KSP for annotation processing (Room, Hilt).

## Module map (multi-module Gradle project)

Root `:app` depends on all of these library modules — each is a focused,
mostly-independent feature/integration:

| Module | Purpose |
|---|---|
| `:app` | Main application — UI, ViewModels, MusicService, DB, DI, app-level orchestration |
| `:core` | Shared models, constants, Room database, DataStore, and common utilities |
| `:playback` | Pure ExoPlayer/Media3 logic — Queues, Equalizer, ChunkingDataSource, SleepTimer, BeatAnalyzer |
| `:lyrics` | Lyrics orchestration — LyricsHelper, LyricsEntry, LyricsUtils, all provider impls, AI translation |
| `:innertube` | YouTube Music InnerTube API client (the core music source) |
| `:kugou`, `:lrclib`, `:betterlyrics`, `:youlyplus`, `:paxsenixlyrics`, `:simpmusic` | Individual lyrics source providers (each consumed by `:lyrics`) |
| `:shazamkit` | Music recognition ("Echo Find") |
| `:canvas`, `:echomusiccanvas`, `:applecanvas` | Canvas-style looping video backgrounds for tracks (different providers) |
| `:artistvideo` | Artist video features |
| `:unison` | Cross-cutting shared utility module (check source before editing) |

When adding a new external integration (a new lyrics source, a new canvas
provider, etc.), the existing pattern is: **new Gradle module**, register it
in `settings.gradle.kts`, add it as an `implementation(project(":name"))` in
`app/build.gradle.kts`, wire it up via Hilt in `di/`.

## App module internal structure

Path: `app/src/main/kotlin/com/music/echo/` (note: source dir is `kotlin/`,
not `java/` — despite what older internal docs may say).

```
ai/             AI features (lyrics translation providers, etc.)
api/            External API clients not covered by a dedicated module
constants/      Preference keys / constant definitions (DataStore keys live here)
data/           Data layer glue
db/
  entities/     Room entities (Song, Album, Artist, Playlist, Lyrics, etc.)
  daos/         Room DAOs
di/             Hilt modules (AppModule, NetworkModule, Qualifiers, entry points)
discord/        Discord Rich Presence integration
echomusic/      Core app-level classes (Application class, core initializers)
eq/             Equalizer
extensions/     Kotlin extension functions
listentogether/ "Listen Together" synced group listening feature
localmedia/     Local on-device media file playback
lyrics/         Lyrics orchestration (aggregates the lyrics provider modules)
models/         Shared data models
playback/       Media3/ExoPlayer service, download manager, queueing, audio
quicksettings/  Android quick settings tile
recognition/    Music recognition (Echo Find) app-side logic
spotify/        Spotify API integration
spotifyimport/  Import playlists/tracks from Spotify
ui/
  component/    Reusable Compose components (backdrop, floating tab bar, shimmer, etc.)
  menu/         Context/dropdown menus
  player/       Now-playing / player UI
  screens/      Top-level screens (Home, Search, Library, Album, Artist, Settings, etc.)
  theme/        Theme.kt, Type.kt, Font.kt, color extraction, dynamic color
  utils/        UI-specific utilities
utils/          General utilities (largest non-UI folder, 44 files)
viewmodels/     ViewModels, one (or a few) per screen/feature (30 files)
widget/         Home-screen widget
```

## UI / design system notes

- **Nothing OS 5.0 / Glassmorphism Aesthetic:**
  - When designing components that require a translucent or frosted glass effect (like bottom navbars or mini-players), use the custom `Modifier.liquidGlass(config = ...)` rather than standard alpha background colors.
  - To achieve a **dense, clean blur** (without a muddy grey tint), use a high `blurRadius` (e.g., `64f`) and explicitly set the `surfaceTintColor` to `MaterialTheme.colorScheme.surface` or `Color.Black` (if pureBlack), while keeping the `surfaceOpacity` moderate (e.g., `0.5f`).
  - **Flat Integration:** In floating toolbars or navbars, integrate action buttons (like FABs or overflow menus) directly as flat `FloatingNavigationToolbarActionItem`s or `StandardFloatingActionButton`s with `elevation = 0.dp`. Do NOT use `VibrantFloatingActionButton` or elements with default drop shadows, as they render a detached, ugly shadow beneath the translucent glass.

- Material 3 with **dynamic color**: on Android 12+, uses system dynamic
  color by default; otherwise generates a scheme from `DefaultThemeColor`
  (`0xFFED5564`) via `materialKolor`'s `rememberDynamicColorScheme`
  (spec `SPEC_2025`, style `TonalSpot`). See `ui/theme/Theme.kt`.
  A custom "pure black" dark mode variant is supported.
  Users can override the seed color from settings.
- Adaptive layouts (`androidx.compose.material3.adaptive`) are used —
  design new screens to work across phone/tablet/foldable widths.
  A "floating tab bar" custom component exists at
  `ui/component/floatingtabbar/` — prefer reusing it over building new nav UI.
- Icons: partly Material Symbols/Icons Extended
  (`androidx.compose.material.material-icons-extended`), plus custom SVG
  drawables generated via `scripts/compose_svg_drawable.py` and
  `scripts/download_material_icons.py` — check those scripts before manually
  adding new vector assets.
- Shimmer loading placeholders (`ui/component/shimmer/`, `libs.shimmer`) are
  the standard loading-state pattern — use them for new async-loading UI
  instead of spinners.

## Commit message format (required)

Every commit/PR title **must** follow Conventional Commits style, matching
this repo's actual history:

```
<type>(<scope>): <short, imperative summary>
```

Examples from this repo's own log:
- `feat(ui): implement Material You pill-shaped inputs and dialog buttons`
- `fix(db): prevent SQLiteConstraintException crash on map inserts`
- `fix(spotify): handle SSO popups in Spotify login by intercepting multiple windows`
- `fix(datastore): gracefully handle ClassCastException on corrupted preference keys`
- `build(deps): upgrade safe non-compose dependencies`

Rules:
- **type**: `feat`, `fix`, `build`, `chore`, `refactor`, `docs`, `perf`,
  `test`, or `ci` — pick the one that actually matches the change.
- **scope**: the module or area touched, lowercase, e.g. `ui`, `db`,
  `spotify`, `datastore`, `deps`, `playback`, `lyrics`, `di`. Keep it short
  and specific — this is what makes the log scannable.
- **summary**: imperative mood ("implement", "fix", "prevent" — not
  "implemented" or "fixes"), no trailing period, concise enough to read as
  a single line in `git log --oneline`.
- **Body** (commit description / PR description): a short paragraph
  explaining *what changed and why*, specific enough that someone reading
  it later understands the change without opening the diff — e.g. naming
  the exact components/files affected and the concrete before→after
  behavior, the way the pill-shaped-inputs commit names `OutlinedTextField`,
  `CircleShape (24.dp)`, and the `TextButton`→`Button` swap explicitly.
  Avoid vague bodies like "UI improvements" or "bug fixes."

When pushing a new feature or fix, generate both the title and body in this
format, and don't omit the body for anything beyond a trivial one-line fix.

## Build variants

**GMS only.** The `foss` product flavor has been removed — do not add code,
docs, or CI steps that reference a `foss` build, and do not re-introduce a
`foss` flavor without an explicit decision to do so. `variant` dimension now
has a single flavor: `gms` (Google Cast + Firebase enabled). The `abi`
dimension is unchanged: `universal`, `arm64`, `armeabi`, `x86`, `x86_64`.

```bash
# Debug build
./gradlew assembleUniversalGmsDebug

# Release build (needs signing env vars: STORE_PASSWORD, KEY_ALIAS,
# KEY_PASSWORD and app/keystore/release.keystore)
./gradlew assembleUniversalGmsRelease
```

Release builds are also GMS only — there is no separate FOSS release
artifact anymore. `gmsImplementation`-scoped dependencies (Firebase, Cast,
Play Services Auth, Google Drive API) are effectively always active now;
new Google-Play-Services-dependent code no longer needs flavor gating, but
keep using `gmsImplementation` in `build.gradle.kts` for consistency and in
case a FOSS flavor is reintroduced later.
min/target/compile SDK: `minSdk 26`, `targetSdk 36`, `compileSdk 36`.
NDK `27.0.12077973`. JDK 21 (kotlin/java toolchain).

## Config & secrets

- `local.properties` (from `local.properties.template`) — Android SDK path.
  Never commit.
- `app/google-services.json` — Firebase config, **optional**; app builds fine
  without it (GMS-only feature).
- Build-time secrets read from `local.properties` first, then env vars:
  `LASTFM_API_KEY`, `LASTFM_SECRET`, `GH_CLIENT_ID`, `GH_CLIENT_SECRET`.
  Also `FLOW_NEURO_BASE_URL` / `FLOW_NEURO_API_KEY` (defaults to
  `https://api.flowneuroengine.com`) as Gradle properties.
- AI lyrics translation is configured **in-app** (Settings → AI Settings),
  not at build time — supports OpenRouter (default) or custom
  OpenAI/Anthropic/Gemini-compatible providers.
- Never commit: `local.properties`, `*.keystore`, real `google-services.json`,
  any `gradle.properties` containing signing credentials.

## Recommendation query safety

`DatabaseDao.quickPicks` and `forgottenFavorites` use a bound, per-invocation random seed to order song row IDs deterministically (including Quick Picks' fallback seeds). Do not use SQLite `RANDOM()` in queries returning Room relations: Room resets and re-executes the parent statement while building relations, so a randomized `LIMIT` can return different IDs and crash with `NoSuchElementException` from the generated relation map even inside `@Transaction`. A new invocation gets a new seed, preserving refresh variety without a schema migration.

## Testing

A 4-tier test suite covers database migrations, scoring algorithms, repository persistence, caching, and UI flows:
- **Tier 1 (Pure Logic):** `app/src/test/.../generate/LocalTasteScoringTest.kt` (pure JVM scoring, skip penalty, recency, time-of-day bonuses, candidate filter) and `RecommendationScoringTest.kt` (Last.fm additive scoring bonuses).
- **Recommendation relations regression:** `core/src/test/.../db/RecommendationRelationsTest.kt` loads Quick Picks and Forgotten Favorites with more than 100 candidates, checks stable seeded selections and artist relations (including artistless songs), exclusion filtering, and variety across seeds.
- **Tier 2 (Database Migration & Cache):** `core/src/test/.../db/Migration46To47Test.kt`, `Migration47To48Test.kt` (v47 -> v48 `taste_profile` table), and `app/src/test/.../generate/TasteProfileProviderTest.kt` (1-hour TTL cache, refresh, offline fallback).
- **Tier 3 (Repository Logic):** `app/src/test/.../generate/SongPlayStatsRepositoryTest.kt` (accumulated play time, skip tracking, error handling with fake DAOs).
- **Tier 4 (UI & Pipeline Flow Tests):** `app/src/test/.../generate/GenerateViewModelTest.kt` (state transitions, exclusions handling, cancel logic) and `RecommendationEngineTest.kt` (multi-source scoring, filtering, exclusion enforcement).

Run tests with:
```bash
./gradlew :core:testDebugUnitTest :app:testUniversalGmsDebugUnitTest
```

## CI

- `.github/workflows/android-build.yml` — runs `:core:testDebugUnitTest :app:testUniversalGmsDebugUnitTest` unit tests before assembling APK artifacts.
- `.github/workflows/codeql.yml` — static analysis / security scanning.

## Local Taste Engine

The Local Taste Engine (`echo.music.iad1tya.generate`) provides offline, account-free algorithmic recommendation and smart mix generation:
- **Database (v47, v48):**
  - v47: Introduces `song_play_stats` (keyed by normalized `trackKey` = `"${title.trim()}|${artist.trim()}".lowercase()`) and `recommendation_exclusions`. Auto-migration backfills legacy playback history without data loss.
  - v48: Re-introduces `taste_profile` table (`TasteProfileEntity`, `TasteProfileDao`) storing Last.fm top artists, tracks, and genre snapshots.
- **Last.fm Taste Integration (Slice 2 & 3):**
  - `LastFmTasteApi`: Ktor-based queries for `track.getSimilar`, `artist.getSimilar`, and `tag.getTopTags`.
  - `TasteProfileProvider`: Manages 1-hour TTL cache snapshot (in-memory + Room database cache, with fallback to 30-day local history).
  - `GenresRepository`: 1-hour in-memory TTL caching for track genres and weighted genre preferences derived from user history and taste profiles.
  - `RecommendationEngine`: Multi-source ranking with additive bonuses (`+2.0f` similar track, `+1.0f` similar artist, `+1.5f` user top genre match, `+1.0f` candidate genre match — max `+5.5f`), seed expansion, exclusion filtering, and YouTube music catalog resolution.
  - `:innertube` `fetchTasteSignals`: InnerTube query extracting candidate genre and mood signals from chip filters and shelf headers.
  - `GenerationStatus`: Cross-screen reactive status tracker (`StateFlow<GenerationState>`) coordinating generation states (`Idle`, `Running`, `Done`, `Failed`) across `GenerateViewModel` and dialogs.
- **Playback Hooks:** Integrated into `MusicService.kt` via `onPlaybackStatsReady` (Hook 1: listened duration and completion ratios) and `onMediaItemTransition` (Hook 2: skip detection).
- **Scoring & Candidate Expansion:** Pure function `scoreStats` applies play weights, heavy skip penalties (-3.0), liked bonuses (+5.0), 7-day recency boosts (+1.5), and time-of-day momentum (morning/night +1.3x). Seeds expand via YouTube related tracks, Last.fm similarity graph, and genre alignments.
- **UI & Navigation:** `GenerateScreen.kt` implements a clean Nothing OS 5.0 layout with surface translucency (`surfaceVariant.copy(alpha = 0.3f)`), flat elevation (`0.dp`), and `RoundedCornerShape(24.dp)` containers. Accessible via the Library Playlists tab and navigable to `local_playlist/{playlistId}` upon generation.
- **Quick Taste Generation:** `CreateAiPlaylistDialog` includes a "From Taste" quick action button, initiating local taste mix generation without requiring LLM setup.
- **Exclusion Action:** `SongMenu.kt` provides "Never recommend again" / "Allow in recommendations" action saving exclusions to `RecommendationExclusionEntity`.

## Music Flow

Optional full-screen vertical swipe discovery (`echo.music.iad1tya.musicflow`, route `music_flow`, settings route `settings/music_flow`). It is a UI/orchestration layer over the existing player — never add a player, ExoPlayer or service for it.

- **Playback ownership:** `MusicFlowCoordinator` turns settled pager pages into commands on the shared `MusicService` via the `MusicFlowPlayer` interface (`PlayerConnectionMusicFlowPlayer` in production). First takeover plays a `MusicFlowQueue` (a normal `Queue`); later swipes `seekTo` inside it. Flow "owns" playback only while `MusicService.activeQueue` is its queue instance — any playlist/radio/clear replaces it and Flow stops commanding. Requests carry increasing ids and are debounced in `MusicFlowViewModel`, so rapid swipes only command the final page.
- **Queue handoff:** before the first takeover, `MusicService.snapshotQueue()` captures the prior queue (in memory only, never persisted, so nothing stale is restored after process death). `Restore previous queue` calls `restoreQueueSnapshot` only while Flow still owns playback; a superseding queue invalidates it. Opening the normal player keeps the current Flow song.
- **Auto-next off:** `MusicService.setPauseAtEndOfItems(queue, true)` sets `pauseAtEndOfMediaItems` scoped to that queue; the `currentQueue` setter clears it as soon as another queue becomes active.
- **Feed:** `MusicFlowSession` (singleton) is append-only with stable page→song mapping, dedups by song id and `trackKeyOf`, caps at 300 items, ~12 initial items, prefetch when ≤4 remain, at most 3 fetches per load. `MusicFlowRepository` seeds from now playing, recent history, liked songs and Quick Picks, expands with InnerTube radio (`YouTube.next` `RDAMVM`), cold-starts from `YouTube.home()` songs, and falls back to downloaded songs offline. Filters reuse explicit/video/blocked-artist settings, `recommendation_exclusions`, hidden Quick Picks and (optionally) recently played. No trending/popularity labels — no verified source exists.
- **Signals:** Not interested = session hide; Never recommend again = `RecommendationExclusionEntity` (existing table, no migration); Less like this = session artist downweight. `MusicService` does not record a skip for a Flow track listened < `MusicFlowQueue.BROWSE_SKIP_THRESHOLD_MS` (10 s).
- **Preferences:** `MusicFlow*Key` in `PreferenceKeys.kt`, read through `MusicFlowPreferences.from` (animated background inherits `CanvasThumbnailAnimationKey` until set).
- **UI:** `ui/screens/musicflow/MusicFlowScreen.kt`, `ui/component/musicflow/` (`MusicFlowPage`, `MusicFlowEntryCard`). Route hides the nav rail and mini-player like `ambient_mode`; `LocalExpandPlayer` (MainActivity) lets "Open player" expand the existing sheet. Canvas reuses `AmbientCanvasBackground` on the active playing page only; lyrics reuse `InlineLyricsView`. `MusicFlowViewModel` fetches missing lyrics for the playing song itself via `LyricsHelper` (respecting `MusicFlowLyricsKey` and Data saver) because `MusicService`'s own fetch is gated on `ShowLyricsKey`, which nothing in the app sets — the player views fetch on demand the same way.
- **Long metadata:** Track titles and artist names use Compose marquee scrolling when they exceed the available width.
- **Playback control:** `MusicFlowPage` overlays a 56dp, 65%-white play/pause icon at the center of the album artwork, inside a transparent 72dp touch target. No filled background or bottom play/pause button; the bottom progress line retains 16dp bottom spacing. Direct icon taps call the shared playback action, and the glyph reflects the active song's playback state.
- **Lyric synchronization:** `MusicFlowScreen` samples the shared player's position every 50ms, including paused seeks, resets timing on song changes and applies the matching song's saved `lyricsOffset`. Parsed lyrics are keyed by media id and lyric contents. `MusicFlowLyricFrame` selects the current timestamp and separate upcoming nonblank line without promoting previews during intros or instrumental gaps. `MusicFlowLyricsView` animates timestamp-index changes with a 280ms slide/fade (180ms outgoing fade), reverses direction for backward seeks, and disables transitions for reduced motion. Timing regression tests live in `MusicFlowLyricFrameTest`.

## Ambient Mode Canvas

Ambient Mode keeps the existing album-art square and layers muted Canvas artwork inside that same clipped square. The normal album art remains underneath as the fallback; Canvas follows playback state and is non-interactive.

## Public owner links and distribution (27 October 2026)

KaiLinks now uses the owner's supplied Instagram, GitHub profile, Buy Me a Coffee and UPI values. Other unconfigured channels remain disabled. Public download/source documentation is for dipakdhurve9975-lgtm/KAI-Music; the first APK is explicitly a complete debug-signed Universal GMS preview. Never publish local signing material, account data, cached lyrics, machine paths or device QA files. Keep upstream GPL/contributor/third-party notices. Original lyric text remains primary by default, with user-selected Romanization/translation preferences preserved. The compact synced-lyrics view also validates track identity, shares the real clock/offset, preserves timed instrumental gaps and clips transitions.

## Minimal public download page (owner instruction, 27 October 2026)

The public GitHub default branch is a download page: README contains only the KAI description, one APK download button and the actual vinyl screenshot. Do not reintroduce contributor photos, tags, support sections or community/security/license sections into that README. Complete matching project source, existing licenses and notices are preserved in the separate public release source ZIP. Local development files remain intact. Device checks were explicitly deferred by the owner while build/publication continued.

## Additional Instagram profile (1.4.5, 27 October 2026)

The existing developer link group places a second Instagram row immediately below the owner's row, reusing the same icon, label, validated link action and handle subtitle. The supplied QR decodes to the canonical `https://www.instagram.com/____rohittt.____09/` profile. `KaiLink.INSTAGRAM_FRIEND` and optional appended friend configuration fields keep the two profiles independent and preserve existing constructor compatibility. Public preview 1.4.5 uses version code166 with unchanged package/data identities. Keep the minimal GitHub README; update its download target only after publishing the new APK/source release.

Public distribution builds use `-PkaiPublicBuild=true -PFLOW_NEURO_API_KEY=` and empty private OAuth/Last.fm inputs. This deliberately ignores any local google-services.json and excludes its generated Firebase/Crashlytics project configuration from the APK. Private local integration setup is preserved; the matching public source needs no Firebase project file.

CI follow-up: the first hosted run at source tag 7f0efaa failed before compilation because sdkmanager was absent from PATH. Main now uses the verified preinstalled SDK manager at ANDROID_HOME/cmdline-tools/latest/bin explicitly, accepts required SDK licenses with exit-status validation, enables the SDK preview channel and uses supported Node 24 actions. The setup-android default requested a retired tools package and was removed. Android source/APK are unchanged. Hosted success is not claimed until the follow-up run completes. Inherited Dependabot configuration opened version-update PRs on source restoration; new version-update PRs are now opt-in (limit 0), and existing PRs/branches were not deleted.
