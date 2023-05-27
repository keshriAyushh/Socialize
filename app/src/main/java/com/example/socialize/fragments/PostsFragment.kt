package com.example.socialize.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialize.activities.AddPostActivity
import com.example.socialize.adapters.IPostAdapter
import com.example.socialize.adapters.PostsAdapter
import com.example.socialize.databinding.FragmentPostsBinding
import com.example.socialize.model.Post
import com.example.socialize.util.StorageUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class PostsFragment: Fragment(), IPostAdapter {

    private lateinit var binding: FragmentPostsBinding
    private lateinit var postAdapter: PostsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(layoutInflater)
        initRecyclerView()

        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(requireActivity(), AddPostActivity::class.java))
        }

        return binding.root
    }

    private fun initRecyclerView() {

        val postCollection = StorageUtil.getPostCollection()
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        postAdapter = PostsAdapter(recyclerViewOptions, this@PostsFragment)
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.itemAnimator = null
        binding.swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            binding.swipeRefreshLayout.isRefreshing = false
            postAdapter.notifyDataSetChanged()
        }

    }

    override fun onStart() {
        super.onStart()
        postAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
    }

    override fun onLikeButtonClicked(postId: String) {
        StorageUtil.updateLikes(postId)
    }

    override fun onShareButtonClicked(post: Post) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, post.text)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share Via"))
    }
}