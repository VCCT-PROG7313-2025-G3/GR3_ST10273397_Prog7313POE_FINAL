package com.example.prog7313poe.ui.login

/**
 * Data validation state of the login form.
 * @param emailError Resource ID for email validation error, or null if valid.
 * @param passwordError Resource ID for password validation error, or null if valid.
 * @param isDataValid Whether the data is valid overall.
 */
data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
