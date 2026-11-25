package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.ProductDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/** FakeStore API: https://fakestoreapi.com */
interface ShopApi {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/categories")
    suspend fun getCategories(): List<String>
}

object ShopClient {
    private const val BASE_URL = "https://fakestoreapi.com/"

    val api: ShopApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShopApi::class.java)
    }
}
