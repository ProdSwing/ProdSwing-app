package com.dicoding.prodswing.ui.product

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.prodswing.data.firebase.model.Category
import com.dicoding.prodswing.data.firebase.model.Product
import com.dicoding.prodswing.data.firebase.model.Review
import com.dicoding.prodswing.databinding.ActivityProductBinding
import com.dicoding.prodswing.databinding.ItemLayoutReviewBinding
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
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        firebaseFirestore = FirebaseFirestore.getInstance()
        product = Product(
            intent.getStringExtra(EXTRA_ID),
            intent.getStringExtra(EXTRA_NAME),
            intent.getLongExtra(EXTRA_PRICE, 0),
            intent.getStringExtra(EXTRA_CATEGORY),
            intent.getStringExtra(EXTRA_DESCRIPTIONS),
            intent.getStringExtra(EXTRA_IMAGETHUMBNAIL),
            intent.getStringArrayListExtra(EXTRA_IMAGEPRODUCT)
        )

        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupOnListener()
//        showProductData()
//        getProductCategory(product?.categoryId)
//        getProductReviews()
    }

    private fun setupOnListener() {
        binding.apply {
            topAppBar.setNavigationOnClickListener { onBackPressed() }

            productName.text = intent.getStringExtra(EXTRA_NAME)
            productCategory.text = intent.getStringExtra(EXTRA_CATEGORY)
            if (intent.getStringExtra(EXTRA_RESULT) == "Positive") productResult.setTextColor(Color.GREEN)
            else productResult.setTextColor(Color.RED)
            productResult.text = intent.getStringExtra(EXTRA_RESULT)
        }
    }

//    private fun getProductReviews() {
//        firebaseFirestore.collection("products")
//            .document(product?.id.orEmpty())
//            .collection("reviews")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val review = document.toObject(Review::class.java)
//                    val reviewBinding = ItemLayoutReviewBinding.inflate(layoutInflater)
//                    reviewBinding.ratingBar.rating = review.rating ?: 0f
//                    reviewBinding.textReview.text = review.review
//                    binding.reviewContainer.addView(reviewBinding.root)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this@ProductActivity, "Failed to get reviews: $exception", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun showProductData() {
//        binding.apply {
//            binding.productName.text = product?.name
//            binding.productDescription.text = product?.description
//            binding.productPrice.text = formatCurrency(product?.price ?: 0L)
//
//            val carouselItems = product?.imageProduct?.map {
//                CarouselItem(it)
//            } ?: emptyList()
//            binding.productImageCarousel.addData(carouselItems)
//        }
//    }
//
//    private fun formatCurrency(price: Long): String {
//        val locale = Locale("in", "ID")
//        val numberFormat = NumberFormat.getCurrencyInstance(locale)
//        return numberFormat.format(price)
//    }
//
//    private fun getProductCategory(categoryId: String?) {
//        firebaseFirestore.collection("categories")
//            .document(categoryId.orEmpty())
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val category = document.toObject(Category::class.java)
//                    binding.productCategory.text = category?.name
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Failed to get category: $exception", Toast.LENGTH_SHORT).show()
//            }
//    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
        const val EXTRA_IMAGEPRODUCT = "extra_image_product"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTIONS = "extra_desc"
        const val EXTRA_CATEGORY_ID = "extra_category_id"
        const val EXTRA_IMAGETHUMBNAIL = "extra_image_thumbnail"

        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CATEGORY = "extra_category"

        const val EXTRA_ID = "extra_id"
    }
}