package com.c241ps447.prodswing.view.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.c241ps447.prodswing.data.UserRepository

class MainFragmentViewModel(private val repository: UserRepository): ViewModel() {

    suspend fun getProducts() = repository.getProducts().asFlow()
}