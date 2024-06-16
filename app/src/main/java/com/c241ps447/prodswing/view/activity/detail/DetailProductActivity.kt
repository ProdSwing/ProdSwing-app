package com.c241ps447.prodswing.view.activity.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241ps447.prodswing.ViewModelFactory
import com.c241ps447.prodswing.data.Result
import com.c241ps447.prodswing.data.response.ProductImageByIdResponseItem
import com.c241ps447.prodswing.databinding.ActivityDetailProductBinding
import com.c241ps447.prodswing.view.adapter.ProductImageAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductBinding
    private val viewModel by viewModels<DetailProductViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME)
        val productCategory = intent.getStringExtra(EXTRA_CATEGORY)
        val productPrice = intent.getDoubleExtra(EXTRA_PRICE, 0.0)
        val productDescription = intent.getStringExtra(EXTRA_DESCRIPTIONS)
        val productReview = intent.getStringExtra(EXTRA_REVIEW)

        binding.apply {
            productName.let { name ->
                detailProductName.text = name
            }

            productCategory.let { category ->
                detailProductCategory.text = category
            }

            productPrice.let { price ->
//                detailProductPrice.text = price.toString()
                detailProductPrice.text = currencyFormatter(price)
            }

            productDescription.let { desc ->
                detailProductDescription.text = desc
            }
        }

        lifecycleScope.launch {
            Log.d(TAG, "setupView: $productId")
            viewModel.getProductImageById(productId!!).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "setupView: ${result.data}")
                        setHorizontalImageList(result.data)
                    }

                    else -> false
                }
            }
        }
    }

    private fun setHorizontalImageList(items: List<ProductImageByIdResponseItem?>) {
        val adapter = ProductImageAdapter()
        adapter.submitList(items)
        with(binding) {
            rvImageSlider.layoutManager = LinearLayoutManager(
                this@DetailProductActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvImageSlider.adapter = adapter
            rvImageSlider.setHasFixedSize(true)
        }
    }

    private fun currencyFormatter(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")) // Locale for Indonesia

        // Format a number
        val formattedAmount = formatter.format(value) // Outputs: Rp1.234.567,89 (Indonesian Rupiah format)

        return formattedAmount
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_DESCRIPTIONS = "extra_description"
        const val EXTRA_REVIEW = "extra_review"
        private const val TAG = "DetailProductActivity"
    }
}