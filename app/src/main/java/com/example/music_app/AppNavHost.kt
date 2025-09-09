package com.example.music_app

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.data.user.UserRole
import com.example.music_app.ui.screens.AlbumDetailsScreen
import com.example.music_app.ui.screens.ArtistDetailsScreen
import com.example.music_app.ui.screens.HomeScreen
import com.example.music_app.ui.screens.LoginScreen
import com.example.music_app.ui.screens.RegisterScreen
import com.example.music_app.ui.screens.TrackDetailsScreen
import com.example.music_app.ui.screens.UserProfileScreen
import com.example.music_app.viewmodel.AuthViewModel
import com.example.music_app.viewmodel.TrackViewModel
import com.example.music_app.viewmodel.UserViewModel
import com.example.music_app.viewmodel.factory.TrackViewModelFactory
import com.example.music_app.viewmodel.factory.UserViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = DatabaseClient.getDatabase(context)
    val authViewModel = remember { AuthViewModel(db) }
    val sharedPrefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)

    val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)
    val startDestination = if (currentUserId != -1) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        // Login
        composable("login") {
            LoginScreen(navController) { email, password ->
                authViewModel.loginUser(email, password) { success, message, userId ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success && userId != null) {
                        sharedPrefs.edit().putInt("logged_in_user_id", userId).apply()

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
                authViewModel.registerUser(username, email, password, role) { success, message, userId ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success && userId != null) {
                        sharedPrefs.edit().putInt("logged_in_user_id", userId).apply()

                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            }
        }

        // Home
        composable("home") {
            val repository = MusicRepository(
                trackDao = db.trackDao(),
                artistDao = db.artistDao(),
                albumDao = db.albumDao(),
                context = context
            )

            val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)

            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(
                    application = context.applicationContext as Application,
                    db = db,
                    currentUserId = currentUserId
                )
            )

            HomeScreen(navController, repository, userViewModel)
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

                val repository = MusicRepository(
                    trackDao = db.trackDao(),
                    artistDao = db.artistDao(),
                    albumDao = db.albumDao(),
                    context = context
                )

                UserProfileScreen(
                    navController = navController,
                    repository = repository,
                    userViewModel = userViewModel,
                    onLogout = {
                        sharedPrefs.edit().remove("logged_in_user_id").apply()
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

        //Track Details
        composable("trackDetails/{trackId}") { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
            val repository = MusicRepository(
                trackDao = db.trackDao(),
                artistDao = db.artistDao(),
                albumDao = db.albumDao(),
                context = context
            )

            val trackViewModel: TrackViewModel = viewModel(
                factory = TrackViewModelFactory(repository)
            )

            TrackDetailsScreen(
                trackId = trackId,
                viewModel = trackViewModel,
                navController = navController
            )
        }

        // Album Details
        composable("albumDetails/{albumId}") { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumDetailsScreen(albumId = albumId, navController = navController)
        }

        // Artist Details
        composable("artistDetails/{artistId}") { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistDetailsScreen(artistId = artistId, navController = navController)
        }
    }
}