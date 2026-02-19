package com.example.appp

import android.Manifest
import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var progressBar: ProgressBar

    private val CHANNEL_ID = "booking_channel"
    private val LOCATION_PERMISSION_CODE = 101
    private val NOTIFICATION_PERMISSION_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_detail)
        setSupportActionBar(toolbar)

        val detailImage = findViewById<ImageView>(R.id.detailImage)
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailDescription = findViewById<TextView>(R.id.detailDescription)
        val btnPickDate = findViewById<Button>(R.id.btnPickDate)
        val btnPickTime = findViewById<Button>(R.id.btnPickTime)
        val btnBookSession = findViewById<Button>(R.id.btnBookSession)

        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        progressBar = findViewById(R.id.progressBar)

        // Receive data from MainActivity
        val workoutName = intent.getStringExtra("text") ?: "Workout"
        val workoutImage = intent.getIntExtra("image", 0)

        detailTitle.text = workoutName
        detailImage.setImageResource(workoutImage)
        detailDescription.text = "Follow the instructions carefully for the best results."

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()
        requestNotificationPermission()
        createNotificationChannel()

        // Date Picker
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    tvSelectedDate.text = "$day/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Time Picker
        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    tvSelectedTime.text = "$hour:$minute"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Book Session
        btnBookSession.setOnClickListener {
            bookSession(workoutName)
        }
    }

    // ================= LOCATION =================

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                currentLocation = it
            }
        }
    }

    private fun getAddress(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val list = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (!list.isNullOrEmpty())
                list[0].getAddressLine(0)
            else
                "Address unavailable"
        } catch (e: Exception) {
            "Unable to fetch address"
        }
    }

    // ================= BOOK SESSION =================

    private fun bookSession(workout: String) {

        if (tvSelectedDate.text == "No Date Selected" ||
            tvSelectedTime.text == "No Time Selected") {

            Toast.makeText(this,
                "Please select date and time",
                Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        getLastLocation()

        progressBar.postDelayed({

            progressBar.visibility = View.GONE

            if (currentLocation != null) {

                val lat = currentLocation!!.latitude
                val lng = currentLocation!!.longitude
                val address = getAddress(currentLocation!!)

                val message = """
                    Workout: $workout
                    Date: ${tvSelectedDate.text}
                    Time: ${tvSelectedTime.text}
                    
                    Lat: $lat
                    Lng: $lng
                    Address: $address
                """.trimIndent()

                showNotification("Session Booked ✅", message)

                Toast.makeText(this,
                    "Session Booked Successfully!",
                    Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this,
                    "Location not available",
                    Toast.LENGTH_SHORT).show()
            }

        }, 2000)
    }

    // ================= NOTIFICATION =================

    private fun showNotification(title: String, message: String) {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Workout Booking",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE)
            }
        }
    }
}
