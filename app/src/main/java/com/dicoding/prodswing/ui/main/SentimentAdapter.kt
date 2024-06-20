package com.dicoding.prodswing.ui.main

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.ItemLayoutProductBinding
import com.dicoding.prodswing.ui.product.ProductActivity

class SentimentAdapter :
    ListAdapter<ProductResponse, SentimentAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemLayoutProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listProduct: ProductResponse) {
            binding.apply {
//                ivProduct.load(listProduct.imageThumbnail)
                tvProduct.text = listProduct.name
                tvResult.text = listProduct.result
                if (tvResult.text == "Positive") tvResult.setTextColor(Color.GREEN) else tvResult.setTextColor(Color.RED)
//                tvProduct.text = listProduct.name
//                textName.text = listProduct.productName
//                textCategory.text = listProduct.category
//
                root.setOnClickListener {
                    Intent(root.context, ProductActivity::class.java).also {
//                        it.putExtra(ProductActivity.EXTRA, listProduct.productID)
//                        it.putExtra(ProductActivity.EXTRA_ID, listProduct.name)
                        it.putExtra(ProductActivity.EXTRA_NAME, listProduct.name)
                        it.putExtra(ProductActivity.EXTRA_RESULT, listProduct.result)
                        it.putExtra(ProductActivity.EXTRA_CATEGORY, listProduct.category)
//                        it.putExtra(ProductActivity.EXTRA_CATEGORY_ID, listProduct.categoryId)
//                        it.putExtra(ProductActivity.EXTRA_PRICE, listProduct.price)
//                        it.putExtra(ProductActivity.EXTRA_DESCRIPTIONS, listProduct.description)
//                        it.putExtra(ProductActivity.EXTRA_REVIEW, listProduct.review)

//                        it.putExtra(ProductActivity.EXTRA_IMAGETHUMBNAIL, listProduct.imageThumbnail)
//                        it.putStringArrayListExtra(ProductActivity.EXTRA_IMAGEPRODUCT, listProduct.imageProduct)

//                        val optionsCompat: ActivityOptionsCompat =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                itemView.context as Activity,
//                                Pair(textName, "name"),
//                                Pair(textCategory, "category"),
//                            )
//
//                        root.context.startActivity(it, optionsCompat.toBundle())
                        root.context.startActivity(it)
                    }
                }
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