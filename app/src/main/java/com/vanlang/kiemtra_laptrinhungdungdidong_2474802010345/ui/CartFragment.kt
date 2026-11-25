package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter.CartAdapter
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.FragmentCartBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = CartAdapter(
            onQtyChange = { id, delta -> viewModel.changeQty(id, delta) },
            onRemove = { id -> viewModel.removeFromCart(id) }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter
        binding.rvCart.setHasFixedSize(true)
        (binding.rvCart.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        viewModel.cart.observe(viewLifecycleOwner) { lines ->
            adapter.submitList(lines)
            val empty = lines.isEmpty()
            binding.stateEmpty.visibility = if (empty) View.VISIBLE else View.GONE
            binding.summaryBar.visibility = if (empty) View.GONE else View.VISIBLE
            val text = String.format(Locale.US, "$%.2f", lines.sumOf { it.lineTotal })
            binding.tvSubtotal.text = text
            binding.tvTotal.text = text
        }

        binding.btnCheckout.setOnClickListener {
            viewModel.checkout()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đặt hàng thành công")
                .setMessage("Cảm ơn bạn đã mua sắm tại ShopEasy! Đơn hàng đã được lưu trong mục \"Đơn hàng của tôi\" (giả lập).")
                .setPositiveButton("Đóng", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
