package by.bashlikovv.messenger.domain.usecase

import by.bashlikovv.messenger.domain.repository.IUsersRepository
import by.bashlikovv.messenger.presentation.view.login.UserImage

class GetUserImageOnlineUseCase(private val usersRepository: IUsersRepository) {

    suspend fun execute(uri: String): UserImage {
        return usersRepository.getUserImage(uri)
    }
}