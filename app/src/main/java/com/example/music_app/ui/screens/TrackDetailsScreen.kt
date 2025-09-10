package com.example.music_app.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.music_app.R
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.PlayingTrack
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.PlayingTrackViewModel
import com.example.music_app.viewmodel.TrackViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun TrackDetailsScreen(
    trackId: String,
    viewModel: TrackViewModel,
    navController: NavController,
    playingTrackViewModel: PlayingTrackViewModel = viewModel()
) {
    val trackUi by viewModel.track.collectAsState()
    val context = LocalContext.current
    val db = DatabaseClient.getDatabase(context)
    val repository = MusicRepository(
        trackDao = db.trackDao(),
        artistDao = db.artistDao(),
        albumDao = db.albumDao(),
        recentlyPlayedAlbumDao = db.recentlyPlayedAlbumDao(),
        recentlyPlayedArtistDao = db.recentlyPlayedArtistDao(),
        context = context
    )

    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val searchResult by musicViewModel.searchResult.collectAsState()
    val loading by musicViewModel.loading.collectAsState()

    val currentTrack by playingTrackViewModel.currentTrack.collectAsState()
    val isPlaying by playingTrackViewModel.isPlaying.collectAsState()
    val showController by playingTrackViewModel.showController.collectAsState()

    val isDarkTheme = isSystemInDarkTheme()

    val sharedPrefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)

    val scope = rememberCoroutineScope()

    LaunchedEffect(trackId) {
        viewModel.loadTrack(trackId)
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
                        repository = repository,
                        userId = currentUserId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                BottomNavBar(
                    navController = navController,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        selectedIndex = index
                    },
                    userId = currentUserId
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
            } else {
                trackUi?.let { ui ->
                    val t = ui.track
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = t.artworkUrl,
                            contentDescription = t.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            placeholder = if (isDarkTheme) painterResource(id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black),
                            error = if (isDarkTheme) painterResource(id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black),
                            fallback = if (isDarkTheme) painterResource(id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = t.title,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ui.artistName ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable{
                                    t.artistId.let{
                                        if(it.isNotEmpty()){
                                            navController.navigate("artistDetails/$it")
                                        }
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ui.albumName ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable{
                                    t.albumId?.let{
                                        if(it.isNotEmpty()){
                                            navController.navigate("albumDetails/$it")
                                        }
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { println("Add button") },
                                modifier = Modifier.size(60.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = if (isDarkTheme) R.drawable.ic_add_gray else R.drawable.ic_add_black),
                                    contentDescription = "Add",
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(40.dp))

                            if (!t.streamUrl.isNullOrEmpty()) {
                                IconButton(
                                    onClick = {
                                        if (isPlaying && currentTrack?.id == t.id) {
                                            playingTrackViewModel.pause()
                                        } else {
                                            playingTrackViewModel.playTrack(t, ui.artistName)
                                        }
                                        if (currentUserId != -1) {
                                            scope.launch {
                                                repository.addRecentlyPlayedAlbum(t.albumId?:"", currentUserId)
                                                repository.addRecentlyPlayedArtist(t.artistId, currentUserId)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = if (isDarkTheme) R.drawable.ic_play_gray else R.drawable.ic_play_black
                                        ),
                                        contentDescription = if (isPlaying && currentTrack?.id == t.id) "Pause" else "Play",
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            } else {
                                Text("No stream available", color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(modifier = Modifier.width(40.dp))

                            IconButton(
                                onClick = { println("Like button") },
                                modifier = Modifier.size(60.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = if(isDarkTheme) R.drawable.ic_heart_gray else R.drawable.ic_heart_black),
                                    contentDescription = "Like",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}