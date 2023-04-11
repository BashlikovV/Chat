package by.bashlikovv.chat.sources

import by.bashlikovv.chat.Const
import by.bashlikovv.chat.sources.base.OkHttpConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import java.util.concurrent.TimeUnit

class SourceProviderHolder {

    val sourcesProvider by lazy {
        OkHttpConfig(
            baseUrl = Const.BASE_URL,
            client = createOkHttpClient(),
            gson = GsonBuilder().setLenient().create()
        )
    }

    private fun createOkHttpClient(): OkHttpClient {
        return Builder()
            .connectTimeout(1000, TimeUnit.MILLISECONDS)
            .callTimeout(10000, TimeUnit.MILLISECONDS)
            .writeTimeout(10000, TimeUnit.MILLISECONDS)
            .callTimeout(10000, TimeUnit.MILLISECONDS)
            .build()
    }
}