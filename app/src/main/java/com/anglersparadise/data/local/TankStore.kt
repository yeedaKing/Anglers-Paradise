// app/src/main/java/com/anglersparadise/data/local/TankStore.kt

package com.anglersparadise.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anglersparadise.domain.model.Fish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("tank_store")

object TankStore {
    private lateinit var appContext: Context

    private val KEY_TANK = stringPreferencesKey("tank_json")
    private val KEY_HISTORY = stringPreferencesKey("history_json")

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val tankFlow: Flow<List<Fish>>
        get() = appContext
            .dataStore
            .data
            .map { prefs ->
                prefs[KEY_TANK]
                    ?.let { runCatching { json.decodeFromString<List<Fish>>(it) }.getOrNull() }
                    ?: emptyList()
            }

    val historyFlow: Flow<List<Fish>>
        get() = appContext
            .dataStore
            .data
            .map { prefs ->
                prefs[KEY_HISTORY]
                    ?.let { runCatching { json.decodeFromString<List<Fish>>(it) }.getOrNull() }
                    ?: emptyList()
            }

    suspend fun saveTank(list: List<Fish>) {
        appContext.dataStore.edit { prefs ->
            prefs[KEY_TANK] = json.encodeToString(list)
        }
    }

    suspend fun saveHistory(list: List<Fish>) {
        appContext.dataStore.edit { prefs ->
            prefs[KEY_HISTORY] = json.encodeToString(list)
        }
    }
}
