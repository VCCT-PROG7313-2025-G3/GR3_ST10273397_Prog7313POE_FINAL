package com.example.prog7313poe.ui.expense

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.DialogAddExpenseBinding

fun showAddExpenseDialog(
    context: Context,
    onSave: (ExpenseData) -> Unit
) {
    val binding = DialogAddExpenseBinding.inflate(LayoutInflater.from(context))

    val dialog = AlertDialog.Builder(context, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
        .setView(binding.root)
        .create()

    binding.btnSave.setOnClickListener {
        val expense = ExpenseData(
            expenseId = 0,
            expenseName = binding.etName.text.toString(),
            expenseCategory = binding.etCategory.text.toString(),
            expenseAmount = binding.etAmount.text.toString().toDouble(),
            expenseDate = binding.etDate.text.toString().toLongOrNull()
                ?: System.currentTimeMillis(),
            expenseStartTime = (binding.etStartTime.text.toString().toLongOrNull() ?: 0).toString(),
            expenseEndTime = (binding.etEndTime.text.toString().toLongOrNull() ?: 0).toString(),
            expenseDesc = binding.etDesc.text.toString(),
            expensePhotoPath = binding.etPhotoPath.text.toString(),
        )
        onSave(expense)
        dialog.dismiss()
    }

    dialog.show()
}
