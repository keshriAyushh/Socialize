package com.example.socialize.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.socialize.R
import com.example.socialize.databinding.ActivityLoginBinding
import com.example.socialize.databinding.ActivityMainBinding
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = AuthUtil.getInstance()

        binding.tvRedirect.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            init()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@LoginActivity, "Log in failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(this@LoginActivity, "Please fill in all the fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("980148775706-o2r8v868h6pnbco5hadff9nektn9i3gb.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>){

        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account!=null){
                signInWithCredentials(account)
            } else {
                Toast.makeText(this@LoginActivity, "Sign in failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun signInWithCredentials(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                StorageUtil.addUserToDb(User(auth.currentUser?.uid!!, auth.currentUser?.displayName, auth.currentUser?.email, null, null, auth.currentUser?.photoUrl.toString()))
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@LoginActivity, "Failed!", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun init() {
        email = binding.etEmail.text.toString()
        password = binding.etPass.text.toString()
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}