package com.example.socialize.util

import com.example.socialize.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object StorageUtil {

    private val storageInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val usersCollection = storageInstance.collection("users")
    fun getInstance() = storageInstance

    fun addUserToDb(user: User?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                usersCollection.document(user.userId).set(it)
            }
        }
    }
}