package com.example.prog7313poe.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.prog7313poe.databinding.ActivityMyLoginBinding
import com.example.prog7313poe.R
import com.example.prog7313poe.MyHomeActivity
import com.example.prog7313poe.UserAccountManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MyLoginActivity : AppCompatActivity() {

    // Declare the ViewModel and binding for the layout
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityMyLoginBinding

    // onCreate is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMyLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel and pass the context to it for database access
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(context = this)
        )[LoginViewModel::class.java]

        // Observe changes in the login form state to handle validation errors
        loginViewModel.loginFormState.observe(this, Observer { formState ->
            formState ?: return@Observer

            // Enable login button if data is valid
            binding.btnLogin?.isEnabled = formState.isDataValid

            // Show error messages for invalid email and password
            if (formState.emailError != null) {
                binding.etxtEmail?.error = getString(formState.emailError)
            }
            if (formState.passwordError != null) {
                binding.etxtPassword?.error = getString(formState.passwordError)
            }
        })

        // Observe login result to handle success or failure
        loginViewModel.loginResult.observe(this, Observer { result ->
            result ?: return@Observer

            // Hide loading spinner after receiving a result
            binding.loading.visibility = View.GONE

            // If login failed, show error
            if (result.error != null) {
                showLoginFailed(result.error)
            }

            // If login was successful, proceed to the home screen
            if (result.success != null) {
                updateUiWithUser(result.success)

                val currentEmail = binding.etxtEmail?.text.toString()
                UserAccountManager.addKnownUser(this, currentEmail) //Save to shared prefs

                val intent = Intent(this, MyHomeActivity::class.java)
                intent.putExtra("email", currentEmail)
                startActivity(intent)
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        // Set up text change listeners to validate form inputs as user types
        binding.etxtEmail?.afterTextChanged { text ->
            loginViewModel.loginDataChanged(
                email = text,
                password = binding.etxtPassword?.text.toString()
            )
        }
        binding.etxtPassword?.afterTextChanged { text ->
            loginViewModel.loginDataChanged(
                email = binding.etxtEmail?.text.toString(),
                password = text
            )
        }

        // Handle login action when the user presses "Done" on the keyboard
        binding.etxtPassword?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin() // Attempt to log in
            }
            false
        }

        // Set up button click listeners
        binding.btnLogin?.setOnClickListener {
            performLogin() // Trigger login when login button is clicked
        }

        // Enable and set the cancel button to go back to the previous screen
        binding.btnCancel?.isEnabled = true
        binding.btnCancel?.setOnClickListener {
            // Go back to the previous activity (MainActivity)
            finish()
        }
    }

    // Perform login by calling the ViewModel's login method
    private fun performLogin() {
        binding.loading.visibility = View.VISIBLE // Show loading indicator
        loginViewModel.login(
            email = binding.etxtEmail?.text.toString(),
            password = binding.etxtPassword?.text.toString()
        )
    }

    // Update the UI with the user's name upon successful login
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        Toast.makeText(
            applicationContext,
            "$welcome ${model.displayName}", // Display welcome message with user's display name
            Toast.LENGTH_LONG
        ).show()
    }

    // Show login failure error message
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension for simplifying afterTextChanged listener
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
