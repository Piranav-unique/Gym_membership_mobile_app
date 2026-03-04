package com.example.appp
import com.example.appp.R
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(this)
        databaseHelper = DatabaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val img1 = findViewById<ImageView>(R.id.gym1)
        val img2 = findViewById<ImageView>(R.id.gym2)
        val img3 = findViewById<ImageView>(R.id.gym3)

        img1.setOnClickListener { openDetail("Chest Workout", R.drawable.chest) }
        img2.setOnClickListener { openDetail("Bicep Workout", R.drawable.bic) }
        img3.setOnClickListener { openDetail("Shoulder Workout", R.drawable.bicep) }

        img1.setOnLongClickListener { showPopup(it, "Chest Workout"); true }
        img2.setOnLongClickListener { showPopup(it, "Bicep Workout"); true }
        img3.setOnLongClickListener { showPopup(it, "Shoulder Workout"); true }
    }

    private fun openDetail(text: String, image: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("text", text)
        intent.putExtra("image", image)
        startActivity(intent)
    } // Added missing closing brace here

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Ensure R.menu.context_menu exists in your res/menu folder
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                val intent = Intent(this, LogsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_about -> {
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopup(view: View, workoutName: String) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_view -> {
                    Toast.makeText(this, "Viewing $workoutName", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.popup_favorite -> {
                    preferenceManager.saveFavoriteWorkout(workoutName)
                    Toast.makeText(this, "$workoutName marked as Favorite", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.popup_log -> {
                    val id = databaseHelper.logWorkout(workoutName)
                    if (id != -1L) {
                        Toast.makeText(this, "$workoutName logged successfully", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}