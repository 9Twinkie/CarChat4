package utils.api

import retrofit2.http.GET
import retrofit2.http.Path
import utils.HeroModel
import retrofit2.http.Query

// utils/api/ApiService.kt
interface ApiService {
    @GET("characters")
    suspend fun getHeroes(@Query("pageSize") pageSize: Int = 50): List<HeroModel>

    @GET("characters/{id}")
    suspend fun getHeroById(@Path("id") id: Int): HeroModel
}