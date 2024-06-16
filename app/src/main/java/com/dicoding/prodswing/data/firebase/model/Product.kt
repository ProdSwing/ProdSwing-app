package com.dicoding.prodswing.data.firebase.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @DocumentId
    val id: String? = null,
    val name: String? = null,
    val price: Long? = 0L,
    val categoryId: String? = null,
    val description: String? = null,
    val imageThumbnail: String? = null,
    val imageProduct: List<String>? = null
) : Parcelable
