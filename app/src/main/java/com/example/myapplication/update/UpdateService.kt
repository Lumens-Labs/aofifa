package com.example.myapplication.update

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "update_prefs")

class UpdateService(
    private val context: Context,
    private val api: GithubApi
) {
    private val LAST_CHECK_KEY = longPreferencesKey("last_check")

    suspend fun checkForUpdates(owner: String, repo: String, currentVersion: String): GithubRelease? {
        val lastCheck = context.dataStore.data.first()[LAST_CHECK_KEY] ?: 0L
        if ((System.currentTimeMillis() - lastCheck) < 86400000) return null

        return try {
            val latest = api.getLatestRelease(owner, repo)
            context.dataStore.edit { it[LAST_CHECK_KEY] = System.currentTimeMillis() }

            val remote = latest.tagName.replace("v", "").trim()
            val local = currentVersion.replace("v", "").trim()

            if (remote != local) latest else null
        } catch (e: Exception) {
            null
        }
    }
}
