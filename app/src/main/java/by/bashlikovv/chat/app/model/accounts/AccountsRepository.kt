package by.bashlikovv.chat.app.model.accounts

import by.bashlikovv.chat.app.model.accounts.entities.Account
import by.bashlikovv.chat.app.model.accounts.entities.SignUpData
import by.bashlikovv.chat.app.struct.Message
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
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