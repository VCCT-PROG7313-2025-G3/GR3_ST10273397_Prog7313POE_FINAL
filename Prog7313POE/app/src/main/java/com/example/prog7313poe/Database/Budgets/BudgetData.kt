package com.example.prog7313poe.Database.Budgets

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "Budgets")
data class BudgetData(
    @PrimaryKey (autoGenerate = true) val budgetId: Int,
    val budgetName: String,
    val budgetCategory: String,
    val budgetStartTime: Long,
    val budgetEndTime: Long,
    val budgetDesc: String,
)
