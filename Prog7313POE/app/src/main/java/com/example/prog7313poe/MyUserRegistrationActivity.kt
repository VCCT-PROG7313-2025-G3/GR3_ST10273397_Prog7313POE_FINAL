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

//Activity for user registration
class MyUserRegistrationActivity : AppCompatActivity() {

    //Declare inpuit fields and buttons for the registration form
    lateinit var userFirstName: EditText
    lateinit var userLastName: EditText
    lateinit var userEmail: EditText
    lateinit var userPassword: EditText
    lateinit var userConfirmPassword: EditText
    lateinit var btnConfirm: Button
    lateinit var btnCancel: Button

    //Declare userDAO to interact with the database
    lateinit var userDAO: UserDAO

    //onCreate method for the activity lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display for a full-screen experience
        enableEdgeToEdge()

        // Set the content view to the registration layout
        setContentView(R.layout.activity_my_user_registration)

        // Find views by their IDs from the layout
        userFirstName = findViewById<EditText>(R.id.etxt_firstname)
        userLastName = findViewById<EditText>(R.id.etxt_lastname)
        userEmail = findViewById<EditText>(R.id.etxt_email)
        userPassword = findViewById<EditText>(R.id.etxt_password)
        userConfirmPassword = findViewById<EditText>(R.id.etxt_confirmpassword)
        btnConfirm = findViewById<Button>(R.id.btn_confirm)
        btnCancel = findViewById<Button>(R.id.btn_cancel)

        // Initialize the database and DAO to interact with user data
        val db = AppDatabase.getDatabase(this)
        userDAO = db.userDAO()

        // Apply window insets to adjust layout for system bars (e.g., status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set click listeners for the confirm and cancel buttons
        btnConfirm.setOnClickListener { btnConfirmClick() }
        btnCancel.setOnClickListener { btnCancelClick() }
    }

    // Action for the Cancel button to return to the MainActivity
    private fun btnCancelClick() {
        val intent = Intent(this, MainActivity()::class.java)
        startActivity(intent)
    }

    // Action for the Confirm button to handle user registration logic
    private fun btnConfirmClick() {
        // Launch the registration logic inside a coroutine to handle background tasks
        lifecycleScope.launch {
            // Get the email entered by the user
            val emailText = userEmail.text.toString()
            // Check if a user with the same email already exists in the database
            val existingUser = withContext(Dispatchers.IO) {
                userDAO.getUserByEmail(emailText)
            }

            // If user already exists, show a toast message and stop further execution
            if (existingUser != null) {
                Toast.makeText(this@MyUserRegistrationActivity,
                    "This user already exists!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // If passwords don't match, show a toast message and stop further execution
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

            // Insert the new user into the database in the background (IO thread)
            withContext(Dispatchers.IO) {
                userDAO.insertUser(newUser)
            }

            // Show a success message once registration is complete
            Toast.makeText(this@MyUserRegistrationActivity,
                "You have successfully registered!", Toast.LENGTH_SHORT).show()

            // Navigate to the home activity after successful registration
            val intent = Intent(this@MyUserRegistrationActivity, MyHomeActivity::class.java)
            intent.putExtra("email", emailText)  // Pass email to the next activity
            startActivity(intent)

        }
    }

    // Function to check if the entered passwords match
    private fun PasswordMatch(): Boolean {
        // Compare password and confirm password fields
        return userPassword.text.toString() == userConfirmPassword.text.toString()
    }
}


