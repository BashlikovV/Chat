package by.bashlikovv.chat

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import by.bashlikovv.chat.sqlite.MessengerSQLiteHelper

object Repositories {

    private lateinit var applicationContext: Context

    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        MessengerSQLiteHelper(applicationContext).writableDatabase
    }
}