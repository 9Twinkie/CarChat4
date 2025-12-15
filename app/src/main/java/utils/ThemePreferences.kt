package utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences(context: Context) {
    private val dataStore = DataStoreModule.provideDataStore(context)

    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }
}