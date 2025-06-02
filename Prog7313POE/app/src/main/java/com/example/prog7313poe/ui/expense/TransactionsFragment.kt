package com.example.prog7313poe.ui.expense

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.R
import com.example.prog7313poe.databinding.FragmentTransactionsBinding // Firestore Timestamp class
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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

    // ===== Realtime Database References =====
    private val realtimeDb = FirebaseDatabase
        .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
    private val expensesRef: DatabaseReference = realtimeDb.getReference("expenses")
    private val categoriesRef: DatabaseReference = realtimeDb.getReference("categories")

    private val storageInstance = FirebaseStorage.getInstance("gs://thriftsense-b5584.firebasestorage.app")
    private val photosRef: StorageReference = storageInstance
        .getReference("expenses_photos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImagePath = it.toString()
                tempImageView?.post { tempImageView?.setImageURI(uri) }
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success) {
                cameraImageFile?.let { file ->
                    selectedImagePath = Uri.fromFile(file).toString()
                    tempImageView?.post {
                        Glide.with(requireContext())
                            .load(file)
                            .into(tempImageView!!)
                    }
                }
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
                addExpenseToRealtimeDb(newExpense)
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
        val dateInput = dialogView.findViewById<EditText>(R.id.et_date)
        dateInput.inputType = InputType.TYPE_NULL

        // Setup DatePickerDialog on click
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create the DatePickerDialog (no Compose, just “classic” Android)
            val dpListener = DatePickerDialog.OnDateSetListener { _, selYear, selMonth, selDay ->
                // Build a Calendar for the chosen date
                val chosen = Calendar.getInstance().apply {
                    set(selYear, selMonth, selDay)
                }
                // Format it as yyyy-MM-dd in the EditText
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateInput.setText(formatter.format(chosen.time))
            }

            // Show the dialog:
            DatePickerDialog(requireContext(), dpListener, year, month, day)
                .show()
        }

        val descInput = dialogView.findViewById<EditText>(R.id.et_desc)
        val btnSelectPhoto = dialogView.findViewById<Button>(R.id.btn_select_photo)
        val btnTakePhoto = dialogView.findViewById<Button>(R.id.btn_take_photo)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        tempImageView = dialogView.findViewById(R.id.iv_selected_photo)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Load categories from Firebase
        // Load categories from Realtime Database
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<String>()
                snapshot.children.forEach { child ->
                    val categoryName = child.child("categoryName").getValue(String::class.java)
                    categoryName?.let { categories.add(it) }
                }
                categories.add(0, "Select category")
                val spinnerAdapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    categories
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = spinnerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Error loading categories: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })

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
            cameraImageUri?.let { uri -> cameraLauncher.launch(uri) }
        }

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val category = categorySpinner.selectedItem?.toString()?.trim()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val dateString = dateInput.text.toString().trim()
            val description = descInput.text.toString().trim()

            if (name.isNotBlank() || category?.isNotBlank() == true || category == "Select category" || amount > 0 || dateString.isNotBlank()) {
                // Parse the “yyyy-MM-dd” string into milliseconds
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate: Date? = try {
                    formatter.parse(dateString)
                } catch (e: Exception) {
                    null
                }
                val expenseDateMillis: Long = parsedDate?.time
                    ?: System.currentTimeMillis() // fallback if parse fails

                val newKey = expensesRef.push().key ?: UUID.randomUUID().toString()

                if (!selectedImagePath.isNullOrBlank()) {
                    // Convert the stored String path into a Uri.
                    // If selectedImagePath was from `GetContent()`, it'll be "content://..."
                    // If it was from `Uri.fromFile(...)`, it'll be "file://..."
                    val imageUri = Uri.parse(selectedImagePath)

                    // Create a child “<newKey>.jpg” inside “expenses_photos/”
                    val imageRef = photosRef.child("$newKey.jpg")

                    // Kick off the upload
                    imageRef.putFile(imageUri)
                        .addOnSuccessListener { taskSnapshot ->
                            // Upload succeeded! Now grab the download URL:
                            imageRef.downloadUrl
                                .addOnSuccessListener { downloadUri ->
                                    // Build your ExpenseData using the HTTP download URL:
                                    val expense = ExpenseData(
                                        expenseId = newKey,
                                        expenseName = name,
                                        expenseCategory = category.toString(),
                                        expenseAmount = amount,
                                        expenseDate = expenseDateMillis,
                                        expenseDesc = description,
                                        // Store the https://… download URL in the model
                                        expensePhotoPath = downloadUri.toString()
                                    )

                                    onExpenseAdded(expense)
                                    dialog.dismiss()
                                }
                                .addOnFailureListener { exc ->
                                    // If we can’t get a download URL for some reason, still save the rest of the expense:
                                    val fallbackExpense = ExpenseData(
                                        expenseId = newKey,
                                        expenseName = name,
                                        expenseCategory = category.toString(),
                                        expenseAmount = amount,
                                        expenseDate = expenseDateMillis,
                                        expenseDesc = description,
                                        // Store an empty string so your adapter just shows a placeholder image
                                        expensePhotoPath = ""
                                    )
                                    onExpenseAdded(fallbackExpense)
                                    dialog.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Could not get photo URL: ${exc.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener { exception ->
                            // Upload failed entirely – skip the image and save without it:
                            val noPhotoExpense = ExpenseData(
                                expenseId = newKey,
                                expenseName = name,
                                expenseCategory = category.toString(),
                                expenseAmount = amount,
                                expenseDate = expenseDateMillis,
                                expenseDesc = description,
                                expensePhotoPath = ""
                            )
                            onExpenseAdded(noPhotoExpense)
                            dialog.dismiss()
                            Toast.makeText(
                                context,
                                "Photo upload failed: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    // 5) No image selected: just save the expense with an empty photoPath
                    val expense = ExpenseData(
                        expenseId = newKey,
                        expenseName = name,
                        expenseCategory = category.toString(),
                        expenseAmount = amount,
                        expenseDate = expenseDateMillis,
                        expenseDesc = description,
                        expensePhotoPath = ""
                    )
                    onExpenseAdded(expense)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun addExpenseToRealtimeDb(expense: ExpenseData) {
        expensesRef.child(expense.expenseId)
            .setValue(expense)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense added", Toast.LENGTH_SHORT).show()
                loadExpenses()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
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
                    val categoryNode = categoriesRef.push()
                    val categoryMap = mapOf("categoryName" to name)
                    categoryNode.setValue(categoryMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
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
        expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                snapshot.children.forEach { child ->
                    child.getValue(ExpenseData::class.java)?.let { expense ->
                        expenseList.add(expense)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading expenses: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
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
