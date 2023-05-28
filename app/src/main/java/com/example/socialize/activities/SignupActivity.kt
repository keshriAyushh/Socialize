package com.example.socialize.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.socialize.databinding.ActivitySignupBinding
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageInstance: FirebaseFirestore
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var conf_password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = AuthUtil.getInstance()
        storageInstance = StorageUtil.getInstance()

        binding.tvRedirect.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            init()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password.isNotEmpty() && conf_password.isNotEmpty()) {
                if (password == conf_password) {
                    signUpUser(name, email, password)
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        "Passwords do not match!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(
                    this@SignupActivity,
                    "Please fill in all the fields!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun init() {
        name = binding.etName.text.toString()
        email = binding.etEmail.text.toString()
        password = binding.etPass.text.toString()
        conf_password = binding.etConfirmPass.text.toString()
    }

    private fun signUpUser(name: String, email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully Created!", Toast.LENGTH_SHORT)
                    .show()
                StorageUtil.addUserToDb(User(auth.currentUser?.uid!!, name, email, null, null, auth.currentUser?.photoUrl.toString()))
                startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign up failed, please try again!", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}