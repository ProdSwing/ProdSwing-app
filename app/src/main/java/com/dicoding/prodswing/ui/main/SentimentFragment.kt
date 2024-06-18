package com.dicoding.prodswing.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.prodswing.ViewModelFactory
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.FragmentSentimentBinding
import com.dicoding.prodswing.ui.product.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SentimentFragment : Fragment() {
    private var _binding: FragmentSentimentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProductViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSentimentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(true)
        lifecycleScope.launch {
            viewModel.getProducts().collectLatest { result ->
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

    private fun setProductList(items: List<ProductResponse?>) {
        val adapter = SentimentAdapter()
        adapter.submitList(items)
        with(binding) {
            rvListStories.layoutManager = LinearLayoutManager(requireActivity())
            rvListStories.adapter = adapter
            rvListStories.setHasFixedSize(true)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }
}