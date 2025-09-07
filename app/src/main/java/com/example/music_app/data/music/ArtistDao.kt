package com.example.music_app.data.music

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(artists: List<ArtistEntity>)

    @Query("SELECT * FROM artists WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<ArtistEntity>
}