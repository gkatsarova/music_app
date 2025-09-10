package com.example.music_app.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.music.entity.TrackEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingTrackViewModel @Inject constructor() : ViewModel() {
    private val _currentTrack = MutableStateFlow<TrackEntity?>(null)
    val currentTrack: StateFlow<TrackEntity?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playlist = MutableStateFlow<List<TrackEntity>>(emptyList())

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    private val _artistName = MutableStateFlow<String?>(null)
    val artistName: StateFlow<String?> = _artistName

    private val _showController = MutableStateFlow(false)
    val showController: StateFlow<Boolean> = _showController

    private val _currentIndex = MutableStateFlow(0)

    fun playTrack(track: TrackEntity, artistName: String? = null) {
        viewModelScope.launch {
            _currentTrack.value = track
            _showController.value = true
            _artistName.value = artistName

            _mediaPlayer.value?.release()

            try {
                val player = MediaPlayer().apply {
                    setOnErrorListener { mp, what, extra ->
                        _isPlaying.value = false
                        false
                    }
                    setDataSource(track.streamUrl)
                    setOnPreparedListener {
                        start()
                        _isPlaying.value = true
                    }
                    prepareAsync()
                }
                _mediaPlayer.value = player
            } catch (e: Exception) {
                _isPlaying.value = false
                _showController.value = false
            }
        }
    }

    fun play() {
        viewModelScope.launch {
            val player = _mediaPlayer.value
            if (player != null && !_isPlaying.value) {
                player.start()
                _isPlaying.value = true
            }
        }
    }

    fun pause() {
        viewModelScope.launch {
            val player = _mediaPlayer.value
            if (player != null && _isPlaying.value) {
                player.pause()
                _isPlaying.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _mediaPlayer.value?.release()
    }

    fun setPlaylist(tracks: List<TrackEntity>, startIndex: Int = 0) {
        _playlist.value = tracks
        _currentIndex.value = startIndex
        _currentTrack.value = tracks.getOrNull(startIndex)
    }

    fun nextTrack() {
        if (_playlist.value.isEmpty()) return

        val currentPlaylist = _playlist.value
        val currentIdx = _currentIndex.value
        val newIndex = (currentIdx + 1) % currentPlaylist.size
        val track = currentPlaylist[newIndex]

        viewModelScope.launch {
            _mediaPlayer.value?.release()
            _currentIndex.value = newIndex
            playTrack(track, _artistName.value)
        }
    }

    fun previousTrack() {
        if (_playlist.value.isEmpty()) return

        val currentPlaylist = _playlist.value
        val currentIdx = _currentIndex.value
        val newIndex = if (currentIdx - 1 < 0) currentPlaylist.lastIndex else currentIdx - 1
        val track = currentPlaylist[newIndex]

        viewModelScope.launch {
            _mediaPlayer.value?.release()
            _currentIndex.value = newIndex
            playTrack(track, _artistName.value)
        }
    }
}