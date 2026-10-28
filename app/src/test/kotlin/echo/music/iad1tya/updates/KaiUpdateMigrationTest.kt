// Modified for KAI Music on 2026-10-29; GPL-3.0.
package echo.music.iad1tya.updates

import androidx.datastore.preferences.core.mutablePreferencesOf
import echo.music.iad1tya.constants.EnableCronetKey
import echo.music.iad1tya.constants.ForceOpusKey
import echo.music.iad1tya.echomusic.updater.addMissingKaiTransportDefaults
import org.junit.Assert.*
import org.junit.Test

class KaiUpdateMigrationTest {
  @Test
  fun updatingAnExistingInstallationPreservesExplicitTransportChoices() {
    val preferences = mutablePreferencesOf(ForceOpusKey to false, EnableCronetKey to false)
    addMissingKaiTransportDefaults(preferences)
    assertFalse(preferences[ForceOpusKey]!!)
    assertFalse(preferences[EnableCronetKey]!!)
  }

  @Test
  fun missingDefaultsAreInitializedWithoutChangingAnExistingChoice() {
    val preferences = mutablePreferencesOf(ForceOpusKey to false)
    addMissingKaiTransportDefaults(preferences)
    assertFalse(preferences[ForceOpusKey]!!)
    assertTrue(preferences[EnableCronetKey]!!)
    addMissingKaiTransportDefaults(preferences)
    assertFalse(preferences[ForceOpusKey]!!)
  }
}
