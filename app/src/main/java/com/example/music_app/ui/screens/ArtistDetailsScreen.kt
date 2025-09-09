package com.example.music_app.ui.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.request.ImageRequest
import com.example.music_app.R
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.music.entity.ArtistEntity
import com.example.music_app.data.music.entity.TrackEntity
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.ui.components.BottomNavBar
import com.example.music_app.ui.components.MusicList
import com.example.music_app.ui.components.SearchBar
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory

@Composable
fun ArtistDetailsScreen(
    artistId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val db = DatabaseClient.getDatabase(context)
    val repository = MusicRepository(
        trackDao = db.trackDao(),
        artistDao = db.artistDao(),
        albumDao = db.albumDao(),
        context = context
    )

    var artist by remember { mutableStateOf<ArtistEntity?>(null) }
    var albums by remember { mutableStateOf<List<AlbumEntity>>(emptyList()) }
    var tracks by remember { mutableStateOf<List<TrackEntity>>(emptyList()) }

    val musicViewModel: MusicViewModel = viewModel(
        factory = MusicViewModelFactory(repository)
    )

    var query by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(2) }
    val searchResult by musicViewModel.searchResult.collectAsState()
    val loading by musicViewModel.loading.collectAsState()

    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val sharedPrefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)

    LaunchedEffect(artistId) {
        artist = repository.artistDao.getArtistById(artistId)
        albums = repository.albumDao.getAll().filter { it.artistId == artistId }
        tracks = repository.trackDao.getAll().filter { it.artistId == artistId }
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
                if (artist == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(artist!!.imageUrl)
                                    .crossfade(true)
                                    .placeholder(if (isDarkTheme) R.drawable.ic_user_avatar_gray else R.drawable.ic_user_avatar_black)
                                    .error(if (isDarkTheme) R.drawable.ic_user_avatar_gray else R.drawable.ic_user_avatar_black)
                                    .build(),
                                contentDescription = artist!!.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(100.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = artist!!.name,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (albums.isNotEmpty()) {
                            item {
                                Text(
                                    "Albums",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            items(albums) { album ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(35.dp))
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(20.dp)
                                        .clickable {
                                            navController.navigate("albumDetails/${album.id}")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(album.artworkUrl)
                                            .crossfade(true)
                                            .placeholder(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                            .error(if (isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                            .build(),
                                        contentDescription = album.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Text(
                                        text = album.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (tracks.isNotEmpty()) {
                            item {
                                Text(
                                    "Tracks",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            items(tracks) { track ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(35.dp))
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .padding(20.dp)
                                        .clickable {
                                            navController.navigate("trackDetails/${track.id}")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
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
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

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
