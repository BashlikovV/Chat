package by.bashlikovv.chat.app.screens.chat

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ChatWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}