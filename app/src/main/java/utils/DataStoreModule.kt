package utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

object DataStoreModule {
    private val Context.dataStore by preferencesDataStore(name = "theme_preferences")

    fun provideDataStore(context: Context) = context.dataStore
}