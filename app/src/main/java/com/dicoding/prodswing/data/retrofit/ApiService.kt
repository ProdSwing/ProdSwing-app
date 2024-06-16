package com.dicoding.prodswing.data.retrofit

import com.dicoding.prodswing.data.retrofit.response.ProductResponse
import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductResponse>

//    @GET("product-images/{id}")
//    suspend fun getImageById(
//        @Path("id") productId: String
//    ) : List<ProductImageByIdResponseItem>
}