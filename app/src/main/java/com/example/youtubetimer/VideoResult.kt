package com.example.youtubetimer

import android.os.CountDownTimer
import android.text.Spanned
import androidx.core.text.toSpanned
import java.time.Duration
import java.util.*

data class VideoResult(val videoId: String,
                       val videoTitle: String,
                       val thumbnail: String,
                       val channelTitle: String,
                       val publishDate: String,
                       // These will be set after VideoResult object is created in Search.generateSearchResults(), so they need to be initialized here
                       var description: String = "",
                       var duration: String = "",
                       var viewCount: Long = 0,
                       var likeCount: Long = 0,
                       var dislikeCount: Long = 0,
                       var commentCount: Long = 0,
)
