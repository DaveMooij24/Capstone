// DataStoreManager.kt
package nl.hva.capstone.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val PASSWORD_KEY = stringPreferencesKey("password")
    }

    suspend fun saveLogin(username: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
            prefs[PASSWORD_KEY] = password
        }
    }

    suspend fun getUsername(): String? {
        return context.dataStore.data
            .map { it[USERNAME_KEY] }
            .first()
    }

    suspend fun getPassword(): String? {
        return context.dataStore.data
            .map { it[PASSWORD_KEY] }
            .first()
    }

    suspend fun clearLogin() {
        context.dataStore.edit { it.clear() }
    }
}
