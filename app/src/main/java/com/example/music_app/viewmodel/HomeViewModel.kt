package com.example.music_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.repository.MusicRepository
import com.example.music_app.data.repository.dto.ApiAlbum
import com.example.music_app.data.repository.dto.ApiArtist
import com.example.music_app.data.repository.dto.ApiTrack
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MusicRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            saveSampleData()
        }
    }

    suspend fun saveSampleData() {
        val sampleArtists = listOf(
            ApiArtist(
                id = "101",
                name = "Artist One",
                handle = "artist1",
                profilePicture = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150.jpg"
                )
            ),
            ApiArtist(
                id = "102",
                name = "Artist Two",
                handle = "artist2",
                profilePicture = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150.jpg"
                )
            ),
            ApiArtist(
                id = "103",
                name = "Artist Three",
                handle = "artist3",
                profilePicture = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150.jpg"
                )
            ),
            ApiArtist(
                id = "104",
                name = "Artist Four",
                handle = "artist4",
                profilePicture = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150.jpg"
                )
            ),
            ApiArtist(
                id = "105",
                name = "Artist Five",
                handle = "artist5",
                profilePicture = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150.jpg"
                )
            )
        )

        val sampleAlbums = listOf(
            ApiAlbum(
                id = "201",
                title = "Album One",
                artist = sampleArtists[0],
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                )
            ),
            ApiAlbum(
                id = "202",
                title = "Album Two",
                artist = sampleArtists[1],
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                )
            ),
            ApiAlbum(
                id = "203",
                title = "Album Three",
                artist = sampleArtists[2],
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                )
            ),
            ApiAlbum(
                id = "204",
                title = "Album Four",
                artist = sampleArtists[3],
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                )
            ),
            ApiAlbum(
                id = "205",
                title = "Album Five",
                artist = sampleArtists[4],
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                )
            )
        )


        val sampleTracks = listOf(
            ApiTrack(
                id = "1",
                title = "Track One",
                user = sampleArtists[0],
                album = sampleAlbums[0],
                genre = "Pop",
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                ),
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            ),
            ApiTrack(
                id = "2",
                title = "Track Two",
                user = sampleArtists[1],
                album = sampleAlbums[1],
                genre = "Hip-Hop",
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                ),
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
            ),
            ApiTrack(
                id = "3",
                title = "Track Three",
                user = sampleArtists[2],
                album = sampleAlbums[2],
                genre = "Rock",
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                ),
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
            ),
            ApiTrack(
                id = "4",
                title = "Track Four",
                user = sampleArtists[3],
                album = sampleAlbums[3],
                genre = "Electronic",
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                ),
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
            ),
            ApiTrack(
                id = "5",
                title = "Track Five",
                user = sampleArtists[4],
                album = sampleAlbums[4],
                genre = "Jazz",
                artwork = mapOf(
                    "150x150" to "https://cn1.mainnet.audiusindex.org/content/01JSWTDQGEECV246XPNDV26066/150x150"
                ),
                streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
            )
        )

        sampleArtists.forEach { repository.saveArtist(it) }
        sampleAlbums.forEach { repository.saveAlbum(it) }
        sampleTracks.forEach { repository.saveTrack(it) }
    }
}