package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prog7313poe.Database.users.AppDatabase
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.prog7313poe.Database.users.UserDAO
import com.example.prog7313poe.Database.users.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyUserRegistrationActivity : AppCompatActivity() {

    lateinit var userFirstName: EditText
    lateinit var userLastName: EditText
    lateinit var userEmail: EditText
    lateinit var userPassword: EditText
    lateinit var userConfirmPassword: EditText
    lateinit var btnConfirm: Button
    lateinit var btnCancel: Button

    lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_user_registration)

        userFirstName = findViewById<EditText>(R.id.etxt_firstname)
        userLastName = findViewById<EditText>(R.id.etxt_lastname)
        userEmail = findViewById<EditText>(R.id.etxt_email)
        userPassword = findViewById<EditText>(R.id.etxt_password)
        userConfirmPassword = findViewById<EditText>(R.id.etxt_confirmpassword)
        btnConfirm = findViewById<Button>(R.id.btn_confirm)
        btnCancel = findViewById<Button>(R.id.btn_cancel)

        val db = AppDatabase.getDatabase(this)
        userDAO = db.userDAO()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnConfirm.setOnClickListener { btnConfirmClick() }
        btnCancel.setOnClickListener { btnCancelClick() }
    }

    private fun btnCancelClick() {
        val intent = Intent(this, MainActivity()::class.java)
        startActivity(intent)
    }

    private fun btnConfirmClick() {
        lifecycleScope.launch {
            val emailText = userEmail.text.toString()
            val existingUser = withContext(Dispatchers.IO) {
                userDAO.getUserByEmail(emailText)
            }
            if (existingUser != null) {
                Toast.makeText(this@MyUserRegistrationActivity,
                    "This user already exists!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (!PasswordMatch()) {
                Toast.makeText(this@MyUserRegistrationActivity,
                    "Passwords don't match!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Only reaches here if both checks pass
            val newUser = UserData(
                email     = emailText,
                firstName = userFirstName.text.toString(),
                lastName  = userLastName.text.toString(),
                password  = userPassword.text.toString()
            )
            withContext(Dispatchers.IO) {
                userDAO.insertUser(newUser)
            }
            Toast.makeText(this@MyUserRegistrationActivity,
                "You have successfully registered!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MyUserRegistrationActivity, MyHomeActivity::class.java))
        }
    }


    private fun PasswordMatch(): Boolean {
        return userPassword.text.toString() == userConfirmPassword.text.toString()
    }
}


