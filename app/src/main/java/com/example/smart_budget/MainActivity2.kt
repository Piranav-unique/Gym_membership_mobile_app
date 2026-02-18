package com.example.smart_budget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SmartBudget", "MainActivity2: onCreate started")
        try {
            // Temporarily disabling enableEdgeToEdge to rule it out as a crash cause
            // enableEdgeToEdge()
            Log.d("SmartBudget", "MainActivity2: Setting content view")
            setContentView(R.layout.activity_main2)

            Log.d("SmartBudget", "MainActivity2: Applying window insets")
            // Optional: adjust for system bars if needed
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Submit button: go to CoordinatorLayoutActivity (dashboard) to add expense
            Log.d("SmartBudget", "MainActivity2: Setting submit button listener")
            val submitButton: Button = findViewById(R.id.button)
            submitButton.setOnClickListener {
                Log.d("SmartBudget", "MainActivity2: Submit button clicked, saving and notifying")
                
                showStatusNotification()
                Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
                
                val intent = Intent(this, CoordinatorLayoutActivity::class.java)
                startActivity(intent)
                finish()
            }
            Log.d("SmartBudget", "MainActivity2: onCreate successfully completed")
        } catch (t: Throwable) {
            Log.e("SmartBudget", "MainActivity2: CRITICAL ERROR during startup", t)
            Toast.makeText(this, "Startup error in Profile: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showStatusNotification() {
        val channelId = "profile_updates"
        val notificationId = 1
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Profile Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for profile changes"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Profile Updated")
            .setContentText("Your profile information has been saved successfully.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            
        notificationManager.notify(notificationId, builder.build())
    }
}
