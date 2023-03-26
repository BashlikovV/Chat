package by.bashlikovv.chat.app.sqlite

object MessengerSQLiteContract {

    object CurrentUserTable {
        const val TABLE_NAME = "current_user"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_LAST_SESSION_TIME = "last_session_time"
        const val COLUMN_TOKEN = "token"
    }

    object BookmarksTable {
        const val TABLE_NAME = "bookmarks"
        const val COLUMN_HAS_IMAGE = "has_image"
        const val COLUMN_MESSAGE = "message"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_TIME = "time"
    }
}