package com.dicoding.prodswing.ui.product

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.dicoding.prodswing.R
import com.dicoding.prodswing.data.firebase.model.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.CategoryViewHolder>() {

    private var productList: List<Product> = emptyList()
    var onItemClickListener: ((Product) -> Unit)? = null

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaProduct: TextView = itemView.findViewById(R.id.tvProduct)
        val imageView: ImageView = itemView.findViewById(R.id.ivProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_product, parent, false)
        return CategoryViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProductList(productList: List<Product>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val product = productList[position]
        holder.namaProduct.text = product.name

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        holder.imageView.load(product.imageThumbnail) {
            crossfade(true)
            placeholder(circularProgressDrawable)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(product)
        }
    }

    override fun getItemCount(): Int = productList.size
}