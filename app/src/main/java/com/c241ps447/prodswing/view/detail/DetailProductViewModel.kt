package com.c241ps447.prodswing.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.c241ps447.prodswing.data.UserRepository

class DetailProductViewModel(private val repository: UserRepository): ViewModel() {

    suspend fun getProductImageById(id: String) =
        repository.getProductImageById(id).asFlow()
}