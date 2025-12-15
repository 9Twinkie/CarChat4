package utils.api

import retrofit2.http.GET
import utils.HeroModel
import retrofit2.http.Query

interface ApiService {
    @GET("characters")
    suspend fun getHeroes(
        @Query("pageSize") pageSize: Int = 20
    ): List<HeroModel>
}