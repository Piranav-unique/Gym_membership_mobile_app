package com.example.smart_budget

import android.content.Context
import android.content.SharedPreferences

class ExpenseRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smart_budget_prefs", Context.MODE_PRIVATE)

    fun saveExpense(expense: Expense) {
        val expenses = getAllExpenses().toMutableList()
        val index = expenses.indexOfFirst { it.id == expense.id }
        if (index != -1) {
            expenses[index] = expense
        } else {
            expenses.add(expense)
        }
        saveAllExpenses(expenses)
    }

    fun deleteExpense(id: String) {
        val expenses = getAllExpenses().toMutableList()
        expenses.removeAll { it.id == id }
        saveAllExpenses(expenses)
    }

    fun getAllExpenses(): List<Expense> {
        val serialized = prefs.getString("expenses_list", "") ?: ""
        if (serialized.isEmpty()) return emptyList()
        
        return serialized.split("|||").filter { it.isNotEmpty() }.map {
            val parts = it.split("###")
            Expense(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5])
        }
    }

    private fun saveAllExpenses(expenses: List<Expense>) {
        val serialized = expenses.joinToString("|||") { 
            "${it.id}###${it.description}###${it.amount}###${it.date}###${it.time}###${it.category}"
        }
        prefs.edit().putString("expenses_list", serialized).apply()
    }

    fun getExpenseById(id: String): Expense? {
        return getAllExpenses().find { it.id == id }
    }
}
