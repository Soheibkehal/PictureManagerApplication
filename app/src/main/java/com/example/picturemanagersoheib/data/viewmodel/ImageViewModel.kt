package com.example.picturemanagersoheib.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picturemanagersoheib.data.models.Image

class ImageViewModel : ViewModel() {
    private val _images = MutableLiveData<List<Image>>()
    var images: LiveData<List<Image>> = _images

    fun setImages(item: List<Image>) {
        _images.value = item
        images = _images
    }
}