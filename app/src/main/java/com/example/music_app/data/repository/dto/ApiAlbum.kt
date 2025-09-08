package com.example.music_app.data.repository.dto

import com.example.music_app.data.repository.dto.ApiArtist

data class ApiAlbum(
    val id: String,
    val title: String,
    val artwork: Map<String, String>?,
    val artist: ApiArtist
)