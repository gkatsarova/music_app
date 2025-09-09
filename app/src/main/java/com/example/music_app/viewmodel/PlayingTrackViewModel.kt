package com.example.music_app.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_app.data.music.entity.TrackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayingTrackViewModel: ViewModel() {
    private val _currentTrack = MutableStateFlow<TrackEntity?>(null)
    val currentTrack: StateFlow<TrackEntity?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer: StateFlow<MediaPlayer?> = _mediaPlayer

    private val _showController = MutableStateFlow(false)
    val showController: StateFlow<Boolean> = _showController

    fun playTrack(track: TrackEntity) {
        viewModelScope.launch {
            _currentTrack.value = track
            _showController.value = true

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
}