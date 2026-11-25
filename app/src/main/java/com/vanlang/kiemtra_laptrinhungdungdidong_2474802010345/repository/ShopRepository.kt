package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.ShopDao
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartItemEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartLine
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.FavoriteEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.OrderEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.toEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.toProduct
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network.ShopApi
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.network.ShopClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ShopRepository(
    private val dao: ShopDao,
    private val api: ShopApi = ShopClient.api
) {
    val products: Flow<List<Product>> = dao.products()
        .map { list -> list.map { it.toProduct() } }
        .distinctUntilChanged()

    val cart: Flow<List<CartLine>> = combine(dao.cartItems(), dao.products()) { items, prods ->
        val byId = prods.associateBy { it.id }
        items.mapNotNull { item -> byId[item.productId]?.let { CartLine(it.toProduct(), item.qty) } }
    }.distinctUntilChanged()

    val favoriteIds: Flow<Set<Int>> = dao.favoriteIds().map { it.toSet() }
    val orders: Flow<List<OrderEntity>> = dao.orders()

    /** Tải từ API và ghi vào Room. Lỗi mạng -> Result.failure, dữ liệu cache vẫn dùng được. */
    suspend fun refresh(): Result<Unit> = try {
        dao.upsertProducts(api.getProducts().map { it.toEntity() })
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getProduct(id: Int): Product? = dao.productById(id)?.toProduct()

    suspend fun addToCart(productId: Int) {
        val inserted = dao.insertCartItem(CartItemEntity(productId, 1, System.currentTimeMillis()))
        if (inserted == -1L) dao.addQty(productId, 1)
    }

    suspend fun changeQty(productId: Int, delta: Int) {
        dao.addQty(productId, delta)
        dao.cleanupCart()
    }

    suspend fun removeFromCart(productId: Int) = dao.deleteCartItem(productId)

    suspend fun toggleFavorite(productId: Int) {
        if (dao.isFavorite(productId) > 0) dao.removeFavorite(productId)
        else dao.addFavorite(FavoriteEntity(productId))
    }

    /** Đặt hàng giả lập: lưu đơn rồi làm trống giỏ. */
    suspend fun checkout(lines: List<CartLine>) {
        if (lines.isEmpty()) return
        dao.insertOrder(
            OrderEntity(
                total = lines.sumOf { it.lineTotal },
                itemCount = lines.sumOf { it.qty },
                createdAt = System.currentTimeMillis()
            )
        )
        dao.clearCart()
    }

    suspend fun clearOrders() = dao.clearOrders()

    suspend fun clearUserData() {
        dao.clearCart()
        dao.clearFavorites()
        dao.clearOrders()
    }
}
