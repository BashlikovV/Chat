package by.bashlikovv.chat

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import by.bashlikovv.chat.model.accounts.AccountsRepository
import by.bashlikovv.chat.model.accounts.SQLiteAccountsRepository
import by.bashlikovv.chat.model.settings.MessengerSettings
import by.bashlikovv.chat.model.settings.SharedPreferencesMessengerSettings
import by.bashlikovv.chat.sqlite.MessengerSQLiteHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Repositories {

    private lateinit var applicationContext: Context

    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        MessengerSQLiteHelper(applicationContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: MessengerSettings by lazy {
        SharedPreferencesMessengerSettings(applicationContext)
    }

    val accountsRepository: AccountsRepository by lazy {
        SQLiteAccountsRepository(database, appSettings, ioDispatcher)
    }

    fun init(context: Context) {
        applicationContext = context
    }
}