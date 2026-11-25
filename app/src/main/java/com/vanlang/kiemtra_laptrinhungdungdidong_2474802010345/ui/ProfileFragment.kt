package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.FragmentProfileBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ProfileViewModel
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val account = profileViewModel.account
        val name = account.name
        val email = account.email
        val uid = account.uid

        binding.tvName.text = name
        binding.tvEmail.text = email
        binding.imgAvatar.load(account.avatarUrl) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.bg_circle_white)
            error(R.drawable.bg_circle_white)
        }

        viewModel.cart.observe(viewLifecycleOwner) { lines ->
            binding.tvCartCount.text = lines.sumOf { it.qty }.toString()
        }
        viewModel.favoriteIds.observe(viewLifecycleOwner) { binding.tvFavCount.text = it.size.toString() }

        binding.rowAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thông tin tài khoản")
                .setMessage("Tên hiển thị: $name\nEmail: $email\nMã tài khoản: $uid")
                .setPositiveButton("Đóng", null)
                .show()
        }

        binding.rowOrders.setOnClickListener {
            val orders = viewModel.orders.value.orEmpty()
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dialog = MaterialAlertDialogBuilder(requireContext()).setTitle("Đơn hàng của tôi")
            if (orders.isEmpty()) {
                dialog.setMessage("Bạn chưa có đơn hàng nào.")
            } else {
                val items = orders.map {
                    "#${it.id} · ${fmt.format(Date(it.createdAt))}\n${it.itemCount} sản phẩm · " +
                        String.format(Locale.US, "$%.2f", it.total)
                }.toTypedArray()
                dialog.setItems(items, null)
            }
            dialog.setPositiveButton("Đóng", null).show()
        }

        binding.rowSettings.setOnClickListener {
            val options = arrayOf("Làm mới dữ liệu sản phẩm", "Xóa lịch sử đơn hàng")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cài đặt ứng dụng")
                .setItems(options) { _, which ->
                    if (which == 0) {
                        viewModel.refresh()
                        Snackbar.make(binding.root, "Đang làm mới dữ liệu...", Snackbar.LENGTH_SHORT).show()
                    } else {
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Xóa toàn bộ lịch sử đơn hàng?")
                            .setPositiveButton("Xóa") { _, _ -> viewModel.clearOrders() }
                            .setNegativeButton("Hủy", null)
                            .show()
                    }
                }
                .setNegativeButton("Đóng", null)
                .show()
        }

        binding.btnLogout.setOnClickListener {
            profileViewModel.logout()
            viewModel.clearUserData()
            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
