package com.c241ps447.prodswing.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c241ps447.prodswing.data.remote.response.ProductsResponseItem
import com.c241ps447.prodswing.databinding.ItemProductsBinding

class ProductAdapter :
    ListAdapter<ProductsResponseItem, ProductAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listProduct: ProductsResponseItem) {
            binding.apply {
                textName.text = listProduct.productName
                textCategory.text = listProduct.category
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductsResponseItem>() {
            override fun areItemsTheSame(
                oldItem: ProductsResponseItem,
                newItem: ProductsResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductsResponseItem,
                newItem: ProductsResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
