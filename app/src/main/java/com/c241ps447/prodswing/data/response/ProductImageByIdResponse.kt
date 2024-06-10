package com.c241ps447.prodswing.data.response

import com.google.gson.annotations.SerializedName

data class ProductImageByIdResponse(

	@field:SerializedName("ProductImageByIdResponse")
	val productImageByIdResponse: List<ProductImageByIdResponseItem?>? = null
)

data class ProductImageByIdResponseItem(

	@field:SerializedName("productID")
	val productID: String? = null,

	@field:SerializedName("imageURL")
	val imageURL: String? = null
)
