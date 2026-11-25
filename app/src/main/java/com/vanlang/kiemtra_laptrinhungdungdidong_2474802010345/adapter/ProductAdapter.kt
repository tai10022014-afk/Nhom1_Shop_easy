package com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.R
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.databinding.ItemProductBinding
import com.vanlang.kiemtra_laptrinhungdungdidong_2474802010345.model.Product

class ProductAdapter(
    private val onClick: (Product) -> Unit,
    private val onFavorite: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.VH>(DIFF) {

    private var favorites: Set<Int> = emptySet()

    fun setFavorites(ids: Set<Int>) {
        if (ids == favorites) return
        val changed = (favorites - ids) + (ids - favorites)
        favorites = ids
        currentList.forEachIndexed { index, p ->
            if (p.id in changed) notifyItemChanged(index, PAYLOAD_FAVORITE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_FAVORITE)) holder.bindFavorite(getItem(position))
        else super.onBindViewHolder(holder, position, payloads)
    }

    inner class VH(private val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(p: Product) {
            b.tvTitle.text = p.title
            b.tvPrice.text = p.priceText
            b.tvRating.text = p.ratingText
            b.imgProduct.load(p.image) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
                error(R.drawable.bg_placeholder)
            }
            bindFavorite(p)
            b.root.setOnClickListener { onClick(p) }
            b.btnFavorite.setOnClickListener { onFavorite(p) }
            b.btnAdd.setOnClickListener { onAddToCart(p) }
        }

        fun bindFavorite(p: Product) {
            b.btnFavorite.setImageResource(
                if (p.id in favorites) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }
    }

    private companion object {
        const val PAYLOAD_FAVORITE = "favorite"
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
            override fun areContentsTheSame(a: Product, b: Product) = a == b
        }
    }
}
