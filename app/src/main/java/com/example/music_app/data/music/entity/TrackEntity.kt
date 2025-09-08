package com.example.music_app.data.music.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist_id") val artistId: String,
    @ColumnInfo(name = "album_id") val albumId: String?,
    @ColumnInfo(name = "genre") val genre: String? = null,
    @ColumnInfo(name = "artwork_url") val artworkUrl: String?,
    @ColumnInfo(name = "stream_url") val streamUrl: String?
)