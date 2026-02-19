package com.example.appp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val LOCATION_PERMISSION_CODE = 101
    private val NOTIFICATION_PERMISSION_CODE = 102
    private val CHANNEL_ID = "booking_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()
        requestNotificationPermission()
        createNotificationChannel()

        val img1 = findViewById<ImageView>(R.id.gym1)
        val img2 = findViewById<ImageView>(R.id.gym2)
        val img3 = findViewById<ImageView>(R.id.gym3)

        img1.setOnClickListener { showDetailsConfirmation("Chest Workout", R.drawable.chest) }
        img2.setOnClickListener { showDetailsConfirmation("Bicep Workout", R.drawable.bic) }
        img3.setOnClickListener { showDetailsConfirmation("Shoulder Workout", R.drawable.bicep) }

        img1.setOnLongClickListener { showPopup(it); true }
        img2.setOnLongClickListener { showPopup(it); true }
        img3.setOnLongClickListener { showPopup(it); true }
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE)
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                }
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


    private fun showDetailsConfirmation(text: String, image: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Workout Details")
        builder.setMessage("Would you like to view details for $text?")

        builder.setPositiveButton("View Details") { _, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("text", text)
            intent.putExtra("image", image)
            startActivity(intent)
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun bookSession(workout: String) {

        getLastLocation()

        if (currentLocation != null) {

            val lat = currentLocation!!.latitude
            val lng = currentLocation!!.longitude
            val address = getAddress(currentLocation!!)

            val message = """
                Session Booked: $workout
                
                Lat: $lat
                Lng: $lng
                Address: $address
            """.trimIndent()

            showNotification("Workout Booked ", message)

            Toast.makeText(this,
                "Session Booked Successfully!",
                Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this,
                "Location not available",
                Toast.LENGTH_SHORT).show()
        }
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

    // ================= POPUP =================

    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_view -> {
                    Toast.makeText(this,
                        "View Details",
                        Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
