package by.bashlikovv.messenger.presentation

import android.app.Application
import by.bashlikovv.messenger.di.appModule
import by.bashlikovv.messenger.di.dataModule
import by.bashlikovv.messenger.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }
    }
}