package by.bashlikovv.chat.model.settings

import android.content.Context
import by.bashlikovv.chat.model.settings.MessengerSettings.Companion.NO_ACCOUNT_ID

class SharedPreferencesMessengerSettings(
    applicationContext: Context
) : MessengerSettings {
    private val sharedPreferences = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override fun setCurrentAccountId(accountId: Long) {
        sharedPreferences.edit()
            .putLong(PREF_CURRENT_ACCOUNT_ID, accountId)
            .apply()
    }

    override fun getCurrentAccountId(): Long = sharedPreferences.getLong(PREF_CURRENT_ACCOUNT_ID, NO_ACCOUNT_ID)

    companion object {
        private const val PREF_CURRENT_ACCOUNT_ID = "currentAccountId"
    }
}