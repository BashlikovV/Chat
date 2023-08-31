package by.bashlikovv.messenger.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.core.content.contentValuesOf
import by.bashlikovv.messenger.data.AccountAlreadyExistsException
import by.bashlikovv.messenger.data.AuthException
import by.bashlikovv.messenger.data.EmptyFieldException
import by.bashlikovv.messenger.data.StorageException
import by.bashlikovv.messenger.data.local.contract.MessengerSQLiteContract
import by.bashlikovv.messenger.data.local.model.Account
import by.bashlikovv.messenger.domain.model.Message
import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import by.bashlikovv.messenger.domain.repository.IMessengerSettings
import by.bashlikovv.messenger.presentation.model.SignUpData
import by.bashlikovv.messenger.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SQLiteAccountsRepository(
    private val db: SQLiteDatabase,
    private val messengerSettings: SharedPreferencesMessengerSettings
) : IAccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(messengerSettings.getCurrentAccountId()))
    }

    private val currentAccountBookmarksFlow = AsyncLoader {
        MutableStateFlow(getAllBookmarks())
    }

    override suspend fun isSignedIn(): Boolean {
        return messengerSettings.getCurrentAccountId() != IMessengerSettings.NO_ACCOUNT_ID
    }

    override suspend fun setDarkTheme() {
        messengerSettings.setCurrentAccountTheme(!messengerSettings.getCurrentAccountTheme())
    }

    private suspend fun <T> wrapSQLiteException(dispatcher: CoroutineDispatcher, block: suspend CoroutineScope.() -> T): T {
        try {
            return withContext(dispatcher, block)
        } catch (e: SQLiteException) {
            val appException = StorageException()
            appException.initCause(e)
            throw appException
        }
    }

    override suspend fun signIn(email: String, password: String) = wrapSQLiteException(Dispatchers.IO) {
        if (email.isBlank()) throw EmptyFieldException()
        if (password.isBlank()) throw EmptyFieldException()

        val accountId = findAccountIdByEmailAndPassword(email, password)
        messengerSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)

        return@wrapSQLiteException
    }

    override suspend fun signUp(signUpData: SignUpData, token: String) = wrapSQLiteException(Dispatchers.IO) {
        signUpData.validate()
        createAccount(signUpData, token)
    }

    override suspend fun logout() {
        deleteCurrentUser()
        messengerSettings.setCurrentAccountId(IMessengerSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(IMessengerSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .map { accountId ->
                getAccountById(accountId.value)
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun isDarkTheme(): Boolean {
        return messengerSettings.getCurrentAccountTheme()
    }

    override suspend fun getBookmarks(): Flow<List<Message>?> {
        return currentAccountBookmarksFlow.get()
            .map { _ ->
                getAllBookmarks()
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateAccountUsername(newUsername: String) = wrapSQLiteException(Dispatchers.IO) {
        if (newUsername.isBlank()) throw EmptyFieldException()
        val accountId = messengerSettings.getCurrentAccountId()
        if (accountId == IMessengerSettings.NO_ACCOUNT_ID) throw AuthException()

        updateUsernameForAccountId(accountId, newUsername)

        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException
    }

    private fun findAccountIdByEmailAndPassword(email: String, password: String): Long {
        val cursor = db.query(
            MessengerSQLiteContract.CurrentUserTable.TABLE_NAME,
            arrayOf(
                MessengerSQLiteContract.CurrentUserTable.COLUMN_ID,
                MessengerSQLiteContract.CurrentUserTable.COLUMN_PASSWORD
            ),
            "${MessengerSQLiteContract.CurrentUserTable.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        return cursor.use {
            if (cursor.count == 0) throw AuthException()
            cursor.moveToFirst()
            val passwordFromDb = cursor.getString(
                cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_PASSWORD)
            )
            if (passwordFromDb != password) throw AuthException()

            cursor.getLong(
                cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_ID)
            )
        }
    }

    private fun createAccount(signUpData: SignUpData, token: String) {
        try {
            db.insertOrThrow(
                MessengerSQLiteContract.CurrentUserTable.TABLE_NAME,
                null,
                contentValuesOf(
                    MessengerSQLiteContract.CurrentUserTable.COLUMN_EMAIL to signUpData.email,
                    MessengerSQLiteContract.CurrentUserTable.COLUMN_PASSWORD to signUpData.password,
                    MessengerSQLiteContract.CurrentUserTable.COLUMN_USERNAME to signUpData.username,
                    MessengerSQLiteContract.CurrentUserTable.COLUMN_LAST_SESSION_TIME to System.currentTimeMillis(),
                    MessengerSQLiteContract.CurrentUserTable.COLUMN_TOKEN to token
                )
            )
        } catch (e: SQLiteConstraintException) {
            val exception = AccountAlreadyExistsException()
            exception.initCause(e)
            throw exception
        }
    }

    override suspend fun addBookmark(bookmark: Message) {
        try {
            val photo = bookmark.imageBitmap

            db.insertOrThrow(
                MessengerSQLiteContract.BookmarksTable.TABLE_NAME,
                null,
                contentValuesOf(
                    MessengerSQLiteContract.BookmarksTable.COLUMN_MESSAGE to bookmark.value,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_IMAGE to photo,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_HAS_IMAGE to bookmark.isImage,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_TIME to bookmark.time
                )
            )
        } catch (e: SQLiteConstraintException) {
            val exception = AccountAlreadyExistsException()
            exception.initCause(e)
            throw exception
        }
    }

    override suspend fun deleteBookmark(bookmark: Message) {
        db.execSQL("DELETE FROM bookmarks WHERE message=('${bookmark.value}')")
    }

    private fun getAccountById(accountId: Long): Account? {
        if (accountId == IMessengerSettings.NO_ACCOUNT_ID) return null
        val cursor = db.query(
            MessengerSQLiteContract.CurrentUserTable.TABLE_NAME,
            arrayOf(
                MessengerSQLiteContract.CurrentUserTable.COLUMN_ID,
                MessengerSQLiteContract.CurrentUserTable.COLUMN_EMAIL,
                MessengerSQLiteContract.CurrentUserTable.COLUMN_USERNAME,
                MessengerSQLiteContract.CurrentUserTable.COLUMN_LAST_SESSION_TIME,
                MessengerSQLiteContract.CurrentUserTable.COLUMN_TOKEN
            ),
            "${MessengerSQLiteContract.CurrentUserTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString()),
            null, null, null
        )
        return cursor.use {
            if (cursor.count == 0) return null
            cursor.moveToFirst()
            Account(
                id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_ID)
                ),
                username = cursor.getString(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_USERNAME)
                ),
                email = cursor.getString(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_EMAIL)
                ),
                createdAt = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_LAST_SESSION_TIME)
                ),
                token = cursor.getString(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.CurrentUserTable.COLUMN_TOKEN)
                )
            )
        }
    }

    private fun getAllBookmarks(): List<Message>? {
        val cursor = db.query(
            MessengerSQLiteContract.BookmarksTable.TABLE_NAME,
            arrayOf(
                MessengerSQLiteContract.BookmarksTable.COLUMN_HAS_IMAGE,
                MessengerSQLiteContract.BookmarksTable.COLUMN_IMAGE,
                MessengerSQLiteContract.BookmarksTable.COLUMN_MESSAGE,
                MessengerSQLiteContract.BookmarksTable.COLUMN_TIME
            ),
            null,
            null,
            null, null, null
        )
        return cursor.use {
            if (cursor.count <= 0) return null
            cursor.moveToFirst()
            val result = mutableListOf<Message>()
            while (cursor.moveToNext()) {
                val image = cursor.getString(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_IMAGE)
                )
                result.add(
                    Message(
                        value = cursor.getString(
                            cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_MESSAGE)
                        ),
                        isImage = cursor.getInt(
                            cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_HAS_IMAGE)
                        ) > 0,
                        imageBitmap = image ?: "no image",
                        time = cursor.getString(
                            cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_TIME)
                        )
                    )
                )
            }
            result
        }
    }

    private fun updateUsernameForAccountId(accountId: Long, newUsername: String) {
        db.update(
            MessengerSQLiteContract.CurrentUserTable.TABLE_NAME,
            contentValuesOf(
                MessengerSQLiteContract.CurrentUserTable.COLUMN_USERNAME to newUsername
            ),
            "${MessengerSQLiteContract.CurrentUserTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString())
        )
    }

    private fun deleteCurrentUser() {
        db.delete(
            MessengerSQLiteContract.CurrentUserTable.TABLE_NAME,
            "${MessengerSQLiteContract.CurrentUserTable.COLUMN_ID} = ?",
            arrayOf(messengerSettings.getCurrentAccountId().toString())
        )
    }

    private class AccountId(val value: Long)
}