package com.example.music_app.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.AppDatabase
import com.example.music_app.data.PasswordUtils
import com.example.music_app.data.UserDao
import com.example.music_app.data.UserEntity
import com.example.music_app.data.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(private val db: AppDatabase): ViewModel(){

    var loggedInUserId: Int? = null
        private set
    fun registerUser(username: String, email: String, password: String, role: UserRole, onResult: (Boolean, String, Int?) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao: UserDao = db.userDao()
            val existingEmail: UserEntity? = userDao.checkEmailExists(email)
            val existingUsername: UserEntity? = userDao.checkUsernameExists(username)

            when {
                existingEmail != null -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email already exists", null)
                    }
                }

                existingUsername != null -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Username already exists.", null)
                    }
                }

                username.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Username cannot be empty.", null)
                    }
                }

                email.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email cannot be empty.", null)
                    }
                }

                password.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password cannot be empty.", null)
                    }
                }

                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Invalid email format.", null)
                    }
                }

                password.length < 8 -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password must be at least 8 characters long.", null)
                    }
                }

                else -> {
                    val hashedPassword = PasswordUtils.hashPassword(password)
                    val user = UserEntity(
                        username = username,
                        email = email,
                        password = hashedPassword,
                        role = role
                    )
                    try {
                        val newUserId = userDao.registerUser(user).toInt()
                        withContext(Dispatchers.Main) {
                            onResult(true, "Registration successful", newUserId)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onResult(false, "Registration failed: ${e.message}", null)
                        }
                    }
                }
            }

        }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String, Int?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDao: UserDao = db.userDao()

            when {
                email.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email cannot be empty.", null)
                    }
                }
                password.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password cannot be empty.", null)
                    }
                }
                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Invalid email format.", null)
                    }
                }
                else -> {
                    val hashedPassword = PasswordUtils.hashPassword(password)
                    val user = userDao.loginUser(email, hashedPassword)

                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            onResult(true, "Successful login", user.uid)
                        } else {
                            onResult(false, "Wrong email or password!", null)
                        }
                    }
                }
            }
        }
    }

    fun clearLoggedInUser() {
        loggedInUserId = null
    }
}