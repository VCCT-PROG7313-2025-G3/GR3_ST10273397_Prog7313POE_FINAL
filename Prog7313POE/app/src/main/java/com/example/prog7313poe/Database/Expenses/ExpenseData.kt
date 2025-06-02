package com.example.prog7313poe.Database.Expenses

data class ExpenseData(
    val expenseId: String = "",
    val expenseName: String = "",
    val expenseCategory: String = "",
    val expenseAmount: Double = 0.0,
    val expenseDate: Long = 0L,
    val expenseDesc: String = "",
    val expensePhotoPath: String = ""
)
