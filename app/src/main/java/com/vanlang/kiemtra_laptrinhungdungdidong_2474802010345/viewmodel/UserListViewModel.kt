package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Resource
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserDto
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.UserRepository
import kotlinx.coroutines.launch

class UserListViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _users = MutableLiveData<Resource<List<UserDto>>>()
    val users: LiveData<Resource<List<UserDto>>> = _users

    fun loadUsers() {
        _users.value = Resource.Loading
        viewModelScope.launch {
            _users.value = repository.fetchUsers()
        }
    }
}