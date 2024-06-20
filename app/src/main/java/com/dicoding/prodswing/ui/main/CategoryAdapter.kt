package com.dicoding.prodswing.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.prodswing.databinding.ItemLayoutCategoryBinding
import com.dicoding.prodswing.ui.product.TrendingProductActivity

data class CategoryList(
    val categoryName: String
)
class CategoryAdapter(private val listCategory: ArrayList<CategoryList>): RecyclerView.Adapter<CategoryAdapter.ListViewHolder>(){
    class ListViewHolder(var binding: ItemLayoutCategoryBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemLayoutCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listCategory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val category  = listCategory[position]
        holder.binding.tvCategory.text = category.categoryName
        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, TrendingProductActivity::class.java).also {
                it.putExtra(TrendingProductActivity.CATEGORY_ID, category.categoryName)
                holder.binding.root.context.startActivity(it)
            }
        }
//        holder.imgPhoto.setImageResource(photo)
//        holder.tvName.text = name
//        holder.tvDescription.text = description
    }

}
//class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
//
//    private var categoryList: List<Category> = emptyList();
//    var onItemClickListener: ((Category) -> Unit)? = null
//
//    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val namaCategory: TextView = itemView.findViewById(R.id.tvCategory)
//        val imageView: ImageView = itemView.findViewById(R.id.ivCategory)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_category, parent, false)
//        return CategoryViewHolder(view)
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun setCategoryList(categoryList: List<Category>) {
//        this.categoryList = categoryList
//        notifyDataSetChanged()
//    }
//
//    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
//        val category = categoryList[position]
//        holder.namaCategory.text = category.name
//
//        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
//        circularProgressDrawable.strokeWidth = 5f
//        circularProgressDrawable.centerRadius = 30f
//        circularProgressDrawable.start()
//        holder.imageView.load(category.image) {
//            crossfade(true)
//            placeholder(circularProgressDrawable)
//        }
//
//        holder.itemView.setOnClickListener {
//            onItemClickListener?.invoke(category)
//        }
//    }
//
//    override fun getItemCount(): Int = categoryList.size
//}