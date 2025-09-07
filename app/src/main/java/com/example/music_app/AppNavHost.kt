package com.example.music_app

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.UserRole
import com.example.music_app.ui.screens.HomeScreen
import com.example.music_app.ui.screens.LoginScreen
import com.example.music_app.ui.screens.RegisterScreen
import com.example.music_app.ui.screens.UserProfileScreen
import com.example.music_app.viewmodel.AuthViewModel
import com.example.music_app.viewmodel.UserViewModel
import com.example.music_app.viewmodel.UserViewModelFactory

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
                authViewModel.loginUser(email, password) { success, message, userId ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        navController.navigate("home"){
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }
        // Register
        composable("register") {
            RegisterScreen(navController) { email, username, password, role: UserRole ->
                authViewModel.registerUser(username, email, password, role) { success, message, userId ->
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

        // Profile
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
            if (userId != null) {
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(
                        application = context.applicationContext as Application,
                        db = db,
                        currentUserId = userId
                    )
                )
                UserProfileScreen(
                    userViewModel = userViewModel,
                    onLogout = {
                        authViewModel.clearLoggedInUser()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
            } else {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            }
        }
    }
}