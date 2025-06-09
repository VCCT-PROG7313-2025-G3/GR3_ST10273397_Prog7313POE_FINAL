package com.example.prog7313poe.ui.budget

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Expenses.ExpenseData
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

    private var allExpenses: List<ExpenseData> = listOf()

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
        holder.binding.tvBudgetAmount.text = "Amount: R${budget.budgetMinAmount} - R${budget.budgetMaxAmount}"

        val startDateString = dateFormatter.format(Date(budget.budgetStartTime))
        val endDateString   = dateFormatter.format(Date(budget.budgetEndTime))
        holder.binding.tvBudgetDateRange.text =
            "Date: $startDateString to $endDateString"

        val matchingExpenses = allExpenses.filter {
            it.expenseCategory == budget.budgetCategory &&
                    it.expenseDate in budget.budgetStartTime..budget.budgetEndTime
        }

        val totalSpent = matchingExpenses.sumOf { it.expenseAmount }
        val min = budget.budgetMinAmount
        val max = budget.budgetMaxAmount

        val progressPercent = if (max > 0) {
            ((totalSpent / max) * 100).toInt().coerceIn(0, 100)
        } else 0

        holder.binding.progressBar.progress = progressPercent
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

    fun setExpenses(expenses: List<ExpenseData>) {
        this.allExpenses = expenses
        notifyDataSetChanged()
    }

}
