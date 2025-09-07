package com.example.music_app.data.repository

data class ApiTrack(
    val id: String,
    val title: String,
    val genre: String?,
    val artwork: Map<String, String>?,
    val streamUrl: String?,
    val user: ApiArtist?,
    val album: ApiAlbum?
)