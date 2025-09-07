package com.example.music_app.data.music

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("SELECT * FROM albums WHERE title LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<AlbumEntity>
}