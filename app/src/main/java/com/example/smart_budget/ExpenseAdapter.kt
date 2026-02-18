package com.example.smart_budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onLongPress: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.descriptionText)
        val details: TextView = view.findViewById(R.id.detailsText)
        val amount: TextView = view.findViewById(R.id.amountText)
        val date: TextView = view.findViewById(R.id.dateText)
        val categoryIndicator: View = view.findViewById(R.id.categoryIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.description.text = expense.description
        holder.details.text = "${expense.category} • ${expense.time}"
        holder.amount.text = "₹${expense.amount}"
        holder.date.text = expense.date

        holder.itemView.setOnLongClickListener {
            onLongPress(expense)
            it.showContextMenu()
            true
        }
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
