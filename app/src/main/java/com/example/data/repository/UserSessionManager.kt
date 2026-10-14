package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import com.google.firebase.Timestamp
import java.util.UUID

class UserSessionManager(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserId(): String {
        var uid = prefs.getString(KEY_USER_ID, "") ?: ""
        if (uid.isBlank()) {
            uid = "user_" + UUID.randomUUID().toString().take(12)
            prefs.edit().putString(KEY_USER_ID, uid).apply()
        }
        return uid
    }

    fun saveUser(name: String) {
        val cleanName = name.trim().ifBlank { "Pencari Kebijakan" }
        prefs.edit()
            .putString(KEY_USER_NAME, cleanName)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getUserName().isNotBlank()
    }

    fun getRealityScore(): Double {
        return prefs.getFloat(KEY_REALITY_SCORE, 50.0f).toDouble()
    }

    fun setRealityScore(score: Double) {
        prefs.edit().putFloat(KEY_REALITY_SCORE, score.toFloat()).apply()
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_USER_NAME, "")
            .apply()
    }

    fun getUserProfile(): UserProfile {
        return UserProfile(
            nama = getUserName().ifBlank { "Pencari Kebijakan" },
            createdAt = Timestamp.now(),
            lastActive = Timestamp.now(),
            skorRealitasTerakhir = getRealityScore()
        )
    }

    companion object {
        private const val PREFS_NAME = "maieutika_user_session"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_REALITY_SCORE = "reality_score"
    }
}
