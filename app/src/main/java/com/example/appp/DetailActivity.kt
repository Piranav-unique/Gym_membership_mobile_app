package com.example.appp

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar


class DetailActivity : AppCompatActivity() {
    private val CHANNEL_ID = "booking_notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra("text")
        val imageRes = intent.getIntExtra("image", 0)

        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<ImageView>(R.id.detailImage).setImageResource(imageRes)

        // Setup toolbar back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val tvSelectedTime = findViewById<TextView>(R.id.tvSelectedTime)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnBookSession = findViewById<Button>(R.id.btnBookSession)

        findViewById<Button>(R.id.btnPickDate).setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                tvSelectedDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            }, year, month, day)
            dpd.show()
        }

        findViewById<Button>(R.id.btnPickTime).setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                tvSelectedTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }, hour, minute, true)
            tpd.show()
        }

        btnBookSession.setOnClickListener {
            checkNotificationPermissionAndBook(title)
        }

        createNotificationChannel()
    }

    private fun checkNotificationPermissionAndBook(title: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
                return
            }
        }
        performBooking(title)
    }

    private fun performBooking(title: String?) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnBookSession = findViewById<Button>(R.id.btnBookSession)
        
        progressBar.visibility = View.VISIBLE
        btnBookSession.isEnabled = false

        // Simulate booking process
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            btnBookSession.isEnabled = true
            showStatusNotification(title ?: "Gym Session")
            Toast.makeText(this, "Booking Successful!", Toast.LENGTH_SHORT).show()
        }, 2000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val title = intent.getStringExtra("text")
                performBooking(title)
            } else {
                Toast.makeText(this, "Notification permission denied. You won't see the booking confirmation.", Toast.LENGTH_LONG).show()
                val title = intent.getStringExtra("text")
                performBooking(title) // Still perform booking, just user won't see notification
            }
        }
    }

    private fun showStatusNotification(workoutName: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Booking Successful")
            .setContentText("Your $workoutName session has been booked!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Booking Notifications"
            val descriptionText = "Channel for gym booking status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}