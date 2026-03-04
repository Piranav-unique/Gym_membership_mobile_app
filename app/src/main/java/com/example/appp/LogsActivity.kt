package com.example.appp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class LogsActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "GymLogs"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_logs)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Workout History"

        val databaseHelper = DatabaseHelper(this)
        val logs = databaseHelper.getAllLogs()

        // Print to Logcat so you can see data in the "Logcat" tab
        Log.d(TAG, "--- GYM WORKOUT HISTORY ---")
        if (logs.isEmpty()) {
            Log.d(TAG, "No logs found.")
        } else {
            logs.forEach { log -> Log.d(TAG, "Log Entry: $log") }
        }
        Log.d(TAG, "---------------------------")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logs.toMutableList())
        val listView = findViewById<ListView>(R.id.listViewLogs)
        listView.adapter = adapter

        // Clear All History button
        findViewById<Button>(R.id.btnClearLogs).setOnClickListener {
            databaseHelper.clearLogs()
            (listView.adapter as ArrayAdapter<String>).clear()
            Toast.makeText(this, "All history cleared", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Database cleared.")
        }
    }
}
