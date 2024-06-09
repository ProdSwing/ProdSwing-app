package com.c241ps447.prodswing.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c241ps447.prodswing.data.Result
import com.c241ps447.prodswing.data.UserRepository
import com.c241ps447.prodswing.data.remote.response.ProductsResponseItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

//    suspend fun getProducts()= repository.getProducts()

    fun getProducts(): LiveData<Result<List<ProductsResponseItem?>>> {
        val result = MediatorLiveData<Result<List<ProductsResponseItem?>>>()
        viewModelScope.launch {
            delay(1000)
            result.addSource(repository.getProducts()) {
                result.value = it
            }
        }
        return result
    }
}