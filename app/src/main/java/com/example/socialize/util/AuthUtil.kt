package com.example.socialize.util

import com.google.firebase.auth.FirebaseAuth

object AuthUtil {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getInstance() = auth
}