package com.example.prog7313poe.Database.Expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ExpenseRepository {

    private val _expensesLiveData = MutableLiveData<List<ExpenseData>>()
    val expensesLiveData: LiveData<List<ExpenseData>> get() = _expensesLiveData

    fun insert(expense: ExpenseData, onComplete: () -> Unit = {}) {
        FirebaseExpenseDbHelper.insertExpenses(expense) {
            // Optionally refresh the list after insert
            getAllExpenses()
            onComplete()
        }
    }

    fun getAllExpenses() {
        FirebaseExpenseDbHelper.getAllExpenses { expenses ->
            _expensesLiveData.postValue(expenses)
        }
    }

    fun deleteAllExpenses(onComplete: () -> Unit = {}) {
        FirebaseExpenseDbHelper.deleteAllExpenses {
            getAllExpenses()
            onComplete()
        }
    }
}
