package com.gravatar.events

import android.content.Context
import androidx.activity.ComponentActivity

class LocalDataStore(private val context: Context) {
    fun getContacts(): List<String> {
        val sharedPreferences = context.getSharedPreferences("events", ComponentActivity.MODE_PRIVATE)
        return sharedPreferences.getStringSet("contacts", mutableSetOf<String>())?.filter { it != getCurrentUser() }?.toList()
            ?: emptyList()
    }

    fun saveContact(profileHash: String) {
        val sharedPreferences = context.getSharedPreferences("events", ComponentActivity.MODE_PRIVATE)
        val contacts = sharedPreferences.getStringSet(
            "contacts",
            mutableSetOf<String>(),
        )?.toMutableSet() ?: mutableSetOf()
        contacts.add(profileHash)
        sharedPreferences.edit().putStringSet("contacts", contacts).apply()
    }

    fun saveCurrentUser(hash: String) {
        val sharedPreferences = context.getSharedPreferences("events", ComponentActivity.MODE_PRIVATE)
        sharedPreferences.edit().putString("currentUser", hash).apply()
    }

    fun getCurrentUser(): String? {
        val sharedPreferences = context.getSharedPreferences("events", ComponentActivity.MODE_PRIVATE)
        return sharedPreferences.getString("currentUser", null)
    }
}