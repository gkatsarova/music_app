package com.example.music_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.music_app.R
import com.example.music_app.viewmodel.PlayingTrackViewModel

@Composable
fun PlayingTrack(
    viewModel: PlayingTrackViewModel,
    modifier: Modifier = Modifier
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val showController by viewModel.showController.collectAsState()
    val artistName by viewModel.artistName.collectAsState()

    if (currentTrack == null || !showController) return

    val isDarkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp),
        shape = RoundedCornerShape(35.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = currentTrack!!.artworkUrl,
                contentDescription = currentTrack!!.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = currentTrack!!.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Text(
                    text = artistName ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = {
                    if (isPlaying) {
                    viewModel.pause()

                    } else {
                    viewModel.play()
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = if (isPlaying) {
                            if (isDarkTheme) R.drawable.ic_pause_track_gray
                            else R.drawable.ic_pause_track_black
                        } else {
                            if (isDarkTheme) R.drawable.ic_play_track_gray
                            else R.drawable.ic_play_track_black
                        }
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}