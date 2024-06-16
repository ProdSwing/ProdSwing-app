package com.dicoding.prodswing.ui.product

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.prodswing.databinding.ActivityProductBinding
import com.dicoding.prodswing.databinding.ItemLayoutReviewBinding
import com.dicoding.prodswing.model.Category
import com.dicoding.prodswing.model.Product
import com.dicoding.prodswing.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.NumberFormat
import java.util.Locale

class ProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        product = intent.getParcelableExtra(EXTRA_PRODUCT, Product::class.java)

        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        showProductData()
        getProductCategory(product?.categoryId)
        getProductReviews()
    }

    private fun getProductReviews() {
        firebaseFirestore.collection("products")
            .document(product?.id.orEmpty())
            .collection("reviews")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    val reviewBinding = ItemLayoutReviewBinding.inflate(layoutInflater)
                    reviewBinding.ratingBar.rating = review.rating ?: 0f
                    reviewBinding.textReview.text = review.review
                    binding.reviewContainer.addView(reviewBinding.root)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get reviews: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProductData() {
        binding.apply {
            binding.productName.text = product?.name
            binding.productDescription.text = product?.description
            binding.productPrice.text = formatCurrency(product?.price ?: 0L)

            val carouselItems = product?.imageProduct?.map {
                CarouselItem(it)
            } ?: emptyList()
            binding.productImageCarousel.addData(carouselItems)
        }
    }

    private fun formatCurrency(price: Long): String {
        val locale = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        return numberFormat.format(price)
    }

    private fun getProductCategory(categoryId: String?) {
        firebaseFirestore.collection("categories")
            .document(categoryId.orEmpty())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val category = document.toObject(Category::class.java)
                    binding.productCategory.text = category?.name
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to get category: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}