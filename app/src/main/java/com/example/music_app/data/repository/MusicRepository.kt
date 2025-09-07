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

    suspend fun saveAllTracks(apiTracks: List<ApiTrack>) {
        val artists = apiTracks.mapNotNull { it.user }
            .map { it.toEntity() }
            .distinctBy { it.id }
        if (artists.isNotEmpty()) {
            artistDao.insertAll(artists)
            Log.d("DB_DEBUG", "Saved artists: ${artists.size}")
        }

        val albums = apiTracks.mapNotNull { it.album }
            .map { it.toEntity() }
            .distinctBy { it.id }
        if (albums.isNotEmpty()) {
            albumDao.insertAll(albums)
            Log.d("DB_DEBUG", "Saved albums: ${albums.size}")
        }

        val tracks = apiTracks.map { it.toEntity() }
        if (tracks.isNotEmpty()) {
            trackDao.insertAll(tracks)
            Log.d("DB_DEBUG", "Saved tracks: ${tracks.size}")
        }
    }

    suspend fun saveAllAlbums(albums: List<ApiAlbum>) {
        val entities = albums.map { it.toEntity() }.distinctBy { it.id }
        if (entities.isNotEmpty()) {
            albumDao.insertAll(entities)
            Log.d("DB_DEBUG", "Saved albums from saveAllAlbums: ${entities.size}")
        }
    }

    suspend fun saveAllArtists(artists: List<ApiArtist>) {
        val entities = artists.map { it.toEntity() }.distinctBy { it.id }
        if (entities.isNotEmpty()) {
            artistDao.insertAll(entities)
            Log.d("DB_DEBUG", "Saved artists from saveAllArtists: ${entities.size}")
        }
    }

    suspend fun searchAll(query: String): SearchResult {
        val tracks = trackDao.search(query)
        val albums = albumDao.search(query)
        val artists = artistDao.search(query)
        Log.d("DB_DEBUG", "Search '$query' results -> tracks: ${tracks.size}, albums: ${albums.size}, artists: ${artists.size}")
        return SearchResult(tracks, albums, artists)
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

                tracksResponse?.data?.let { saveAllTracks(it) }
                albumsResponse?.data?.let { saveAllAlbums(it) }
                artistsResponse?.data?.let { saveAllArtists(it) }
            }

            onComplete(true, "All data loaded")
        } catch (e: Exception) {
            Log.e("REPO_ERROR", "Error loading all data: ${e.message}")
            onComplete(false, "Error loading data: ${e.message}")
        }
    }
}

fun ApiTrack.toEntity() = TrackEntity(
    id = id ?: "UnknownTrackId",
    title = title ?: "Unknown Title",
    artistId = user?.id ?: "UnknownArtist",
    albumId = album?.id,
    genre = genre ?: "",
    artworkUrl = artwork?.get("150x150") ?: "",
    streamUrl = streamUrl ?: ""
)

fun ApiAlbum.toEntity() = AlbumEntity(
    id = id ?: "UnknownAlbum",
    title = title ?: "Unknown Album",
    artistId = artist.id ?: "UnknownArtist",
    artworkUrl = artwork?.get("150x150")
)

fun ApiArtist.toEntity() = ArtistEntity(
    id = id ?: "UnknownArtist",
    name = name ?: "Unknown Artist",
    handle = handle ?: "unknown_handle",
    imageUrl = profilePicture?.get("150x150")
)
