package com.example.prog7313poe.Database.Expenses

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExpenseRepository(application: Application) {

    private val expenseDAO: ExpenseDAO = AppDatabase.getDatabase(application).expenseDAO()
    val allExpenses: LiveData<List<ExpenseData>> = expenseDAO.getAllExpenses()

    suspend fun insert(expenseData: ExpenseData) {
        withContext(Dispatchers.IO) {
            expenseDAO.insertExpense(expenseData)
        }
    }

    suspend fun delete(expenseData: ExpenseData) {
        withContext(Dispatchers.IO) {
            expenseDAO.deleteExpense(expenseData)
        }
    }
}
