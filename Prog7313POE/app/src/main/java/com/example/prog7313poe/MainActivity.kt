package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prog7313poe.ui.login.MyLoginActivity
import com.google.firebase.FirebaseApp

//Main activity class for the app
class MainActivity : AppCompatActivity() {

    //Declare buttons for login and register actions
    lateinit var userLogin: Button
    lateinit var userRegister: Button

    //onCreate method, called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        //Enables edge-to-edge display, removing the default margins/padding from system UI elements (like status bar)
        enableEdgeToEdge()

        //Sets he content view to the layout file 'activity_main.xml'
        setContentView(R.layout.activity_main)

        //Find the buttons by their ID from the layout
        userLogin = findViewById<Button>(R.id.btn_login)
        userRegister = findViewById<Button>(R.id.btn_register)

        //Apply window insets to the main layout, adjusting padding based on system bar insets (like the status and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //Adjust the padding based on the system bar insets (top, right, left, bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Set onClick for the buttons, triggering navigation to the respective pages
        userRegister.setOnClickListener { userRegisterPage() }
        userLogin.setOnClickListener { userLoginPage() }
    }

    //Function to navigate to the user registration page
    private fun userRegisterPage() {
        //Create an Intent to start the 'MyUserRegistrationActivity'
        val intent = Intent(this, MyUserRegistrationActivity::class.java)
        //Start the registration activity
        startActivity(intent)
    }

    //Function to navigate to the user login page
    private fun userLoginPage() {
        //Create an Intent to start the 'MyLoginActivity'
        val intent = Intent(this, MyLoginActivity::class.java)
        //Start the login activity
        startActivity(intent)
    }

}



