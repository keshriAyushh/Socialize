package com.example.socialize.util

import android.content.Context
import android.widget.Toast
import com.example.socialize.model.Post
import com.example.socialize.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(DelicateCoroutinesApi::class)
object StorageUtil {

    private val storageInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val usersCollection = storageInstance.collection("users")
    private val postCollection = storageInstance.collection("posts")

    fun getInstance() = storageInstance

    fun getPostCollection() = postCollection
    fun addUserToDb(user: User?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                usersCollection.document(user.userId).set(it)
            }
        }
    }

    fun addPost(postText: String, context: Context) {
        val currentUserId = AuthUtil.getInstance().currentUser!!.uid

        GlobalScope.launch(Dispatchers.IO) {

            val user = getUserById(currentUserId).await().toObject(User::class.java) //mapping the DocumentSnapshot to an object of User

            val currentTime = System.currentTimeMillis()
            val post = Post(postText, user, currentTime)

            postCollection.document().set(post)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post added successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Post was not added, please try again!", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return usersCollection.document(uId).get()
    }

    fun getPostId(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }


    fun updateLikes(postId: String) {

        GlobalScope.launch(Dispatchers.IO) {
            val currentUserId = AuthUtil.getInstance().currentUser!!.uid
            val post = getPostId(postId).await().toObject(Post::class.java)

             val isLiked = post?.likedBy?.contains(currentUserId)

            if(isLiked!!) {
                post.likedBy.remove(currentUserId)
            } else {
                post.likedBy.add(currentUserId)
            }
            postCollection.document(postId).set(post)
        }

    }

    fun updateUser(userId: String, user: User) {
        usersCollection.document(userId).set(user)
    }
}