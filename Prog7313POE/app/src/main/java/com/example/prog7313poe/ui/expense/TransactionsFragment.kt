package com.example.prog7313poe.ui.expense

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313poe.Database.Expenses.AppDatabase
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.R
import com.example.prog7313poe.databinding.FragmentTransactionsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpenseAdapter
    private val expenseList = mutableListOf<ExpenseData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ExpenseAdapter(expenseList)
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        binding.btnAddExpense.setOnClickListener {
            showAddExpenseDialog(requireContext()) { newExpense ->
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).expenseDAO().insertExpense(newExpense)
                }
            }
        }
        loadExpenses() //safe to call this ONCE on the main thread (outside the coroutine)

    }

    private fun showAddExpenseDialog(context: Context, onExpenseAdded: (ExpenseData) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val categoryInput = dialogView.findViewById<EditText>(R.id.et_category)
        val amountInput = dialogView.findViewById<EditText>(R.id.et_amount)
        val startTimeInput = dialogView.findViewById<EditText>(R.id.et_start_time)
        val endTimeInput = dialogView.findViewById<EditText>(R.id.et_end_time)
        val descInput = dialogView.findViewById<EditText>(R.id.et_desc)
        val photoPathInput = dialogView.findViewById<EditText>(R.id.et_photo_path)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val category = categoryInput.text.toString().trim()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val start = startTimeInput.text.toString().trim()
            val end = endTimeInput.text.toString().trim()
            val description = descInput.text.toString().trim()
            val photo = photoPathInput.text.toString().trim()

            if (name.isNotBlank() && category.isNotBlank() && amount > 0) {
                val expense = ExpenseData(
                    expenseId = 0, // let Room auto-generate
                    expenseName = name,
                    expenseCategory = category,
                    expenseAmount = amount,
                    expenseDate = System.currentTimeMillis(), // or a parsed date if needed
                    expenseStartTime = start,
                    expenseEndTime = end,
                    expenseDesc = description,
                    expensePhotoPath = photo
                )

                onExpenseAdded(expense)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loadExpenses() {
        val dao = AppDatabase.getDatabase(requireContext()).expenseDAO()
        dao.getAllExpenses().observe(viewLifecycleOwner) { expenses ->
            expenseList.clear()
            expenseList.addAll(expenses)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
