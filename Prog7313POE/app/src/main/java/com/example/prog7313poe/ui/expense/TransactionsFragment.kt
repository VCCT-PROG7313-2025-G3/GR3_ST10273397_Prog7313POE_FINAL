package com.example.prog7313poe.ui.expense

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313poe.Database.Expenses.AppDatabase
import com.example.prog7313poe.Database.Expenses.ExpenseData
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
            // ✅ Show dialog when button clicked
            AddExpenseDialog(requireContext()) { newExpense ->
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).expenseDAO().insertExpense(newExpense)
                    loadExpenses()
                }
            }.show()
        }

        loadExpenses()
    }

    private fun loadExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(requireContext()).expenseDAO()
            val expenses = dao.getAllExpenses()
            withContext(Dispatchers.Main) {
                expenseList.clear()
                expenseList.addAll(expenses)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
