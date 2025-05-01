package com.example.prog7313poe.ui.login

import com.example.prog7313poe.ui.login.LoggedInUserView

/**
 * Authentication result: success (user details) or error (resource ID).
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
