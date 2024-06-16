package com.dicoding.prodswing.data.firebase.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    val id: String? = null,
    val name: String? = null,
    val image: String? = null
)
