package com.example.music_app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.music_app.viewmodel.TrackViewModel

@Composable
fun TrackDetailsScreen(
    trackId: String,
    viewModel: TrackViewModel
) {
    val context = LocalContext.current
    val trackUi by viewModel.track.collectAsState()

    LaunchedEffect(trackId) {
        viewModel.loadTrack(trackId)
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
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = t.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Artist: ${ui.artistName ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Album: ${ui.albumName ?: "Unknown"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(24.dp))

            if (!t.streamUrl.isNullOrEmpty()) {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(t.streamUrl))
                    context.startActivity(intent)
                }) {
                    Text("Play")
                }
            } else {
                Text("No stream available", color = MaterialTheme.colorScheme.error)
            }
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
