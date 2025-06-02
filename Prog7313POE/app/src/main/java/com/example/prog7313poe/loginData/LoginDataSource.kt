package com.example.prog7313poe.loginData

import com.example.prog7313poe.Database.users.UserData
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Class that handles authentication w/ login credentials and retrieves user information from Room.
 */
class LoginDataSource {

    private val usersRef: DatabaseReference by lazy {
        FirebaseDatabase
            .getInstance("https://thriftsense-b5584-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("users")
    }

    /**
     * Attempts to log in with a plain-text password (no hashing).
     * @throws IOException on failure (user not found / wrong password / DB error).
     */
    suspend fun login(email: String, password: String): Result<UserData> {
        return try {
            // 1) Query Realtime DB for any user node where "email" == email
            val userList: List<UserData> = suspendCancellableCoroutine { cont ->
                val query = usersRef.orderByChild("email").equalTo(email)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val foundUsers = mutableListOf<UserData>()
                        for (childSnap in snapshot.children) {
                            childSnap.getValue(UserData::class.java)?.let { u ->
                                foundUsers.add(u)
                            }
                        }
                        cont.resume(foundUsers)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        cont.resumeWithException(IOException("DB error: ${error.message}"))
                    }
                })
            }

            // 2) If no user, return Error
            if (userList.isEmpty()) {
                Result.Error(IOException("User not found"))
            } else {
                // 3) There should only be one matching user
                val user = userList.first()
                return if (user.password == password) {
                    Result.Success(user)
                } else {
                    Result.Error(IOException("Invalid credentials"))
                }
            }
        } catch (e: Exception) {
            Result.Error(IOException("Login error", e))
        }
    }

    fun logout() {
        // Clear your own session state (e.g. SharedPreferences entry)
    }
}
