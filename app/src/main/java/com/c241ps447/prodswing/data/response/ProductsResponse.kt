package com.c241ps447.prodswing.data.response

import com.google.gson.annotations.SerializedName

data class ProductsResponse(

	@field:SerializedName("ProductsResponse")
	val productsResponse: ArrayList<ProductsResponseItem>
)

data class ProductsResponseItem(

	@field:SerializedName("productID")
	val productID: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("review")
	val review: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("productName")
	val productName: String? = null
)
