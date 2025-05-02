package com.example.prog7313poe.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.ItemExpenseBinding

class ExpenseAdapter(private val expenses: List<ExpenseData>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExpenseBinding.inflate(inflater, parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.binding.tvExpenseName.text = expense.expenseName
        holder.binding.tvExpenseCategory.text = "Category: ${expense.expenseCategory}"
        holder.binding.tvExpenseTime.text =
            "From ${expense.expenseStartTime} to ${expense.expenseEndTime}"
    }

    override fun getItemCount(): Int = expenses.size
}
