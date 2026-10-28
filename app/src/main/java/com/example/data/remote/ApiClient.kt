package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Default MockAPI endpoint for Recursos
    const val DEFAULT_API_URL = "https://673f44c7a9bc27832439a2b5.mockapi.io/api/v1/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private var currentBaseUrl = DEFAULT_API_URL
    private var cachedRetrofit: Retrofit? = null
    private var cachedService: ApiService? = null

    @Synchronized
    fun getService(baseUrl: String = currentBaseUrl): ApiService {
        val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        if (cachedService != null && currentBaseUrl == normalizedUrl) {
            return cachedService!!
        }

        currentBaseUrl = normalizedUrl
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        cachedRetrofit = retrofit
        val service = retrofit.create(ApiService::class.java)
        cachedService = service
        return service
    }

    fun getMoshi(): Moshi = moshi

    fun parseResourcesJson(rawJson: String): List<ResourceDto> {
        val trimmed = rawJson.trim()
        val listType = Types.newParameterizedType(List::class.java, ResourceDto::class.java)
        val listAdapter = moshi.adapter<List<ResourceDto>>(listType)

        if (trimmed.startsWith("[")) {
            return listAdapter.fromJson(trimmed) ?: emptyList()
        }

        // Try wrapped object {"recursos": [...]}
        try {
            val wrapperAdapter = moshi.adapter(ResourcesResponseWrapper::class.java)
            val wrapper = wrapperAdapter.fromJson(trimmed)
            if (wrapper?.recursos != null) {
                return wrapper.recursos
            }
        } catch (_: Exception) {}

        return emptyList()
    }
}
