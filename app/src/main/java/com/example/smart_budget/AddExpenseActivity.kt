package com.example.smart_budget

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var descriptionInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var dateInput: TextInputEditText
    private lateinit var timeInput: TextInputEditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: MaterialButton
    private lateinit var repository: ExpenseRepository

    private var selectedDate = ""
    private var selectedTime = ""
    private var editingExpenseId: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showExpenseNotification()
        } else {
            Toast.makeText(this, "Notification permission denied. Cannot show notification.", Toast.LENGTH_SHORT).show()
        }
        finish() // Return to dashboard after saving or attempt
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        repository = ExpenseRepository(this)
        descriptionInput = findViewById(R.id.descriptionInput)
        amountInput = findViewById(R.id.amountInput)
        dateInput = findViewById(R.id.dateInput)
        timeInput = findViewById(R.id.timeInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveButton)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        dateInput.setOnClickListener {
            showDatePicker()
        }

        timeInput.setOnClickListener {
            showTimePicker()
        }

        saveButton.setOnClickListener {
            saveExpense()
        }

        // Check if we are editing
        editingExpenseId = intent.getStringExtra("EXPENSE_ID")
        editingExpenseId?.let { id ->
            val expense = repository.getExpenseById(id)
            expense?.let {
                descriptionInput.setText(it.description)
                amountInput.setText(it.amount)
                dateInput.setText(it.date)
                timeInput.setText(it.time)
                selectedDate = it.date
                selectedTime = it.time
                
                // Pre-select category in spinner
                val adapter = categorySpinner.adapter
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i).toString() == it.category) {
                        categorySpinner.setSelection(i)
                        break
                    }
                }
                
                saveButton.text = "Update Expense"
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            dateInput.setText(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            timeInput.setText(selectedTime)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    private fun saveExpense() {
        val description = descriptionInput.text.toString()
        val amount = amountInput.text.toString()

        if (description.isBlank() || amount.isBlank() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            id = editingExpenseId ?: UUID.randomUUID().toString(),
            description = description,
            amount = amount,
            date = selectedDate,
            time = selectedTime,
            category = categorySpinner.selectedItem.toString()
        )

        repository.saveExpense(expense)
        checkNotificationPermissionAndShow()
    }

    private fun checkNotificationPermissionAndShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    showExpenseNotification()
                    finish()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            showExpenseNotification()
            finish()
        }
    }

    private fun showExpenseNotification() {
        val channelId = "expense_notifications"
        val notificationId = 2 
        val category = categorySpinner.selectedItem.toString()
        val expenseDescription = descriptionInput.text.toString()
        val amount = amountInput.text.toString()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Expense Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for added expenses"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val title = if (editingExpenseId != null) "Expense Updated: $category" else "Expense Added: $category"

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle(title)
            .setContentText("$expenseDescription - ₹$amount on $selectedDate at $selectedTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            
        notificationManager.notify(notificationId, builder.build())
    }
}
