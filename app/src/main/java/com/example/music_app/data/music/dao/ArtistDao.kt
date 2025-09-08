package com.example.music_app.data.music.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.music_app.data.music.entity.ArtistEntity

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ArtistEntity>
}