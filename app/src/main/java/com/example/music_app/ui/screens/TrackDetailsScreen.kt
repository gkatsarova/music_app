package com.example.music_app.ui.screens

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

@Composable
fun TrackDetailsScreen(
    trackId: String,
    viewModel: TrackViewModel
) {
    val trackUi by viewModel.track.collectAsState()

    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(trackId) {
        viewModel.loadTrack(trackId)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

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
            Text(text = t.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ui.artistName ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = ui.albumName ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
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
