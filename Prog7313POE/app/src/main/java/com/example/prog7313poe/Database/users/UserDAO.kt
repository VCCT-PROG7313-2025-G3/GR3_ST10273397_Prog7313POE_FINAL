package com.example.prog7313poe.Database.users

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO {

    @Insert
    fun insertUser(users: UserData): Long

    @Query("SELECT * FROM Users")
    fun getAllUsers(): List<UserData>

    @Delete
    fun deleteUser(users: UserData): Int

    @Query("SELECT * FROM Users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): UserData?
}