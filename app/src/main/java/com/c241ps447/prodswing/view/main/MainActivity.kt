package com.c241ps447.prodswing.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241ps447.prodswing.R
import com.c241ps447.prodswing.ViewModelFactory
import com.c241ps447.prodswing.data.Result
import com.c241ps447.prodswing.data.response.ProductsResponseItem
import com.c241ps447.prodswing.databinding.ActivityMainBinding
import com.c241ps447.prodswing.view.adapter.ProductAdapter
import com.c241ps447.prodswing.view.signin.SignInActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        setupView()
    }

    private fun setupView() {
        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId){
                    R.id.logout -> {
                        //handle logout

                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
        lifecycleScope.launch {
            viewModel.getProducts().collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        Log.d("MainActivity API", "setupView: ${result.data}")
                        setProductList(result.data)
                        showLoading(false)
                    }

                    is Result.Error -> Toast.makeText(
                        this@MainActivity,
                        "error in Activity",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
        showLoading(false)
    }

    private fun setProductList(items: List<ProductsResponseItem?>) {
        val adapter = ProductAdapter()
        adapter.submitList(items)
        with(binding) {
            rvListStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStories.adapter = adapter
            rvListStories.setHasFixedSize(true)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }
}