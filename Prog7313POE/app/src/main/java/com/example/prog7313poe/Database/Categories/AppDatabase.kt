package com.example.prog7313poe.Database.Categories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CategoryData::class], version = 3, exportSchema = false) // ✅ bumped version
abstract class AppDatabase : RoomDatabase() {

    abstract fun CategoryDAO(): CategoryDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "category_database"
                ).fallbackToDestructiveMigration() // ✅ avoids crash if schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

