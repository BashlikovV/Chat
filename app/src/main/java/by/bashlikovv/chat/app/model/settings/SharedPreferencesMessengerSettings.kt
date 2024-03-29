package by.bashlikovv.chat.app.model.settings

import android.content.Context
import by.bashlikovv.chat.app.model.settings.MessengerSettings.Companion.NO_ACCOUNT_ID
import by.bashlikovv.chat.app.model.settings.MessengerSettings.Companion.NO_ACCOUNT_THEME

class SharedPreferencesMessengerSettings(
    applicationContext: Context
) : MessengerSettings {
    private val sharedPreferences = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun setCurrentAccountId(accountId: Long) {
        sharedPreferences.edit()
            .putLong(PREF_CURRENT_ACCOUNT_ID, accountId)
            .apply()
    }

    override fun setCurrentAccountTheme(dark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PREF_CURRENT_ACCOUNT_THEME, dark)
            .apply()
    }

    override fun getCurrentAccountTheme(): Boolean = sharedPreferences.getBoolean(
        PREF_CURRENT_ACCOUNT_THEME, NO_ACCOUNT_THEME
    )

    override fun getCurrentAccountId(): Long = sharedPreferences.getLong(PREF_CURRENT_ACCOUNT_ID, NO_ACCOUNT_ID)

    companion object {
        private const val PREF_CURRENT_ACCOUNT_ID = "currentAccountId"
        private const val PREF_CURRENT_ACCOUNT_THEME = "currentAccountTheme"
    }
}