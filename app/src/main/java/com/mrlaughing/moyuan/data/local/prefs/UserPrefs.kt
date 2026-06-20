package com.mrlaughing.moyuan.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "moyuan_prefs")

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object Keys {
        val API_KEY = stringPreferencesKey("weread_api_key")
        val LAST_SYNC_DATE = stringPreferencesKey("last_sync_date")
        val INSTALL_DATE = stringPreferencesKey("install_date")
    }

    suspend fun setApiKey(key: String) {
        dataStore.edit { it[API_KEY] = key }
    }

    suspend fun getApiKey(): String? {
        return dataStore.data.map { prefs -> prefs[API_KEY] }.first()
    }

    suspend fun setLastSyncDate(date: String) {
        dataStore.edit { it[LAST_SYNC_DATE] = date }
    }

    suspend fun getLastSyncDate(): String? {
        return dataStore.data.map { prefs -> prefs[LAST_SYNC_DATE] }.first()
    }

    suspend fun setInstallDate(date: String) {
        dataStore.edit { it[INSTALL_DATE] = date }
    }

    suspend fun getInstallDate(): String? {
        return dataStore.data.map { prefs -> prefs[INSTALL_DATE] }.first()
    }

    fun observeApiKey(): Flow<String?> {
        return dataStore.data.map { it[API_KEY] }
    }

    fun observeLastSyncDate(): Flow<String?> {
        return dataStore.data.map { it[LAST_SYNC_DATE] }
    }
}
