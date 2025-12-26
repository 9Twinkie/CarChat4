package utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HeroEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppSuiteDatabase : RoomDatabase() {

    abstract fun heroDao(): HeroDao

    companion object {
        @Volatile
        private var INSTANCE: AppSuiteDatabase? = null

        fun getInstance(context: Context): AppSuiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSuiteDatabase::class.java,
                    "hero_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}