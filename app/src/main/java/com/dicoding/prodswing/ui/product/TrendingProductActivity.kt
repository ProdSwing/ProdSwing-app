package com.dicoding.prodswing.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.prodswing.R
import com.dicoding.prodswing.databinding.ActivityTrendingProductsBinding
import com.dicoding.prodswing.model.Product
import com.dicoding.prodswing.ui.sign_in.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class TrendingProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrendingProductsBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private val productAdapter: ProductAdapter by lazy { ProductAdapter() }

    private lateinit var categoryId: String
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val searchQuery: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        categoryId = intent.getStringExtra(CATEGORY_ID).orEmpty()

        fetchUser()
        setupListeners()
        setupRecyclerView()
        fetchDataAndFilter()
        observeData()
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

    private fun setupListeners() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery.value = query.orEmpty()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText.orEmpty()
                return true
            }
        })
    }

    private fun observeData() {
        isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        searchQuery.observe(this) {
            fetchDataAndFilter()
        }
    }

    private fun fetchDataAndFilter() {
        isLoading.value = true
        lifecycleScope.launch(Dispatchers.IO) {
            firebaseFirestore.collection("products")
                .orderBy("name")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener { result ->
                    isLoading.value = false
                    try {
                        val listProduct = result.toObjects(Product::class.java)
                        val filteredProducts = if (searchQuery.value.isNullOrEmpty()) {
                            listProduct
                        } else {
                            val normalizedSearchQuery = searchQuery.value!!.lowercase(Locale.ROOT)
                            listProduct.filter { product ->
                                product.name?.lowercase(Locale.ROOT)?.contains(normalizedSearchQuery)
                                    ?: false
                            }
                        }
                        productAdapter.setProductList(filteredProducts)
                    } catch (e: Exception) {
                        Log.e("fetchDataAndFilter", "Error fetching data: ${e.message}", e)
                        runOnUiThread {
                            Toast.makeText(this@TrendingProductActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    isLoading.value = false
                    Log.e("fetchDataAndFilter", "Error fetching data: ${exception.message}", exception)
                    runOnUiThread {
                        Toast.makeText(this@TrendingProductActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rvProduct.layoutManager = LinearLayoutManager(this)
        binding.rvProduct.isNestedScrollingEnabled = false
        binding.rvProduct.adapter = productAdapter

        productAdapter.onItemClickListener = { product ->
            val intent = Intent(this, ProductActivity::class.java)
            intent.putExtra(ProductActivity.EXTRA_PRODUCT, product)
            startActivity(intent)
        }
    }

    companion object {
        const val CATEGORY_ID = "category_id"
    }
}