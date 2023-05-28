package com.example.socialize.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.socialize.R
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
import java.io.ByteArrayOutputStream

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageInstance: FirebaseFirestore
    private lateinit var name: String
    private lateinit var age: String
    private lateinit var gender: String
    private val RC_SELECT_IMAGE: Int = 2
    private var selectedImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initSpinner()
        auth = AuthUtil.getInstance()
        storageInstance = StorageUtil.getInstance()

        binding.btnUpdate.setOnClickListener {
            init()

            if(name.isNotEmpty() && gender.isNotEmpty() && age.isNotEmpty()){
                GlobalScope.launch(Dispatchers.Main) {
                    val currentUser = StorageUtil.getUserById(auth.currentUser?.uid!!).await()
                        .toObject(User::class.java)
                    if(!selectedImagePath.toString().isNotEmpty()) {
                        val updatedUser = User(auth.currentUser?.uid!!, name, currentUser?.email, age, gender)

                        StorageUtil.updateUser(auth.currentUser?.uid!!, updatedUser)
                        finish()
                    } else {
                        val updatedUser = User(auth.currentUser?.uid!!, name, currentUser?.email, age, gender, selectedImagePath)

                        StorageUtil.updateUser(auth.currentUser?.uid!!, updatedUser)
                        finish()
                    }

                }
            } else {
                Toast.makeText(this@UpdateProfileActivity, "Fill in all the details!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }


    }

    private fun init() {
        name = binding.etName.text.toString()
        age = binding.etAge.text.toString()
        gender = binding.tvGender.text.toString()
    }

    private fun initSpinner() {
        binding.tvGender.setAdapter(ArrayAdapter(this, R.layout.gender_list_item, resources.getStringArray(R.array.genders)))
    }

}