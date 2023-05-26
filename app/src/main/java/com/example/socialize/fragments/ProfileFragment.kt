package com.example.socialize.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.socialize.activities.LoginActivity
import com.example.socialize.activities.UpdateProfileActivity
import com.example.socialize.databinding.FragmentProfileBinding
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        auth = AuthUtil.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("980148775706-o2r8v868h6pnbco5hadff9nektn9i3gb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        initViews()
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.btnUpdate.setOnClickListener {
            startActivity(Intent(requireActivity(), UpdateProfileActivity::class.java))
        }
        return binding.root
    }

    private fun initViews() {

        GlobalScope.launch(Dispatchers.IO) {
            val task = StorageUtil.getUserById(AuthUtil.getInstance().currentUser!!.uid)
            val user = task.await().toObject(User::class.java)

            withContext(Dispatchers.Main) {
                if(user != null){
                    binding.tvName.text = user.name
                    binding.tvEmail.text = user.email
                    if(!user.gender.isNullOrEmpty()){
                        binding.tvGender.text = user.gender
                    } else {
                        binding.tvGender.text = "Gender"
                    }

                    if(!user.age.isNullOrEmpty()){
                        binding.tvAge.text = user.age
                    } else {
                        binding.tvAge.text = "Age"
                    }
                }
            }
        }
    }
}