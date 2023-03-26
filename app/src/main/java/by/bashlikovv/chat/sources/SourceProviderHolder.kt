package by.bashlikovv.chat.sources

import by.bashlikovv.chat.Const
import by.bashlikovv.chat.sources.base.OkHttpConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.*

class SourceProviderHolder {

    val sourcesProvider by lazy {
        OkHttpConfig(
            baseUrl = Const.BASE_URL,
            client = createOkHttpClient(),
            gson = Gson()
        )
    }

    private fun createOkHttpClient(): OkHttpClient {
        return Builder().build()
    }
}