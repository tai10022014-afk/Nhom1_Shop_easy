package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model

import java.util.Locale

/** Sản phẩm (cấu trúc tương thích JSON của FakeStore API). */
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rate: Double,
    val count: Int
) {
    val priceText: String get() = String.format(Locale.US, "$%.2f", price)
    val ratingText: String get() = String.format(Locale.US, "%.1f (%d)", rate, count)
    val categoryLabel: String get() = Categories.labelOf(category)
}

/** Dòng trong giỏ hàng. */
data class CartLine(val product: Product, val qty: Int) {
    val lineTotal: Double get() = product.price * qty
}

object Categories {
    /** key (theo API) -> nhãn hiển thị */
    val all = linkedMapOf(
        "men's clothing" to "Thời trang nam",
        "women's clothing" to "Thời trang nữ",
        "jewelery" to "Trang sức",
        "electronics" to "Điện tử"
    )

    fun labelOf(key: String) = all[key] ?: key
}
