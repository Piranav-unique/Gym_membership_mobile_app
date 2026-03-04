package com.example.smart_budget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CoordinatorLayoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: ExpenseAdapter
    private lateinit var repository: ExpenseRepository
    private var selectedExpense: Expense? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SmartBudget", "CoordinatorLayoutActivity: onCreate started")
        try {
            setContentView(R.layout.activity_coordinator_layout)

            Log.d("SmartBudget", "CoordinatorLayoutActivity: Setting toolbar")
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            
            repository = ExpenseRepository(this)
            recyclerView = findViewById(R.id.expenseRecyclerView)
            emptyState = findViewById(R.id.emptyStateContainer)
            
            recyclerView.layoutManager = LinearLayoutManager(this)
            
            adapter = ExpenseAdapter(emptyList()) { expense ->
                selectedExpense = expense
            }
            recyclerView.adapter = adapter
            registerForContextMenu(recyclerView)

            // FAB Logic: Navigate to Add Expense Activity
            val fab: FloatingActionButton = findViewById(R.id.addExpenseFab)
            fab.setOnClickListener {
                val intent = Intent(this, AddExpenseActivity::class.java)
                startActivity(intent)
            }
            
            Log.d("SmartBudget", "CoordinatorLayoutActivity: onCreate successfully completed")
        } catch (t: Throwable) {
            Log.e("SmartBudget", "CoordinatorLayoutActivity: CRITICAL ERROR during startup", t)
            Toast.makeText(this, "Startup error in Dashboard: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshExpenses()
    }

    private fun refreshExpenses() {
        val expenses = repository.getAllExpenses()
        adapter.updateData(expenses)
        
        if (expenses.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateContextMenu(menu: android.view.ContextMenu?, v: View?, menuInfo: android.view.ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.expense_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                Toast.makeText(this, "edit clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_delete -> {
                Toast.makeText(this, "delete clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_about -> {
                Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
