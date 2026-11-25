package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter.ProductAdapter
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.FragmentHomeBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Categories
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private enum class Sort(val label: String) {
        DEFAULT("Mặc định"),
        PRICE_ASC("Giá tăng dần"),
        PRICE_DESC("Giá giảm dần"),
        RATING("Đánh giá cao")
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()
    private var category: String? = null
    private var query: String = ""
    private var sort = Sort.DEFAULT
    private var searchJob: Job? = null
    private var shownCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ProductAdapter(
            onClick = { p ->
                startActivity(Intent(requireContext(), DetailActivity::class.java)
                    .putExtra(DetailActivity.EXTRA_ID, p.id))
            },
            onFavorite = { p -> viewModel.toggleFavorite(p.id) },
            onAddToCart = { p ->
                viewModel.addToCart(p)
                Snackbar.make(binding.root, "Đã thêm vào giỏ hàng", Snackbar.LENGTH_SHORT).show()
            }
        )
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.setHasFixedSize(true)
        (binding.rvProducts.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        setupChips()
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            // Debounce: chỉ lọc khi người dùng ngừng gõ 250ms (không lọc + diff mỗi phím)
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(250)
                query = text?.toString().orEmpty().trim()
                applyFilter()
            }
        }
        binding.btnSort.setOnClickListener { showSortMenu() }
        binding.btnRetry.setOnClickListener { viewModel.refresh() }

        // Dữ liệu luôn đọc từ Room (cache), API chỉ làm mới Room
        viewModel.products.observe(viewLifecycleOwner) {
            allProducts = it
            applyFilter()
        }
        viewModel.favoriteIds.observe(viewLifecycleOwner) { adapter.setFavorites(it) }
        viewModel.loading.observe(viewLifecycleOwner) { updateState() }
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                if (allProducts.isNotEmpty()) {
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                        .setAction("Thử lại") { viewModel.refresh() }
                        .show()
                }
                viewModel.clearError()
                updateState()
            }
        }
    }

    private fun setupChips() {
        val options = listOf<Pair<String?, String>>(null to "Tất cả") +
            Categories.all.map { it.key to it.value }
        options.forEachIndexed { index, (key, label) ->
            val chip = layoutInflater.inflate(R.layout.chip_category, binding.chipGroup, false) as Chip
            chip.id = View.generateViewId()
            chip.text = label
            chip.tag = key
            chip.isChecked = index == 0
            binding.chipGroup.addView(chip)
        }
        binding.chipGroup.setOnCheckedStateChangeListener { group, ids ->
            val chip = ids.firstOrNull()?.let { group.findViewById<Chip>(it) }
            category = chip?.tag as? String
            applyFilter()
        }
    }

    private fun showSortMenu() {
        val popup = PopupMenu(requireContext(), binding.btnSort)
        Sort.entries.forEachIndexed { i, s -> popup.menu.add(0, i, i, s.label) }
        popup.setOnMenuItemClickListener { item ->
            sort = Sort.entries[item.itemId]
            binding.btnSort.text = sort.label
            applyFilter()
            true
        }
        popup.show()
    }

    private fun applyFilter() {
        if (_binding == null) return
        var list = allProducts.filter {
            (category == null || it.category == category) &&
                (query.isEmpty() || it.title.contains(query, ignoreCase = true))
        }
        list = when (sort) {
            Sort.DEFAULT -> list
            Sort.PRICE_ASC -> list.sortedBy { it.price }
            Sort.PRICE_DESC -> list.sortedByDescending { it.price }
            Sort.RATING -> list.sortedByDescending { it.rate }
        }
        adapter.submitList(list)
        shownCount = list.size
        updateState()
    }

    private fun updateState() {
        if (_binding == null) return
        val listSize = shownCount
        binding.tvCount.text = "$listSize sản phẩm"

        val loading = viewModel.loading.value == true
        binding.progressBar.visibility = if (loading && allProducts.isEmpty()) View.VISIBLE else View.GONE
        when {
            allProducts.isEmpty() && !loading -> {
                binding.stateEmpty.visibility = View.VISIBLE
                binding.tvEmptyMsg.text = "Không tải được sản phẩm.\nKiểm tra kết nối mạng và thử lại."
                binding.btnRetry.visibility = View.VISIBLE
            }
            allProducts.isNotEmpty() && listSize == 0 -> {
                binding.stateEmpty.visibility = View.VISIBLE
                binding.tvEmptyMsg.text = "Không tìm thấy sản phẩm phù hợp"
                binding.btnRetry.visibility = View.GONE
            }
            else -> binding.stateEmpty.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
