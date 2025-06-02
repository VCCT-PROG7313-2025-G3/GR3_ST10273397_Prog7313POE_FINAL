package com.example.prog7313poe.ui.expense

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.ItemExpenseBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private var displayedExpenses: List<ExpenseData> = listOf()) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var allExpenses: List<ExpenseData> = listOf()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExpenseBinding.inflate(inflater, parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = displayedExpenses[position]

        holder.binding.tvExpenseName.text = expense.expenseName
        holder.binding.tvExpenseCategory.text = "Category: ${expense.expenseCategory}"
        holder.binding.tvExpenseAmount.text = "Amount: R${expense.expenseAmount}"

        // Format the stored Long → "yyyy-MM-dd"
        val dateString = dateFormatter.format(Date(expense.expenseDate))
        holder.binding.tvExpenseDate.text = "Date: $dateString"

        val photoPath = expense.expensePhotoPath

        if (photoPath.isNotBlank()) {
            if (photoPath.startsWith("http")) {
                // 1) Remote URL: load directly from Firebase Storage HTTP link
                Glide.with(holder.itemView.context)
                    .load(photoPath)
                    .placeholder(android.R.drawable.picture_frame)
                    .into(holder.binding.ivExpensePhoto)

            } else {
                // 2) Assume it's a local File URI (e.g. "file:///storage/…")
                val imageFile = File(photoPath)
                if (imageFile.exists()) {
                    Glide.with(holder.itemView.context)
                        .load(imageFile)
                        .placeholder(android.R.drawable.picture_frame)
                        .into(holder.binding.ivExpensePhoto)
                } else {
                    holder.binding.ivExpensePhoto.setImageResource(android.R.drawable.picture_frame)
                }
            }
        } else {
            // 3) No path at all → show placeholder
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
        displayedExpenses = if (query.isBlank()) {
            allExpenses
        } else {
            allExpenses.filter { expense ->
                expense.expenseName.contains(query, ignoreCase = true) ||
                        expense.expenseCategory.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
