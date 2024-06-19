package com.dicoding.prodswing.data.retrofit.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(

	@field:SerializedName("imageProduct")
	val imageProduct: ArrayList<String?>? = null,

	@field:SerializedName("price")
	val price: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("categoryId")
	val categoryId: String? = null,

	@field:SerializedName("imageThumbnail")
	val imageThumbnail: String? = null
)
