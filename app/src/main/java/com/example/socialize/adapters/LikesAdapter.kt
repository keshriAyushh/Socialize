package com.example.socialize.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialize.R
import com.example.socialize.model.Post
import com.example.socialize.util.TimeUtil
import com.google.android.material.imageview.ShapeableImageView

class LikesAdapter(
    private val data: ArrayList<Post>
): RecyclerView.Adapter<LikesAdapter.LikeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.liked_item, parent, false)

        val viewHolder = LikeViewHolder(view)

        //Writing this here since we need the id of the post, that we can get from document snapshot
        //but we required viewHolder, so we need to write it here

        return LikeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LikeViewHolder, position: Int) {
        holder.postText.text = data[position].text
        holder.postedBy.text = data[position].createdBy!!.name
        holder.postedAt.text = TimeUtil.getTimeAgo(data[position].createdAt).toString()

        Glide.with(holder.profilePicture.context).load(data[position].createdBy?.imageUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(holder.profilePicture.context, R.drawable.account))
            .into(holder.profilePicture)
    }

    override fun getItemCount() = data.size


    class LikeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.tvPost)
        val postedBy: TextView = itemView.findViewById(R.id.tvPostedBy)
        val postedAt: TextView = itemView.findViewById(R.id.tvPostedAt)
        val profilePicture: ShapeableImageView = itemView.findViewById(R.id.ivProfilePicture)
    }
}