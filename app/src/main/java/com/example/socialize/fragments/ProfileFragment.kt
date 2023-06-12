package com.example.socialize.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.socialize.R
import com.example.socialize.activities.LoginActivity
import com.example.socialize.activities.UpdateProfileActivity
import com.example.socialize.databinding.FragmentProfileBinding
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
   @SuppressLint("MissingInflatedId")
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

        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.btnUpdate.setOnClickListener {
            startActivity(Intent(requireActivity(), UpdateProfileActivity::class.java))
        }

       binding.btnDelete.setOnClickListener {
           val dialogView = LayoutInflater.from(activity).inflate(R.layout.delete_user_dialog, null)
           val builder = AlertDialog.Builder(activity)
               .setView(dialogView)
               .show()
           val btnYes = dialogView.findViewById<MaterialButton>(R.id.btnYes)
           val btnNo = dialogView.findViewById<MaterialButton>(R.id.btnNo)

           btnYes.setOnClickListener {
               StorageUtil.deleteUser(AuthUtil.getInstance().uid!!)
               builder.dismiss()
               googleSignInClient.signOut()
               AuthUtil.getInstance().signOut()
               AuthUtil.getInstance().currentUser?.delete()
               
               startActivity(Intent(requireContext(), LoginActivity::class.java))
               requireActivity().finish()
           }

           btnNo.setOnClickListener {
               builder.dismiss()
           }
       }
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()
        if(this@ProfileFragment.isVisible) {
            GlobalScope.launch(Dispatchers.IO) {
                val task = StorageUtil.getUserById(AuthUtil.getInstance().currentUser!!.uid)
                val user = task.await().toObject(User::class.java)

                withContext(Dispatchers.Main) {
                    if(user != null){

                        Glide.with(requireContext())
                            .load(user.imageUrl)
                            .placeholder(R.drawable.account)
                            .into(binding.ivProfilePicture)

                        binding.tvName.text = user.name
                        binding.tvEmail.text = user.email
                        if(!user.gender.isNullOrEmpty()){
                            binding.tvGender.text = user.gender
                        } else {
                            "Gender".also { binding.tvGender.text = it }
                        }

                        if(!user.age.isNullOrEmpty()){
                            binding.tvAge.text = user.age
                        } else {
                            "Age".also { binding.tvAge.text = it }
                        }
                    }
                }
            }
        }
    }
}