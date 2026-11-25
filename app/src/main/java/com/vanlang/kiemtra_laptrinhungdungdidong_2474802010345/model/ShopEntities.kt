package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cache danh sách sản phẩm để xem ngoại tuyến. */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rate: Double,
    val count: Int
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val qty: Int,
    val addedAt: Long
)

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val productId: Int)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val total: Double,
    val itemCount: Int,
    val createdAt: Long
)

/** JSON của FakeStore API. */
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto?
)

data class RatingDto(val rate: Double?, val count: Int?)

fun ProductDto.toEntity() = ProductEntity(
    id, title, price, description, category, image, rating?.rate ?: 0.0, rating?.count ?: 0
)

fun ProductEntity.toProduct() = Product(id, title, price, description, category, image, rate, count)
