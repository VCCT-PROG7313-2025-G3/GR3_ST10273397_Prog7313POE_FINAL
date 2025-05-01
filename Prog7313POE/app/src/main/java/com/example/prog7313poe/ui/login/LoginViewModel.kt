package com.example.prog7313poe.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.prog7313poe.data.LoginRepository
import com.example.prog7313poe.data.Result
import com.example.prog7313poe.data.Result.Success
import com.example.prog7313poe.R
import kotlinx.coroutines.launch

/**
 * ViewModel handling login logic and form state.
 */
class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    /**
     * Attempts login via [loginRepository] on IO dispatcher.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.login(email, password)
            if (result is Success) {
                _loginResult.value = LoginResult(
                    success = LoggedInUserView.fromModel(result.data)
                )
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    /**
     * Validates form input and updates form state.
     */
    fun loginDataChanged(email: String, password: String) {
        when {
            !isEmailValid(email) -> {
                _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
            }
            !isPasswordValid(password) -> {
                _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
            }
            else -> {
                _loginForm.value = LoginFormState(isDataValid = true)
            }
        }
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String): Boolean =
        password.length > 5
}
