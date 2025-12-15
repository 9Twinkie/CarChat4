package utils.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    private const val BASE_URL = "https://thronesapi.com/api/v2/"
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
    private val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val repository: HeroRepository by lazy {
        HeroRepository(api)
    }
}