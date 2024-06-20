package com.dicoding.prodswing.data.retrofit

import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("results")
    suspend fun getProducts(): List<ProductResponse>

    @GET("results/{category}")
    suspend fun getProductByCategory(
        @Path("category") productCategory: String
    ): List<ProductResponse>

//    @GET("product-images/{id}")
//    suspend fun getImageById(
//        @Path("id") productId: String
//    ) : List<ProductImageByIdResponseItem>
}