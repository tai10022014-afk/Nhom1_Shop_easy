package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.database.AppDatabase
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartLine
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.OrderEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.repository.ShopRepository
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ShopRepository(AppDatabase.getInstance(application).shopDao())

    val products: LiveData<List<Product>> = repo.products.asLiveData()
    val cart: LiveData<List<CartLine>> = repo.cart.asLiveData()
    val favoriteIds: LiveData<Set<Int>> = repo.favoriteIds.asLiveData()
    val orders: LiveData<List<OrderEntity>> = repo.orders.asLiveData()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /** Thông báo lỗi mạng (null = không lỗi). */
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var started = false

    /** Chỉ MainActivity gọi, đúng 1 lần mỗi phiên; các màn hình khác dùng dữ liệu Room có sẵn. */
    fun start() {
        if (started) return
        started = true
        refresh()
    }

    fun refresh() {
        if (_loading.value == true) return
        viewModelScope.launch {
            _loading.value = true
            val result = repo.refresh()
            _loading.value = false
            _error.value = result.exceptionOrNull()?.let {
                "Không thể kết nối máy chủ. Đang dùng dữ liệu ngoại tuyến."
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    suspend fun getProduct(id: Int): Product? = repo.getProduct(id)

    fun addToCart(p: Product) = viewModelScope.launch { repo.addToCart(p.id) }
    fun changeQty(id: Int, delta: Int) = viewModelScope.launch { repo.changeQty(id, delta) }
    fun removeFromCart(id: Int) = viewModelScope.launch { repo.removeFromCart(id) }
    fun toggleFavorite(id: Int) = viewModelScope.launch { repo.toggleFavorite(id) }
    fun checkout() = viewModelScope.launch { repo.checkout(cart.value.orEmpty()) }
    fun clearOrders() = viewModelScope.launch { repo.clearOrders() }
    fun clearUserData() = viewModelScope.launch { repo.clearUserData() }
}
