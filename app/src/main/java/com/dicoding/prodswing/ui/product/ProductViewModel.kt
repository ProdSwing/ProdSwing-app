package com.dicoding.prodswing.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.dicoding.prodswing.data.UserRepository

class ProductViewModel(private val repository: UserRepository): ViewModel() {

    suspend fun getProducts() = repository.getProducts().asFlow()

    suspend fun getProductByCategory(category: String) = repository.getProductByCategoryName(category).asFlow()
}