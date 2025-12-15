package utils.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import utils.HeroModel
import java.io.IOException

class HeroRepository(
    private val apiService: ApiService
) {
    private var cache: List<HeroModel>? = null

    suspend fun fetchHeroes(): List<HeroModel> {
        return withContext(Dispatchers.IO) {
            try {
                // Запрашиваем первую страницу (максимум 50 на странице)
                val allHeroes = apiService.getHeroes(pageSize = 50)
                cache = allHeroes
                allHeroes
            } catch (e: HttpException) {
                throw Exception("Server error: ${e.code()}")
            } catch (e: IOException) {
                throw Exception("No internet connection")
            }
        }
    }

    fun getCachedHeroes(): List<HeroModel> {
        return cache ?: emptyList()
    }
}