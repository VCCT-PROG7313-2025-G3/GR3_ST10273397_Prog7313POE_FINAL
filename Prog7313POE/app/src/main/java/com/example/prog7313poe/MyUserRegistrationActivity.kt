package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

//Activity for user registration
class MyUserRegistrationActivity : AppCompatActivity() {

    // Input fields and buttons
    private lateinit var userFirstName: EditText
    private lateinit var userLastName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var userConfirmPassword: EditText
    private lateinit var btnConfirm: Button
    private lateinit var btnCancel: Button

    // Reference to "/users" node in Realtime DB
    private val usersRef: DatabaseReference by lazy {
        FirebaseDatabase
            .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_user_registration)

        userFirstName = findViewById(R.id.etxt_firstname)
        userLastName = findViewById(R.id.etxt_lastname)
        userEmail = findViewById(R.id.etxt_email)
        userPassword = findViewById(R.id.etxt_password)
        userConfirmPassword = findViewById(R.id.etxt_confirmpassword)
        btnConfirm = findViewById(R.id.btn_confirm)
        btnCancel = findViewById(R.id.btn_cancel)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnConfirm.setOnClickListener { btnConfirmClick() }
        btnCancel.setOnClickListener { btnCancelClick() }
    }

    private fun btnCancelClick() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun btnConfirmClick() {
        val email = userEmail.text.toString().trim()
        val firstName = userFirstName.text.toString().trim()
        val lastName = userLastName.text.toString().trim()
        val password = userPassword.text.toString()
        val confirmPassword = userConfirmPassword.text.toString()

        // 1) Basic validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
            password.isBlank() || confirmPassword.isBlank()
        ) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // 2) Check if email is already registered
        val query = usersRef.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // That email already exists
                    Toast.makeText(
                        this@MyUserRegistrationActivity,
                        "User with that email already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // 3) Create a UserData map and save under "/users/{encodedEmail}"
                    val safeKey = encodeEmailAsKey(email)
                    val newUser = mapOf<String, Any>(
                        "email" to email,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "password" to password          // store raw password
                    )

                    usersRef.child(safeKey).setValue(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MyUserRegistrationActivity,
                                "Registration successful!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 4) Save “logged-in” flag in SharedPreferences if desired
                            val prefs = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                            prefs.edit().putString("CURRENT_USER_EMAIL", email).apply()
                            UserAccountManager.addKnownUser(this@MyUserRegistrationActivity, email)

                            // 5) Navigate to home screen
                            val intent = Intent(this@MyUserRegistrationActivity, MyHomeActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        }
                        .addOnFailureListener { ex ->
                            Toast.makeText(
                                this@MyUserRegistrationActivity,
                                "Error saving user: ${ex.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MyUserRegistrationActivity,
                    "Error checking user: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Turn an email into a safe Firebase key (no '.' or '@')
    private fun encodeEmailAsKey(email: String): String {
        return email.replace(".", "_")
            .replace("@", "_")
    }
}


