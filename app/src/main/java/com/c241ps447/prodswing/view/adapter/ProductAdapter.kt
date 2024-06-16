package com.c241ps447.prodswing.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c241ps447.prodswing.data.response.ProductsResponseItem
import com.c241ps447.prodswing.databinding.ItemProductsBinding
import com.c241ps447.prodswing.view.activity.detail.DetailProductActivity

class ProductAdapter :
    ListAdapter<ProductsResponseItem, ProductAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listProduct: ProductsResponseItem) {
            binding.apply {
                textName.text = listProduct.productName
                textCategory.text = listProduct.category

                root.setOnClickListener {
                    Intent(root.context, DetailProductActivity::class.java).also {
                        it.putExtra(DetailProductActivity.EXTRA_PRODUCT_ID, listProduct.productID)
                        it.putExtra(DetailProductActivity.EXTRA_PRODUCT_NAME, listProduct.productName)
                        it.putExtra(DetailProductActivity.EXTRA_CATEGORY, listProduct.category)
                        it.putExtra(DetailProductActivity.EXTRA_PRICE, listProduct.price)
                        it.putExtra(DetailProductActivity.EXTRA_DESCRIPTIONS, listProduct.description)
                        it.putExtra(DetailProductActivity.EXTRA_REVIEW, listProduct.review)

                        val optionsCompat: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                itemView.context as Activity,
                                Pair(textName, "name"),
                                Pair(textCategory, "category"),
                            )

                        root.context.startActivity(it, optionsCompat.toBundle())
                    }
                }
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
