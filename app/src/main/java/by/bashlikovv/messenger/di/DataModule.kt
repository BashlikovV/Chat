package by.bashlikovv.messenger.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import by.bashlikovv.messenger.data.local.MessengerSQLiteHelper
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.repository.MessagesRepository
import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository
import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository
import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository
import by.bashlikovv.messenger.data.repository.SharedPreferencesMessengerSettings
import by.bashlikovv.messenger.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val dataModule = module {

    single<SQLiteDatabase> {
        MessengerSQLiteHelper(applicationContext = get()).writableDatabase
    }

    single<SharedPreferencesMessengerSettings> {
        SharedPreferencesMessengerSettings(applicationContext = get())
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .connectTimeout(1000, TimeUnit.MILLISECONDS)
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .build()
    }

    single<OkHttpConfig> {
        OkHttpConfig(
            baseUrl = Constants.BASE_URL,
            client = get(),
            gson = GsonBuilder().setLenient().create()
        )
    }

    single<ConnectivityManager> {
        val context: Context = get()
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single<SQLiteAccountsRepository> {
        SQLiteAccountsRepository(
            db = get(),
            messengerSettings = get()
        )
    }

    single<OkHTTPRoomsRepository> {
        OkHTTPRoomsRepository(okHttpConfig = get())
    }

    single<MessagesRepository> {
        MessagesRepository(
            okHttpConfig = get(),
            accountsRepository = get(),
            cm = get()
        )
    }

    single<SharedPreferencesMessengerSettings> {
        SharedPreferencesMessengerSettings(applicationContext = get())
    }

    single<OkHTTPUsersRepository> {
        OkHTTPUsersRepository(okHttpConfig = get())
    }

}