package com.example.music_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.ui.components.PlayingTrack
import com.example.music_app.viewmodel.HomeViewModel
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.PlayingTrackViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory
import com.example.music_app.viewmodel.UserViewModel
import com.example.music_app.viewmodel.factory.HomeViewModelFactory

@Composable
fun HomeScreen(navController: NavController,
               repository: MusicRepository,
               userViewModel: UserViewModel,
               playingTrackViewModel: PlayingTrackViewModel = viewModel()
) {
    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(2) }

    val searchResult by musicViewModel.searchResult.collectAsState()
    val userState = userViewModel.user.collectAsState()
    val user = userState.value

    val context = LocalContext.current
    val loading by musicViewModel.loading.collectAsState()

    val showController by playingTrackViewModel.showController.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.saveSampleData()
        musicViewModel.loadAllData{ message ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                if (showController) {
                    PlayingTrack(
                        viewModel = playingTrackViewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                BottomNavBar(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        selectedIndex = index
                    },
                    userId = user?.uid
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    musicViewModel.search(newQuery)
                },
                onClose = {
                    query = ""
                }
            )

            if (query.isNotEmpty()) {
                MusicList(
                    loading = loading,
                    searchResult = searchResult,
                    navController = navController
                )
            }
        }
    }
}