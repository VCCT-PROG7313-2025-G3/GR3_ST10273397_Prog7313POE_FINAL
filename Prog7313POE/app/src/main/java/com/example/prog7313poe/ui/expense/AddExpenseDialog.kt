package com.example.prog7313poe.ui.expense

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.databinding.DialogAddExpenseBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val realtimeDb = FirebaseDatabase
    .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
private val expensesRef: DatabaseReference = realtimeDb.getReference("expenses")

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
        val newKey = expensesRef.push().key ?: UUID.randomUUID().toString()

        // Parse date string or fallback to now
        val dateString = binding.etDate.text.toString().trim()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate: Date? = try {
            dateFormatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
        val expenseDateMillis: Long = parsedDate?.time ?: System.currentTimeMillis()

        // Parse amount safely
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0

        // NOTE: You may want to validate amount > 0 before saving

        // Placeholder for photo path — this currently just gets the ImageView’s string representation
        val photoPath = binding.ivSelectedPhoto.tag?.toString() ?: ""

        val expense = ExpenseData(
            expenseId = newKey,
            expenseName = binding.etName.text.toString(),
            expenseCategory = binding.spnCategory.selectedItem.toString(),
            expenseAmount = amount,
            expenseDate = expenseDateMillis,   // <-- long timestamp!
            expenseDesc = binding.etDesc.text.toString(),
            expensePhotoPath = photoPath
        )

        onSave(expense)
        dialog.dismiss()
    }

    dialog.show()
}
