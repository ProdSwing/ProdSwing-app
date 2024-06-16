package com.c241ps447.prodswing.view.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.c241ps447.prodswing.data.UserRepository

class MainViewModel(private val repository: UserRepository) : ViewModel() {

//    suspend fun getProducts() = repository.getProducts()
    suspend fun getProducts() = repository.getProducts().asFlow()

//    fun getProducts(): LiveData<Result<List<ProductsResponseItem?>>> {
//        val result = MediatorLiveData<Result<List<ProductsResponseItem?>>>()
//        viewModelScope.launch {
//            delay(1000)
//            result.addSource(repository.getProducts()) {
//                result.value = it
//            }
//        }
//        return result
//    }
}