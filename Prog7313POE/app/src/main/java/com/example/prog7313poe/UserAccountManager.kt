package com.example.prog7313poe

import android.content.Context
import android.content.SharedPreferences

object UserAccountManager {
    private const val PREFS_NAME = "user_accounts"
    private const val LAST_USER_EMAIL = "last_user_email"
    private const val KNOWN_USERS = "known_users"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun addKnownUser(context: Context, email: String) {
        val editor = prefs(context).edit()
        val userSet = getKnownUsers(context).toMutableSet()
        userSet.add(email)
        editor.putStringSet(KNOWN_USERS, userSet)
        editor.putString(LAST_USER_EMAIL, email)
        editor.apply()
    }

    fun getKnownUsers(context: Context): Set<String> {
        return prefs(context).getStringSet(KNOWN_USERS, emptySet()) ?: emptySet()
    }

    fun getLastUser(context: Context): String {
        return prefs(context).getString(LAST_USER_EMAIL, "") ?: ""
    }

    fun switchUser(context: Context, email: String) {
        prefs(context).edit().putString(LAST_USER_EMAIL, email).apply()
    }

    fun clearUsers(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
