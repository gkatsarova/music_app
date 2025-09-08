package com.example.music_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.music.entity.TrackEntity
import com.example.music_app.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrackViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    data class TrackUiModel(
        val track: TrackEntity,
        val artistName: String?,
        val albumName: String?
    )

    private val _track = MutableStateFlow<TrackUiModel?>(null)
    val track: StateFlow<TrackUiModel?> = _track

    fun loadTrack(trackId: String) {
        viewModelScope.launch {
            val trackEntity = repository.getTrackById(trackId) ?: return@launch
            val artistName = trackEntity.artistId.takeIf { it.isNotEmpty() }?.let { repository.artistDao.getArtistById(it)?.name }
            val albumName = trackEntity.albumId?.let { repository.albumDao.getAlbumById(it)?.title }

            _track.value = TrackUiModel(
                track = trackEntity,
                artistName = artistName,
                albumName = albumName
            )
        }
    }
}
