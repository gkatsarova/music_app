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
    fun registerUser(username: String, email: String, password: String, role: UserRole, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            val userDao: UserDao = db.userDao()
            val existingEmail: UserEntity? = userDao.checkEmailExists(email)
            val existingUsername: UserEntity? = userDao.checkUsernameExists(username)

            when {
                existingEmail != null -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email already exists")
                    }
                }

                existingUsername != null -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Username already exists.")
                    }
                }

                username.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Username cannot be empty.")
                    }
                }

                email.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email cannot be empty.")
                    }
                }

                password.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password cannot be empty.")
                    }
                }

                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Invalid email format.")
                    }
                }

                password.length < 8 -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password must be at least 8 characters long.")
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
                    userDao.registerUser(user)
                    withContext(Dispatchers.Main) {
                        onResult(true, "Registration successful")
                    }

                }
            }

        }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDao: UserDao = db.userDao()

            when {
                email.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Email cannot be empty.")
                    }
                }
                password.isEmpty() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Password cannot be empty.")
                    }
                }
                Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Invalid email format.")
                    }
                }
                else -> {
                    val hashedPassword = PasswordUtils.hashPassword(password)
                    val user = userDao.loginUser(email, hashedPassword)

                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            onResult(true, "Successful login")
                        } else {
                            onResult(false, "Wrong email or password!")
                        }
                    }
                }
            }
        }
    }
}