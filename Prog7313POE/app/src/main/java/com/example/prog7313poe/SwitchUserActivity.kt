package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7313poe.loginData.LoginDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SwitchUserActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSwitch: Button
    private lateinit var btnCancel: Button

    private val loginDataSource = LoginDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_user)

        etEmail = findViewById(R.id.et_email_switch)
        etPassword = findViewById(R.id.et_password_switch)
        btnSwitch = findViewById(R.id.btn_switch_user)
        btnCancel = findViewById(R.id.btn_cancel_switch)

        btnSwitch.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val result = loginDataSource.login(email, password)
                if (result is com.example.prog7313poe.loginData.Result.Success) {
                    CurrentUser.email = email
                    Toast.makeText(this@SwitchUserActivity, "Switched to $email", Toast.LENGTH_SHORT).show()
                    UserAccountManager.saveUser(this@SwitchUserActivity, email)

                    val intent = Intent(this@SwitchUserActivity, MyHomeActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SwitchUserActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            finish() // Go back to SettingsFragment
        }
    }
}
