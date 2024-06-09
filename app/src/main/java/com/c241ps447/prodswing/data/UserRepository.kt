package com.c241ps447.prodswing.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.c241ps447.prodswing.data.remote.ApiConfig
import com.c241ps447.prodswing.data.remote.response.ProductsResponseItem
import retrofit2.HttpException

class UserRepository() {

    suspend fun getProducts(): LiveData<Result<List<ProductsResponseItem?>>> {
        val result = MediatorLiveData<Result<List<ProductsResponseItem?>>>()
        result.value = Result.Loading
        try {
            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.getProducts()
            result.value = Result.Success(successResponse)

        } catch (e: HttpException) {
            // Handle the error here if needed
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository()
            }.also { instance = it }
    }
}
