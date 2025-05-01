package com.example.prog7313poe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.prog7313poe.Database.users.AppDatabase
import com.example.prog7313poe.Database.users.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullNameEdit = view.findViewById<EditText>(R.id.edit_fullname)
        val emailEdit = view.findViewById<EditText>(R.id.edit_email)
        val btnEdit = view.findViewById<Button>(R.id.btn_edit)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        val db = AppDatabase.getDatabase(requireContext())
        val userEmail = CurrentUser.email

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                db.userDAO().getUserByEmail(userEmail)
            }

            user?.let {
                fullNameEdit.setText("${it.firstName} ${it.lastName}")
                emailEdit.setText(it.email)

                btnEdit.setOnClickListener {
                    fullNameEdit.isEnabled = true
                    emailEdit.isEnabled = true
                    btnSave.visibility = View.VISIBLE
                }

                btnSave.setOnClickListener {
                    val fullName = fullNameEdit.text.toString().trim()
                    val updatedEmail = emailEdit.text.toString().trim()

                    if (fullName.isBlank() || updatedEmail.isBlank()) {
                        Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val (firstName, lastName) = fullName.split(" ", limit = 2).run {
                        if (size < 2) Pair(get(0), "") else Pair(get(0), get(1))
                    }

                    val updatedUser = UserData(
                        email = updatedEmail,
                        firstName = firstName,
                        lastName = lastName,
                        password = user.password
                    )


                    // Update in background
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            db.userDAO().updateUser(updatedUser)
                        }

                        // Update local state and UI on main thread
                        CurrentUser.email = updatedEmail
                        Toast.makeText(requireContext(), "User updated.", Toast.LENGTH_SHORT).show()
                        fullNameEdit.isEnabled = false
                        emailEdit.isEnabled = false
                        btnSave.visibility = View.GONE
                    }
                }
            }
        }
    }
}
