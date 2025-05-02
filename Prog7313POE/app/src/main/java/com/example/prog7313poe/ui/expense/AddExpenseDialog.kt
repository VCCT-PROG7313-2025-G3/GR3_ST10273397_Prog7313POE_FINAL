package com.example.prog7313poe.ui.expense

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.DialogAddExpenseBinding

class AddExpenseDialog(
    context: Context,
    private val onSave: (ExpenseData) -> Unit
) : AlertDialog(context) {

            private lateinit var binding: DialogAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddExpenseBinding.inflate(LayoutInflater.from(context))
        setView(binding.root) // ✅ This line is essential!

        binding.btnSave.setOnClickListener {
            val expense = ExpenseData(
                expenseId = 0,
                     expenseName = binding.etName.text.toString(),
                expenseCategory = binding.etCategory.text.toString(),
                    expenseDate = binding.etDate.text.toString().toLongOrNull() ?: System.currentTimeMillis(),
                expenseStartTime = binding.etStartTime.text.toString().toLongOrNull() ?: 0,
                        expenseEndTime = binding.etEndTime.text.toString().toLongOrNull() ?: 0,
                    expenseDesc = binding.etDesc.text.toString(),
                expensePhotoPath = binding.etPhotoPath.text.toString()
            )
                    onSave(expense)
            dismiss()
        }
    }
}
