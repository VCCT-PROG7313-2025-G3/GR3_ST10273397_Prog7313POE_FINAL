package com.example.prog7313poe.ui.expense

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.prog7313poe.Database.Categories.CategoryData
import com.example.prog7313poe.Database.Expenses.AppDatabase
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.R
import com.example.prog7313poe.databinding.FragmentTransactionsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.example.prog7313poe.Database.Expenses.AppDatabase as ExpenseDB

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ExpenseAdapter
    private val expenseList = mutableListOf<ExpenseData>()

    private var cameraImageUri: Uri? = null
    private var cameraImageFile: File? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
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
                tempImageView?.post { tempImageView?.setImageURI(uri) }
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success) {
                cameraImageFile?.let { file ->
                    if (file.exists()) {
                        selectedImagePath = file.absolutePath
                        tempImageView?.post {
                            Glide.with(requireContext())
                                .load(file)
                                .into(tempImageView!!)
                        }
                    } else {
                        Log.w("TransactionsFragment", "Camera wrote no file at ${file.path}")
                    }
                } ?: Log.w("TransactionsFragment", "cameraImageFile was null")
            } else {
                Log.w("TransactionsFragment", "User cancelled full-res camera capture")
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

        binding.btnGoToCategories.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.btnFilter.setOnClickListener {
            showFilterFragment()
        }

        loadExpenses()
    }

    private fun showAddExpenseDialog(context: Context, onExpenseAdded: (ExpenseData) -> Unit) {
        selectedImagePath = null
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_expense, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_name)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spn_category)
        val amountInput = dialogView.findViewById<EditText>(R.id.et_amount)
        val startTimeInput = dialogView.findViewById<EditText>(R.id.et_start_time)
        val endTimeInput = dialogView.findViewById<EditText>(R.id.et_end_time)
        val descInput = dialogView.findViewById<EditText>(R.id.et_desc)
        val btnSelectPhoto = dialogView.findViewById<Button>(R.id.btn_select_photo)
        val btnTakePhoto = dialogView.findViewById<Button>(R.id.btn_take_photo)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

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

        btnTakePhoto.setOnClickListener {
            val context = requireContext()
            val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val newFile = File(imagesDir, "${System.currentTimeMillis()}.jpg")

            cameraImageFile = newFile

            cameraImageUri = FileProvider.getUriForFile(
                context,
                "com.example.prog7313poe.fileprovider",
                newFile
            )

            cameraImageUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
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

    private fun showAddCategoryDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter new category"
            setPadding(32, 24, 32, 24)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Category")
            .setView(input)
            .setPositiveButton("Save") { dialog, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val dao = ExpenseDB.getDatabase(requireContext()).categoryDAO()
                            dao.insertCategory(CategoryData(categoryName = name))
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadExpenses() {
        val dao = AppDatabase.getDatabase(requireContext()).expenseDAO()
        dao.getAllExpenses().observe(viewLifecycleOwner) { expenses ->
            expenseList.clear()
            expenseList.addAll(expenses)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showFilterFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, com.example.prog7313poe.ui.expense.expenseview.ExpenseFilterFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

