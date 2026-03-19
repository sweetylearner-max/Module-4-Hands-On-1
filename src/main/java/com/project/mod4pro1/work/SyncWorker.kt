package project.project1.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import project.project1.data.repository.NoteRepository

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = NoteRepository(applicationContext)
            repository.syncNotes()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_worker"
    }
}