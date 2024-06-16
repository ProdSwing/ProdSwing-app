package com.dicoding.prodswing.ui.main

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
import com.dicoding.prodswing.data.firebase.model.Category


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categoryList: List<Category> = emptyList();
    var onItemClickListener: ((Category) -> Unit)? = null

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val imageView: ImageView = itemView.findViewById(R.id.ivCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_category, parent, false)
        return CategoryViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryList(categoryList: List<Category>) {
        this.categoryList = categoryList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.namaCategory.text = category.name

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        holder.imageView.load(category.image) {
            crossfade(true)
            placeholder(circularProgressDrawable)
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(category)
        }
    }

    override fun getItemCount(): Int = categoryList.size
}