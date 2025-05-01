package com.example.prog7313poe.Database.Budgets

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudgetDAO {

    @Insert
    fun insertBudget(budget: BudgetData): Long

    @Query("SELECT * FROM Budgets")
    fun getAllBudgets(): List<BudgetData>

    @Delete
    fun deleteBudget(budget: BudgetData): Int
}