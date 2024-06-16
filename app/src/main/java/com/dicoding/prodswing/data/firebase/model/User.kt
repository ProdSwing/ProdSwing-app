package com.dicoding.prodswing.data.firebase.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String? = null,
    val email: String? = null,
    val username: String? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val profilePicture: String? = null,
)
