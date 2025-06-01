package com.example.prog7313poe.ui.expense

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.ItemExpenseBinding
import java.io.File
import java.util.Locale

class ExpenseAdapter(private var displayedExpenses: List<ExpenseData> = listOf()) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var allExpenses: List<ExpenseData> = listOf()


    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExpenseBinding.inflate(inflater, parent, false)

        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = displayedExpenses[position]

        Log.d("ExpenseAdapter", "Photo path: ${expense.expensePhotoPath}")

        holder.binding.tvExpenseName.text = expense.expenseName
        holder.binding.tvExpenseCategory.text = "Category: ${expense.expenseCategory}"
        holder.binding.tvExpenseAmount.text = "Amount: R${expense.expenseAmount}"
        holder.binding.tvExpenseDate.text = "Date: ${expense.expenseStartTime} to ${expense.expenseEndTime}"

        if (expense.expensePhotoPath.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(File(expense.expensePhotoPath)) // local file path
                .into(holder.binding.ivExpensePhoto)
        } else {
            holder.binding.ivExpensePhoto.setImageResource(android.R.drawable.picture_frame)
        }

    }

    override fun getItemCount(): Int = displayedExpenses.size

    fun setExpenses(expenses: List<ExpenseData>) {
        this.allExpenses = expenses
        this.displayedExpenses = expenses
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            displayedExpenses = allExpenses
        } else {
            displayedExpenses = allExpenses.filter { expense ->
                expense.expenseName.contains(query, ignoreCase = true) ||
                        expense.expenseCategory.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
