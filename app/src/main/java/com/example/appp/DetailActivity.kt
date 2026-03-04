package com.example.appp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.widget.Button
import android.widget.Toast

class DetailActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        preferenceManager = PreferenceManager(this)
        databaseHelper = DatabaseHelper(this)

        val title = intent.getStringExtra("text")
        val imageRes = intent.getIntExtra("image", 0)

        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<ImageView>(R.id.detailImage).setImageResource(imageRes)

        // Show favorite status
        val favStatusTxt = findViewById<TextView>(R.id.favoriteStatus)
        if (preferenceManager.isFavorite(title)) {
            favStatusTxt.text = "⭐ This is your Favorite Workout!"
        } else {
            favStatusTxt.text = "Not marked as Favorite"
        }

        // Log workout button
        findViewById<Button>(R.id.btnLogWorkout).setOnClickListener {
            title?.let { workoutName ->
                databaseHelper.logWorkout(workoutName)
                Toast.makeText(this, "$workoutName logged!", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup toolbar back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}