package by.bashlikovv.chat.sources

import by.bashlikovv.chat.Const
import by.bashlikovv.chat.Repositories
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
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .callTimeout(1000, TimeUnit.MILLISECONDS)
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("User-Token", Repositories.myToken)
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }
            .build()
    }
}