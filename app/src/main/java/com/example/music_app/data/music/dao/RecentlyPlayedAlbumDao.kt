package com.example.music_app.data.music.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.music_app.data.music.entity.RecentlyPlayedAlbumEntity

@Dao
interface RecentlyPlayedAlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentlyPlayed: RecentlyPlayedAlbumEntity)

    @Query("""
        SELECT * FROM recently_played_albums 
        WHERE user_id = :userId 
        GROUP BY album_id 
        ORDER BY MAX(played_at) DESC 
        LIMIT 5
    """)
    suspend fun getRecentlyPlayedAlbums(userId: Int): List<RecentlyPlayedAlbumEntity>

    @Query("DELETE FROM recently_played_albums WHERE user_id = :userId AND album_id = :albumId")
    suspend fun removeAlbumFromRecentlyPlayed(userId: Int, albumId: String)
}