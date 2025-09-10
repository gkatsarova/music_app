package com.example.music_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.music_app.data.music.dao.AlbumDao
import com.example.music_app.data.music.entity.AlbumEntity
import com.example.music_app.data.music.dao.ArtistDao
import com.example.music_app.data.music.dao.RecentlyPlayedAlbumDao
import com.example.music_app.data.music.entity.ArtistEntity
import com.example.music_app.data.music.dao.TrackDao
import com.example.music_app.data.music.entity.RecentlyPlayedAlbumEntity
import com.example.music_app.data.music.entity.TrackEntity
import com.example.music_app.data.user.Converters
import com.example.music_app.data.user.UserDao
import com.example.music_app.data.user.UserEntity
import com.example.music_app.data.music.DateConverter

@Database(entities = [UserEntity::class, ArtistEntity::class, AlbumEntity::class, TrackEntity::class, RecentlyPlayedAlbumEntity::class],
    version = 1)
@TypeConverters(Converters::class, DateConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao

    abstract fun recentlyPlayedAlbumDao(): RecentlyPlayedAlbumDao

}