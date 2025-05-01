package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prog7313poe.ui.login.MyLoginActivity

class MainActivity : AppCompatActivity() {

    lateinit var userLogin: Button
    lateinit var userRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        userLogin = findViewById<Button>(R.id.btn_login)
        userRegister = findViewById<Button>(R.id.btn_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userRegister.setOnClickListener { userRegisterPage() }
        userLogin.setOnClickListener { userLoginPage() }
    }

    private fun userRegisterPage() {
        val intent = Intent(this, MyUserRegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun userLoginPage() {
        val intent = Intent(this, MyLoginActivity::class.java)
        startActivity(intent)
    }

}



