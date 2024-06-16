package com.dicoding.prodswing.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.ItemLayoutProductBinding


class ProductAdapter :
    ListAdapter<ProductResponse, ProductAdapter.MyViewHolder>(DIFF_CALLBACK) {
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
        val binding = ItemLayoutProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

//class ProductAdapter : RecyclerView.Adapter<ProductAdapter.CategoryViewHolder>() {
//
//    private var productList: List<Product> = emptyList()
//    var onItemClickListener: ((Product) -> Unit)? = null
//
//    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val namaProduct: TextView = itemView.findViewById(R.id.tvProduct)
//        val imageView: ImageView = itemView.findViewById(R.id.ivProduct)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_product, parent, false)
//        return CategoryViewHolder(view)
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun setProductList(productList: List<Product>) {
//        this.productList = productList
//        notifyDataSetChanged()
//    }
//
//    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
//        val product = productList[position]
//        holder.namaProduct.text = product.name
//
//        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
//        circularProgressDrawable.strokeWidth = 5f
//        circularProgressDrawable.centerRadius = 30f
//        circularProgressDrawable.start()
//        holder.imageView.load(product.imageThumbnail) {
//            crossfade(true)
//            placeholder(circularProgressDrawable)
//        }
//
//        holder.itemView.setOnClickListener {
//            onItemClickListener?.invoke(product)
//        }
//    }
//
//    override fun getItemCount(): Int = productList.size
//}