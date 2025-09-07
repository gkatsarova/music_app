package com.example.music_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.MusicViewModelFactory

@Composable
fun HomeScreen(repository: MusicRepository) {
    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        musicViewModel.loadAllData {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            loading = false
        }
    }

    var query by remember { mutableStateOf("") }
    val searchResult by musicViewModel.searchResult.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                musicViewModel.search(newQuery)
            }
        )

        if (loading) {
            Text("Loading data...", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(searchResult.tracks) { track ->
                    Text(track.title)
                }
                items(searchResult.albums) { album ->
                    Text(album.title)
                }
                items(searchResult.artists) { artist ->
                    Text(artist.name)
                }
            }
        }
    }
}