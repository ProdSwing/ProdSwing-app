package com.c241ps447.prodswing.view.fragment.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c241ps447.prodswing.ViewModelFactory
import com.c241ps447.prodswing.data.Result
import com.c241ps447.prodswing.data.response.ProductsResponseItem
import com.c241ps447.prodswing.databinding.FragmentMainBinding
import com.c241ps447.prodswing.view.adapter.ProductAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainFragmentViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(true)
        lifecycleScope.launch {
            viewModel.getProducts().collectLatest { result ->
                when(result){
                    is Result.Success -> {
                        Log.d("MainFragment API", "setupView: ${result.data}")
                        setProductList(result.data)
                        showLoading(false)
                    }

                    is Result.Error -> TODO()

                    else -> Result.Loading
                }
            }
        }
    }

    private fun setProductList(items: List<ProductsResponseItem?>) {
        val adapter = ProductAdapter()
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