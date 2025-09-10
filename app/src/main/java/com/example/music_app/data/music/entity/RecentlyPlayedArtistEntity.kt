package com.example.music_app.data.music.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_played_artists")
data class RecentlyPlayedArtistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "artist_id") val artistId: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo("played_at") val playedAt: Long
)