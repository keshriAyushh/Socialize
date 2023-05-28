package com.example.socialize.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialize.R
import com.example.socialize.model.Post
import com.example.socialize.util.AuthUtil
import com.example.socialize.util.TimeUtil
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.imageview.ShapeableImageView

class PostsAdapter(
    options: FirestoreRecyclerOptions<Post>,
    private val listener: IPostAdapter
): FirestoreRecyclerAdapter<Post, PostsAdapter.PostViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)

        val viewHolder = PostViewHolder(view)

        //Writing this here since we need the id of the post, that we can get from document snapshot
        //but we required viewHolder, so we need to write it here

        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.postedBy.text = model.createdBy!!.name
        holder.postedAt.text = TimeUtil.getTimeAgo(model.createdAt).toString()
        holder.likeCount.text = model.likedBy.size.toString()

        Glide.with(holder.profilePicture.context).load(model.createdBy.imageUrl)
            .circleCrop()
            .placeholder(ContextCompat.getDrawable(holder.profilePicture.context, R.drawable.account))
            .into(holder.profilePicture)

        holder.shareBtn.setOnClickListener {
            listener.onShareButtonClicked(model)
        }

        val isLiked = model.likedBy.contains(AuthUtil.getInstance().currentUser!!.uid)

        if(isLiked) {
            holder.likeBtn.setImageDrawable(ContextCompat.getDrawable(holder.likeBtn.context, R.drawable.heart2))
        } else {
            holder.likeBtn.setImageDrawable(ContextCompat.getDrawable(holder.likeBtn.context, R.drawable.heart))
        }


        holder.likeBtn.setOnClickListener {
            listener.onLikeButtonClicked(snapshots.getSnapshot(holder.adapterPosition).id)
        }
    }

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.tvPost)
        val postedBy: TextView = itemView.findViewById(R.id.tvPostedBy)
        val postedAt: TextView = itemView.findViewById(R.id.tvPostedAt)
        val likeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val profilePicture: ShapeableImageView = itemView.findViewById(R.id.ivProfilePicture)
        val likeBtn: ImageView = itemView.findViewById(R.id.ivLike)
        val shareBtn: ShapeableImageView = itemView.findViewById(R.id.btnShare)
    }
}
interface IPostAdapter {
    fun onLikeButtonClicked(postId: String)

    fun onShareButtonClicked(post: Post)
}