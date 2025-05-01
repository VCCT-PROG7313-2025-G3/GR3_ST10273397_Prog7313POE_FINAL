package com.example.prog7313poe.Database.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class UserData(
    @PrimaryKey val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)
