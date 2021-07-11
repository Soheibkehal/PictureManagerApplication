package com.example.picturemanagersoheib.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picturemanagersoheib.data.models.Album

class AlbumViewModel : ViewModel() {
    private val _albums = MutableLiveData<List<Album>>()
    var albums: LiveData<List<Album>> = _albums

    fun setAlbums(item: List<Album>) {
        _albums.value = item
        albums = _albums
    }
}