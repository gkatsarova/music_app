package com.example.music_app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.music_app.R

@Composable
fun BottomNavBar(
    navController: NavController,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    userId: Int?
) {
    val isDarkTheme = isSystemInDarkTheme()
    val items = listOf(
        BottomNavItem("Create playlist", if (isDarkTheme)
            painterResource(R.drawable.ic_add_gray)
        else painterResource(R.drawable.ic_add_black)),
        BottomNavItem("Liked songs", if (isDarkTheme)
            painterResource(R.drawable.ic_heart_gray)
        else painterResource(R.drawable.ic_heart_black)),
        BottomNavItem("Home", if (isDarkTheme)
            painterResource(R.drawable.ic_vinyl_gray)
        else painterResource(R.drawable.ic_vinyl_black)),
        BottomNavItem("Playing now", if (isDarkTheme)
            painterResource(R.drawable.ic_play_gray)
        else painterResource(R.drawable.ic_play_black)),
        BottomNavItem("Profile", if (isDarkTheme)
            painterResource(R.drawable.ic_user_avatar_gray)
        else painterResource(R.drawable.ic_user_avatar_black))
    )

    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(35.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 12.dp)
            .height(75.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                selectedIndex == index

                IconButton(
                    onClick = {
                        onItemSelected(index)
                        when (index) {
                            2 -> {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                            4 -> {
                                if (userId != null && userId != -1) {
                                    navController.navigate("profile/$userId") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                } else {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val name: String,
    val icon: androidx.compose.ui.graphics.painter.Painter
)