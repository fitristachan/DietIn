package com.dietinapp.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userPref")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val token = stringPreferencesKey("token")
    private val session = booleanPreferencesKey("session")
    private val username = stringPreferencesKey("username")
    private val email = stringPreferencesKey("email")
    private val photo = stringPreferencesKey("photo")

    suspend fun getToken(): String {
        return dataStore.data.map { preferences ->
            preferences[token] ?: ""
        }.first()
    }

    fun getSession(): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[session] ?: false
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[username] ?: ""
        }
    }

    fun getEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[email] ?: ""
        }
    }

    fun getPhoto(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[photo] ?: ""
        }
    }

    suspend fun saveUser(token: String, session: Boolean, name: String, email: String, photo: String) {
        dataStore.edit { preferences ->
            preferences[this.token] = token
            preferences[this.session] = session
            preferences[this.username] = name
            preferences[this.email] = email
            preferences[this.photo] = photo
        }
    }

    suspend fun saveOnlyToken(token: String) {
        dataStore.edit { preferences ->
            preferences[this.token] = token
        }
    }

    suspend fun deleteOnlyToken() {
        dataStore.edit { preferences ->
            preferences[this.token] = ""
        }
    }


    suspend fun saveOnlyUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[this.username] = ""
        }
        dataStore.edit { preferences ->
            preferences[this.username] = username
        }
    }


    suspend fun saveOnlyPhoto(photo: String) {
        dataStore.edit { preferences ->
            preferences[this.photo] = ""
        }
        dataStore.edit { preferences ->
            preferences[this.photo] = photo
        }
    }


    suspend fun deleteAll() {
        dataStore.edit { preferences ->
            preferences[this.token] = ""
            preferences[this.session] = false
            preferences[this.username] = ""
            preferences[this.email] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}