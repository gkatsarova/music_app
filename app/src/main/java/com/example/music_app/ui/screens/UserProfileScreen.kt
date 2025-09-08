package com.example.music_app.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.music_app.R
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.viewmodel.UserViewModel

@Composable
fun UserProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val user by userViewModel.user.collectAsState()
    val deleteState by userViewModel.deleteProfileState.collectAsState()
    val logoutState by userViewModel.logoutState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(4) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { userViewModel.updateProfilePicture(it.toString()) }
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                },
                userId = user?.uid
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            user?.let { u ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val isDarkTheme = isSystemInDarkTheme()

                    val avatarRes = if (isDarkTheme) {
                        R.drawable.ic_user_avatar_gray
                    } else {
                        R.drawable.ic_user_avatar_black
                    }

                    AsyncImage(
                        model = u.profilePicture ?: avatarRes,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable { permissionLauncher.launch(permission) },
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = avatarRes),
                        error = painterResource(id = avatarRes)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(35.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = u.username,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )
                            Text(
                                text = u.email,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { userViewModel.logoutUser() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            painter = if (isSystemInDarkTheme()) {
                                                painterResource(id = R.drawable.ic_logout_gray)
                                            } else {
                                                painterResource(id = R.drawable.ic_logout_black)
                                            },
                                            contentDescription = "Logout",
                                            modifier = Modifier.size(50.dp),
                                            tint = Color.Unspecified
                                        )
                                        Text(
                                            "Logout",
                                            fontSize = 16.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            painter = if (isSystemInDarkTheme()) {
                                                painterResource(id = R.drawable.ic_delete_gray)
                                            } else {
                                                painterResource(id = R.drawable.ic_delete_black)
                                            },
                                            contentDescription = "Delete Account",
                                            modifier = Modifier.size(50.dp),
                                            tint = Color.Unspecified
                                        )
                                        Text(
                                            "Delete Account",
                                            fontSize = 16.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading profile...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            userViewModel.deleteUserProfile()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to delete your account? This action cannot be undone.") }
            )
        }

        if (logoutState == true) {
            onLogout()
            userViewModel.resetLogoutState()
        }

        when (deleteState) {
            true -> {
                onLogout()
                userViewModel.resetDeleteProfileState()
            }
            false -> {
                SnackbarHost(
                    hostState = remember { SnackbarHostState() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Snackbar {
                        Text("Failed to delete profile.")
                    }
                }
                userViewModel.resetDeleteProfileState()
            }
            null -> {}
        }
    }
}