package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ItemCartBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.CartLine

class CartAdapter(
    private val onQtyChange: (productId: Int, delta: Int) -> Unit,
    private val onRemove: (productId: Int) -> Unit
) : ListAdapter<CartLine, CartAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(line: CartLine) {
            val p = line.product
            b.tvTitle.text = p.title
            b.tvPrice.text = p.priceText
            b.tvQty.text = line.qty.toString()
            b.imgProduct.load(p.image) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
                error(R.drawable.bg_placeholder)
            }
            b.btnMinus.setOnClickListener { onQtyChange(p.id, -1) }
            b.btnPlus.setOnClickListener { onQtyChange(p.id, 1) }
            b.btnDelete.setOnClickListener { onRemove(p.id) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<CartLine>() {
            override fun areItemsTheSame(a: CartLine, b: CartLine) = a.product.id == b.product.id
            override fun areContentsTheSame(a: CartLine, b: CartLine) = a == b
        }
    }
}
