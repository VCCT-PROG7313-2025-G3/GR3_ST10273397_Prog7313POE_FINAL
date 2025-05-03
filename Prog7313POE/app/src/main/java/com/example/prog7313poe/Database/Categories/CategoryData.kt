package com.example.prog7313poe.Database.Categories

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class CategoryData(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val categoryName: String
) {
    override fun toString(): String {
        return categoryName
    }
}
