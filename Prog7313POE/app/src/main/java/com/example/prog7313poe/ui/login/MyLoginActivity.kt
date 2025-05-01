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
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MyLoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityMyLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel with Context for DAO
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(context = this)
        )[LoginViewModel::class.java]

        // Observe form state for validation errors and button enable state
        loginViewModel.loginFormState.observe(this, Observer { formState ->
            formState ?: return@Observer
            binding.btnLogin?.isEnabled = formState.isDataValid
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
            binding.loading.visibility = View.GONE
            if (result.error != null) {
                showLoginFailed(result.error)
            }
            if (result.success != null) {
                updateUiWithUser(result.success)
                // Navigate to home screen
                startActivity(Intent(this, MyHomeActivity::class.java))
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        // Set up text change listeners for validation
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

        // Handle login action from keyboard
        binding.etxtPassword?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin()
            }
            false
        }

        // Button click handlers
        binding.btnLogin?.setOnClickListener {
            performLogin()
        }
        binding.btnCancel?.isEnabled = true
        binding.btnCancel?.setOnClickListener {
            // Simply go back to previous screen
            finish()
        }
    }

    private fun performLogin() {
        binding.loading.visibility = View.VISIBLE
        loginViewModel.login(
            email = binding.etxtEmail?.text.toString(),
            password = binding.etxtPassword?.text.toString()
        )
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        Toast.makeText(
            applicationContext,
            "$welcome ${model.displayName}",
            Toast.LENGTH_LONG
        ).show()
    }

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
