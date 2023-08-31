package by.bashlikovv.messenger.domain.repository

import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.data.local.model.Account
import by.bashlikovv.messenger.presentation.model.SignUpData
import kotlinx.coroutines.flow.Flow

interface IAccountsRepository {
    suspend fun isSignedIn(): Boolean

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(signUpData: SignUpData, token: String)

    suspend fun logout()

    suspend fun getAccount(): Flow<Account?>

    suspend fun getBookmarks(): Flow<List<Message>?>

    suspend fun addBookmark(bookmark: Message)

    suspend fun deleteBookmark(bookmark: Message)

    suspend fun updateAccountUsername(newUsername: String)

    suspend fun isDarkTheme(): Boolean

    suspend fun setDarkTheme()
}