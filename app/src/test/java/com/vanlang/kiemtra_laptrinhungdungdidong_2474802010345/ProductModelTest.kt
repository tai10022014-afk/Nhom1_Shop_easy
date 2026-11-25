package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345

import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartLine
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Categories
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.ProductDto
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.RatingDto
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.toEntity
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.toProduct
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductModelTest {

    private val sample = Product(1, "Áo", 12.5, "mô tả", "electronics", "img", 4.5, 120)

    @Test
    fun priceAndRating_areFormatted() {
        assertEquals("$12.50", sample.priceText)
        assertEquals("4.5 (120)", sample.ratingText)
    }

    @Test
    fun cartLine_totalIsPriceTimesQty() {
        assertEquals(37.5, CartLine(sample, 3).lineTotal, 0.0001)
    }

    @Test
    fun categories_unknownKeyFallsBackToKey() {
        assertEquals("Điện tử", Categories.labelOf("electronics"))
        assertEquals("other", Categories.labelOf("other"))
    }

    @Test
    fun dto_mapsToEntityAndBack_withMissingRating() {
        val dto = ProductDto(7, "Nhẫn", 99.0, "d", "jewelery", "u", RatingDto(null, null))
        val product = dto.toEntity().toProduct()
        assertEquals(7, product.id)
        assertEquals(0.0, product.rate, 0.0)
        assertEquals(0, product.count)
    }
}
