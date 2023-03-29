package by.bashlikovv.chat.app.utils

import android.content.res.Resources
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


fun Float.dpToFloat(resources: Resources): Float {
    val scale = resources.displayMetrics.density
    return (this * scale)
}
class AsyncLoader<T>(
    private val loader: suspend () -> T
) {

    private val mutex = Mutex()
    private var value: T? = null

    suspend fun get(): T {
        mutex.withLock {
            if (value == null) {
                value = loader()
            }
        }
        return value!!
    }
}

fun buildTime(date: String): String {
    return try {
        val sb = StringBuilder()
        val i = date.indexOfFirst { it == ':' }
        sb
            .append(date[i - 2])
            .append(date[i - 1])
            .append(date[i])
            .append(date[i + 1])
            .append(date[i + 2])
        sb.toString()
    } catch (e: Exception) {
        ""
    }
}