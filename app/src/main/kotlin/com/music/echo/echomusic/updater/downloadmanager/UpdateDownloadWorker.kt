/* KAI modification notice (2026-10-28): changed from the supplied Echo Music snapshot; see MODIFICATIONS.md. Original notices retained. */ package echo.music.iad1tya.echomusic.updater.downloadmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/** Kept at its original class name so persisted legacy jobs terminate without fetching an APK. */
class UpdateDownloadWorker(context: Context, workerParams: WorkerParameters) :
  CoroutineWorker(context, workerParams) {
  override suspend fun doWork(): Result = Result.success()
}
