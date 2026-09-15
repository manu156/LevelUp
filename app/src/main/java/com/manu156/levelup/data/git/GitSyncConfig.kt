package com.manu156.levelup.data.git

import android.content.Context
import android.content.SharedPreferences

data class GitSyncConfig(
    val remoteUrl: String = "",
    val personalAccessToken: String = "",
    val branch: String = "main",
    val authorName: String = "LevelUp User",
    val authorEmail: String = "user@levelup.local",
    val lastSyncTimestamp: Long? = null,
    val lastSyncCommitHash: String? = null
) {
    val isConfigured: Boolean
        get() = remoteUrl.isNotBlank() && personalAccessToken.isNotBlank()

    val sanitizedRemoteUrl: String
        get() = remoteUrl.trim().let { url ->
            if (url.endsWith(".git")) url else "$url.git"
        }

    val maskedToken: String
        get() = if (personalAccessToken.length > 8) {
            personalAccessToken.take(4) + "••••••••" + personalAccessToken.takeLast(4)
        } else if (personalAccessToken.isNotBlank()) {
            "••••••••"
        } else {
            ""
        }

    companion object {
        private const val PREF_NAME = "levelup_git_sync_prefs"
        private const val KEY_REMOTE_URL = "git_remote_url"
        private const val KEY_PAT = "git_pat"
        private const val KEY_BRANCH = "git_branch"
        private const val KEY_AUTHOR_NAME = "git_author_name"
        private const val KEY_AUTHOR_EMAIL = "git_author_email"
        private const val KEY_LAST_SYNC_TS = "git_last_sync_timestamp"
        private const val KEY_LAST_SYNC_COMMIT = "git_last_sync_commit"

        fun load(context: Context): GitSyncConfig {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val lastSync = prefs.getLong(KEY_LAST_SYNC_TS, -1L).takeIf { it != -1L }
            return GitSyncConfig(
                remoteUrl = prefs.getString(KEY_REMOTE_URL, "") ?: "",
                personalAccessToken = prefs.getString(KEY_PAT, "") ?: "",
                branch = prefs.getString(KEY_BRANCH, "main") ?: "main",
                authorName = prefs.getString(KEY_AUTHOR_NAME, "LevelUp User") ?: "LevelUp User",
                authorEmail = prefs.getString(KEY_AUTHOR_EMAIL, "user@levelup.local") ?: "user@levelup.local",
                lastSyncTimestamp = lastSync,
                lastSyncCommitHash = prefs.getString(KEY_LAST_SYNC_COMMIT, null)
            )
        }

        fun save(context: Context, config: GitSyncConfig) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_REMOTE_URL, config.remoteUrl.trim())
                .putString(KEY_PAT, config.personalAccessToken.trim())
                .putString(KEY_BRANCH, config.branch.trim().ifBlank { "main" })
                .putString(KEY_AUTHOR_NAME, config.authorName.trim().ifBlank { "LevelUp User" })
                .putString(KEY_AUTHOR_EMAIL, config.authorEmail.trim().ifBlank { "user@levelup.local" })
                .apply {
                    if (config.lastSyncTimestamp != null) {
                        putLong(KEY_LAST_SYNC_TS, config.lastSyncTimestamp)
                    } else {
                        remove(KEY_LAST_SYNC_TS)
                    }
                    if (config.lastSyncCommitHash != null) {
                        putString(KEY_LAST_SYNC_COMMIT, config.lastSyncCommitHash)
                    } else {
                        remove(KEY_LAST_SYNC_COMMIT)
                    }
                }
                .apply()
        }

        fun clear(context: Context) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }
}
