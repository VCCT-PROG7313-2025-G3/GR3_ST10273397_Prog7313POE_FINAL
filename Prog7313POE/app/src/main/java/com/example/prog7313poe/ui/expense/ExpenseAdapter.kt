package com.example.prog7313poe.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.ItemExpenseBinding
import java.io.File

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

    override fun getItemCount(): Int = expenses.size
}
