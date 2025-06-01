package com.example.prog7313poe.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.databinding.ItemBudgetBinding

class BudgetAdapter(private val budgets: List<BudgetData>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBudgetBinding.inflate(inflater, parent, false)

        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]

        holder.binding.tvBudgetName.text = budget.budgetName
        holder.binding.tvBudgetCategory.text = "Category: ${budget.budgetCategory}"
        holder.binding.tvBudgetAmount.text = "Amount: R${budget.budgetAmount}"
        holder.binding.tvBudgetDateRange.text = "Date: ${budget.budgetStartTime} to ${budget.budgetEndTime}"
    }

    override fun getItemCount(): Int = budgets.size
}
