package com.example.smart_budget

import android.os.Bundle
import android.view.*
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TableLayout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_layout)

        val rowFood = findViewById<TableRow>(R.id.row_food)
        val rowTransport = findViewById<TableRow>(R.id.row_transport)
        val rowShopping = findViewById<TableRow>(R.id.row_shopping)

        registerForContextMenu(rowFood)
        registerForContextMenu(rowTransport)
        registerForContextMenu(rowShopping)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.context_edit -> {
                Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.context_delete -> {
                Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}
