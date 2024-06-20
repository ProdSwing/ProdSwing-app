package com.dicoding.prodswing.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.prodswing.R
import com.dicoding.prodswing.ViewModelFactory
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.ActivityTrendingProductsBinding
import com.dicoding.prodswing.ui.main.SentimentAdapter
import com.dicoding.prodswing.ui.sign_in.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrendingProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrendingProductsBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val productAdapter: ProductAdapter by lazy { ProductAdapter() }

    private lateinit var categoryId: String
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val searchQuery: MutableLiveData<String> = MutableLiveData()

    private val viewModel by viewModels<ProductViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
//        categoryId = intent.getStringExtra(CATEGORY_ID).orEmpty()

        fetchUser()
        intent.getStringExtra(CATEGORY_ID).let {categoryName ->
            lifecycleScope.launch {
                viewModel.getProductByCategory(categoryName!!)
                    .collectLatest { result ->
                        when (result) {
                            is com.dicoding.prodswing.data.Result.Success -> {
                                Log.d("MainFragment API", "setupView: ${result.data}")
                                setProductList(result.data)
                        showLoading(false)
                            }

                            is com.dicoding.prodswing.data.Result.Error -> TODO()

                            else -> com.dicoding.prodswing.data.Result.Loading
                        }
                    }
            }
        }
//        setupListeners()
//        setupRecyclerView()
//        fetchDataAndFilter()
//        fetchProducts()
//        observeData()
    }

    private fun setProductList(items: List<ProductResponse?>) {
        val adapter = SentimentAdapter()
        adapter.submitList(items)
        with(binding) {
            rvProduct.layoutManager = LinearLayoutManager(this@TrendingProductActivity)
            rvProduct.adapter = adapter
            rvProduct.setHasFixedSize(true)
        }
    }

    private fun fetchUser() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        currentUser?.let {
            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            binding.ivProfile.load(it.photoUrl.toString()) {
                crossfade(true)
                placeholder(circularProgressDrawable)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_placeholder_avatar)
            }
        }
    }

//    private fun setupListeners() {
//        binding.searchView.setOnQueryTextListener(object :
//            androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchQuery.value = query.orEmpty()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                searchQuery.value = newText.orEmpty()
//                return true
//            }
//        })
//    }
//
//    private fun observeData() {
//        isLoading.observe(this) {
//            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
//        }
//        searchQuery.observe(this) {
//            fetchDataAndFilter()
//        }
//    }
//
////    private fun fetchProducts() {
////        lifecycleScope.launch {
////            viewModel.getProducts().collectLatest { result ->
////                when (result) {
////                    is com.dicoding.prodswing.data.Result.Success -> {
////                        Log.d("MainFragment API", "setupView: ${result.data}")
////                        setProductList(result.data)
////                        isLoading.value = false
//////                        showLoading(false)
////                    }
////
////                    is com.dicoding.prodswing.data.Result.Error -> TODO()
////
////                    else -> com.dicoding.prodswing.data.Result.Loading
////                }
////            }
////        }
////    }
////
////    private fun setProductList(items: List<ProductResponse?>) {
////        val adapter = ProductAdapter()
////        adapter.submitList(items)
////        with(binding) {
////            rvProduct.layoutManager = LinearLayoutManager(this@TrendingProductActivity)
////            rvProduct.adapter = adapter
////            rvProduct.setHasFixedSize(true)
////        }
////    }
//
//    private fun fetchDataAndFilter() {
//        isLoading.value = true
//        lifecycleScope.launch(Dispatchers.IO) {
//            firebaseFirestore.collection("products")
//                .orderBy("name")
//                .whereEqualTo("categoryId", categoryId)
//                .get()
//                .addOnSuccessListener { result ->
//                    isLoading.value = false
//                    try {
//                        val listProduct = result.toObjects(Product::class.java)
//                        val filteredProducts = if (searchQuery.value.isNullOrEmpty()) {
//                            listProduct
//                        } else {
//                            val normalizedSearchQuery = searchQuery.value!!.lowercase(Locale.ROOT)
//                            listProduct.filter { product ->
//                                product.name?.lowercase(Locale.ROOT)?.contains(normalizedSearchQuery)
//                                    ?: false
//                            }
//                        }
//                        productAdapter.setProductList(filteredProducts)
//                    } catch (e: Exception) {
//                        Log.e("fetchDataAndFilter", "Error fetching data: ${e.message}", e)
//                        runOnUiThread {
//                            Toast.makeText(this@TrendingProductActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    isLoading.value = false
//                    Log.e("fetchDataAndFilter", "Error fetching data: ${exception.message}", exception)
//                    runOnUiThread {
//                        Toast.makeText(this@TrendingProductActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//    private fun setupRecyclerView() {
//        binding.rvProduct.layoutManager = LinearLayoutManager(this)
//        binding.rvProduct.isNestedScrollingEnabled = false
//        binding.rvProduct.adapter = productAdapter
//
//        productAdapter.onItemClickListener = { product ->
////            val intent = Intent(this, ProductActivity::class.java)
//            Intent(this, ProductActivity::class.java).also {
//                it.putExtra(ProductActivity.EXTRA_IMAGETHUMBNAIL, product.imageThumbnail)
//                it.putExtra(ProductActivity.EXTRA_ID, product.id)
//                it.putExtra(ProductActivity.EXTRA_NAME, product.name)
//                it.putExtra(ProductActivity.EXTRA_CATEGORY_ID, product.categoryId)
//                it.putExtra(ProductActivity.EXTRA_PRICE, product.price)
//                it.putExtra(ProductActivity.EXTRA_DESCRIPTIONS, product.description)
//                it.putStringArrayListExtra(ProductActivity.EXTRA_IMAGEPRODUCT, product.imageProduct)
////                it.putExtra(ProductActivity.EXTRA_PRODUCT, product)
//                startActivity(it)
//            }
//        }
//    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    companion object {
        const val CATEGORY_ID = "category_id"
    }
}