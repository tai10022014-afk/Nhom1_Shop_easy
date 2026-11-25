package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(@Query("limit") limit: Int = 10): UsersResponse
}