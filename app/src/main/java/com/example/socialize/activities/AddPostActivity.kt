package com.example.socialize.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialize.databinding.ActivityAddPostBinding
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageInstance: FirebaseFirestore
    private lateinit var postText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = AuthUtil.getInstance()
        storageInstance = StorageUtil.getInstance()

        binding.btnPost.setOnClickListener {
            init()
            if(postText.isNotEmpty()) {
                StorageUtil.addPost(postText, this@AddPostActivity)
                finish()
            } else {
                Toast.makeText(this@AddPostActivity, "Please enter some text!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun init() {
        postText = binding.etPost.text.toString().trim()
    }
}