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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

    private fun openDetail(text: String, image: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("text", text)
        intent.putExtra("image", image)
        startActivity(intent)
    }

    private fun showDetailsConfirmation(text: String, image: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Workout Details")
        builder.setMessage("Would you like to view the details for $text?")
        builder.setPositiveButton("View Details") { _, _ ->
            openDetail(text, image)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Ensure R.menu.context_menu exists in your res/menu folder
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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

    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_view -> {
                    Toast.makeText(this, "View Details", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.popup_favorite -> {
                    Toast.makeText(this, "Added to Favorite", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}