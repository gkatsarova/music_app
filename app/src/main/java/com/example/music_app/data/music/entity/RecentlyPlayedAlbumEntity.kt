package com.example.music_app.data.music.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played_albums")
data class RecentlyPlayedAlbumEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "album_id") val albumId: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo("played_at") val playedAt: Long
)