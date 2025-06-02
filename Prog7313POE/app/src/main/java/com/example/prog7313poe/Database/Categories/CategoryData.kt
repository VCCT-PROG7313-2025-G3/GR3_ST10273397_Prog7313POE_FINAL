package com.example.prog7313poe.Database.Categories

data class CategoryData(
    var categoryId: String = " ",
    var categoryName: String = ""
) {
    override fun toString(): String {
        return categoryName
    }
}
