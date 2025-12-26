package utils

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Query("SELECT * FROM heroes WHERE isPlaceholder = 0")
    fun getAllHeroes(): Flow<List<HeroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(heroes: List<HeroEntity>)

    @Query("DELETE FROM heroes WHERE isPlaceholder = 0")
    suspend fun deleteAllRealHeroes()

    @Query("SELECT COUNT(*) FROM heroes WHERE isPlaceholder = 0")
    suspend fun countRealHeroes(): Int
}