package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ActivityDetailBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "product_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        binding.btnBack.setOnClickListener { finish() }

        lifecycleScope.launch {
            val product = viewModel.getProduct(intent.getIntExtra(EXTRA_ID, -1))
            if (product == null) finish() else bind(product)
        }
    }

    private fun bind(product: Product) {
        binding.tvTitle.text = product.title
        binding.tvCategory.text = product.categoryLabel
        binding.tvRating.text = "${product.rate} · ${product.count} đánh giá"
        binding.tvDescription.text = product.description
        binding.tvPrice.text = product.priceText
        binding.imgProduct.load(product.image) {
            crossfade(true)
            placeholder(R.drawable.bg_placeholder)
            error(R.drawable.bg_placeholder)
        }

        binding.btnFavorite.setOnClickListener { viewModel.toggleFavorite(product.id) }
        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart(product)
            Snackbar.make(binding.root, "Đã thêm vào giỏ hàng", Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.bottomBar)
                .show()
        }
        viewModel.favoriteIds.observe(this) { ids ->
            binding.btnFavorite.setImageResource(
                if (product.id in ids) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }
    }
}
