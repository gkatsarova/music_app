package com.example.music_app.data.api

import com.example.music_app.data.repository.response.AlbumsResponse
import com.example.music_app.data.repository.response.ArtistsResponse
import com.example.music_app.data.repository.response.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AudiusApi {

    @GET("v1/tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 50
    ): TracksResponse

    @GET("v1/albums/search")
    suspend fun searchAlbums(
        @Query("query") query: String,
        @Query("limit") limit: Int = 50
    ): AlbumsResponse

    @GET("v1/users/search")
    suspend fun searchArtists(
        @Query("query") query: String,
        @Query("limit") limit: Int = 50
    ): ArtistsResponse

}
