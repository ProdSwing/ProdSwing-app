package com.dicoding.prodswing.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.prodswing.R
import com.dicoding.prodswing.ViewModelFactory
import com.dicoding.prodswing.data.Result
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import com.dicoding.prodswing.databinding.HomeCategoryBinding
import com.dicoding.prodswing.ui.product.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: HomeCategoryBinding? = null
    private val binding get() = _binding!!
//    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter() }
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val searchQuery: MutableLiveData<String> = MutableLiveData()

    private val list = ArrayList<CategoryList>()

    private val viewModel by viewModels<ProductViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

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

        binding.apply {
            rvCategory.setHasFixedSize(true)

        }
        list.addAll(getListHeroes())
        showRecyclerList()
        showLoading(false)

//        fetchUser()
//        setupListeners()
//        setupRecyclerView()
//        fetchDataAndFilter()
//        observeData()
    }

    private fun getListHeroes(): ArrayList<CategoryList> {
        val dataName = resources.getStringArray(R.array.category_name)
        val listHero = ArrayList<CategoryList>()
        for (i in dataName.indices) {
            val hero = CategoryList(dataName[i])
            listHero.add(hero)
        }
        return listHero
    }

    private fun showRecyclerList() {
        binding.apply {
            rvCategory.layoutManager = LinearLayoutManager(requireActivity())
            val listCategoryAdapter = CategoryAdapter(list)
            rvCategory.adapter = listCategoryAdapter
        }
    }

//    private fun setProductList(items: List<ProductResponse?>) {
//        val adapter = SentimentAdapter()
//        adapter.submitList(items)
//        with(binding) {
//            rvCategory.layoutManager = LinearLayoutManager(requireActivity())
//            rvCategory.adapter = adapter
//            rvCategory.setHasFixedSize(true)
//        }
//    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility =
            View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

//    private fun fetchUser() {
//        val currentUser = firebaseAuth.currentUser
//
//        if (currentUser == null) {
//            Toast.makeText(requireContext(), "User logged out", Toast.LENGTH_SHORT).show()
//
//            val intent = Intent(requireContext(), SignInActivity::class.java)
//            startActivity(intent)
//            requireActivity().finishAffinity()
//        }
//
//        currentUser?.let {
//            val circularProgressDrawable = CircularProgressDrawable(requireContext())
//            circularProgressDrawable.strokeWidth = 5f
//            circularProgressDrawable.centerRadius = 30f
//            circularProgressDrawable.start()
//
////            binding.ivProfile.load(it.photoUrl.toString()) {
////                crossfade(true)
////                placeholder(circularProgressDrawable)
////                transformations(CircleCropTransformation())
////                error(R.drawable.ic_placeholder_avatar)
////            }
//        }
//    }
//
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
//        isLoading.observe(viewLifecycleOwner) {
//            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
//        }
//        searchQuery.observe(viewLifecycleOwner) {
//            fetchDataAndFilter()
//        }
//    }
//
//    private fun fetchDataAndFilter() {
//        isLoading.value = true
//        lifecycleScope.launch(Dispatchers.IO) {
//            firebaseFirestore.collection("categories")
//                .orderBy("name")
//                .get()
//                .addOnSuccessListener { result ->
//                    val listCategory = result.toObjects(Category::class.java)
//                    val filteredCategories = if (searchQuery.value.isNullOrEmpty()) {
//                        listCategory
//                    } else {
//                        val normalizedSearchQuery = searchQuery.value!!.toLowerCase(Locale.ROOT)
//                        listCategory.filter { category ->
//                            category.name?.lowercase(Locale.ROOT)?.contains(normalizedSearchQuery)
//                                ?: false
//                        }
//                    }
//                    isLoading.value = false
//                    categoryAdapter.setCategoryList(filteredCategories)
//                }
//                .addOnFailureListener { exception ->
//                    isLoading.value = false
//                    Log.e("fetchDataAndFilter", "Error fetching data: ${exception.message}")
//                }
//        }
//    }
//
//    private fun setupRecyclerView() {
//        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvCategory.isNestedScrollingEnabled = false
//        binding.rvCategory.adapter = categoryAdapter
//
//        categoryAdapter.onItemClickListener = { category ->
//            val intent = Intent(requireContext(), TrendingProductActivity::class.java)
//            intent.putExtra(TrendingProductActivity.CATEGORY_ID, category.id)
//            startActivity(intent)
//        }
//    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
}
