package com.example.music_app.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music_app.R
import com.example.music_app.data.DatabaseClient
import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.music.entity.ArtistEntity
import com.example.music_app.data.music.entity.TrackEntity
import com.example.music_app.data.repository.MusicRepository

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
        albumDao = db.albumDao()
    )

    var artist by remember { mutableStateOf<ArtistEntity?>(null) }
    var albums by remember { mutableStateOf<List<AlbumEntity>>(emptyList()) }
    var tracks by remember { mutableStateOf<List<TrackEntity>>(emptyList()) }

    LaunchedEffect(artistId) {
        artist = repository.artistDao.getArtistById(artistId)
        albums = repository.albumDao.getAll().filter { it.artistId == artistId }
        tracks = repository.trackDao.getAll().filter { it.artistId == artistId }
    }

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
                        Text(text = album.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center)
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
                        Text(text = track.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
