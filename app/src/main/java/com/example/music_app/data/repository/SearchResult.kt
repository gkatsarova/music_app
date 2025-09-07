package com.example.music_app.data.repository

import com.example.music_app.data.music.AlbumEntity
import com.example.music_app.data.music.ArtistEntity
import com.example.music_app.data.music.TrackEntity

data class SearchResult(
    val tracks: List<TrackEntity>,
    val albums: List<AlbumEntity>,
    val artists: List<ArtistEntity>
)