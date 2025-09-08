package com.example.music_app.data.repository

import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.music.entity.ArtistEntity
import com.example.music_app.data.music.entity.TrackEntity

data class SearchResult(
    val tracks: List<TrackEntity>,
    val albums: List<AlbumEntity>,
    val artists: List<ArtistEntity>
)