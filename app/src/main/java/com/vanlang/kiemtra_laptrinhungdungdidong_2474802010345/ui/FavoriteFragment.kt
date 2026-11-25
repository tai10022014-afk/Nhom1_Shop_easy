package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter.ProductAdapter
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.FragmentFavoriteBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ProductAdapter(
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
        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.setHasFixedSize(true)
        (binding.rvFavorites.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        fun render() {
            val ids = viewModel.favoriteIds.value.orEmpty()
            val list = viewModel.products.value.orEmpty().filter { it.id in ids }
            adapter.submitList(list)
            adapter.setFavorites(ids)
            binding.stateEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.favoriteIds.observe(viewLifecycleOwner) { render() }
        viewModel.products.observe(viewLifecycleOwner) { render() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
