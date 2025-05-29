package com.example.prog7313poe.loginData

import com.example.prog7313poe.loginData.model.LoggedInUser
import com.example.prog7313poe.Database.users.UserDAO
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information from Room.
 */
class LoginDataSource(
    private val userDAO: UserDAO
) {

    /**
     * Validates credentials against stored users.
     * @throws IOException on auth failure or errors.
     */
    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val user = userDAO.getUserByEmail(username)
            when {
                user == null -> {
                    Result.Error(IOException("User not found"))
                }
                user.password != password -> {
                    Result.Error(IOException("Invalid credentials"))
                }
                else -> {
                    val loggedIn = LoggedInUser.fromUserData(user)
                    Result.Success(loggedIn)
                }
            }
        } catch (e: Exception) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication, clear session if needed
    }
}
