package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ActivityMainBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        // Tải sản phẩm từ API -> Room đúng 1 lần cho cả phiên làm việc
        viewModel.start()

        binding.bottomNav.setOnItemSelectedListener { item ->
            showTab(item.itemId)
            true
        }
        binding.bottomNav.setOnItemReselectedListener { /* đang ở tab này rồi: không làm gì */ }
        if (savedInstanceState == null) binding.bottomNav.selectedItemId = R.id.nav_home

        // Badge số lượng trên tab Giỏ hàng
        viewModel.cart.observe(this) { lines ->
            val count = lines.sumOf { it.qty }
            val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_cart)
            badge.isVisible = count > 0
            badge.number = count
        }
    }

    /**
     * Giữ nguyên các tab đã mở (show/hide) thay vì replace: chuyển tab không phải inflate lại layout,
     * nạp lại ảnh, và vẫn giữ vị trí cuộn/ô tìm kiếm. Tab nào chưa mở thì chưa tạo.
     */
    private fun showTab(itemId: Int) {
        val tag = tagOf(itemId)
        val fm = supportFragmentManager
        val tx = fm.beginTransaction().setReorderingAllowed(true)
        fm.fragments.forEach { if (it.tag != tag) tx.hide(it) }
        val existing = fm.findFragmentByTag(tag)
        if (existing == null) tx.add(R.id.container, createTab(itemId), tag) else tx.show(existing)
        tx.commit()
    }

    private fun tagOf(itemId: Int) = when (itemId) {
        R.id.nav_favorite -> "tab_favorite"
        R.id.nav_cart -> "tab_cart"
        R.id.nav_profile -> "tab_profile"
        else -> "tab_home"
    }

    private fun createTab(itemId: Int): Fragment = when (itemId) {
        R.id.nav_favorite -> FavoriteFragment()
        R.id.nav_cart -> CartFragment()
        R.id.nav_profile -> ProfileFragment()
        else -> HomeFragment()
    }
}
