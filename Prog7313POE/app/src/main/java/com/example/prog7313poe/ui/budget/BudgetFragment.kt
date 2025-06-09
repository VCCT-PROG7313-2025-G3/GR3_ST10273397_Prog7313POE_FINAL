package com.example.prog7313poe.ui.budget

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Budgets.FirebaseBudgetDbHelper
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.Database.Expenses.FirebaseExpenseDbHelper
import com.example.prog7313poe.R
import com.example.prog7313poe.databinding.DialogBudgetSummaryBinding
import com.example.prog7313poe.databinding.FragmentBudgetBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BudgetAdapter
    private val budgetList = mutableListOf<BudgetData>()
    private val expenseList = mutableListOf<ExpenseData>()

    private var totalBudget: Double = 0.0

    // ===== Realtime Database Reference for categories =====
    private val realtimeDb = FirebaseDatabase.getInstance(
        "https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/"
    )
    private val categoriesRef: DatabaseReference = realtimeDb.getReference("categories")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter()
        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = adapter

        adapter.setBudgets(budgetList)
        adapter.setExpenses(expenseList)

        binding.btnAddBudget.setOnClickListener {
            showAddBudgetDialog(requireContext()) { newBudget ->
                FirebaseBudgetDbHelper.insertBudget(
                    budgetName = newBudget.budgetName,
                    budgetCategory = newBudget.budgetCategory,
                    budgetMinAmount = newBudget.budgetMinAmount,
                    budgetMaxAmount = newBudget.budgetMaxAmount,
                    budgetStartTime = newBudget.budgetStartTime,
                    budgetEndTime = newBudget.budgetEndTime,
                    budgetDesc = newBudget.budgetDesc
                ) {
                    loadLatestBudgetAndExpense()
                }
            }
        }

        binding.btnBudgetSummary.setOnClickListener {
            showBudgetSummary()
        }

        loadLatestBudgetAndExpense()
    }

    private fun showAddBudgetDialog(
        context: Context,
        onBudgetAdded: (BudgetData) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_add_budget, null)

        val minAmountInput = dialogView.findViewById<EditText>(R.id.et_min_amount)
        val maxAmountInput = dialogView.findViewById<EditText>(R.id.et_max_amount)
        val descInput = dialogView.findViewById<EditText>(R.id.et_desc)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val categoryInput = dialogView.findViewById<Spinner>(R.id.spn_category)
        val startDateInput = dialogView.findViewById<EditText>(R.id.et_start_time)
        val endDateInput = dialogView.findViewById<EditText>(R.id.et_end_time)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        startDateInput.apply {
            showSoftInputOnFocus = false
            inputType = InputType.TYPE_NULL
            isFocusable = false
            isCursorVisible = false
            isClickable = true
        }
        endDateInput.apply {
            showSoftInputOnFocus = false
            inputType = InputType.TYPE_NULL
            isFocusable = false
            isCursorVisible = false
            isClickable = true
        }

        // ========== 1) Load categories into the Spinner ==========
        // This reads every child under “categories” in your Realtime DB,
        // extracts the "categoryName" field, and feeds it into the Spinner.
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<String>()
                snapshot.children.forEach { child ->
                    child.child("categoryName")
                        .getValue(String::class.java)
                        ?.let { categories.add(it) }
                }
                categories.add(0, "Select category")

                val spinnerAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    categories
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categoryInput.adapter = spinnerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Error loading categories: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        startDateInput.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val chosen = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    startDateInput.setText(fmt.format(chosen.time))
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        endDateInput.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val chosen = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    endDateInput.setText(fmt.format(chosen.time))
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val category = categoryInput.selectedItem.toString().trim()
            val minAmount = minAmountInput.text.toString().toDoubleOrNull() ?: 0.0
            val maxAmount = maxAmountInput.text.toString().toDoubleOrNull() ?: 0.0
            val startStr = startDateInput.text.toString().trim()
            val endStr = endDateInput.text.toString().trim()
            val description = descInput.text.toString().trim()

            if (name.isBlank() || category.isBlank() || category == "Select category" || maxAmount <= 0.0 || startStr.isBlank() || endStr.isBlank()) {
                Toast.makeText(
                    context,
                    "Please enter name, positive amount, and both start/end dates.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedStart: Date? = try {
                dateFormat.parse(startStr)
            } catch (e: Exception) {
                null
            }
            val parsedEnd: Date? = try {
                dateFormat.parse(endStr)
            } catch (e: Exception) {
                null
            }

            if (parsedStart == null || parsedEnd == null) {
                Toast.makeText(
                    context,
                    "Dates must be in yyyy-MM-dd format.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val startMillis = parsedStart.time
            val endMillis = parsedEnd.time

            val budget = BudgetData(
                budgetId = "",
                budgetName = name,
                budgetCategory = category,
                budgetMinAmount = minAmount,
                budgetMaxAmount = maxAmount,
                budgetStartTime = startMillis,
                budgetEndTime = endMillis,
                budgetDesc = description
            )

            onBudgetAdded(budget)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBudgetSummary() {
        totalBudget = budgetList.sumOf { it.budgetMaxAmount }

        FirebaseExpenseDbHelper.getAllExpenses { expenses ->
            val totalSpent = expenses.sumOf { it.expenseAmount }
            val remaining = totalBudget - totalSpent

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

    private fun loadLatestBudgetAndExpense() {
        // 1) Load budgets
        FirebaseBudgetDbHelper.getAllBudgets { budgets ->
            budgetList.clear()
            budgetList.addAll(budgets)
            adapter.setBudgets(budgetList)

            // 2) Now load expenses
            FirebaseExpenseDbHelper.getAllExpenses { expenses ->
                expenseList.clear()
                expenseList.addAll(expenses)
                adapter.setExpenses(expenseList)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
