package com.example.prog7313poe.Database.Expenses

import com.google.firebase.database.*

object FirebaseExpenseDbHelper {
    private val db = FirebaseDatabase.getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("expenses")

    fun insertExpenses(expense: ExpenseData, onComplete: () -> Unit = {}) {
        val key = db.push().key ?: return
        db.child(key).setValue(expense).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllExpenses(callback: (List<ExpenseData>) -> Unit) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ExpenseData>()
                snapshot.children.forEach {
                    it.getValue(ExpenseData::class.java)?.let { expense -> list.add(expense) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllExpenses(onComplete: () -> Unit = {}) {
        db.removeValue().addOnCompleteListener {
            onComplete()
        }
    }
}