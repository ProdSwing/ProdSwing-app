package com.dicoding.prodswing.data.firebase.model

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val id: String? = null,
    val rating: Float? = null,
    val review: String? = null,
)
