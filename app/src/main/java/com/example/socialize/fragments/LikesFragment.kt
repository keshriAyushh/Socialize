package com.example.socialize.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialize.R
import com.example.socialize.adapters.LikesAdapter
import com.example.socialize.databinding.FragmentLikesBinding
import com.example.socialize.model.Post
import com.example.socialize.model.User
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.StorageUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LikesFragment : Fragment() {

    private lateinit var binding: FragmentLikesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usersCollectionRef: CollectionReference
    private lateinit var likesAdapter: LikesAdapter
    private lateinit var data: ArrayList<Post>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikesBinding.inflate(layoutInflater)
        auth = AuthUtil.getInstance()
        data = arrayListOf()
        usersCollectionRef = StorageUtil.getUsersCollection()

        GlobalScope.launch(Dispatchers.IO) {
            val user = usersCollectionRef.document(auth.currentUser!!.uid)
                .get()
                .await()
                .toObject(User::class.java)

            data = user!!.likedPosts

            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity(), data.size.toString(), Toast.LENGTH_LONG)
                    .show()
                initRecyclerView(data)
            }
        }

        return binding.root
    }

    private fun initRecyclerView(data: ArrayList<Post>) {
        likesAdapter = LikesAdapter(data)
        binding.recyclerView.adapter = likesAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.itemAnimator = null


    }
}