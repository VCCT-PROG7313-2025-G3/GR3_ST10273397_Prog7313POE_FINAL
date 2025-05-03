package com.example.prog7313poe.Database.Expenses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expenses")
data class ExpenseData(
    @PrimaryKey(autoGenerate = true) val expenseId: Int,
    val expenseName: String,
    val expenseCategory: String,
    val expenseAmount: Double,
    val expenseDate: Long,
    val expenseStartTime: String,
    val expenseEndTime: String,
    val expenseDesc: String,
    val expensePhotoPath: String
)
