package com.example.prog7313poe.Database.Categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryDao = AppDatabase.getDatabase(application).CategoryDAO()
    val categories: LiveData<List<CategoryData>> = categoryDao.getAllCategories()
}
