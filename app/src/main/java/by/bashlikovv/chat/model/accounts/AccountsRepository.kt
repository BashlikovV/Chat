package by.bashlikovv.chat.model.accounts

import by.bashlikovv.chat.model.accounts.entities.Account
import by.bashlikovv.chat.model.accounts.entities.SignUpData
import by.bashlikovv.chat.struct.Message
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    suspend fun isSignedIn(): Boolean

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(signUpData: SignUpData)

    suspend fun logout()

    suspend fun getAccount(): Flow<Account?>

    suspend fun getBookmarks(): Flow<List<Message>?>

    suspend fun addBookmark(bookmark: Message)

    suspend fun deleteBookmark(bookmark: Message)

    suspend fun updateAccountUsername(newUsername: String)

    suspend fun isDarkTheme(): Boolean

    suspend fun setDarkTheme()
}