package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}