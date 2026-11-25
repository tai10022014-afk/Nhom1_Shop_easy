package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model

data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String? = null
)

data class UsersResponse(
    val users: List<UserDto>
)