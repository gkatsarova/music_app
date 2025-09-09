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

    var album by remember { mutableStateOf<AlbumEntity?>(null) }
    var albumTracks by remember { mutableStateOf<List<TrackEntity>>(emptyList()) }

    LaunchedEffect(albumId) {
        album = repository.albumDao.getAlbumById(albumId)
        album?.let { alb ->
            albumTracks = repository.trackDao.getAll().filter { it.albumId == alb.id }
        }
    }

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
                text = "Artist: ${album!!.artistId}",
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
            }
        }
    }
}