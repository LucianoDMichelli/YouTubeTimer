package com.example.youtubetimer

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class CommentListAdapter(private val dataSet: MutableList<CommentResult>) :
    RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvProfileAndTime: TextView = v.findViewById(R.id.tvProfileAndTime)
        val ivProfilePic: ImageView = v.findViewById(R.id.ivProfilePic)
        val tvComment: TextView = v.findViewById(R.id.tvComment)
        val tvReplyNumber: TextView = v.findViewById(R.id.tvReplyNumber)
        val tvReplyPadding: TextView = v.findViewById(R.id.tvReplyPadding)


        init { } // Needs to be here
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.comment_row_item, viewGroup, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val videoInfoString = dataSet[position]
        viewHolder.tvProfileAndTime.text = videoInfoString.commenterName + " * " + videoInfoString.commentPostTime
        viewHolder.tvComment.text = videoInfoString.commentBody
        viewHolder.tvComment.movementMethod = LinkMovementMethod.getInstance() // Allows us to click links (using autoLink in XML seems not to work with YouTube timestamps)
        Picasso.get().load(videoInfoString.profilePic).into(viewHolder.ivProfilePic)
        if (videoInfoString.isReply) {
            viewHolder.tvReplyPadding.visibility = View.VISIBLE
            viewHolder.tvReplyNumber.visibility = View.GONE
        }
        else {
            viewHolder.tvReplyNumber.text = videoInfoString.numReplies
            viewHolder.tvReplyNumber.visibility = View.VISIBLE
            viewHolder.tvReplyPadding.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size
}