package by.bashlikovv.messenger.di

import by.bashlikovv.messenger.domain.usecase.CheckDarkThemeUseCase
import by.bashlikovv.messenger.domain.usecase.CheckSignedInUseCase
import by.bashlikovv.messenger.domain.usecase.CreateChatOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteBookmarkUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteChatOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.DeleteMessageUseCase
import by.bashlikovv.messenger.domain.usecase.GetAccountOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetBookmarksOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.GetMessagesUseCase
import by.bashlikovv.messenger.domain.usecase.GetMessagesOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetRoomUseCase
import by.bashlikovv.messenger.domain.usecase.GetRoomsOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserImageOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUserOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUsernameOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.GetUsersOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.LogOutOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.ReadRoomMessagesOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SendBookmarkUseCase
import by.bashlikovv.messenger.domain.usecase.SendImageUseCase
import by.bashlikovv.messenger.domain.usecase.SendMessageUseCase
import by.bashlikovv.messenger.domain.usecase.SetDarkThemeUseCase
import by.bashlikovv.messenger.domain.usecase.SignInOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.SignInOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.SignUpOfflineUseCase
import by.bashlikovv.messenger.domain.usecase.SignUpOnlineUseCase
import by.bashlikovv.messenger.domain.usecase.UpdateUsernameUseCase
import org.koin.dsl.module

val domainModule = module {

    factory<SignInOnlineUseCase> {
        SignInOnlineUseCase(usersRepository = get())
    }

    factory<SignInOfflineUseCase> {
        SignInOfflineUseCase(accountsRepository = get())
    }

    factory<CheckSignedInUseCase> {
        CheckSignedInUseCase(accountsRepository = get())
    }

    factory<GetUsernameOnlineUseCase> {
        GetUsernameOnlineUseCase(usersRepository = get())
    }

    factory<SignUpOnlineUseCase> {
        SignUpOnlineUseCase(usersRepository = get())
    }

    factory<SignUpOfflineUseCase> {
        SignUpOfflineUseCase(accountsRepository = get())
    }

    factory<ReadRoomMessagesOnlineUseCase> {
        ReadRoomMessagesOnlineUseCase(messagesRepository = get())
    }

    factory<DeleteChatOnlineUseCase> {
        DeleteChatOnlineUseCase(roomsRepository = get())
    }

    factory<SetDarkThemeUseCase> {
        SetDarkThemeUseCase(accountsRepository = get())
    }

    factory<GetAccountOfflineUseCase> {
        GetAccountOfflineUseCase(accountsRepository = get())
    }

    factory<GetUserOnlineUseCase> {
        GetUserOnlineUseCase(usersRepository = get())
    }

    factory<LogOutOfflineUseCase> {
        LogOutOfflineUseCase(accountsRepository = get())
    }

    factory<GetUserImageOnlineUseCase> {
        GetUserImageOnlineUseCase(usersRepository = get())
    }

    factory<CreateChatOnlineUseCase> {
        CreateChatOnlineUseCase(roomsRepository = get())
    }

    factory<CheckDarkThemeUseCase> {
        CheckDarkThemeUseCase(accountsRepository = get())
    }

    factory<GetBookmarksOfflineUseCase> {
        GetBookmarksOfflineUseCase(accountsRepository = get())
    }

    factory<GetRoomsOnlineUseCase> {
        GetRoomsOnlineUseCase(roomsRepository = get())
    }

    factory<GetMessagesOnlineUseCase> {
        GetMessagesOnlineUseCase(messagesRepository = get())
    }

    factory<UpdateUsernameUseCase> {
        UpdateUsernameUseCase(
            usersRepository = get(),
            accountsRepository = get()
        )
    }

    factory<GetUsersOnlineUseCase> {
        GetUsersOnlineUseCase(usersRepository = get())
    }

    factory<GetMessagesUseCase> {
        GetMessagesUseCase(messagesRepository = get())
    }

    factory<SendMessageUseCase> {
        SendMessageUseCase(messagesRepository = get())
    }

    factory<GetRoomUseCase> {
        GetRoomUseCase(roomsRepository = get())
    }

    factory<DeleteMessageUseCase> {
        DeleteMessageUseCase(messagesRepository = get())
    }

    factory<SendImageUseCase> {
        SendImageUseCase(messagesRepository = get())
    }

    factory<SendBookmarkUseCase> {
        SendBookmarkUseCase(messagesRepository = get())
    }

    factory<DeleteBookmarkUseCase> {
        DeleteBookmarkUseCase(messagesRepository = get())
    }
}