package com.c241ps447.prodswing.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.c241ps447.prodswing.data.response.ProductImageByIdResponseItem
import com.c241ps447.prodswing.databinding.ItemImageSliderBinding

class ProductImageAdapter :
    ListAdapter<ProductImageByIdResponseItem, ProductImageAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageListProduct: ProductImageByIdResponseItem) {
            binding.apply {
                imageSlider.load(imageListProduct.imageURL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductImageByIdResponseItem>() {
            override fun areItemsTheSame(
                oldItem: ProductImageByIdResponseItem,
                newItem: ProductImageByIdResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductImageByIdResponseItem,
                newItem: ProductImageByIdResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}