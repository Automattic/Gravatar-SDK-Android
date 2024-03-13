package com.gravatar.events

import android.content.Context
import androidx.activity.ComponentActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class LocalDataStore(private val context: Context) {
    fun getContacts(): List<Pair<String, Long>> {
        val sharedPreferences = context.getSharedPreferences("contacts", ComponentActivity.MODE_PRIVATE)
        return (sharedPreferences.all?.toList() as List<Pair<String, Long>>?)?.sortedBy { it.second }?.filter { it.first != getCurrentUser() }
            ?: emptyList()
    }

    fun saveContact(profileHash: String, date: Date) {
        val sharedPreferences = context.getSharedPreferences("contacts", ComponentActivity.MODE_PRIVATE)
        sharedPreferences.edit().putLong(
            profileHash,
            date.time,
        ).apply()
    }

    fun saveCurrentUser(hash: String) {
        val sharedPreferences = context.getSharedPreferences("user", ComponentActivity.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentUser", hash).apply()
    }

    fun getCurrentUser(): String? {
        val sharedPreferences = context.getSharedPreferences("user", ComponentActivity.MODE_PRIVATE)
        return sharedPreferences.getString("currentUser", null)
    }

    fun logout() {
        val sharedPreferences = context.getSharedPreferences("events", ComponentActivity.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
