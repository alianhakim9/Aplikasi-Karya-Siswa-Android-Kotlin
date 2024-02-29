package tedc.alian.data.remote.api.builder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tedc.alian.data.local.repository.MySharedPref
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiBuilder @Inject constructor(
    private val preferences: MySharedPref
) {
    companion object {
        const val BASE_URL = "https://ali.serveo.net/"
        private const val CONNECTION_TIMEOUT = 1L
        private const val READ_TIMEOUT = 1L
        private const val WRITE_TIMEOUT = 30L
    }

    fun <Api> builder(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(api)
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder().also {
                        it.addHeader("X-Requested-With", "XMLHttpRequest")
                            .addHeader("Content-Type", "application/json")
                            .addHeader(
                                "Authorization", "Bearer ${preferences.getToken().toString()}"
                            )
                    }.build()
                )
            }.also { client ->
                client.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
                    .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.MINUTES)
            }.build()
    }
}