package com.coffilation.app.storage

import android.content.Context
import androidx.core.content.edit

/**
 * @author pvl-zolotov on 23.10.2022
 */
interface PrefRepository {

    suspend fun getRefreshToken(): String

    suspend fun getAccessToken(): String

    suspend fun putRefreshToken(token: String)

    suspend fun putAccessToken(token: String)

    suspend fun getUsername() : String

    suspend fun getUserId(): Long

    suspend fun putUsername(username: String)

    suspend fun putUserId(userId: Long)
}

class PrefRepositoryImpl(private val context: Context): PrefRepository {

    override suspend fun getRefreshToken(): String {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).getString(REFRESH_TOKEN_PREF_NAME, "") ?: ""
    }

    override suspend fun getAccessToken(): String {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).getString(ACCESS_TOKEN_PREF_NAME, "") ?: ""
    }

    override suspend fun putRefreshToken(token: String) {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).edit {
            putString(REFRESH_TOKEN_PREF_NAME, token)
        }
    }

    override suspend fun putAccessToken(token: String) {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).edit {
            putString(ACCESS_TOKEN_PREF_NAME, token)
        }
    }

    override suspend fun getUsername(): String {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).getString(USERNAME_PREF_NAME, "") ?: ""
    }

    override suspend fun getUserId(): Long {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).getLong(USER_ID_PREF_NAME, -1L)
    }

    override suspend fun putUsername(username: String) {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).edit {
            putString(USERNAME_PREF_NAME, username)
        }
    }

    override suspend fun putUserId(userId: Long) {
        return context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).edit {
            putLong(USER_ID_PREF_NAME, userId)
        }
    }

    companion object {

        const val PREF_FILENAME = "main"
        const val REFRESH_TOKEN_PREF_NAME = "refresh_token"
        const val ACCESS_TOKEN_PREF_NAME = "access_token"
        const val USERNAME_PREF_NAME = "username"
        const val USER_ID_PREF_NAME = "user_id"
    }
}
