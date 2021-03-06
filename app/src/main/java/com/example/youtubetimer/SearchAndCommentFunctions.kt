package com.example.youtubetimer

import android.content.Context
import android.content.res.Resources
import android.os.Looper
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import java.io.IOException
import java.security.GeneralSecurityException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.*


object SearchAndCommentFunctions {

    private const val APPLICATION_NAME = "com.example.youtubetimer"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

    private const val apiKey: String = DeveloperKey.DEVELOPER_KEY


    @Throws(GeneralSecurityException::class, IOException::class)
    private fun getService(): YouTube? {
        val httpTransport = NetHttpTransport() //GoogleNetHttpTransport.newTrustedTransport() // throws NoSuchAlgorithmException: KeyStore JKS implementation not found https://stackoverflow.com/a/39249969
        return YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME) // may not be needed unless you are restricting your API key, not sure
                .build()
    }

    @Throws(GeneralSecurityException::class, IOException::class, GoogleJsonResponseException::class)
    fun generateSearch(
        query: String,
        videoList: MutableList<VideoResult>,
        relatedToVideoId: String = "",
        context: Context,
    ) {
        val youtubeService = getService()
        try {
            // Define and execute the API request
            val searchRequest = youtubeService!!.search()
                .list("snippet")
            val searchResponse = if (relatedToVideoId == "") {
                searchRequest.setKey(apiKey)
                    .setMaxResults(20L)
                    .setQ(query)
                    .execute()
            }
            // relatedToVideoId lets us generate "Related Videos" List on VideoPage
            else {
                searchRequest.setKey(apiKey)
                    .setMaxResults(5L)
                    .setQ(query)
                    .setRelatedToVideoId(relatedToVideoId)
                    .setType("video")
                    .setPrettyPrint(true)
                    .execute()
            }
            val videoIdList: MutableList<String> = mutableListOf()
            // Search returns date as ex. 2021-03-21 -> change it to March 21, 2021 or equivalent for locale
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = DateFormat.getDateInstance(1, Locale.getDefault())
            for (result in searchResponse.items) {
                if (result.snippet != null) { // Search can return unavailable videos, skip them
                    if (result.id.videoId != null) {
                        videoIdList.add(result.id.videoId)
                    } else { // Result likely channel or playlist
                        continue
                    }

                    videoList.add(
                        VideoResult(
                            result.id.videoId,
                            HtmlCompat.fromHtml(result.snippet.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(), // Contains HTML tags, so we use this to display text properly
                            result.snippet.thumbnails.high.url,
                            HtmlCompat.fromHtml(result.snippet.channelTitle, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(), // Contains HTML tags, so we use this to display text properly
                            formatter.format(parser.parse(result.snippet.publishedAt.toString().substring(0, 10))!!, // Time is also included but we just want the date
                            )
                        )
                    )
                }
            }
            // Need to use the videos().list method to get some fields
            val statsRequest = youtubeService.videos()
                .list("statistics,contentDetails,snippet")
            // Video IDs need to be single string separated by commas (no spaces) ex. "tRZWqqWW,zAAfeRTT,RRYssfvB"
            val videoIdListString: String = videoIdList.toString().substring(
                1,
                videoIdList.toString().length - 1
            ).replace(" ", "")
            val statsResponse = statsRequest.setKey(apiKey).setId(videoIdListString).execute()
            statsResponse.items.forEachIndexed { index, video ->
                when (val duration = video.contentDetails.duration) {
                    "P0D" -> videoList[index].duration = context.getString(R.string.SACF_livevideo)
                    else -> videoList[index].duration = getDuration(duration)
                }
                videoList[index].description = video.snippet.description
                // Some videos return nothing for these fields
                videoList[index].viewCount =
                    if (video.statistics.viewCount != null) video.statistics.viewCount.toLong() else 0
                videoList[index].likeCount =
                    if (video.statistics.likeCount != null) video.statistics.likeCount.toInt() else 0
                videoList[index].dislikeCount =
                    if (video.statistics.dislikeCount != null) video.statistics.dislikeCount.toInt() else 0
                videoList[index].commentCount =
                    if (video.statistics.commentCount != null) video.statistics.commentCount.toInt() else 0
            }
        }
        catch (e: GoogleJsonResponseException) {
            jsonResponseError(e, context)
        }
    }

    @Throws(GeneralSecurityException::class, IOException::class, GoogleJsonResponseException::class)
    fun retrieveComments(videoId: String, commentList: MutableList<CommentResult>, context: Context) {
        val resources = context.resources
        val youtubeService = getService()
        // Define and execute the API request
        val commentRequest = youtubeService!!.commentThreads().list("snippet,replies")
        try {
            val commentResponse = commentRequest.setKey(apiKey)
                .setVideoId(videoId)
                .setMaxResults(20L)
                .setOrder("relevance") // Sorts by Top Comments instead of Newest
                .execute()

            for (result in commentResponse.items) {
                if (result.snippet != null) { // Search can return unavailable videos, skip them
                    val topLevelComment = result.snippet.topLevelComment
                    val totalReplyCount = result.snippet.totalReplyCount.toInt()
                    commentList.add(
                        CommentResult(
                            topLevelComment.snippet.authorProfileImageUrl,
                            topLevelComment.snippet.authorDisplayName,
                            getTimeSincePosted(topLevelComment.snippet.publishedAt.toString(), resources) ,
                            HtmlCompat.fromHtml(topLevelComment.snippet.textDisplay, HtmlCompat.FROM_HTML_MODE_LEGACY),
                            if (result.snippet.totalReplyCount != null)
                                resources.getQuantityString(R.plurals.SACF_replies, totalReplyCount, totalReplyCount) // If one reply, "1 reply" --> else "X replies"
                            else ""
                        )


                    )
                    if (result.replies != null) {
                        result.replies.comments.forEach { reply ->
                            commentList.add(
                                CommentResult(
                                    reply.snippet.authorProfileImageUrl,
                                    reply.snippet.authorDisplayName,
                                    getTimeSincePosted(reply.snippet.publishedAt.toString(), resources),
                                    HtmlCompat.fromHtml(reply.snippet.textDisplay, HtmlCompat.FROM_HTML_MODE_LEGACY),
                                    "",
                                    true
                                )
                            )
                        }
                    }
                }
            }
        }
        catch (e: GoogleJsonResponseException) {
            jsonResponseError(e, context)
        }
    }

    private fun jsonResponseError(e: GoogleJsonResponseException, context: Context) {
        // avoids java.lang.RuntimeException: Can't toast on a thread that has not called Looper.prepare()
        android.os.Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context,
                context.getString(R.string.SACF_error, e.details.code, e.details.errors[0].reason),
                Toast.LENGTH_LONG
            ).show()
        }
        e.printStackTrace()
    }

    private fun getTimeSincePosted(datePosted: String, resources: Resources): String {
        val instant: Instant = Instant.parse(datePosted)
        val duration: Duration = Duration.between(instant, Instant.now())

        val seconds = duration.seconds.toInt()
        if (seconds < 60) {
            return resources.getQuantityString(R.plurals.SACF_timeSince_seconds, seconds, seconds)
        }
        val minutes = duration.toMinutes().toInt()
        if (minutes < 60) {
            return resources.getQuantityString(R.plurals.SACF_timeSince_minutes, minutes, minutes)
        }
        val hours = duration.toHours().toInt()
        if (hours < 24) {
            return resources.getQuantityString(R.plurals.SACF_timeSince_hours, hours, hours)
        }
        val days = duration.toDays().toInt()
        if (days <= 30) {
            return resources.getQuantityString(R.plurals.SACF_timeSince_days, days, days)
        }

        val today: LocalDate = LocalDate.now()
        val period: Period = Period.between(LocalDate.parse(datePosted.substring(0,10)), today)
        return if (period.years > 0) {
            resources.getQuantityString(R.plurals.SACF_timeSince_years, period.years, period.years)
        } else {
            resources.getQuantityString(R.plurals.SACF_timeSince_months, period.months, period.months)
        }

    }

    // Convert ISO 8601 Duration to HH:MM:SS
    // Java/Kotlin solutions I found did not account for days being included
    // This will work until video durations reach months or years (longest video currently is ~24 days long)
    private fun getDuration(duration: String): String {
        var days = 0
        var hours = 0
        var minutes = "0"
        var seconds = ""
        if (duration.contains('D')) {
            days = duration.substring(1, duration.indexOf('D')).toInt()
        }
        if (duration.contains('H')) {
            hours = duration.substring(duration.indexOf('H') - 2, duration.indexOf('H')).replace(
                "T",
                ""
            ).toInt()
        }
        if (duration.contains('M')) {
            minutes = duration.substring(duration.indexOf('M') - 2, duration.indexOf('M')).replace(
                "H",
                ""
            ).replace("T", "")
        }
        if (duration.contains('S')) {
            seconds = duration.substring(duration.indexOf('S') - 2, duration.indexOf('S')).replace(
                "M",
                ""
            ).replace("H", "").replace("T", "")
        }
        hours += days * 24
        var temp = ""
        if (hours != 0) {
            temp += "$hours:"
        }
        if (minutes.length == 1 && hours == 0) {
            temp += "$minutes:"
        }
        else {
            temp += minutes.padStart(2, '0') + ":"
        }
        temp += seconds.padStart(2, '0')
        return temp
    }

}