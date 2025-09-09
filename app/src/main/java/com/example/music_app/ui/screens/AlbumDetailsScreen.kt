package com.example.music_app.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.repository.MusicRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.music.entity.TrackEntity
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music_app.R
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import android.content.Context
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun AlbumDetailsScreen(
    albumId: String,
    navController: NavController
){
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val db = DatabaseClient.getDatabase(context)
    val repository = MusicRepository(
        trackDao = db.trackDao(),
        artistDao = db.artistDao(),
        albumDao = db.albumDao()
    )

    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(2) }
    val searchResult by musicViewModel.searchResult.collectAsState()
    val loading by musicViewModel.loading.collectAsState()

    var album by remember { mutableStateOf<AlbumEntity?>(null) }
    var albumTracks by remember { mutableStateOf<List<TrackEntity>>(emptyList()) }
    var artistName by remember { mutableStateOf("Unknown") }

    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val sharedPrefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)

    LaunchedEffect(albumId) {
        album = repository.albumDao.getAlbumById(albumId)
        album?.let { alb ->
            albumTracks = repository.trackDao.getAll().filter { it.albumId == alb.id }
            val artist = repository.artistDao.getArtistById(alb.artistId)
            artistName = artist?.name ?: "Unknown"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
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
                userId = currentUserId
            )
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
                if (album == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(album!!.artworkUrl)
                                .crossfade(true)
                                .placeholder(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                .error(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                .build(),
                            contentDescription = album!!.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = album!!.title,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(albumTracks) { track ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("trackDetails/${track.id}")
                                        }
                                        .clip(RoundedCornerShape(35.dp))
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(horizontal = 20.dp, vertical = 20.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(track.artworkUrl)
                                            .crossfade(true)
                                            .placeholder(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                            .error(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                            .build(),
                                        contentDescription = track.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Text(track.title)

                                    Spacer(modifier = Modifier.weight(1f))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = { println("Add track") },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = if (isDarkTheme) R.drawable.ic_add_gray else R.drawable.ic_add_black
                                                ),
                                                contentDescription = "Add track",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }

                                        if (!track.streamUrl.isNullOrEmpty()) {
                                            IconButton(onClick = {
                                                if (isPlaying) {
                                                    mediaPlayer?.pause()
                                                } else {
                                                    mediaPlayer?.release()
                                                    mediaPlayer = MediaPlayer().apply {
                                                        setDataSource(track.streamUrl)
                                                        setOnPreparedListener {
                                                            start()
                                                            isPlaying = true
                                                        }
                                                        prepareAsync()
                                                    }
                                                }
                                            }) {
                                                Image(
                                                    painter = painterResource(
                                                        id = if (isDarkTheme) R.drawable.ic_play_gray else R.drawable.ic_play_black
                                                    ),
                                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { println("Like track") },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = if (isDarkTheme) R.drawable.ic_heart_gray else R.drawable.ic_heart_black
                                                ),
                                                contentDescription = "Like track",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}