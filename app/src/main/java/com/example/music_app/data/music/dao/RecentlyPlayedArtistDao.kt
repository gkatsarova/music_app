package com.example.music_app.data.music.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.music_app.data.music.entity.RecentlyPlayedArtistEntity

@Dao
interface RecentlyPlayedArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentlyPlayed: RecentlyPlayedArtistEntity)

    @Query("""
        SELECT * FROM recently_played_artists 
        WHERE user_id = :userId 
        GROUP BY artist_id 
        ORDER BY MAX(played_at) DESC 
        LIMIT 5
    """)
    suspend fun getRecentlyPlayedArtists(userId: Int): List<RecentlyPlayedArtistEntity>

    @Query("DELETE FROM recently_played_artists WHERE user_id = :userId AND artist_id = :artistId")
    suspend fun removeArtistFromRecentlyPlayed(userId: Int, artistId: String)
}