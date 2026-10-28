// Modified for KAI Music on 2026-10-29; GPL-3.0.
package echo.music.iad1tya.ui.component

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class LyricsClockSamplerTest {
  private class NoUi : AbstractApplier<Unit>(Unit) {
    override fun insertTopDown(index: Int, instance: Unit) = Unit

    override fun insertBottomUp(index: Int, instance: Unit) = Unit

    override fun remove(index: Int, count: Int) = Unit

    override fun move(from: Int, to: Int, count: Int) = Unit

    override fun onClear() = Unit
  }

  @Test
  fun asyncLyricsEnableTheAlreadyRememberedSamplerAndKeepFollowingLivePosition() = runTest {
    val frames = BroadcastFrameClock()
    val recomposer = Recomposer(coroutineContext + frames)
    val running =
      launch(UnconfinedTestDispatcher(testScheduler) + frames) {
        recomposer.runRecomposeAndApplyChanges()
      }
    val composition = Composition(NoUi(), recomposer)
    val visible = mutableStateOf(false)
    var actual = 11_435L
    var observed = -1L
    var sampler: () -> Unit = {}
    composition.setContent {
      sampler =
        rememberLyricsClockSampler(visible.value, { true }, { null }, { actual }) { value, _ ->
          observed = value
        }
    }
    val retained = sampler
    sampler()
    assertEquals(-1L, observed)
    visible.value = true
    Snapshot.sendApplyNotifications()
    runCurrent()
    frames.sendFrame(1L)
    runCurrent()
    // Invoking the original retained callback is the regression: initial hidden -> loaded lyrics.
    actual = 20_000L
    retained()
    assertEquals(20_000L, observed)
    actual = 46_000L
    retained()
    assertEquals(46_000L, observed)
    visible.value = false
    Snapshot.sendApplyNotifications()
    runCurrent()
    frames.sendFrame(2L)
    runCurrent()
    actual = 90_000L
    retained()
    assertEquals(46_000L, observed)
    composition.dispose()
    recomposer.close()
    running.join()
  }

  @Test
  fun pausedSeekResumeAndPlayerReplacementReadCurrentSources() = runTest {
    val recomposer = Recomposer(coroutineContext)
    val composition = Composition(NoUi(), recomposer)
    var resumed = true
    var actual = 17_000L
    var preview: Long? = null
    var sample = 0L
    var seeking = false
    var sampler: () -> Unit = {}
    composition.setContent {
      sampler =
        rememberLyricsClockSampler(true, { resumed }, { preview }, { actual }) { value, seek ->
          sample = value
          seeking = seek
        }
    }
    sampler()
    assertEquals(17_000L, sample)
    sampler() // A pause does not invent elapsed time.
    assertEquals(17_000L, sample)
    preview = 12_000L
    sampler()
    assertEquals(12_000L, sample)
    assertTrue(seeking)
    preview = null
    actual = 12_100L
    sampler()
    assertEquals(12_100L, sample)
    assertFalse(seeking)
    resumed = false
    actual = 25_000L
    sampler()
    assertEquals(12_100L, sample)
    resumed = true
    actual = 0L // Replacement session / new song.
    sampler()
    assertEquals(0L, sample)
    composition.dispose()
    recomposer.close()
  }
}
