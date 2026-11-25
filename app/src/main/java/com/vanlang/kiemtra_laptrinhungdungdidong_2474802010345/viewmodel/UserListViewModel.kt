package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.AppDatabase
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Resource
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UserEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.UserRepository
import kotlinx.coroutines.launch

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(AppDatabase.getInstance(application))

    val users: LiveData<List<UserEntity>> = repository.users.asLiveData()

    private val _status = MutableLiveData<Resource<Unit>>()
    val status: LiveData<Resource<Unit>> = _status

    fun loadUsers() {
        _status.value = Resource.Loading
        viewModelScope.launch {
            _status.value = repository.refresh()
        }
    }
}