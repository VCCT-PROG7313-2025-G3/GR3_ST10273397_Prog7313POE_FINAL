package com.example.prog7313poe.ui.budget

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.databinding.ItemBudgetBinding
import java.text.SimpleDateFormat
import java.util.*

class BudgetAdapter(
    private var displayedBudgets: List<BudgetData> = listOf()
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // Keep an unfiltered master list
    private var allBudgets: List<BudgetData> = listOf()

    // Reuse the same date format as ExpenseAdapter
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class BudgetViewHolder(val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBudgetBinding.inflate(inflater, parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = displayedBudgets[position]

        // Log the raw timestamps if you need debugging
        Log.d("BudgetAdapter", "Budget start‐time (ms): ${budget.budgetStartTime}")
        Log.d("BudgetAdapter", "Budget end‐time   (ms): ${budget.budgetEndTime}")

        holder.binding.tvBudgetName.text = budget.budgetName
        holder.binding.tvBudgetCategory.text = "Category: ${budget.budgetCategory}"
        holder.binding.tvBudgetAmount.text = "Amount: R${budget.budgetAmount}"

        val startDateString = dateFormatter.format(Date(budget.budgetStartTime))
        val endDateString   = dateFormatter.format(Date(budget.budgetEndTime))
        holder.binding.tvBudgetDateRange.text =
            "Date: $startDateString to $endDateString"
    }

    override fun getItemCount(): Int = displayedBudgets.size

    /**
     * Call this when you have a brand‐new list of budgets from Firebase (or elsewhere).
     * It replaces both allBudgets and displayedBudgets, then notifies the adapter.
     */
    fun setBudgets(budgets: List<BudgetData>) {
        this.allBudgets = budgets
        this.displayedBudgets = budgets
        notifyDataSetChanged()
    }
}
