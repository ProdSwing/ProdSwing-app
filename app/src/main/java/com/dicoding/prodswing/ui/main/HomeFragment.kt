package com.dicoding.prodswing.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.prodswing.R
import com.dicoding.prodswing.databinding.HomeCategoryBinding
import com.dicoding.prodswing.model.Category
import com.dicoding.prodswing.ui.product.TrendingProductActivity
import com.dicoding.prodswing.ui.sign_in.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: HomeCategoryBinding? = null
    private val binding get() = _binding!!
    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter() }
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val searchQuery: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = HomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUser()
        setupListeners()
        setupRecyclerView()
        fetchDataAndFilter()
        observeData()
    }

    private fun fetchUser() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
        }

        currentUser?.let {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
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
        isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        searchQuery.observe(viewLifecycleOwner) {
            fetchDataAndFilter()
        }
    }

    private fun fetchDataAndFilter() {
        isLoading.value = true
        lifecycleScope.launch(Dispatchers.IO) {
            firebaseFirestore.collection("categories")
                .orderBy("name")
                .get()
                .addOnSuccessListener { result ->
                    val listCategory = result.toObjects(Category::class.java)
                    val filteredCategories = if (searchQuery.value.isNullOrEmpty()) {
                        listCategory
                    } else {
                        val normalizedSearchQuery = searchQuery.value!!.toLowerCase(Locale.ROOT)
                        listCategory.filter { category ->
                            category.name?.lowercase(Locale.ROOT)?.contains(normalizedSearchQuery)
                                ?: false
                        }
                    }
                    isLoading.value = false
                    categoryAdapter.setCategoryList(filteredCategories)
                }
                .addOnFailureListener { exception ->
                    isLoading.value = false
                    Log.e("fetchDataAndFilter", "Error fetching data: ${exception.message}")
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategory.isNestedScrollingEnabled = false
        binding.rvCategory.adapter = categoryAdapter

        categoryAdapter.onItemClickListener = { category ->
            val intent = Intent(requireContext(), TrendingProductActivity::class.java)
            intent.putExtra(TrendingProductActivity.CATEGORY_ID, category.id)
            startActivity(intent)
        }
    }
}
