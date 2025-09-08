package com.example.music_app.data.repository

import android.util.Log
import com.example.music_app.data.api.AudiusApi
import com.example.music_app.data.api.RetrofitInstance
import com.example.music_app.data.music.*

class MusicRepository(
    private val trackDao: TrackDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao
) {
    private val api: AudiusApi = RetrofitInstance.api

    private suspend fun saveTracks(tracks: List<ApiTrack>) {
        val trackEntities = tracks.map { track ->
            track.toEntity().apply {
                if (streamUrl.isNullOrEmpty()) {
                    Log.d("TRACK_DEBUG", "Track ${track.title} missing streamUrl")
                }
            }
        }
        if (trackEntities.isNotEmpty()) {
            trackDao.insertAll(trackEntities)
            Log.d("DB_DEBUG", "Saved tracks: ${trackEntities.size}")
        }
    }

    private suspend fun saveAlbums(albums: List<ApiAlbum>) {
        val albumEntities = albums.map { it.toEntity() }
        if (albumEntities.isNotEmpty()) {
            albumDao.insertAll(albumEntities)
            Log.d("DB_DEBUG", "Saved albums: ${albumEntities.size}")
        }
    }

    private suspend fun saveArtists(artists: List<ApiArtist>) {
        val artistEntities = artists.map { it.toEntity() }
        if (artistEntities.isNotEmpty()) {
            artistDao.insertAll(artistEntities)
            Log.d("DB_DEBUG", "Saved artists: ${artistEntities.size}")
        }
    }

    suspend fun loadAllData(onComplete: (success: Boolean, message: String) -> Unit) {
        try {
            val letters = 'a'..'z'

            letters.forEach { query ->
                Log.d("API_DEBUG", "Loading data for query: $query")

                val tracksResponse = try { api.searchTracks(query.toString()) } catch (e: Exception) {
                    Log.e("API_ERROR", "Tracks query '$query' failed: ${e.message}")
                    null
                }

                val albumsResponse = try { api.searchAlbums(query.toString()) } catch (e: Exception) {
                    Log.e("API_ERROR", "Albums query '$query' failed: ${e.message}")
                    null
                }

                val artistsResponse = try { api.searchArtists(query.toString()) } catch (e: Exception) {
                    Log.e("API_ERROR", "Artists query '$query' failed: ${e.message}")
                    null
                }

                tracksResponse?.data?.let { saveTracks(it) }
                albumsResponse?.data?.let { saveAlbums(it) }
                artistsResponse?.data?.let { saveArtists(it) }
            }

            onComplete(true, "All data loaded")
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error loading data: ${e.message}")
            onComplete(false, "Error loading data: ${e.message}")
        }
    }

    suspend fun searchAll(query: String): SearchResult {
        val tracks = trackDao.search(query)
        val albums = albumDao.search(query)
        val artists = artistDao.search(query)
        Log.d("DB_DEBUG", "Search '$query' results -> tracks: ${tracks.size}, albums: ${albums.size}, artists: ${artists.size}")
        return SearchResult(tracks, albums, artists)
    }
}

fun ApiTrack.toEntity() = TrackEntity(
    id = id,
    title = title,
    artistId = user?.id ?: "",
    albumId = album?.id,
    genre = genre ?: "",
    artworkUrl = artwork?.get("150x150"),
    streamUrl = streamUrl ?: ""
)

fun ApiAlbum.toEntity() = AlbumEntity(
    id = id,
    title = title,
    artistId = artist.id,
    artworkUrl = artwork?.get("150x150") ?: ""
)

fun ApiArtist.toEntity() = ArtistEntity(
    id = id,
    name = name,
    handle = handle ?: "",
    imageUrl = profilePicture?.get("150x150") ?: ""
)
