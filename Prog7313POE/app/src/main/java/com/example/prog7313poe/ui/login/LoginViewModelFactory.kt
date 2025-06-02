package com.example.prog7313poe.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prog7313poe.loginData.LoginDataSource
import com.example.prog7313poe.loginData.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel with required dependencies.
 */
class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // FirebaseAuth-based LoginDataSource takes no DAO
            val dataSource = LoginDataSource()
            val repository = LoginRepository(dataSource)
            return LoginViewModel(loginRepository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
