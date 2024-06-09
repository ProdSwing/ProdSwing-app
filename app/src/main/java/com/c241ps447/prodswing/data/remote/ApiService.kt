package com.c241ps447.prodswing.data.remote

import com.c241ps447.prodswing.data.remote.response.ProductsResponseItem
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductsResponseItem>
}