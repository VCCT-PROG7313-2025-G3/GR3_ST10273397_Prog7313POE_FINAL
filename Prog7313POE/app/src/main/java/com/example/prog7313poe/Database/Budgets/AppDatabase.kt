package com.example.prog7313poe.Database.Budgets

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database

@Database(entities = [BudgetData::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDAO(): BudgetDAO

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}