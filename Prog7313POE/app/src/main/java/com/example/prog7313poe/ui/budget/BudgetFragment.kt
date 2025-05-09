package com.example.prog7313poe.ui.budget

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Categories.CategoryData
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

        binding.btnGoToTransactions.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.prog7313poe.R.id.fragment_container, com.example.prog7313poe.ui.expense.TransactionsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnGoToCategories.setOnClickListener {
            showAddCategoryDialog()
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
                        budgetAmount = amount,
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
        val dao = ExpenseDB.getDatabase(requireContext()).expenseDAO()
        dao.getAllExpenses().observe(viewLifecycleOwner) { expenses ->
            // now `expenses` is a List<ExpenseData>, so sumOf works
            val totalSpent = expenses.sumOf { it.expenseAmount }
            val remaining  = totalBudget - totalSpent

            val dialogBinding = DialogBudgetSummaryBinding.inflate(layoutInflater)
            dialogBinding.tvSummaryTotal.text     = "Total Budget: R$totalBudget"
            dialogBinding.tvSummarySpent.text     = "Spent: R$totalSpent"
            dialogBinding.tvSummaryRemaining.text = "Remaining: R$remaining"

            AlertDialog.Builder(requireContext())
                .setTitle("Budget Summary")
                .setView(dialogBinding.root)
                .setPositiveButton("Close", null)
                .show()
        }
    }

    private fun showAddCategoryDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter new category"
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Category")
            .setView(input)
            .setPositiveButton("Save") { dialog, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val dao = ExpenseDB.getDatabase(requireContext()).categoryDAO()
                            dao.insertCategory(CategoryData(categoryName = name))
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
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
