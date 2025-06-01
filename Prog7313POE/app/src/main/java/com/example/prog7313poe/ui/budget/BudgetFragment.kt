package com.example.prog7313poe.ui.budget

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Budgets.AppDatabase
import com.example.prog7313poe.Database.Budgets.BudgetDAO
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.R
import com.example.prog7313poe.Database.Budgets.AppDatabase as BudgetDB
import com.example.prog7313poe.Database.Expenses.AppDatabase as ExpenseDB
import com.example.prog7313poe.databinding.DialogBudgetSummaryBinding
import com.example.prog7313poe.databinding.FragmentBudgetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BudgetAdapter
    private val budgetList = mutableListOf<BudgetData>()

    private var totalBudget: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter(budgetList)
        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = adapter

        binding.btnAddBudget.setOnClickListener {
            showAddBudgetDialog(requireContext()) { newBudget ->
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).budgetDAO().insertBudget(newBudget)
                }
            }
        }

        binding.btnBudgetSummary.setOnClickListener {
            showBudgetSummary()
        }

        loadLatestBudget()
    }

    private fun showAddBudgetDialog(context: Context, onBudgetAdded: (BudgetData) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_budget, null)
        val amountInput = dialogView.findViewById<EditText>(R.id.et_budget_amount)
        val descInput = dialogView.findViewById<EditText>(R.id.et_budget_desc)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_Save)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = descInput.text.toString().trim()

            if (amount > 0) {
                val budget = BudgetData(
                    budgetId = 0,
                    budgetName = "",
                    budgetCategory = "",
                    budgetAmount = amount,
                    budgetStartTime = 0,
                    budgetEndTime = 0,
                    budgetDesc = description
                )
                onBudgetAdded(budget)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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

    private fun loadLatestBudget() {
        val dao = AppDatabase.getDatabase(requireContext()).budgetDAO()

        lifecycleScope.launch {
            // Switch to IO thread for the DB query
            val budgets = withContext(Dispatchers.IO) {
                dao.getAllBudgets()
            }

            // Now safely update UI on the Main thread
            budgetList.clear()
            budgetList.addAll(budgets)
            adapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
