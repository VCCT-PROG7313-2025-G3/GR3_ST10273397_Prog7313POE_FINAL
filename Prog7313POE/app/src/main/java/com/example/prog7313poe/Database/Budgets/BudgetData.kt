// BudgetData.kt
package com.example.prog7313poe.Database.Budgets

import com.google.firebase.Timestamp

/**
 * A Firebase‐friendly data class for storing budget entries.
 * All properties have default values so Firebase can deserialize.
 * budgetId is a String because that's how push().key is represented.
 */
data class BudgetData(
    var budgetId: String = "",          // mapped to the Firebase key
    var budgetName: String = "",
    var budgetCategory: String = "",
    var budgetMinAmount: Double = 0.0,
    var budgetMaxAmount: Double = 0.0,
    var budgetStartTime: Long = 0L,
    var budgetEndTime: Long = 0L,
    var budgetDesc: String = ""
)
