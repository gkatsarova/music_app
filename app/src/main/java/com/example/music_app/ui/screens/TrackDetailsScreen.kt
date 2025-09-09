package com.example.music_app.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.music_app.R
import com.example.music_app.viewmodel.TrackViewModel
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.example.music_app.ui.components.BottomNavBar
import androidx.navigation.NavController
import androidx.compose.material3.TextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.viewmodel.MusicViewModel
import com.example.music_app.viewmodel.factory.MusicViewModelFactory

@Composable
fun TrackDetailsScreen(
    trackId: String,
    viewModel: TrackViewModel,
    navController: NavController
) {
    val trackUi by viewModel.track.collectAsState()
    val context = LocalContext.current
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

    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()

    val sharedPrefs = context.getSharedPreferences("user_session_prefs", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getInt("logged_in_user_id", -1)

    LaunchedEffect(trackId) {
        viewModel.loadTrack(trackId)
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
            TextField(
                value = query,
                onValueChange = { newQuery ->
                    query = newQuery
                    musicViewModel.search(newQuery)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(35.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "Search",
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                    )
                }
            )

            if (query.isNotEmpty()) {
                if (loading) {
                    Text("Loading data...", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchResult.tracks) { track ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("trackDetails/${track.id}")
                                    }
                                    .padding(4.dp)
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
                            }
                        }

                        items(searchResult.albums) { album ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(album.artworkUrl)
                                        .crossfade(true)
                                        .placeholder(if(isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                        .error(if(isDarkTheme) R.drawable.ic_record_player_gray else R.drawable.ic_record_player_black)
                                        .build(),
                                    contentDescription = album.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Text(album.title)
                            }
                        }

                        items(searchResult.artists) { artist ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(artist.imageUrl)
                                        .crossfade(true)
                                        .placeholder(if(isDarkTheme) R.drawable.ic_user_avatar_gray else R.drawable.ic_user_avatar_black)
                                        .error(if(isDarkTheme) R.drawable.ic_user_avatar_gray else R.drawable.ic_user_avatar_black)
                                        .build(),
                                    contentDescription = artist.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Text(artist.name)
                            }
                        }
                    }
                }
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
                            placeholder = if(isDarkTheme) painterResource
                                (id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black),
                            error = if(isDarkTheme) painterResource
                                (id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black),
                            fallback = if(isDarkTheme) painterResource
                                (id = R.drawable.ic_record_player_gray)
                            else painterResource(id = R.drawable.ic_record_player_black)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = t.title,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ui.artistName ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ui.albumName ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                println("Add button") },
                                modifier = Modifier.size(60.dp)
                            ){
                                Image(
                                    painter = painterResource(id = if(isDarkTheme) R.drawable.ic_add_gray else R.drawable.ic_add_black),
                                    contentDescription = "Add",
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(40.dp))

                            if (!t.streamUrl.isNullOrEmpty()) {
                                IconButton(onClick = {
                                    if (!t.streamUrl.isEmpty()) {
                                        if (isPlaying) {
                                            mediaPlayer?.pause()
                                            isPlaying = false
                                        } else {
                                            if (mediaPlayer == null) {
                                                mediaPlayer = MediaPlayer().apply {
                                                    setDataSource(t.streamUrl)
                                                    setOnPreparedListener {
                                                        start()
                                                        isPlaying = true
                                                    }
                                                    prepareAsync()
                                                }
                                            } else {
                                                mediaPlayer?.start()
                                                isPlaying = true
                                            }
                                        }
                                    }
                                },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = if(isDarkTheme) R.drawable.ic_play_gray else R.drawable.ic_play_black),
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            } else {
                                Text("No stream available", color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(modifier = Modifier.width(40.dp))

                            IconButton(onClick = {
                                println("Like button") },
                                modifier = Modifier.size(60.dp)){
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