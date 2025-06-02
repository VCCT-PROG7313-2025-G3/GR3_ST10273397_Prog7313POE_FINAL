package com.example.prog7313poe.Database.Categories

import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.google.firebase.database.*

object FirebaseCategoryDbHelper {
    private val db = FirebaseDatabase.getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("category")

    fun insertCategory(categoryName: String, onComplete: () -> Unit = {}) {
        val key = db.push().key ?: return
        val category = CategoryData(categoryId = key, categoryName = categoryName)
        db.child(key).setValue(category).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllCategories(callback: (List<CategoryData>) -> Unit) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryData>()
                snapshot.children.forEach { child ->
                    child.getValue(CategoryData::class.java)?.let { category ->
                        list.add(category.apply { categoryId = child.key ?: "" })
                    }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteCategory(categoryId: String, onComplete: () -> Unit = {}) {
        db.child(categoryId).removeValue().addOnCompleteListener {
            onComplete()
        }
    }
}