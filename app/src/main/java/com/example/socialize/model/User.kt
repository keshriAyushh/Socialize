package com.example.socialize.model

data class User(
    var userId: String = "",
    var name: String? = null,
    var email: String? = null,
    var age:  String? = null,
    var gender: String? = null,
    var imageUrl: String? = null,
)
