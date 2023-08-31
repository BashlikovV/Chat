package by.bashlikovv.messenger.domain.usecase

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.messenger.data.repository.MessagesRepository

class SendImageUseCase(private val messagesRepository: MessagesRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun execute(
        image: Bitmap,
        room: String,
        owner: String,
        isSignUp: Boolean
    ): String {
        return messagesRepository.sendImage(image, room, owner, isSignUp)
    }
}