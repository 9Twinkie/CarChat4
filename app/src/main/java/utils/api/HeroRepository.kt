package utils.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import utils.*
import java.io.IOException

class HeroRepository(
    private val apiService: ApiService,
    private val heroDao: HeroDao
) {
    // Flow из Room → сразу в UI
    fun getHeroesFromDb(): Flow<List<HeroModel>> {
        return heroDao.getAllHeroes()
            .map { entities -> entities.map { HeroEntity.toModel(it) } }
            .flowOn(Dispatchers.IO)
    }

    suspend fun fetchAndSaveFromApi(): List<HeroModel> {
        return withContext(Dispatchers.IO) {
            try {
                val heroes = apiService.getHeroes(pageSize = 50)
                val entities = heroes.map { HeroEntity.fromModel(it) }
                heroDao.deleteAllRealHeroes()
                heroDao.insertAll(entities)
                heroes
            } catch (e: HttpException) {
                throw Exception("Ошибка сервера: ${e.code()}")
            } catch (e: IOException) {
                throw Exception("Нет интернета")
            }
        }
    }
    suspend fun loadHeroesByIdRange(startId: Int, count: Int): List<HeroModel> {
        val heroes = mutableListOf<HeroModel>()
        for (i in 0 until count) {
            try {
                val hero = apiService.getHeroById(startId + i)
                heroes.add(hero)
            } catch (e: Exception) {
                // Пропускаем несуществующих героев (не все ID заняты)
                continue
            }
        }
        val entities = heroes.map { HeroEntity.fromModel(it) }
        heroDao.insertAll(entities)
        return heroes
    }

    suspend fun hasDataInDb(): Boolean {
        return heroDao.countRealHeroes() > 0
    }
}