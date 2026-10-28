# About attribution pattern (28 October 2026)

About retains the KAI header, theme, type scale, player insets and LazyColumn. Ordered sections distinguish origins, the original creator, independent KAI development and existing account links, then licensing. A restrained tinted acknowledgment panel has wrapping 48dp Material text buttons. Long origin/thanks text expands on demand; no nested scroll cards or network avatars. License text opens in a bounded scrollable dialog, loaded from local assets off the main thread. All labels use Android resources and font scaling can wrap them. No decorative animation or added framework is needed. Updates is an explicit browser link to KAI releases, with no automatic feed.

# Lyrics typography — 28 October 2026

Use the existing Lyrics style & font dialog for five presentation choices, a compact font dropdown, static soft-glow switch and bilingual preview. Keep all customization inside this dialog and the existing menus; do not add permanent player buttons or another settings screen. Follow theme is the default. Use bundled Jakarta/Outfit, Android system/serif and script fallback; preserve Devanagari joining and exact original text. A single bounded text shadow replaces continuous line-only glow gradients.

# KAI update placeholder — 28 October 2026

Update Settings contains one disabled System update row labeled Coming soon, using the existing grouped settings surface. Omit remote changelogs, update badges, check/download buttons and automatic dialogs. Saved legacy updater navigation shows the same screen. Keep the existing back navigation, content insets and translated labels.

# KAI Design Guidelines

## Navigation and lyrics reliability (27 October 2026)

Root-to-root navigation is immediate; detail routes use a restrained 140ms fade. The shared liquid control starts its tap settle before dispatching navigation and retains its in-flight release spring. Existing entry ownership, singleTop and save/restore remain intact. The full player's 200ms transport polling is limited to its expanded state so tab changes do not compete with hidden player recomposition.

Lyrics distinguishes actual progression from zero/constant placeholder timestamps. Untimed lyrics are readable static text with a compact timing-unavailable label and no fake highlight/seek. The parser accepts whole seconds, 1/2/3-digit fractions and timed blank gaps; stable sorting and upper-bound lookup use the real playback timeline plus the saved per-song offset, with no implicit 300ms lead. Scroll follow uses one cancellable effect and is suspended only by actual user scrolling. The clipped, finite lyrics viewport is separate from the song information/controls; short/high-font layouts scroll the bounded controls region. Debug-only KaiLyricsSync logs record position/index/timestamps/offset/target without lyric text.

LyricsHelper is process-scoped, shares in-flight/cache results, honors provider enablement/order and keys search options by track/duration/metadata. One bounded session attempt may upgrade automatically fetched untimed lyrics with genuinely timed existing-provider data, retaining text on failures or concurrent user edits. YouLyPlus leaves missing timing as plain text; LRCLib automatic matching requires title/artist eligibility as well as duration. Keep the existing per-song ±3s offset range/reset and compatible values; all adjustment buttons now use 100ms.


## Home dock motion and selector settle correction (27 October 2026)

Home's navigation bar and mini-player translate together after 12dp of consumed downward content motion and return on upward scrolling. The shared 180ms hide / 220ms reveal preserves content insets, never consumes the scroll, resets on navigation and player expansion, and snaps with reduced motion. Expanded Now Playing and landscape rails are never translated. Direction hysteresis prevents tiny reversals from flickering the dock.

The liquid selector retains an in-flight spring when navigation confirms its existing release target, avoiding a second zero-velocity animation. Layout, RTL and reduced-motion changes still force a fresh settle; different programmatic selections retarget normally.


## Ambient and finishing details (27 October 2026)

Ambient uses a dark, muted album atmosphere with clear white text. Keep the original Normal artwork/lyrics composition and add Vinyl as a second landscape presentation: record on the left, restrained metadata and optional lyrics on the right. Share the record renderer and audio session. A compact Normal/Vinyl selector belongs in the controls overlay, which fades during playback inactivity and reappears on touch; retain accessible exit and reveal actions. Remember the choice without an entry confirmation dialog.

The record has fine low-contrast grooves, a dark outer edge, a recessed artwork label and stationary soft reflection/spindle. Avoid bright metallic rings. The integrated K/note silhouette is an original vector, with warm white on burgundy and a monochrome equivalent for themed icons. The KAI spiral is a small in-app loading signature, never an artificial splash delay.

Home retains vivid artwork and real editorial sections. Use consistent24dp section margins, stable shelf identities, readable title truncation and quiet mood chips. Keep cached music visible while refreshing; distinguish actual initial loading, pagination and errors with compact feedback/retry. Library shortcuts form one compact collection group above sorting and real library content, preserving every existing collection.

## Liquid player selector

The same gesture system also drives main bottom navigation, using fixed equal-width icon/label cells and one moving translucent lens over one stationary frosted surface. Do not measure widths that change with selection, and do not animate the whole bar for tab changes. Keep selection derived from the current navigation entry, including its saved root owner on details. More reflects the actual open sheet. Mini-player and navigation share16dp margins and an8dp gap, with content insets covering their full bounds; mini-player corners remain24dp while navigation is a capsule. Glass is enabled on fresh settings, supported fromAPI31, with a bounded blur/refraction budget and readable fallback. Preserve explicit saved appearance choices.

Use one compact pill with a continuous, finger-tracked highlight, restrained directional stretch and a soft spring settle. Selection commits once on release; labels retain the committed state during preview. Vertical gestures stay available to the parent sheet. Keep 48dp targets and Tab semantics. The lens uses a strongly tinted surface and thin highlight, sampling only the player's background when glass is enabled and supported; otherwise use the same readable opaque palette. Reduced motion keeps direct touch tracking and removes decorative elasticity/settling.

Lyrics styles live in one compact chooser in existing lyrics/appearance controls. Classic Flow is the reliable default; Floating uses gentle layer movement, Cinematic uses larger phrases and extra breathing room, Kinetic reveals only validated real source words, and Minimal Focus reduces surrounding emphasis. Keep original and Romanized/translated hierarchy intact, with Android fallback for scripts absent in the bundled Latin font. Auto is a stable per-song choice, never a periodic shuffle. Use muted album gradients with cached atmospheric light fields, strong foreground contrast and lifecycle-gated motion.

## Premium KAI direction (26 October 2026)

This section supersedes the historical dynamic-first/Nothing OS guidance below. Use an original, restrained KAI identity inspired by the clarity of premium music players. Do not copy Apple branding or the reference screenshots' assets.

| Role | Dark | Light |
| --- | --- | --- |
| Background | `#100D10` | `#FFFAF7` |
| Surface | `#1C171C` | `#F6EEF0` |
| Elevated surface | `#292127` | `#EFE5E9` |
| Primary | `#D85C78` | `#8D314C` |
| Primary text | `#F8F3F5` | `#241C22` |
| Secondary text | `#AAA0A6` | `#6B5D65` |

`KaiDesign.kt` is the source of shared spacing, shapes and motion. Use MaterialTheme semantic roles instead of copying hex values into screens. The default is the bundled, OFL-licensed Plus Jakarta Sans; existing explicit font selections remain available. Headings use 36/30sp bold and 24sp semibold, titles 23/18/15sp, body 16/15sp, secondary 13sp and metadata 12sp via theme typography. Respect system font scale and multiline intrinsic row heights.

Page content follows a 24dp margin and 4/8/12/16/24/32/48dp rhythm. Artwork uses 16dp corners; grouped cards use 24dp corners; chips and the mode selector use a pill. Shared headers, empty states, rounded artwork and clear title/metadata hierarchy carry the design through Home, Library, Search and Settings. Group settings semantically rather than by row position. Use actual data and stable identities, without staged delays or item-by-item entrance effects.

Player chrome adds context, a 48dp collapse action and an overflow action; the visual is Vinyl, Normal artwork or real lyrics. All use existing metadata, engine and queue. Thin progress tracks retain their touch/seek behavior. Play/Pause crossfades in 140ms and transport presses scale subtly; reduced motion removes these transitions. Mode labels can wrap and omit decorative icons to fit compact phones and large text. Use the calculated player foreground for lyric text and controls, including the light theme.

Mini-player artwork is compact and rounded with metadata and accessible transport controls. Progress is drawn from lifecycle-scoped state so polling does not recompose artwork. Tapping metadata opens the existing player sheet, while buttons and swipe gestures retain their own actions. Preserve local/Cast and Listen Together routing. Glass remains selective in mini-player/navigation surfaces and respects support checks/preferences; library rows, About and settings groups are opaque.

Standard feedback uses 140ms and UI transitions 260ms, with FastOutSlowInEasing and no excessive bounce. One shared system observer supplies reduced-motion state. Vinyl frame work and lyric/progress updates stop when the UI is not resumed. Preserve explicit legacy appearance options; fresh preferences use KAI colors, a circular transport button and readable fade lyrics.

About and Welcome share the KAI mark, tagline, actual version/channel and grouped developer/support/community rows. Configure public destinations only in `branding/KaiLinks.kt`. Missing or invalid links show disabled Coming soon; do not invent accounts or reuse the upstream developer's payment/profile links. Retain required legal/source attribution.

## Vinyl Now Playing

The user-requested vinyl view extends the existing player rather than introducing another playback surface or engine. Use an original Canvas-drawn grooved black record on a brushed, rounded, theme-colored plinth. Crop live album artwork to a circular center label with a Kai/theme fallback and metallic center spindle. Keep the tonearm on a separate stationary layer; animate it around its own pivot between resting and engaged positions. A theme-aware Vinyl / Normal / Lyrics tab row below transport controls uses a subtle animated selection fill, 48dp minimum targets and selected tab semantics. The existing artwork-derived background supplies ambient color.

Rotate only the disc/label graphics layer at 33 1/3 RPM, retaining its angle on pause and across mode switches. Stop frame work when hidden, backgrounded, buffering, or reduced motion is enabled. Preserve existing metadata, seeking, shuffle/repeat, queue and sleep controls. Fit the square visual inside available bounds, and allow controls to scroll on compact portrait and landscape layouts. QQ Music and MD Vinyl user-supplied screenshots are visual references only; assets and implementation are original.

Echo Music follows a **custom, modern aesthetic** that blends some Material Design principles with unique, iOS-inspired patterns. 

This document is the definitive guide for designing and implementing UI in the Echo Music codebase. All new UI work and refactors must follow these custom principles rather than strictly adhering to Google's Material Design 3 spec.

---

## 1. Color System & Theming

We use a dynamic color system, but apply it in a custom way to achieve a unique look.

### Dynamic Color & Seed
*   **Dynamic First:** Colors must come from `MaterialTheme.colorScheme`. For devices running Android 12+, dynamic system colors are extracted. On older devices, the default theme seed `0xFFED5564` is used to generate the palette using `materialKolor`.
*   **Translucency:** A core part of the Echo Music look is translucent surfaces. For example, cards often use `surfaceVariant.copy(alpha = 0.3f)` rather than solid M3 container colors.

### Semantic Color Roles
Use the correct semantic color roles as defined by our theme:
*   **Primary (`primary` / `onPrimary`):** Used for the most prominent components across the app, active states, and filled buttons.
*   **Surface (`surface` / `onSurface`):** Backgrounds for the app and solid menus.
*   **Translucent Surfaces:** Custom translucent backgrounds (like `surfaceVariant.copy(alpha = 0.3f)`) are heavily used for cards, segmented buttons, and grouped lists to create a softer, layered aesthetic.

---

## 2. Liquid Glass System (Glassmorphism)

A signature part of Echo Music's design is the **Liquid Glass** effect, which provides high-quality blur, refraction, and translucency to navigation bars, headers, and media players.

### Core Implementation
The Liquid Glass effect is driven by a custom `Modifier.liquidGlass()` extension found in `GlassEffect.kt`. It utilizes a heavily customized RenderEffect pipeline (available on Android 12 / API 31+) over a recorded `Backdrop`.

```kotlin
// Basic Usage
Modifier.liquidGlass(
    config = LocalGlassEffectConfig.current,
    shape = RoundedCornerShape(24.dp),
    applyEdgeEffects = true // Set to false for full-screen surfaces
)
```

### Technical Details & Parameters

1. **Backdrop Resolution Scaling:** To maintain high performance, the glass surface records and processes its backdrop at a lower resolution (down to `33%`), relying on the blur to hide the upscaling. The `glassResolutionScale` function dynamically adjusts this based on the requested blur radius.
2. **API Requirements:** Glass requires Android 12 (`Build.VERSION_CODES.S`). Devices on older versions gracefully fall back to solid/standard transparency by checking `isGlassSupported()`.
3. **Lens Refraction (Edge Effects):** Small pills (like floating bottom bars) read as physical glass by applying edge effects. These include lens refraction (`lensHeight`, `lensAmount`), a specular highlight rim (`Highlight.Default`), and a drop shadow (`Shadow.Default`). **Note:** These are enabled via `applyEdgeEffects = true` and rely on `CornerBasedShape`s. (Using a non-corner-based shape throws `UnsupportedOperationException`).
4. **Full-Screen Blur Tuning:** For large surfaces like the full-screen player background, edge effects should be disabled (`applyEdgeEffects = false`) to avoid a stray band of light. Instead, they use a `PLAYER_BLUR_MULTIPLIER` (4x the standard pill blur) to match the deep-blurred material seen in Apple Music.

### Color and Vibrancy
*   **Vibrancy:** Configurable saturation multiplier (default `1.5x` saturation) applies directly via `colorControls` RenderEffect.
*   **Surface Tint:** Apple's glass is typically a light material on light content and dark on dark. If `surfaceTintColor` is unspecified, it defaults to `0xFFFAFAFA` for light mode and `0xFF121212` for dark mode, layered with a typical `surfaceOpacity` of `0.4f`.
*   **Flat Integration:** Action buttons (like FABs or overflow menus) placed on top of Liquid Glass surfaces must use flat elevations (`elevation = 0.dp`). M3 drop shadows interact poorly with translucent layers and will render as ugly dark blobs beneath the component.

---

## 3. Components in Detail

Do NOT strictly force Material 3 components if they break the app's custom aesthetic. Match the existing components found in the app.

### Buttons & Controls
*   **Segmented Controls:** We frequently use custom segmented buttons (e.g., Row with rounded buttons) rather than M3 standard tabs or segmented buttons.
*   **Rounded Shapes:** Elements heavily lean towards large corner radii (`RoundedCornerShape(24.dp)` or `CircleShape`), providing a friendlier, softer UI.
*   **Settings Groups:** We use a grouped-list style (similar to iOS Settings) by encapsulating lists within a `surfaceContainerHighest` card with `24.dp` corners, rather than separate pill cards.

### Cards & Surfaces
*   **Custom Cards:** Unlike standard M3 cards (which use solid `surfaceContainer` colors), Echo Music cards typically use:
    *   *Container:* `surfaceVariant.copy(alpha = 0.3f)`
    *   *Shape:* `RoundedCornerShape(24.dp)` or `28.dp`
    *   *Elevation:* 0.dp (flat, translucent look).
*   Grouped items within cards are a common pattern.

### Navigation & Headers
*   **Top App Bars:** We often use custom implementations or standard `TopAppBar` rather than `LargeTopAppBar`. Headers are sometimes manually placed over scrolling content with custom fade-in animations rather than using standard M3 `Scaffold` scroll behaviors.
*   **Bottom Navigation Bar:** Custom floating tab bars (`ui/component/floatingtabbar/`) are preferred over the standard M3 `NavigationBar`.

---

## 4. Typography & Iconography

*   **Typography:** Always use `MaterialTheme.typography` but respect the app's established font weights and sizes. Echo Music leans towards bold, expressive headers and softer, highly legible body text.
*   **Iconography:** We use a mix of Material Symbols/Icons Extended (`androidx.compose.material.material-icons-extended`) and custom SVG drawables. Check `scripts/compose_svg_drawable.py` and existing drawables before importing new vector assets to avoid duplication.

---

## 5. Extending the Design System

Before adding a brand new UI component, always check `ui/component/` to see if an existing one already implements our conventions.

**Key Rule:** When working on UI, **look at the existing screens** (like the original Listen Together or Settings screens) and copy their specific visual style, spacing, and modifier chains. Do NOT refactor existing screens to match standard Material 3 unless explicitly requested. Our custom aesthetic takes precedence over M3 guidelines.


## Music Flow

Vertical swipe discovery, laid out like a Resso-style "For You" page (interaction pattern only — no copied assets).

*   **Full-bleed artwork:** Black base, the artwork blurred (`blur(80.dp)` on API 31+) as a tinted backdrop, and the sharp artwork as a centred square with 24dp side padding and 24dp rounded corners. A page-wide scrim darkens the top (0.45) and bottom (0.8 → 0.95) for text contrast. Muted Canvas replaces the square on the active playing page only.
*   **Lyrics first:** Under the transparent top bar, the current synced line in `headlineMedium` ExtraBold white, the next line in `titleLarge` at 45% white. The pair advances at the next lyric timestamp with a 280ms upward slide/fade (24dp incoming travel, 12dp outgoing travel, 180ms outgoing fade) and smooth height changes; backward seeks reverse the slide. Reduced motion disables transitions. Position is sampled every 50ms, including paused seeks, with the saved per-song timing offset applied. Intros and timestamped instrumental gaps keep the upcoming line dimmed until its timestamp. Hidden when no synced lyrics exist.
*   **Metadata:** Bottom-left title (`headlineSmall`, bold), artist (`titleMedium`, 75% white) followed by an outlined "View artist" pill (1dp 40% white border, 24dp radius).
*   **Action row:** Icon-only 48dp touch targets with spoken labels — like, lyrics, share on the left; download, add to playlist, more on the right. No counts (we have no real numbers to show). Disabled actions drop to 35% alpha.
*   **Playback:** A 56dp play glyph at 65% white appears centered inside the album artwork only while playback is paused, with a transparent 72dp touch target and no filled button background. The glyph hides while playing; tapping the target toggles the shared player. The 2dp progress line with an 8dp white thumb stays at the bottom, with 16dp bottom spacing. Tapping anywhere on the page also toggles playback; double-tap likes; long-press opens `YouTubeSongMenu`.
*   **Chrome:** Top bar is just back, "Music Flow" and an overflow (Previous/Next for people who cannot swipe, Open player, Restore previous queue, feedback, Settings). Previous/Next are also TalkBack custom actions on the pager. A translucent status pill (`White 14%`, 24dp radius) reports loading, casting/Listen Together blocks and "playing elsewhere".
*   **Home entry:** `MusicFlowEntryCard` follows the card rule above — `surfaceVariant.copy(alpha = 0.3f)`, 24dp corners, flat — with an inverted (onSurface) circular icon.

## Ambient Mode Canvas

Ambient Mode may layer muted Canvas video artwork inside the existing album-art square. Keep the original square size, rounded clipping, and interaction surface unchanged; Canvas is a non-interactive visual layer above the normal album art and follows playback state. The existing glow background remains separate underneath the screen.

## Public owner links and distribution (27 October 2026)

KaiLinks now uses the owner's supplied Instagram, GitHub profile, Buy Me a Coffee and UPI values. Other unconfigured channels remain disabled. Public download/source documentation is for dipakdhurve9975-lgtm/KAI-Music; the first APK is explicitly a complete debug-signed Universal GMS preview. Never publish local signing material, account data, cached lyrics, machine paths or device QA files. Keep upstream GPL/contributor/third-party notices. Original lyric text remains primary by default, with user-selected Romanization/translation preferences preserved. The compact synced-lyrics view also validates track identity, shares the real clock/offset, preserves timed instrumental gaps and clips transitions.

## Minimal public download page (owner instruction, 27 October 2026)

The public GitHub default branch is a download page: README contains only the KAI description, one APK download button and the actual vinyl screenshot. Do not reintroduce contributor photos, tags, support sections or community/security/license sections into that README. Complete matching project source, existing licenses and notices are preserved in the separate public release source ZIP. Local development files remain intact. Device checks were explicitly deferred by the owner while build/publication continued.

## Additional Instagram profile (1.4.5, 27 October 2026)

The existing developer link group places a second Instagram row immediately below the owner's row, reusing the same icon, label, validated link action and handle subtitle. The supplied QR decodes to the canonical `https://www.instagram.com/____rohittt.____09/` profile. `KaiLink.INSTAGRAM_FRIEND` and optional appended friend configuration fields keep the two profiles independent and preserve existing constructor compatibility. Public preview 1.4.5 uses version code166 with unchanged package/data identities. Keep the minimal GitHub README; update its download target only after publishing the new APK/source release.
