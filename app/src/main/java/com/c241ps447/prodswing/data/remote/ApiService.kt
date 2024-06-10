package com.c241ps447.prodswing.data.remote

import com.c241ps447.prodswing.data.response.ProductImageByIdResponseItem
import com.c241ps447.prodswing.data.response.ProductsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductsResponseItem>

    @GET("product-images/{id}")
    suspend fun getImageById(
        @Path("id") productId: String
    ) : List<ProductImageByIdResponseItem>
}