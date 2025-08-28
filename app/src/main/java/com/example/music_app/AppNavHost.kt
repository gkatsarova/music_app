package com.example.music_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.runtime.remember
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.UserRole
import com.example.music_app.ui.screens.HomeScreen
import com.example.music_app.ui.screens.LoginScreen
import com.example.music_app.ui.screens.RegisterScreen
import com.example.music_app.viewmodel.AuthViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = DatabaseClient.getDatabase(context)
    val authViewModel = remember { AuthViewModel(db) }

    NavHost(navController = navController, startDestination = "login") {
        // Login
        composable("login") {
            LoginScreen(navController) { email, password ->
                authViewModel.loginUser(email, password) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }
        // Register
        composable("register") {
            RegisterScreen(navController) { email, username, password, role: UserRole ->
                authViewModel.registerUser(username, email, password, role) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            }
        }
        // Home
        composable("home") {
            HomeScreen()
        }
    }
}