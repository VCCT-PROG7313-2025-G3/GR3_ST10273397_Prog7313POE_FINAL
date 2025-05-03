package com.example.prog7313poe.Database.Expenses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.prog7313poe.Database.Categories.CategoryDAO
import com.example.prog7313poe.Database.Categories.CategoryData

@Database(entities = [ExpenseData::class, CategoryData::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDAO(): ExpenseDAO
    abstract fun categoryDAO(): CategoryDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE Expenses_new (
                expenseId INTEGER NOT NULL PRIMARY KEY,
                expenseName TEXT NOT NULL,
                expenseCategory TEXT NOT NULL,
                expenseAmount REAL NOT NULL,
                expenseDate INTEGER NOT NULL,
                expenseStartTime TEXT NOT NULL,
                expenseEndTime TEXT NOT NULL,
                expenseDesc TEXT NOT NULL,
                expensePhotoPath TEXT NOT NULL
            )
        """)

                database.execSQL("""
            INSERT INTO Expenses_new (
                expenseId,
                expenseName,
                expenseCategory,
                expenseAmount,
                expenseDate,
                expenseStartTime,
                expenseEndTime,
                expenseDesc,
                expensePhotoPath
            )
            SELECT
                expenseId,
                expenseName,
                expenseCategory,
                expenseAmount,
                expenseDate,
                CAST(expenseStartTime AS TEXT),
                CAST(expenseEndTime AS TEXT),
                expenseDesc,
                expensePhotoPath
            FROM Expenses
        """)

                database.execSQL("DROP TABLE Expenses")
                database.execSQL("ALTER TABLE Expenses_new RENAME TO Expenses")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expenses_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
