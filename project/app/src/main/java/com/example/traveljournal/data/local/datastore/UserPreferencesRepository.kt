package com.example.traveljournal.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 创建DataStore实例
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    
    object PreferencesKeys {
        val DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val LAST_VIEWED_TRIP_ID = stringPreferencesKey("last_viewed_trip_id")
    }
    
    // 流式获取用户偏好设置
    val userPreferencesFlow: Flow<UserPreferencesData> = context.dataStore.data
        .map { preferences ->
            UserPreferencesData(
                defaultUnit = preferences[PreferencesKeys.DEFAULT_UNIT] ?: "metric",
                darkThemeEnabled = preferences[PreferencesKeys.DARK_THEME_ENABLED] ?: false,
                lastViewedTripId = preferences[PreferencesKeys.LAST_VIEWED_TRIP_ID] ?: ""
            )
        }
    
    suspend fun updateDefaultUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_UNIT] = unit
        }
    }
    
    suspend fun updateDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_ENABLED] = enabled
        }
    }
    
    suspend fun updateLastViewedTripId(tripId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_VIEWED_TRIP_ID] = tripId
        }
    }
}

// 数据类来表示用户偏好
data class UserPreferencesData(
    val defaultUnit: String = "metric",
    val darkThemeEnabled: Boolean = false,
    val lastViewedTripId: String = ""
)