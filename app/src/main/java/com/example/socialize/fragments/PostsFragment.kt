package com.example.socialize.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialize.activities.AddPostActivity
import com.example.socialize.databinding.FragmentPostsBinding

class PostsFragment: Fragment() {

    private lateinit var binding: FragmentPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(layoutInflater)

        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(requireActivity(), AddPostActivity::class.java))
        }
        return binding.root
    }
}