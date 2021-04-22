package com.example.youtubetimer

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.NumberFormat
import java.util.*


class VideoListAdapter(private val dataSet: MutableList<VideoResult>) :
        RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvRowInfo: TextView = v.findViewById(R.id.tvRowInfo)
        val tvDuration: TextView = v.findViewById(R.id.tvDuration)
        val tvRowTitle: TextView = v.findViewById(R.id.tvRowTitle)
        val tvRowVideoInfo: TextView = v.findViewById(R.id.tvRowVideoInfo)
        var ivThumbnail: ImageView = v.findViewById(R.id.ivThumbnail)
        var videoCode = ""

        init {

            v.setOnClickListener {
                val backToVideo = Intent(v.context, VideoPage::class.java)
                val videoInfo = tvRowVideoInfo.text.split("*@*@*@*")
                backToVideo.putExtra("videoTitle", tvRowTitle.text)
                backToVideo.putExtra("videoInfo", videoInfo[0])
                backToVideo.putExtra("videoDescription", videoInfo[1])
                backToVideo.putExtra("videoCode", videoCode)
                if (VideoPage.timeLeft != 0L) {
                    backToVideo.putExtra("timeLeft", VideoPage.timeLeft)
                }
                backToVideo.putExtra("isFullscreen", VideoPage.isFullscreen)

                VideoPage.isGeneratedRelatedVideos = false
                VideoPage.isGeneratedComments = false
                // If we are selecting from Related Video list on VideoPage, finish the activity so we can start the new one
                try {
                    (v.context as YouTubeFailureRecoveryActivity).finish()
                }
                catch (e: Exception){
                }
                startActivity(v.context, backToVideo, null)

            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.video_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val videoInfoString = dataSet[position]
        viewHolder.tvRowTitle.text = videoInfoString.videoTitle
        viewHolder.videoCode = videoInfoString.videoId
        val viewCount = NumberFormat.getNumberInstance(Locale.US).format(videoInfoString.viewCount)
        viewHolder.tvRowInfo.text = videoInfoString.channelTitle + " * " + viewCount + " views * " + videoInfoString.publishDate
        viewHolder.tvDuration.text = videoInfoString.duration
        // To pass along to VideoPage
        viewHolder.tvRowVideoInfo.text =    viewCount + " views\n" +
                                            NumberFormat.getNumberInstance(Locale.US).format(videoInfoString.likeCount) + " likes * " + NumberFormat.getNumberInstance(Locale.US).format(videoInfoString.dislikeCount) + " dislikes * " + NumberFormat.getNumberInstance(Locale.US).format(videoInfoString.commentCount) + " comments\n" +
                                            "Posted " + videoInfoString.publishDate + " by " + videoInfoString.channelTitle +
                                            "*@*@*@*" + videoInfoString.description
        Picasso.get().load(videoInfoString.thumbnail).into(viewHolder.ivThumbnail)
    }

    override fun getItemCount() = dataSet.size
}