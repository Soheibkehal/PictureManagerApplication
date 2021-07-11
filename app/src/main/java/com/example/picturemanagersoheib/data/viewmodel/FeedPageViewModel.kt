package com.example.picturemanagersoheib.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturemanagersoheib.data.models.ImageFeed
import com.example.picturemanagersoheib.data.repository.ImageRepository
import kotlinx.coroutines.launch

class FeedPageViewModel : ViewModel() {
    private val _feed= MutableLiveData<List<ImageFeed>>()

    var feed: LiveData<List<ImageFeed>> = _feed

    init {
        viewModelScope.launch {
            _feed.value=ImageRepository().getUserFeed()
            feed=_feed
        }
    }
}