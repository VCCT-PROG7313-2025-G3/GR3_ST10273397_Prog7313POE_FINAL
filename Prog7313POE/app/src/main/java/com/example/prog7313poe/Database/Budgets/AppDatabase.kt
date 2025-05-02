package com.example.prog7313poe.Database.Budgets

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BudgetData::class], version = 2, exportSchema = false) // ✅ bumped version
abstract class AppDatabase : RoomDatabase() {

    abstract fun budgetDAO(): BudgetDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budgets_database"
                ).fallbackToDestructiveMigration() // ✅ avoids crash if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

