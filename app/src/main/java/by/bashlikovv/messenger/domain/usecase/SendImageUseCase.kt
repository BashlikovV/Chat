package by.bashlikovv.messenger.domain.usecase

import android.graphics.Bitmap
import by.bashlikovv.messenger.domain.repository.IMessagesRepository

class SendImageUseCase(private val messagesRepository: IMessagesRepository) {

    suspend fun execute(
        image: Bitmap,
        room: String,
        owner: String,
        isSignUp: Boolean
    ): String {
        return messagesRepository.sendImage(image, room, owner, isSignUp)
    }
}