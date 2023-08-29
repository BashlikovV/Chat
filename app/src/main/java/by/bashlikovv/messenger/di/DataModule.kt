package by.bashlikovv.messenger.di

import android.database.sqlite.SQLiteDatabase
import by.bashlikovv.messenger.data.local.MessengerSQLiteHelper
import by.bashlikovv.messenger.data.remote.base.OkHttpConfig
import by.bashlikovv.messenger.data.repository.OkHTTPMessagesRepository
import by.bashlikovv.messenger.data.repository.OkHTTPRoomsRepository
import by.bashlikovv.messenger.data.repository.OkHTTPUsersRepository
import by.bashlikovv.messenger.data.repository.SQLiteAccountsRepository
import by.bashlikovv.messenger.data.repository.SharedPreferencesMessengerSettings
import by.bashlikovv.messenger.domain.repository.IAccountsRepository
import by.bashlikovv.messenger.domain.repository.IMessagesRepository
import by.bashlikovv.messenger.domain.repository.IMessengerSettings
import by.bashlikovv.messenger.domain.repository.IRoomsRepository
import by.bashlikovv.messenger.domain.repository.IUsersRepository
import by.bashlikovv.messenger.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val dataModule = module {

    single<SQLiteDatabase> {
        MessengerSQLiteHelper(applicationContext = get()).writableDatabase
    }

    single<IMessengerSettings> {
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

    single<IAccountsRepository> {
        SQLiteAccountsRepository(
            db = get(),
            messengerSettings = get()
        )
    }

    single<IRoomsRepository> {
        OkHTTPRoomsRepository(okHttpConfig = get())
    }

    single<IMessagesRepository> {
        OkHTTPMessagesRepository(
            okHttpConfig = get(),
            sendBookmarkUseCase = get(),
            deleteBookmarkUseCase = get()
        )
    }

    single<IMessengerSettings> {
        SharedPreferencesMessengerSettings(applicationContext = get())
    }

    single<IUsersRepository> {
        OkHTTPUsersRepository(okHttpConfig = get())
    }

}