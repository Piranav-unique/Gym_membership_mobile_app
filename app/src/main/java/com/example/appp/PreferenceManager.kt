package com.example.appp

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("GymAppPrefs", Context.MODE_PRIVATE)

    fun saveFavoriteWorkout(workoutName: String?) {
        sharedPreferences.edit().putString("favorite_workout", workoutName).apply()
    }

    fun getFavoriteWorkout(): String? {
        return sharedPreferences.getString("favorite_workout", null)
    }

    fun isFavorite(workoutName: String?): Boolean {
        return getFavoriteWorkout() == workoutName
    }
}
