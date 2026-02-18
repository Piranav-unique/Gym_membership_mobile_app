package com.example.smart_budget

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val amount: String,
    val date: String,
    val time: String,
    val category: String
)
