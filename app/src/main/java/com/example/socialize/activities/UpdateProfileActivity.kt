package com.example.socialize.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialize.databinding.ActivityUpdateProfileBinding
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageInstance: FirebaseFirestore
    private lateinit var name: String
    private lateinit var age: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = AuthUtil.getInstance()
        storageInstance = StorageUtil.getInstance()

        binding.btnUpdate.setOnClickListener {
            init()

            GlobalScope.launch(Dispatchers.Main) {
                val currentUser = StorageUtil.getUserById(auth.currentUser?.uid!!).await()
                    .toObject(User::class.java)

                val updatedUser = User(auth.currentUser?.uid!!, name, currentUser?.email, age, gender)

                StorageUtil.updateUser(auth.currentUser?.uid!!, updatedUser)
                finish()
            }

        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        name = binding.etName.text.toString()
        age = binding.etAge.text.toString()
        gender = binding.etGender.text.toString()
    }
}