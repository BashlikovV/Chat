package by.bashlikovv.messenger.domain.repository

interface IMessengerSettings {

    fun getCurrentAccountId(): Long

    fun setCurrentAccountId(accountId: Long)

    fun setCurrentAccountTheme(dark: Boolean)

    fun getCurrentAccountTheme(): Boolean

    companion object {
        const val NO_ACCOUNT_ID = -1L
        const val NO_ACCOUNT_THEME = true
    }
}