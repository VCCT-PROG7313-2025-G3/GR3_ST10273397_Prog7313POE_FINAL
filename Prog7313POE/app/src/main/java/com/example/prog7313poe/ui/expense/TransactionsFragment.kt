package com.example.prog7313poe.ui.expense

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313poe.Database.Expenses.AppDatabase
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.R
import com.example.prog7313poe.databinding.FragmentTransactionsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpenseAdapter
    private val expenseList = mutableListOf<ExpenseData>()

    private var selectedImagePath: String? = null
    private lateinit var getImage: ActivityResultLauncher<String>
    private var tempImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val context = requireContext()
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, "${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                selectedImagePath = file.absolutePath
                tempImageView?.setImageURI(uri) // Need a way to update imageView
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ExpenseAdapter(expenseList)
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        binding.btnAddExpense.setOnClickListener {
            showAddExpenseDialog(requireContext()) { newExpense ->
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).expenseDAO().insertExpense(newExpense)
                }
            }
        }

        loadExpenses()
    }

    private fun showAddExpenseDialog(context: Context, onExpenseAdded: (ExpenseData) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spn_category)
        val amountInput = dialogView.findViewById<EditText>(R.id.et_amount)
        val startTimeInput = dialogView.findViewById<EditText>(R.id.et_start_time)
        val endTimeInput = dialogView.findViewById<EditText>(R.id.et_end_time)
        val descInput = dialogView.findViewById<EditText>(R.id.et_desc)
        val btnSelectPhoto = dialogView.findViewById<Button>(R.id.btn_select_photo)
        val imageView = dialogView.findViewById<ImageView>(R.id.iv_selected_photo)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        var selectedImagePath: String? = null

        tempImageView = dialogView.findViewById(R.id.iv_selected_photo)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Load categories from RoomDB
        val categoryDao = AppDatabase.getDatabase(context).categoryDAO()
        categoryDao.getAllCategories().observe(viewLifecycleOwner) { categories ->
            val spinnerAdapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                categories
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = spinnerAdapter
        }

        btnSelectPhoto.setOnClickListener {
            getImage.launch("image/*")
        }

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val category = categorySpinner.selectedItem?.toString()?.trim()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val start = startTimeInput.text.toString().trim()
            val end = endTimeInput.text.toString().trim()
            val description = descInput.text.toString().trim()
            val photo = selectedImagePath ?: ""

            if (name.isNotBlank() && category?.isNotBlank() == true && amount > 0) {
                val expense = ExpenseData(
                    expenseId = 0,
                    expenseName = name,
                    expenseCategory = category.toString(),
                    expenseAmount = amount,
                    expenseDate = System.currentTimeMillis(),
                    expenseStartTime = start,
                    expenseEndTime = end,
                    expenseDesc = description,
                    expensePhotoPath = photo
                )

                onExpenseAdded(expense)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loadExpenses() {
        val dao = AppDatabase.getDatabase(requireContext()).expenseDAO()
        dao.getAllExpenses().observe(viewLifecycleOwner) { expenses ->
            expenseList.clear()
            expenseList.addAll(expenses)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}