package com.example.smart_budget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SmartBudget", "MainActivity: onCreate started")
        try {
            // Temporarily disabling enableEdgeToEdge to rule it out as a crash cause
            // enableEdgeToEdge()
            Log.d("SmartBudget", "MainActivity: Setting content view")
            setContentView(R.layout.activity_main)

            Log.d("SmartBudget", "MainActivity: Applying window insets")
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Context menu: long-press on the "Budget Tracker" title
            Log.d("SmartBudget", "MainActivity: Registering context menu")
            val titleTextView: TextView = findViewById(R.id.textView3)
            registerForContextMenu(titleTextView)

            // Login button: start MainActivity2 (profile) when clicked
            Log.d("SmartBudget", "MainActivity: Setting login button listener")
            val loginButton: Button = findViewById(R.id.button2)
            val progressBar: ProgressBar = findViewById(R.id.loginProgress)
            
            loginButton.setOnClickListener {
                Log.d("SmartBudget", "MainActivity: Login button clicked, starting simulation")
                
                // Show Progress Bar and disable button
                progressBar.visibility = View.VISIBLE
                loginButton.isEnabled = false
                
                // Simulate network delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                    
                    // Show AlertDialog
                    AlertDialog.Builder(this)
                        .setTitle("Welcome Back!")
                        .setMessage("You have successfully logged in to SmartBudget.")
                        .setPositiveButton("Continue") { _, _ ->
                            val intent = Intent(this, MainActivity2::class.java)
                            startActivity(intent)
                        }
                        .setCancelable(false)
                        .show()
                }, 2000) // 2 second delay
            }
            Log.d("SmartBudget", "MainActivity: onCreate successfully completed")
        } catch (t: Throwable) {
            // Catching Throwable to capture errors and potential native crashes more broadly
            Log.e("SmartBudget", "MainActivity: CRITICAL ERROR during startup", t)
            Toast.makeText(this, "Startup error: ${t.message}", Toast.LENGTH_LONG).show()
            // Optionally finish the activity if it's in a broken state
            // finish()
        }
    }

    // Context menu (long press on registered view)
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
        menu?.setHeaderTitle("Select Action")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.context_edit -> {
                Toast.makeText(this, "Edit selected", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.context_delete -> {
                Toast.makeText(this, "Delete selected", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.context_share -> {
                Toast.makeText(this, "Share selected", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    // Popup menu (attached to "Menu" button)
    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_add_income -> {
                    Toast.makeText(this, "Add Income clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.popup_add_expense -> {
                    Toast.makeText(this, "Add Expense clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.popup_view_report -> {
                    Toast.makeText(this, "View Report clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}
