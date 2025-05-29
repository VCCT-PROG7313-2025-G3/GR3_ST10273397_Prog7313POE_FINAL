package com.example.prog7313poe.loginData.model

import com.example.prog7313poe.Database.users.UserData

/**
 * Data class that captures logged-in user information,
 * mapping from UserData entity.
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
) {
    companion object {
        fun fromUserData(user: UserData): LoggedInUser {
            return LoggedInUser(
                userId = user.email,
                displayName = "${user.firstName} ${user.lastName}"
            )
        }
    }
}
