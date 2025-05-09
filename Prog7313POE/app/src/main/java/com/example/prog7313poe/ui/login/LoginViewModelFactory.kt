package com.example.prog7313poe.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prog7313poe.Database.users.AppDatabase
import com.example.prog7313poe.data.LoginDataSource
import com.example.prog7313poe.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel with required dependencies.
 */
class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Obtain UserDAO from Room database
            val userDAO = AppDatabase.getDatabase(context).userDAO()
            // Create DataSource and Repository
            val dataSource = LoginDataSource(userDAO)
            val repository = LoginRepository(dataSource)
            return LoginViewModel(loginRepository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
