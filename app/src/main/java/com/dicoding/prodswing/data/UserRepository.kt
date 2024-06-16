package com.dicoding.prodswing.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.prodswing.data.retrofit.ApiConfig
import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import retrofit2.HttpException

class UserRepository private constructor(
    context: Context
) {

    suspend fun getProducts(): LiveData<Result<List<ProductResponse?>>> {
        val result = MediatorLiveData<Result<List<ProductResponse?>>>()
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

//    suspend fun getProductImageById(id: String): LiveData<Result<List<ProductImageByIdResponseItem>>> {
//        val result = MediatorLiveData<Result<List<ProductImageByIdResponseItem>>>()
//        result.value = Result.Loading
//        try {
//            val apiService = ApiConfig.getApiService()
//            val successResponse = apiService.getImageById(id)
//            result.value = Result.Success(successResponse)
//
//        } catch (e: HttpException) {
//            // Handle the error here if needed
//        }
//        return result
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            context: Context
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(context)
            }.also { instance = it }
    }
}
