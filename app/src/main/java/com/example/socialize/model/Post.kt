package com.example.socialize.model

data class Post(
    val text: String? = null,
    val createdBy: User? = null,
    val createdAt: Long = 0L,
    val likedBy: ArrayList<String> = ArrayList()
)
