package com.example.prog7313poe.Database.Expenses

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time
import kotlin.time.TimedValue

@Entity(tableName = "Expenses")
data class ExpenseData(
    @PrimaryKey (autoGenerate = true) val expenseId: Int,
    val expenseName: String,
    val expenseCategory: String,
    val expenseDate: Long,
    val expenseStartTime: Long,
    val expenseEndTime: Long,
    val expenseDesc: String,
    val expensePhotoPath: String,
)
