/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import echo.music.iad1tya.LocalPlayerAwareWindowInsets
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.component.KaiScreenHeader
import echo.music.iad1tya.ui.component.Material3SettingsGroup
import echo.music.iad1tya.ui.component.Material3SettingsItem
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.theme.KaiShapes
import echo.music.iad1tya.ui.theme.KaiSpacing
import echo.music.iad1tya.ui.utils.backToMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
  highlightKey: String? = null,
) {
  val uriHandler = LocalUriHandler.current
  val context = LocalContext.current
  val keyboard = LocalSoftwareKeyboardController.current
  val isAndroid12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  var searchQuery by rememberSaveable { mutableStateOf("") }
  val searchLower = searchQuery.lowercase()

  val accountText = stringResource(R.string.account)
  val appearanceText = stringResource(R.string.appearance)
  val playerText = stringResource(R.string.player_and_audio)
  val listenTogetherText = stringResource(R.string.listen_together)
  val contentText = stringResource(R.string.content)
  val aiLyricsText = stringResource(R.string.ai_lyrics_translation)
  val privacyText = stringResource(R.string.privacy)
  val storageText = stringResource(R.string.storage)
  val backupText = stringResource(R.string.backup_restore)
  val systemUpdateText = stringResource(R.string.system_update)
  val aboutText = stringResource(R.string.about)

  val accountDesc = stringResource(R.string.setting_desc_account)
  val appearanceDesc = stringResource(R.string.setting_desc_appearance)
  val playerDesc = stringResource(R.string.setting_desc_player)
  val listenTogetherDesc = stringResource(R.string.setting_desc_listen_together)
  val contentDesc = stringResource(R.string.setting_desc_content)
  val aiLyricsDesc = stringResource(R.string.setting_desc_ai)
  val privacyDesc = stringResource(R.string.setting_desc_privacy)
  val storageDesc = stringResource(R.string.setting_desc_storage)
  val backupDesc = stringResource(R.string.setting_desc_backup)
  val systemUpdateDesc = stringResource(R.string.kai_manual_updates)
  val aboutDesc = stringResource(R.string.setting_desc_about)

  val scrollState = rememberScrollState()
  Column(
    Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal)
      )
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(
      Modifier.windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top))
    )
    KaiScreenHeader(
      title = stringResource(R.string.settings),
      subtitle = stringResource(R.string.kai_settings_subtitle),
      horizontalPadding = KaiSpacing.xs,
    )

    TextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text(stringResource(R.string.search)) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Rounded.Search,
          contentDescription = stringResource(R.string.search),
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(
              imageVector = Icons.Rounded.Clear,
              contentDescription = stringResource(R.string.kai_clear_search),
            )
          }
        }
      },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
      shape = KaiShapes.card,
      colors =
        TextFieldDefaults.colors(
          focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
          unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
          disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        ),
      modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
    )

    val itemsList = buildList {
      if (
        accountText.lowercase().contains(searchLower) ||
          accountDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == accountText),
            icon = painterResource(R.drawable.account),
            groupKey = "account",
            title = { Text(accountText) },
            description = { Text(accountDesc) },
            onClick = { navController.navigate("settings/account") },
          )
        )
      }

      if (
        aiLyricsText.lowercase().contains(searchLower) ||
          aiLyricsDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == aiLyricsText),
            customIcon = {
              Text(
                text = "Ai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color =
                  if (highlightKey == aiLyricsText) MaterialTheme.colorScheme.onSurface
                  else MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            groupKey = "account",
            title = { Text(aiLyricsText) },
            description = { Text(aiLyricsDesc) },
            onClick = { navController.navigate("settings/ai") },
          )
        )
      }

      if ("listening summary".contains(searchLower)) {
        add(
          Material3SettingsItem(
            icon = painterResource(echo.music.iad1tya.R.drawable.history),
            groupKey = "activity",
            title = { Text(stringResource(echo.music.iad1tya.R.string.listening_summary)) },
            description = {
              Text(stringResource(echo.music.iad1tya.R.string.listening_summary_desc))
            },
            onClick = { navController.navigate("settings/listening_summary") },
          )
        )
      }

      if (
        appearanceText.lowercase().contains(searchLower) ||
          appearanceDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == appearanceText),
            icon = painterResource(R.drawable.palette),
            groupKey = "experience",
            title = { Text(appearanceText) },
            description = { Text(appearanceDesc) },
            onClick = { navController.navigate("settings/appearance") },
          )
        )
      }
      if (
        playerText.lowercase().contains(searchLower) || playerDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == playerText),
            icon = painterResource(R.drawable.play),
            groupKey = "experience",
            title = { Text(playerText) },
            description = { Text(playerDesc) },
            onClick = { navController.navigate("settings/player") },
          )
        )
      }
      val musicFlowText = stringResource(R.string.music_flow)
      val musicFlowDesc = stringResource(R.string.music_flow_settings_description)
      if (
        musicFlowText.lowercase().contains(searchLower) ||
          musicFlowDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == musicFlowText),
            icon = painterResource(R.drawable.swipe),
            groupKey = "experience",
            title = { Text(musicFlowText) },
            description = { Text(musicFlowDesc) },
            onClick = { navController.navigate("settings/music_flow") },
          )
        )
      }
      if (
        listenTogetherText.lowercase().contains(searchLower) ||
          listenTogetherDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == listenTogetherText),
            icon = painterResource(R.drawable.group),
            groupKey = "experience",
            title = { Text(listenTogetherText) },
            description = { Text(listenTogetherDesc) },
            onClick = { navController.navigate(Screens.ListenTogether.route) },
          )
        )
      }
      if (
        contentText.lowercase().contains(searchLower) ||
          contentDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == contentText),
            icon = painterResource(R.drawable.language),
            groupKey = "data",
            title = { Text(contentText) },
            description = { Text(contentDesc) },
            onClick = { navController.navigate("settings/content") },
          )
        )
      }

      if (
        privacyText.lowercase().contains(searchLower) ||
          privacyDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == privacyText),
            icon = painterResource(R.drawable.security),
            groupKey = "data",
            title = { Text(privacyText) },
            description = { Text(privacyDesc) },
            onClick = { navController.navigate("settings/privacy") },
          )
        )
      }
      if (
        storageText.lowercase().contains(searchLower) ||
          storageDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == storageText),
            icon = painterResource(R.drawable.storage),
            groupKey = "data",
            title = { Text(storageText) },
            description = { Text(storageDesc) },
            onClick = { navController.navigate("settings/storage") },
          )
        )
      }

      if (
        backupText.lowercase().contains(searchLower) || backupDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == backupText),
            icon = painterResource(R.drawable.restore),
            groupKey = "data",
            title = { Text(backupText) },
            description = { Text(backupDesc) },
            onClick = { navController.navigate("settings/backup_restore") },
          )
        )
      }
      if (
        systemUpdateText.lowercase().contains(searchLower) ||
          systemUpdateDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == systemUpdateText),
            icon = painterResource(R.drawable.update),
            groupKey = "app",
            title = { Text(systemUpdateText) },
            description = { Text(systemUpdateDesc) },
            onClick = { navController.navigate("settings/update") },
          )
        )
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if ("supported links".contains(searchLower)) {
          add(
            Material3SettingsItem(
              isHighlighted = (highlightKey == "supported links"),
              icon = painterResource(R.drawable.link),
              groupKey = "app",
              title = { Text("Supported Links") },
              description = { Text("App linking settings") },
              onClick = {
                try {
                  val intent =
                    Intent(
                      Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                      Uri.parse("package:${context.packageName}"),
                    )
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  context.startActivity(intent)
                } catch (e: Exception) {
                  when (e) {
                    is ActivityNotFoundException,
                    is SecurityException -> {
                      Toast.makeText(context, "Cannot open settings", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                      Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                  }
                }
              },
            )
          )
        }
      }
      if (
        aboutText.lowercase().contains(searchLower) || aboutDesc.lowercase().contains(searchLower)
      ) {
        add(
          Material3SettingsItem(
            isHighlighted = (highlightKey == aboutText),
            icon = painterResource(R.drawable.info),
            groupKey = "app",
            title = { Text(aboutText) },
            description = { Text(aboutDesc) },
            onClick = { navController.navigate("settings/about") },
          )
        )
      }
    }

    val finalItemsList =
      if (searchQuery.isNotEmpty()) {
        val subSettings = getAllSearchableSettings()

        val matchedSubSettings =
          subSettings
            .filter {
              it.title.lowercase().contains(searchLower) ||
                (it.description?.lowercase()?.contains(searchLower) == true)
            }
            .map { setting ->
              Material3SettingsItem(
                icon = painterResource(R.drawable.search),
                title = { Text(setting.title) },
                description = {
                  if (setting.description != null) {
                    Text("${setting.category} • ${setting.description}")
                  } else {
                    Text(setting.category)
                  }
                },
                onClick = {
                  val encodedTitle = Uri.encode(setting.title)
                  val finalRoute =
                    if (setting.route.contains("?")) "${setting.route}&highlightKey=$encodedTitle"
                    else "${setting.route}?highlightKey=$encodedTitle"
                  navController.navigate(finalRoute)
                },
              )
            }

        itemsList + matchedSubSettings
      } else {
        itemsList
      }

    if (searchQuery.isNotEmpty() && finalItemsList.isEmpty()) {
      Spacer(modifier = Modifier.height(32.dp))
      Text(
        text = "No settings found for \"$searchQuery\"",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      )
    } else if (searchQuery.isNotEmpty()) {
      Spacer(modifier = Modifier.height(16.dp))
      Material3SettingsGroup(scrollState = scrollState, items = finalItemsList)
    } else {
      Spacer(modifier = Modifier.height(16.dp))

      val groups =
        listOf(
          "account" to R.string.kai_settings_account_group,
          "activity" to R.string.kai_settings_activity_group,
          "experience" to R.string.kai_settings_experience_group,
          "data" to R.string.kai_settings_data_group,
          "app" to R.string.kai_settings_app_group,
        )
      groups.forEach { (key, title) ->
        val groupItems = itemsList.filter { (it.groupKey ?: "app") == key }
        if (groupItems.isNotEmpty()) {
          Material3SettingsGroup(
            title = stringResource(title),
            scrollState = scrollState,
            items = groupItems,
          )
          Spacer(Modifier.height(KaiSpacing.lg))
        }
      }
    }

    Spacer(modifier = Modifier.height(50.dp))
    Spacer(
      Modifier.windowInsetsPadding(
        LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)
      )
    )
  }

  TopAppBar(
    title = {
      androidx.compose.animation.AnimatedVisibility(
        visible = scrollState.value > 100,
        enter = androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.fadeOut(),
      ) {
        Text(text = stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge)
      }
    },
    navigationIcon = {
      IconButton(onClick = navController::navigateUp, onLongClick = navController::backToMain) {
        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
      }
    },
  )
}
