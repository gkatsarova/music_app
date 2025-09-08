package com.example.music_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.MusicViewModelFactory

@Composable
fun HomeScreen(repository: MusicRepository) {
    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    val searchResult by musicViewModel.searchResult.collectAsState()

    LaunchedEffect(Unit) {
        musicViewModel.loadAllData {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                searchResult = searchResult
            )
        }
    }
}
