package com.example.youtubetimer

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    
        val resources = viewHolder.itemView.context.resources
        // Format number according to locale and detect if plural
        // Use Number type because view count is long, others are int
        fun formatNumber(number: Number, pluralID: Int): String {
            val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
            // Views are stored as long but getQuantityString requires an integer for the count
            // Numbers larger than max integer value become negative so just use MAX_VALUE
            val count: Int = if (number.toLong() > Int.MAX_VALUE) Int.MAX_VALUE else number.toInt()
            return resources.getQuantityString(pluralID, count, formatted)
        }

        val videoInfoString = dataSet[position]
    
        viewHolder.tvRowTitle.text = videoInfoString.videoTitle
        viewHolder.videoCode = videoInfoString.videoId
    
        val numViews = formatNumber(videoInfoString.viewCount, R.plurals.VLAdapter_views)
        val numLikes = formatNumber(videoInfoString.likeCount, R.plurals.VLAdapter_likes)
        val numDislikes = formatNumber(videoInfoString.dislikeCount, R.plurals.VLAdapter_dislikes)
        val numComments = formatNumber(videoInfoString.commentCount, R.plurals.VLAdapter_comments)
        val publishDateAndChannel = resources.getString(R.string.VLAdapter_publishdate_and_channel, videoInfoString.publishDate, videoInfoString.channelTitle)
    
        viewHolder.tvRowInfo.text = videoInfoString.channelTitle + " * " + numViews + " * " + videoInfoString.publishDate
        viewHolder.tvDuration.text = videoInfoString.duration
        if (videoInfoString.duration == resources.getString(R.string.SACF_livevideo)) {
            viewHolder.tvDuration.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.red))
        }
        else {
            viewHolder.tvDuration.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.VLA_durationbox))
        }
    
        // To pass along to VideoPage
        viewHolder.tvRowVideoInfo.text =    numViews + "\n" +
                                            numLikes + " * " + numDislikes + " * " + numComments + "\n" +
                                            publishDateAndChannel +
                                            "*@*@*@*" + videoInfoString.description
        Picasso.get().load(videoInfoString.thumbnail).into(viewHolder.ivThumbnail)
    

    }
    


    override fun getItemCount() = dataSet.size
}