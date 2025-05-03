package com.example.prog7313poe.Database.Categories

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(category: CategoryData)

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): LiveData<List<CategoryData>>

    @Delete
    fun deleteCategory(budget: CategoryData): Int
}