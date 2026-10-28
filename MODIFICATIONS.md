# KAI modification notices — 28 October 2026

KAI Music is an independent modification of Echo Music by Aditya and its contributors, under GNU GPL-3.0 for GPL-covered code. Original notices/copyrights and separate third-party licenses remain. Independent KAI modifications are maintained by @dipakdhurve9975-lgtm. This notice does not claim authorship of inherited features.

## Known independent changes

- KAI branding and independent vector mark
- Vinyl/Normal/Lyrics presentation and shared retained-angle vinyl component
- Liquid selector/navigation, mini-player and home scroll behavior
- Ambient Normal/Vinyl, saved style and lifecycle/reduced-motion gating
- Lyrics presentation/fonts/provider/parser/session clock/refetch changes
- Original updater retirement and explicit manual KAI release entry
- About attribution, source/license notices and draft release/download infrastructure

## Evidence and dates

Compared with the original supplied Echo Music 1.4.4 archive (hash/provenance in docs/source-provenance.json); it contains no Git metadata, so the exact base commit is unknown. Earlier private dated checkpoints establish incremental KAI work. The date on file notices is the date differences were documented, not a claim about the precise original edit date or author of every difference. GPL-covered changes remain under GPL-3.0.

The following existing files were changed before this attribution/release task. New/changed source and build files in this task carry the same dated notice where their format permits comments. Binary/JSON/documentation changes are identified through this inventory, the Git diff and future tagged source revisions. The restricted font replacement is documented separately in docs/font-license-evidence.json; no original copyright header is removed.

## Existing modified files at audit start

- `.github/FUNDING.yml`
- `.gitignore`
- `AGENT.md`
- `DESIGN.md`
- `README.md`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/kotlin/com/music/echo/MainActivity.kt`
- `app/src/main/kotlin/com/music/echo/ai/AiRecommendationHelper.kt`
- `app/src/main/kotlin/com/music/echo/discord/DiscordSocialPresenceClient.kt`
- `app/src/main/kotlin/com/music/echo/discord/GatewayClient.kt`
- `app/src/main/kotlin/com/music/echo/echomusic/UpdateNotificationHelper.kt`
- `app/src/main/kotlin/com/music/echo/echomusic/component/UpdateAvailableDialog.kt`
- `app/src/main/kotlin/com/music/echo/echomusic/updater/downloadmanager/UpdateDownloadWorker.kt`
- `app/src/main/kotlin/com/music/echo/echomusic/updater/downloadmanager/downloadnotificationmanager.kt`
- `app/src/main/kotlin/com/music/echo/echomusic/updater/echomusicupdater.kt`
- `app/src/main/kotlin/com/music/echo/playback/MusicService.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/AppNavigation.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/AutoPlaylistButton.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/ChipsRow.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/FloatingNavBar.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/FloatingNavigationToolbar.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/GlassEffect.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/IconButton.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/Items.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/Lyrics.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/LyricsImageCard.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/Material3SettingsGroup.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/NavigationTitle.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/Preference.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/RandomizeGridItem.kt`
- `app/src/main/kotlin/com/music/echo/ui/component/floatingtabbar/FloatingTabBar.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/AlbumMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/ArtistMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/LyricsMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/OldPlayerMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/PlayerMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/PlaylistMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/PlaylistScreenMenus.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/QueueMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/menu/SongMenu.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/FloatingMiniPlayer.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/MiniPlayer.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/Player.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/PlayerSyncedLyricsView.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/Queue.kt`
- `app/src/main/kotlin/com/music/echo/ui/player/Thumbnail.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/ActivityHistory.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/AlbumScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/CrashActivity.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/HomeScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/SettingDialoge.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/WelcomeDialog.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/ambient/AmbientCanvasBackground.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/ambient/AmbientGlowBackground.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/ambient/AmbientModeScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/equalizer/axion/AxionEqViewModel.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibraryAlbumsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibraryArtistsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibraryMixScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibraryPlaylistsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibraryScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/library/LibrarySongsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/musicflow/MusicFlowScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/search/LocalSearchScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/search/SearchScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/search/suggestions/TabNewsSuggestion.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/AboutScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/AmbientSettingsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/AppIconSettingsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/AppearanceSettings.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/DiscordExperimental.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/DiscordSettings.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/FontSelectionScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/GlassEffectSettings.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/ListenBrainzManager.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/SearchableSettings.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/SettingsScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/ThemeScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/UpdateSettings.kt`
- `app/src/main/kotlin/com/music/echo/ui/screens/settings/UptimeScreen.kt`
- `app/src/main/kotlin/com/music/echo/ui/theme/Theme.kt`
- `app/src/main/kotlin/com/music/echo/ui/theme/Type.kt`
- `app/src/main/kotlin/com/music/echo/ui/utils/ShowOffsetDialog.kt`
- `app/src/main/kotlin/com/music/echo/utils/ComposeToImage.kt`
- `app/src/main/kotlin/com/music/echo/utils/CrashHandler.kt`
- `app/src/main/kotlin/com/music/echo/utils/DiscordImageResolver.kt`
- `app/src/main/kotlin/com/music/echo/utils/DiscordRPC.kt`
- `app/src/main/kotlin/com/music/echo/viewmodels/HomeViewModel.kt`
- `app/src/main/kotlin/com/music/echo/viewmodels/LyricsMenuViewModel.kt`
- `app/src/main/res/drawable/ic_launcher.xml`
- `app/src/main/res/drawable/widget_turntable_default_art.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `app/src/main/res/mipmap-anydpi/ic_launcher_static.xml`
- `app/src/main/res/mipmap-anydpi/ic_launcher_static_round.xml`
- `app/src/main/res/values-ar/echo_strings.xml`
- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-as/strings.xml`
- `app/src/main/res/values-az/strings.xml`
- `app/src/main/res/values-b+sr+Latn/strings.xml`
- `app/src/main/res/values-be/strings.xml`
- `app/src/main/res/values-bg/echo_strings.xml`
- `app/src/main/res/values-bg/strings.xml`
- `app/src/main/res/values-bn-rIN/strings.xml`
- `app/src/main/res/values-bn/strings.xml`
- `app/src/main/res/values-bs/strings.xml`
- `app/src/main/res/values-ca/echo_strings.xml`
- `app/src/main/res/values-ca/strings.xml`
- `app/src/main/res/values-cs/echo_strings.xml`
- `app/src/main/res/values-cs/strings.xml`
- `app/src/main/res/values-de/echo_strings.xml`
- `app/src/main/res/values-de/strings.xml`
- `app/src/main/res/values-el/echo_strings.xml`
- `app/src/main/res/values-el/strings.xml`
- `app/src/main/res/values-en-rCA/strings.xml`
- `app/src/main/res/values-es-rUS/echo_strings.xml`
- `app/src/main/res/values-es-rUS/strings.xml`
- `app/src/main/res/values-es/echo_strings.xml`
- `app/src/main/res/values-es/strings.xml`
- `app/src/main/res/values-es/updater_strings.xml`
- `app/src/main/res/values-et/strings.xml`
- `app/src/main/res/values-eu/strings.xml`
- `app/src/main/res/values-fa/echo_strings.xml`
- `app/src/main/res/values-fa/strings.xml`
- `app/src/main/res/values-fa/updater_strings.xml`
- `app/src/main/res/values-fi/strings.xml`
- `app/src/main/res/values-fil/strings.xml`
- `app/src/main/res/values-fr/echo_strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-fr/updater_strings.xml`
- `app/src/main/res/values-ga/strings.xml`
- `app/src/main/res/values-hi/strings.xml`
- `app/src/main/res/values-hr/strings.xml`
- `app/src/main/res/values-hu/echo_strings.xml`
- `app/src/main/res/values-hu/strings.xml`
- `app/src/main/res/values-in/echo_strings.xml`
- `app/src/main/res/values-in/strings.xml`
- `app/src/main/res/values-it/echo_strings.xml`
- `app/src/main/res/values-it/strings.xml`
- `app/src/main/res/values-iw/strings.xml`
- `app/src/main/res/values-ja/echo_strings.xml`
- `app/src/main/res/values-ja/strings.xml`
- `app/src/main/res/values-km/strings.xml`
- `app/src/main/res/values-ko/strings.xml`
- `app/src/main/res/values-lt/echo_strings.xml`
- `app/src/main/res/values-lt/strings.xml`
- `app/src/main/res/values-mfe/strings.xml`
- `app/src/main/res/values-ml/strings.xml`
- `app/src/main/res/values-ms/strings.xml`
- `app/src/main/res/values-nb-rNO/strings.xml`
- `app/src/main/res/values-night/strings.xml`
- `app/src/main/res/values-nl/echo_strings.xml`
- `app/src/main/res/values-nl/strings.xml`
- `app/src/main/res/values-nn/metrolist_strings.xml`
- `app/src/main/res/values-nn/strings.xml`
- `app/src/main/res/values-pa/strings.xml`
- `app/src/main/res/values-pl/echo_strings.xml`
- `app/src/main/res/values-pl/strings.xml`
- `app/src/main/res/values-pt-rBR/echo_strings.xml`
- `app/src/main/res/values-pt-rBR/strings.xml`
- `app/src/main/res/values-pt/echo_strings.xml`
- `app/src/main/res/values-pt/strings.xml`
- `app/src/main/res/values-ro/echo_strings.xml`
- `app/src/main/res/values-ro/strings.xml`
- `app/src/main/res/values-ru/echo_strings.xml`
- `app/src/main/res/values-ru/strings.xml`
- `app/src/main/res/values-sk/echo_strings.xml`
- `app/src/main/res/values-sk/strings.xml`
- `app/src/main/res/values-sl/echo_strings.xml`
- `app/src/main/res/values-sl/strings.xml`
- `app/src/main/res/values-sv/strings.xml`
- `app/src/main/res/values-ta/strings.xml`
- `app/src/main/res/values-te/strings.xml`
- `app/src/main/res/values-th/echo_strings.xml`
- `app/src/main/res/values-th/strings.xml`
- `app/src/main/res/values-tr/echo_strings.xml`
- `app/src/main/res/values-tr/strings.xml`
- `app/src/main/res/values-tr/updater_strings.xml`
- `app/src/main/res/values-uk/echo_strings.xml`
- `app/src/main/res/values-uk/strings.xml`
- `app/src/main/res/values-vi/echo_strings.xml`
- `app/src/main/res/values-vi/strings.xml`
- `app/src/main/res/values-wae/strings.xml`
- `app/src/main/res/values-zh-rCN/echo_strings.xml`
- `app/src/main/res/values-zh-rCN/strings.xml`
- `app/src/main/res/values-zh-rTW/echo_strings.xml`
- `app/src/main/res/values-zh-rTW/strings.xml`
- `app/src/main/res/values/app_name.xml`
- `app/src/main/res/values/echo_strings.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/updater_strings.xml`
- `artistvideo/build.gradle.kts`
- `audio-dsp/build.gradle.kts`
- `betterlyrics/build.gradle.kts`
- `core/build.gradle.kts`
- `innertube/build.gradle.kts`
- `kugou/build.gradle.kts`
- `lrclib/build.gradle.kts`
- `lrclib/src/main/kotlin/com/music/lrclib/LrcLib.kt`
- `lrclib/src/main/kotlin/com/music/lrclib/models/Track.kt`
- `lyrics/build.gradle.kts`
- `lyrics/src/main/kotlin/echo/music/iad1tya/lyrics/LyricsEntry.kt`
- `lyrics/src/main/kotlin/echo/music/iad1tya/lyrics/LyricsHelper.kt`
- `lyrics/src/main/kotlin/echo/music/iad1tya/lyrics/LyricsUtils.kt`
- `paxsenixlyrics/build.gradle.kts`
- `playback/build.gradle.kts`
- `shazamkit/build.gradle.kts`
- `simpmusic/build.gradle.kts`
- `upcomingupdate.json`
- `usbaudio/build.gradle.kts`
- `youlyplus/build.gradle.kts`
- `youlyplus/src/main/kotlin/com/music/youlyplus/YouLyPlus.kt`
- `youlyplus/src/main/kotlin/com/music/youlyplus/models/LyricsResponse.kt`

## This task

About attribution and license viewer; manual KAI releases link; GPL/Apache/OFL materials; official licensed Google Sans Flex replacement; removal of inherited build-script Last.fm credentials; full sanitized source restoration; README/origins/notices/compliance/dependency evidence; signature-checked draft-release and download-refresh automation. Versions increase to 1.4.8 (169), preserving IDs/storage. Exact per-file task changes are listed in the final review report. No playback/lyrics algorithm is changed in this task.


## 29 October 2026 — focused lyrics candidate 1.4.9

Correct retained lyric-sampler visibility after asynchronous loading and test the callback through actual Compose recomposition. Preserve original Echo Music playback timestamp/index/scrolling behavior, KAI typography, providers, package/data/signing identities. Move the verified preview download to the top of README (published separately). The owner clarified that the other attached-prompt objectives remain requested; the KAI-only updater is combined after the isolated lyric regression check. The updater introduces metadata validation, bounded streaming downloads, installed-signer/APK verification, optional release dialogs and Android installation handoff; no music/data identity changes.


## 29 October 2026 — 1.4.10 follow-up

Enable compatible preview updates by default for preview builds while preserving explicit choices, reduce successful-check cache to one hour, and invalidate checks across channel changes. Move the attributed original creator Instagram below the owner in About, preserving other links and the existing lyric repair. Package/signing/data identities remain compatible.
