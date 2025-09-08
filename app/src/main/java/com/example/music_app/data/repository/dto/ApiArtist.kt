package com.example.music_app.data.repository.dto

data class ApiArtist(
    val id: String,
    val handle: String,
    val name: String,
    val profilePicture: Map<String, String>?
)