package com.example.prog7313poe.Database.Expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ExpenseViewModel : ViewModel() {

    private val repository = ExpenseRepository()
    val allExpenses: LiveData<List<ExpenseData>> = repository.expensesLiveData

    init {
        // Load data when ViewModel is created
        repository.getAllExpenses()
    }

    fun insertExpense(expense: ExpenseData, onComplete: () -> Unit = {}) {
        repository.insert(expense, onComplete)
    }

    fun deleteAllExpenses(onComplete: () -> Unit = {}) {
        repository.deleteAllExpenses(onComplete)
    }
}
