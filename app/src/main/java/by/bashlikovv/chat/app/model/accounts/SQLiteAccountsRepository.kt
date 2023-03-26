package by.bashlikovv.chat.app.model.accounts

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.contentValuesOf
import by.bashlikovv.chat.app.model.*
import by.bashlikovv.chat.app.model.accounts.entities.Account
import by.bashlikovv.chat.app.model.accounts.entities.SignUpData
import by.bashlikovv.chat.app.model.settings.MessengerSettings
import by.bashlikovv.chat.app.sqlite.MessengerSQLiteContract
import by.bashlikovv.chat.app.struct.Message
import by.bashlikovv.chat.app.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class SQLiteAccountsRepository(
    private val db: SQLiteDatabase,
    private val messengerSettings: MessengerSettings,
    private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(messengerSettings.getCurrentAccountId()))
    }

    private val currentAccountBookmarksFlow = AsyncLoader {
        MutableStateFlow(getAllBookmarks())
    }

    override suspend fun isSignedIn(): Boolean {
        return messengerSettings.getCurrentAccountId() != MessengerSettings.NO_ACCOUNT_ID
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

    override suspend fun signIn(email: String, password: String) = wrapSQLiteException(ioDispatcher) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        val accountId = findAccountIdByEmailAndPassword(email, password)
        messengerSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)

        return@wrapSQLiteException
    }

    override suspend fun signUp(signUpData: SignUpData, token: String) = wrapSQLiteException(ioDispatcher) {
        signUpData.validate()
        createAccount(signUpData, token)
    }

    override suspend fun logout() {
        messengerSettings.setCurrentAccountId(MessengerSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(MessengerSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .map { accountId ->
                getAccountById(accountId.value)
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun isDarkTheme(): Boolean {
        return messengerSettings.getCurrentAccountTheme()
    }

    override suspend fun getBookmarks(): Flow<List<Message>?> {
        return currentAccountBookmarksFlow.get()
            .map { _ ->
                getAllBookmarks()
            }.flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) = wrapSQLiteException(ioDispatcher) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)
        delay(1000)
        val accountId = messengerSettings.getCurrentAccountId()
        if (accountId == MessengerSettings.NO_ACCOUNT_ID) throw AuthException()

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
            val bos = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.PNG, 10, bos)
            val input = bos.toByteArray()
            val date = Date(System.currentTimeMillis())

            db.insertOrThrow(
                MessengerSQLiteContract.BookmarksTable.TABLE_NAME,
                null,
                contentValuesOf(
                    MessengerSQLiteContract.BookmarksTable.COLUMN_MESSAGE to bookmark.value,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_IMAGE to input,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_HAS_IMAGE to bookmark.isImage,
                    MessengerSQLiteContract.BookmarksTable.COLUMN_TIME to date.toGMTString()
                        .substringBefore(" G").substringAfter("2023 ")
                        .substringBeforeLast(":")
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
        if (accountId == MessengerSettings.NO_ACCOUNT_ID) return null
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
                val tmp = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_IMAGE)
                )
                val bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.size)
                result.add(
                    Message(
                        value = cursor.getString(
                            cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_MESSAGE)
                        ),
                        isImage = cursor.getInt(
                            cursor.getColumnIndexOrThrow(MessengerSQLiteContract.BookmarksTable.COLUMN_HAS_IMAGE)
                        ) > 0,
                        imageBitmap = bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
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

    private class AccountId(val value: Long)
}