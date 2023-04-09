package by.bashlikovv.chat.sources

import android.os.Build
import androidx.annotation.RequiresApi
import by.bashlikovv.chat.Const
import by.bashlikovv.chat.sources.base.OkHttpConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
class SourceProviderHolder {

    val sourcesProvider by lazy {
        OkHttpConfig(
            baseUrl = Const.BASE_URL,
            client = createOkHttpClient(),
            gson = GsonBuilder().setLenient().create()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOkHttpClient(): OkHttpClient {
        return Builder().connectTimeout(Duration.ofMillis(1000)).build()
    }
}