package by.bashlikovv.messenger.di

import by.bashlikovv.messenger.presentation.viewmodel.ChatViewModel
import by.bashlikovv.messenger.presentation.viewmodel.LogInViewModel
import by.bashlikovv.messenger.presentation.viewmodel.MessengerViewModel
import by.bashlikovv.messenger.presentation.viewmodel.UsersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<LogInViewModel> {
        LogInViewModel(
            signInOnlineUseCase = get(),
            signInOfflineUseCase = get(),
            checkSignedInUseCase = get(),
            getUsernameOnlineUseCase = get(),
            signUpOnlineUseCase = get(),
            signUpOfflineUseCase = get()
        )
    }

    viewModel<MessengerViewModel> {
        MessengerViewModel(
            context = get(),
            readRoomMessagesOnlineUseCase = get(),
            deleteChatOnlineUseCase = get(),
            setDarkThemeUseCase = get(),
            getAccountOfflineUseCase = get(),
            getUserOnlineUseCase = get(),
            logOutOfflineUseCase = get(),
            getUserImageOnlineUseCase = get(),
            createChatOnlineUseCase = get(),
            checkDarkThemeUseCase = get(),
            getBookmarksOfflineUseCase = get(),
            getRoomsOnlineUseCase = get(),
            getMessagesOnlineUseCase = get(),
            updateUsernameUseCase = get()
        )
    }

    viewModel<UsersViewModel> {
        UsersViewModel(
            getUserOnlineUseCase = get(),
            getAccountOfflineUseCase = get(),
            getUsersOnlineUseCase = get(),
            getUserImageOnlineUseCase = get()
        )
    }

    viewModel<ChatViewModel> {
        ChatViewModel(
            getRoomMessagesOnlineUseCase = get(),
            getAccountOfflineUseCase = get(),
            getUserImageOnlineUseCase = get(),
            getMessagesUseCase = get(),
            getMessageImageOnlineUseCase = get(),
            sendMessageUseCase = get(),
            getRoomUseCase = get(),
            deleteMessageUseCase = get(),
            sendImageUseCase = get(),
            sendBookmarkUseCase = get(),
            deleteBookmarkUseCase = get(),
            deleteChatOnlineUseCase = get(),
            getMessagesOnlineUseCase = get()
        )
    }
}