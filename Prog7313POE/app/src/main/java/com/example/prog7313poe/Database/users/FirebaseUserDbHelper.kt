package com.example.prog7313poe.Database.users

import com.google.firebase.database.*

object FirebaseUserDbHelper {
    private val db = FirebaseDatabase.getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("users")

    fun insertUser(user: UserData, onComplete: () -> Unit = {}) {
        val key = db.push().key ?: return
        db.child(key).setValue(user).addOnCompleteListener {
            onComplete()
        }
    }

    fun getAllUsers(callback: (List<UserData>) -> Unit) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UserData>()
                snapshot.children.forEach {
                    it.getValue(UserData::class.java)?.let { user -> list.add(user) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllUsers(onComplete: () -> Unit = {}) {
        db.removeValue().addOnCompleteListener {
            onComplete()
        }
    }
}