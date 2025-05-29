package com.example.prog7313poe.loginData

import com.example.prog7313poe.loginData.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository handling authentication requests and caching user credentials.
 */
class LoginRepository(
    private val dataSource: LoginDataSource
) {

    // In-memory cache of the logged-in user
    private var user: LoggedInUser? = null

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // Initialize cache (could load persisted credentials here)
        user = null
    }

    /**
     * Attempts login on IO dispatcher, checks credentials via [LoginDataSource],
     * caches user on success.
     */
    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return withContext(Dispatchers.IO) {
            val result = dataSource.login(username, password)
            if (result is Result.Success) {
                setLoggedInUser(result.data)
            }
            result
        }
    }

    /**
     * Clears cached user and notifies data source.
     */
    fun logout() {
        user = null
        dataSource.logout()
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        user = loggedInUser
        // TODO: persist or encrypt credentials in secure storage
    }
}
