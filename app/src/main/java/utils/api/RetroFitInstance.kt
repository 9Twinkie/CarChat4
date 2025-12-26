package utils.api

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import utils.AppSuiteDatabase

object RetrofitInstance {

    private const val BASE_URL = "https://www.anapioficeandfire.com/api/"
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private fun getRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    private val api: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    fun getRepository(context: Context): HeroRepository {
        val database = AppSuiteDatabase.getInstance(context)
        return HeroRepository(api, database.heroDao())
    }
}