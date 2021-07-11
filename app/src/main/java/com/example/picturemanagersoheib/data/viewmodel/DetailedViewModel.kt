package com.example.picturemanagersoheib.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picturemanagersoheib.data.models.User
import com.example.picturemanagersoheib.data.repository.UserRepository
import kotlinx.coroutines.launch


class DetailedViewModel : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    var users: LiveData<List<User>> = _users

    fun setUsers(items: List<User>) {
        _users.value = items
        users = _users
    }

}