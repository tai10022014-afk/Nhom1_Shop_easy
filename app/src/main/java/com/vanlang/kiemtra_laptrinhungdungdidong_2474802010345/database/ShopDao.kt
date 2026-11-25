package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartItemEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.FavoriteEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.OrderEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    // ---- Sản phẩm (cache)
    @Query("SELECT * FROM products ORDER BY id")
    fun products(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProducts(list: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun productById(id: Int): ProductEntity?

    // ---- Giỏ hàng
    @Query("SELECT * FROM cart_items ORDER BY addedAt")
    fun cartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Query("UPDATE cart_items SET qty = qty + :delta WHERE productId = :id")
    suspend fun addQty(id: Int, delta: Int)

    @Query("DELETE FROM cart_items WHERE qty <= 0")
    suspend fun cleanupCart()

    @Query("DELETE FROM cart_items WHERE productId = :id")
    suspend fun deleteCartItem(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // ---- Yêu thích
    @Query("SELECT productId FROM favorites")
    fun favoriteIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(f: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :id")
    suspend fun removeFavorite(id: Int)

    @Query("SELECT COUNT(*) FROM favorites WHERE productId = :id")
    suspend fun isFavorite(id: Int): Int

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    // ---- Đơn hàng (giả lập)
    @Insert
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun orders(): Flow<List<OrderEntity>>

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}
