// FirebaseBudgetDbHelper.kt
package com.example.prog7313poe.Database.Budgets

import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Helper object to insert, fetch, and delete budgets in Firebase Realtime Database.
 *
 * Note:
 *  - We're using `String` for budgetId since Firebase push keys are strings.
 *  - insertBudget() now takes in all necessary budget fields.
 *  - getAllBudgets() reads the entire "budget" node once.
 *  - deleteBudget() simply removes the child by key.
 */
object FirebaseBudgetDbHelper {
    // Change this URL to your Realtime DB URL; we assume Europe-West1 region.
    private val db = FirebaseDatabase
        .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("budget")

    /**
     * Insert a new budget record into Firebase.
     *
     * @param budgetName        The name of the budget.
     * @param budgetCategory    A string category for the budget.
     * @param budgetAmount      The monetary amount of the budget (Double).
     * @param budgetStartTime   Epoch millis for the start date.
     * @param budgetEndTime     Epoch millis for the end date.
     * @param budgetDesc        A short description.
     * @param onComplete        Lambda invoked once Firebase write is done.
     */
    fun insertBudget(
        budgetName: String,
        budgetCategory: String,
        budgetMinAmount: Double,
        budgetMaxAmount: Double,
        budgetStartTime: Long,
        budgetEndTime: Long,
        budgetDesc: String,
        onComplete: () -> Unit = {}
    ) {
        // Generate a new push key
        val key = db.push().key ?: return

        // Construct a BudgetData object matching our data class
        val budget = BudgetData(
            budgetId      = key,
            budgetName    = budgetName,
            budgetCategory = budgetCategory,
            budgetMinAmount = budgetMinAmount,
            budgetMaxAmount  = budgetMaxAmount,
            budgetStartTime = budgetStartTime,
            budgetEndTime   = budgetEndTime,
            budgetDesc     = budgetDesc
        )

        // Write to /budget/{key}
        db.child(key).setValue(budget)
            .addOnCompleteListener { task ->
                // You might want to check task.isSuccessful and handle errors
                onComplete()
            }
    }

    /**
     * Fetch all budgets a single time. If you need "live" updates, use addValueEventListener instead.
     *
     * @param callback Returns a List<BudgetData> in the onDataChange.
     */
    fun getAllBudgets(callback: (List<BudgetData>) -> Unit) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BudgetData>()
                snapshot.children.forEach { child ->
                    child.getValue(BudgetData::class.java)?.let { budget ->
                        // Ensure the budgetId is set to the key (if it wasn’t present in the DB object)
                        budget.budgetId = child.key ?: ""
                        list.add(budget)
                    }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                // If there's an error, we could log it and return an empty list
                callback(emptyList())
            }
        })
    }

    /**
     * Delete a budget entry by its key.
     *
     * @param budgetId   The key of the budget to delete.
     * @param onComplete Lambda invoked after removal (success or failure).
     */
    fun deleteBudget(budgetId: String, onComplete: () -> Unit = {}) {
        db.child(budgetId).removeValue()
            .addOnCompleteListener { task ->
                // Optionally check task.isSuccessful
                onComplete()
            }
    }
}
