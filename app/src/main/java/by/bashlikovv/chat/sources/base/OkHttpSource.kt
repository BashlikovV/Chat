package by.bashlikovv.chat.sources.base

import by.bashlikovv.chat.app.model.ParseBackendResponseException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.readBomAsCharset
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class BaseOkHttpSource(
    private val config: OkHttpConfig
) {
    val gson: Gson = config.gson
    val client: OkHttpClient = config.client

    private val contentType = "application/json; charset=utf-8".toMediaType()

    suspend fun Call.suspendEnqueue(): Response {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }
            enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // well done
                        continuation.resume(response)
                    } else {
                        handleErrorResponse(response, continuation)
                    }
                }
            })
        }
    }

    fun Request.Builder.endpoint(endpoint: String): Request.Builder {
        url("${config.baseUrl}$endpoint")
        return this
    }

    fun <T> T.toJsonRequestBody(): RequestBody {
        val json = gson.toJson(this)
        return json.toRequestBody(contentType)
    }

    fun <T> Response.parseJsonResponse(typeToken: TypeToken<T>): T {
        try {
            return gson.fromJson(this.body!!.string(), typeToken.type)
        } catch (e: Exception) {
            throw ParseBackendResponseException(e)
        }
    }

    inline fun <reified T> Response.parseJsonResponse(): T {
        try {
            val str = this.body!!.source().use {
                it.readString(charset = it.readBomAsCharset(Charsets.UTF_8))
            }
            return gson.fromJson(str, T::class.java)
        } catch (e: Exception) {
            throw ParseBackendResponseException(e)
        }
    }

    private fun handleErrorResponse(
        response: Response,
        continuation: CancellableContinuation<Response>
    ) {
        val httpCode = response.code
        try {
            // parse error body:
            // {
            //   "error": "..."
            // }
            val map = gson.fromJson(response.body!!.string(), Map::class.java)
            val message = map["error"].toString()
            continuation.resumeWithException(Exception(message))
        } catch (e: Exception) {
            // failed to parse error body -> throw parse exception
            val appException = ParseBackendResponseException(e)
            continuation.resumeWithException(appException)
        }
    }

}