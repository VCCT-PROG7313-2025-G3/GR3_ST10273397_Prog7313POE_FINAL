package com.example.prog7313poe

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Budgets.AppDatabase as BudgetDB
import com.example.prog7313poe.Database.Expenses.AppDatabase as ExpenseDB
import com.example.prog7313poe.databinding.DialogAddBudgetBinding
import com.example.prog7313poe.databinding.DialogBudgetSummaryBinding
import com.example.prog7313poe.databinding.FragmentBudgetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private var totalBudget: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAddBudget.setOnClickListener {
            showAddBudgetDialog()
        }

        binding.btnBudgetSummary.setOnClickListener {
            showBudgetSummary()
        }

        loadLatestBudget()
    }

    private fun showAddBudgetDialog() {
        val dialogBinding = DialogAddBudgetBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Budget")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val amount = dialogBinding.etBudgetAmount.text.toString().toDoubleOrNull()
                val desc = dialogBinding.etBudgetDesc.text.toString()

                if (amount != null) {
                    val newBudget = BudgetData(
                        budgetId = 0,
                        budgetName = "Monthly Budget",
                        budgetCategory = "General",
                        budgetAmount = amount, // ✅ required field
                        budgetStartTime = System.currentTimeMillis(),
                        budgetEndTime = System.currentTimeMillis(),
                        budgetDesc = desc
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        BudgetDB.getDatabase(requireContext()).budgetDAO().insertBudget(newBudget)
                        loadLatestBudget()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBudgetSummary() {
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = ExpenseDB.getDatabase(requireContext()).expenseDAO().getAllExpenses()
            val totalSpent = expenses.sumOf {
                it.expenseCategory.toDoubleOrNull() ?: 0.0 // ⚠ placeholder
            }
            val remaining = totalBudget - totalSpent

            withContext(Dispatchers.Main) {
                val dialogBinding = DialogBudgetSummaryBinding.inflate(layoutInflater)

                dialogBinding.tvSummaryTotal.text = "Total Budget: R$totalBudget"
                dialogBinding.tvSummarySpent.text = "Spent: R$totalSpent"
                dialogBinding.tvSummaryRemaining.text = "Remaining: R$remaining"

                AlertDialog.Builder(requireContext())
                    .setTitle("Budget Summary")
                    .setView(dialogBinding.root)
                    .setPositiveButton("Close", null)
                    .show()
            }
        }
    }

    private fun loadLatestBudget() {
        lifecycleScope.launch(Dispatchers.IO) {
            val budgets = BudgetDB.getDatabase(requireContext()).budgetDAO().getAllBudgets()
            totalBudget = budgets.lastOrNull()?.budgetAmount ?: 0.0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
