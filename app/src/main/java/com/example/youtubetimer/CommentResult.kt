package com.example.youtubetimer

import android.text.Spanned

data class CommentResult(val profilePic: String,
                         val commenterName: String,
                         val commentPostTime: String,
                         val commentBody: Spanned,
                         val numReplies: String,
                         var isReply: Boolean = false
)