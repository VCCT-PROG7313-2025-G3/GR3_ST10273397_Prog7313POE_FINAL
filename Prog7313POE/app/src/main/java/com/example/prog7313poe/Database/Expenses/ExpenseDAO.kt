package com.example.prog7313poe.Database.Expenses

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {

    @Insert
    fun insertExpense(expenses: ExpenseData): Long

    @Query("SELECT * FROM Expenses")
    fun getAllExpenses(): List<ExpenseData>

    @Delete
    fun deleteExpense(expenses: ExpenseData): Int
}