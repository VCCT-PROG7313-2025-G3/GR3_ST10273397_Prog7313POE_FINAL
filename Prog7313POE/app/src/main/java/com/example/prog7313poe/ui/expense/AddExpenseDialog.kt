package com.example.prog7313poe.ui.expense

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.DialogAddExpenseBinding

fun showAddExpenseDialog(
    context: Context,
    categories: List<String>,
    onSave: (ExpenseData) -> Unit
) {
    val binding = DialogAddExpenseBinding.inflate(LayoutInflater.from(context))

    val dialog = AlertDialog.Builder(context, R.style.Theme_Material_Dialog_Alert)
        .setView(binding.root)
        .create()

    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, categories)
    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    binding.spnCategory.adapter = adapter


    binding.btnSave.setOnClickListener {
        val expense = ExpenseData(
            expenseId = 0,
            expenseName = binding.etName.text.toString(),
            expenseCategory = binding.spnCategory.selectedItem.toString(),
            expenseAmount = binding.etAmount.text.toString().toDouble(),
            expenseDate = binding.etDate.text.toString().toLongOrNull()
                ?: System.currentTimeMillis(),
            expenseStartTime = (binding.etStartTime.text.toString().toLongOrNull() ?: 0).toString(),
            expenseEndTime = (binding.etEndTime.text.toString().toLongOrNull() ?: 0).toString(),
            expenseDesc = binding.etDesc.text.toString(),
            expensePhotoPath = binding.ivSelectedPhoto.toString()
        )
        onSave(expense)
        dialog.dismiss()
    }

    dialog.show()
}
