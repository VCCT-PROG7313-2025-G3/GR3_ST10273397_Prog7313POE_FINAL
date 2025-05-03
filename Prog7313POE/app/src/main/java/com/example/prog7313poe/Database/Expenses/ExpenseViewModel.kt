package com.example.prog7313poe.Database.Expenses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDAO: ExpenseDAO = AppDatabase.getDatabase(application).expenseDAO()
    val allExpenses: LiveData<List<ExpenseData>> = expenseDAO.getAllExpenses()

    fun insert(expenses: ExpenseData) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseDAO.insertExpense(expenses)
        }
    }

    fun delete(expenses: ExpenseData) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDAO.deleteExpense(expenses)
        }
    }
}

