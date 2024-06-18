package com.dicoding.prodswing.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.ItemLayoutProductBinding

class SentimentAdapter :
    ListAdapter<ProductResponse, SentimentAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemLayoutProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listProduct: ProductResponse) {
            binding.apply {
                ivProduct.load(listProduct.imageThumbnail)
                tvProduct.text = listProduct.name
//                textName.text = listProduct.productName
//                textCategory.text = listProduct.category
//
//                root.setOnClickListener {
//                    Intent(root.context, DetailProductActivity::class.java).also {
//                        it.putExtra(DetailProductActivity.EXTRA_PRODUCT_ID, listProduct.productID)
//                        it.putExtra(DetailProductActivity.EXTRA_PRODUCT_NAME, listProduct.productName)
//                        it.putExtra(DetailProductActivity.EXTRA_CATEGORY, listProduct.category)
//                        it.putExtra(DetailProductActivity.EXTRA_PRICE, listProduct.price)
//                        it.putExtra(DetailProductActivity.EXTRA_DESCRIPTIONS, listProduct.description)
//                        it.putExtra(DetailProductActivity.EXTRA_REVIEW, listProduct.review)
//
//                        val optionsCompat: ActivityOptionsCompat =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                itemView.context as Activity,
//                                Pair(textName, "name"),
//                                Pair(textCategory, "category"),
//                            )
//
//                        root.context.startActivity(it, optionsCompat.toBundle())
//                    }
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemLayoutProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductResponse>() {
            override fun areItemsTheSame(
                oldItem: ProductResponse,
                newItem: ProductResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductResponse,
                newItem: ProductResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}