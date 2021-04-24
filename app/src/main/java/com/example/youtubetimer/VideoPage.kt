package com.example.youtubetimer


import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.youtube.player.*
import java.util.concurrent.TimeUnit


class VideoPage : YouTubeFailureRecoveryActivity(), YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener {

    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0

    private lateinit var tvTimerDisplay: TextView
    private lateinit var tvBlackScreen: TextView
    private lateinit var btnTimer: FloatingActionButton
    private lateinit var btnBlackScreen: FloatingActionButton
    private lateinit var tvTitle: TextView
    private lateinit var tvVideoInfo: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnPreferences: FloatingActionButton
    private lateinit var swDescription: SwitchCompat
    private lateinit var swComments: SwitchCompat
    private lateinit var swRelatedVideos: SwitchCompat
    private lateinit var rvComments: RecyclerView
    private lateinit var rvRelatedVideos: RecyclerView

    private lateinit var videoListAdapter: VideoListAdapter
    private lateinit var commentListAdapter: CommentListAdapter

    private lateinit var player: YouTubePlayer

    private lateinit var timer: CountDownTimer
    private var startTimer: Boolean = false
    private var timerPaused: Boolean = false

    private var sVideoCode = ""
    private lateinit var sVideoInfo: String
    private lateinit var sVideoTitle: String
    private lateinit var sDescription: String

    private var swLoop: Boolean = false
    private var swFade: Boolean = false
    private var swBluetooth: Boolean = false
    private var swPause: Boolean = false
    private var swAutoplayNext: Boolean = false

    private val apiKey: String = DeveloperKey.DEVELOPER_KEY

    // Will allow us to adjust media volume in timer.onTick()
    private lateinit var audio: AudioManager

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var youtubePlayerView: YouTubePlayerView

    // Read preferences in onResume so they can get updated when we come back from settings page
    override fun onResume() {
        super.onResume()
        swLoop = sharedPreferences.getBoolean("swLoop", false)
        swPause = sharedPreferences.getBoolean("swPause", false)
        swBluetooth = sharedPreferences.getBoolean("swBluetooth", false)
        swFade = sharedPreferences.getBoolean("swFade", false)
        swAutoplayNext = sharedPreferences.getBoolean("swAutoplayNext", false)

        // Autoplay video on start and when coming back from another activity or app
        if (this::player.isInitialized) {
            player.play()
        }

        if (swAutoplayNext && !isGeneratedRelatedVideos) {
            getRelatedVideos()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_page)

        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Needs to be initialized in onCreate
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        // Timer info received from TimerMenu
        startTimer = intent.getBooleanExtra("startTimer", false)
        hours = intent.getIntExtra("hours", 0)
        minutes = intent.getIntExtra("minutes", 0)
        seconds = intent.getIntExtra("seconds", 0)

        isFullscreen = intent.getBooleanExtra("isFullscreen", false)

        tvTimerDisplay = findViewById(R.id.tvTimerDisplay)
        tvTitle = findViewById(R.id.tvTitle)
        tvVideoInfo = findViewById(R.id.tvVideoInfo)
        tvDescription = findViewById(R.id.tvDescription)
        swComments = findViewById(R.id.swComments)
        swDescription = findViewById(R.id.swDescription)
        swRelatedVideos = findViewById(R.id.swRelatedVideos)

        rvComments = findViewById(R.id.rvComments)
        rvRelatedVideos = findViewById(R.id.rvRelatedVideos)

        // Setting up RecyclerViews
        videoListAdapter = VideoListAdapter(videoList)
        val relatedVideoLayoutManager = LinearLayoutManager(applicationContext)
        rvRelatedVideos.layoutManager = relatedVideoLayoutManager
        rvRelatedVideos.itemAnimator = DefaultItemAnimator()
        rvRelatedVideos.adapter = videoListAdapter

        commentListAdapter = CommentListAdapter(commentList)
        val commentLayoutManager = LinearLayoutManager(applicationContext)
        rvComments.layoutManager = commentLayoutManager
        rvComments.itemAnimator = DefaultItemAnimator()
        rvComments.adapter = commentListAdapter

        // Video info received from Search
        sVideoInfo = intent.getStringExtra("videoInfo").toString()
        sVideoTitle = intent.getStringExtra("videoTitle").toString()
        sVideoCode = intent.getStringExtra("videoCode").toString()
        sDescription = intent.getStringExtra("videoDescription").toString()

        tvVideoInfo.text = sVideoInfo
        tvDescription.text = sDescription
        tvTitle.text = sVideoTitle

        // Initialize YouTubePlayerView
        youtubePlayerView = findViewById(R.id.youtube_player)
        youtubePlayerView.initialize(apiKey, this)

        // Convert timer length to milliseconds
        var timerLength: Long = (hours*3600000 + minutes*60000 + seconds*1000).toLong()

        // If we switched videos while timer was running, use the time remaining on the original timer to start a new one
        val continuedTimer = intent.getLongExtra("timeLeft", 0)
        if (continuedTimer > 0) {
            timerLength = continuedTimer
            startTimer = true
        }


        btnTimer = findViewById(R.id.btnTimer)
        // If timer has been set -> show timer display, change set timer button to cancel timer button, and start timer
        if (startTimer) {
            tvTimerDisplay.visibility = View.VISIBLE
            btnTimer.setImageResource(android.R.drawable.ic_delete)
            btnTimer.backgroundTintList = ColorStateList.valueOf((Color.argb(255, 255, 0, 0)))
            startTimer(timerLength)
        }

        btnTimer.setOnClickListener {
            // If cancel timer button is pressed -> cancel timer, do button flip animation, and switch it to set timer button
            if (startTimer) {
                timer.cancel()
                startTimer = false
                timeLeft = 0
                tvTimerDisplay.visibility = View.GONE
                val timerCancelAnimation: AnimatorSet = AnimatorInflater.loadAnimator(
                        this@VideoPage,
                        R.animator.fab_flip
                ) as AnimatorSet
                timerCancelAnimation.setTarget(btnTimer)
                timerCancelAnimation.start()
                btnTimer.setImageResource(android.R.drawable.ic_lock_idle_alarm)
                btnTimer.backgroundTintList = ColorStateList.valueOf(
                        (Color.argb(
                                255,
                                19,
                                204,
                                81
                        ))
                )
            }
            // If set timer button is pressed -> save video info so we can resume playback after timer is set
            else {
                val toTimerMenu = Intent(this@VideoPage, TimerMenu::class.java)
                toTimerMenu.putExtra("videoTitle", sVideoTitle)
                toTimerMenu.putExtra("videoInfo", sVideoInfo)
                toTimerMenu.putExtra("videoCode", sVideoCode)
                toTimerMenu.putExtra("timeStamp", player.currentTimeMillis)
                toTimerMenu.putExtra("videoDescription", sDescription)
                startActivity(toTimerMenu)
            }
        }

        // Creates black overlay to cover video info (cannot cover video player or the video will pause)
        // Reduces light for when user is trying to sleep and saves battery if phone has OLED screen
        tvBlackScreen = findViewById(R.id.tvBlackScreen)
        btnBlackScreen = findViewById(R.id.btnBlackScreen)
        var isBlackScreen = false
        // Lets us change the status bar color to black (if light mode is being used)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var changeBack = false
        btnBlackScreen.setOnClickListener {
            // Set tvBlackScreen to VISIBLE, change icon, and set timer display background and status bar to black (if needed)
            if (!isBlackScreen) {
                if (window.statusBarColor == ContextCompat.getColor(this@VideoPage, R.color.red)) {
                    window.statusBarColor = ContextCompat.getColor(this@VideoPage, R.color.black)
                    changeBack = true
                }
                btnBlackScreen.setImageResource(R.drawable.blackscreen_turnoff)
                tvBlackScreen.visibility = View.VISIBLE
                isBlackScreen = true
                tvTimerDisplay.setBackgroundColor((Color.argb(255, 0, 0, 0)))
            }
            // If tvBlackScreen is visible -> get rid of it, set timer display background back to gray, change status bar back to red (if needed), and change button icon back
            else {
                if (changeBack) {
                    window.statusBarColor = ContextCompat.getColor(this@VideoPage, R.color.red)
                }
                btnBlackScreen.setImageResource(R.drawable.blackscreen_turnon)
                tvBlackScreen.visibility = View.GONE
                isBlackScreen = false
                tvTimerDisplay.setBackgroundColor((Color.argb(255, 83, 83, 83)))
            }
        }

        // Preferences Menu
        btnPreferences = findViewById(R.id.btnPreferences)
        btnPreferences.setOnClickListener {
            val toPreferences = Intent(this@VideoPage, Preferences::class.java)
            // Don't need to save video info for this one, it will still be there when we come back to VideoPage
            startActivity(toPreferences)
        }


        var isTouched1 = false

        swDescription.setOnTouchListener { _, _ ->
                isTouched1 = true
                // Set to false so we can go to OnCheckedChangeListener
                false
            }

        swDescription.setOnCheckedChangeListener { _, isChecked ->
            if (isTouched1) {
                isTouched1 = false
                if (isChecked) {
                    tvDescription.visibility = View.VISIBLE
                }
                else {
                    tvDescription.visibility = View.GONE
                }
            }
        }

        var isTouched2 = false

        swComments.setOnTouchListener { _, _ ->
            isTouched2 = true
            // Set to false so we can go to OnCheckedChangeListener
            false
        }

        swComments.setOnCheckedChangeListener { _, isChecked ->
            if (isTouched2) {
                isTouched2 = false
                if (isChecked) {
                    if (!isGeneratedComments) {
                        retrieveComments()
                    }
                    rvComments.visibility = View.VISIBLE
                }
                else {
                    rvComments.visibility = View.GONE
                }
            }
        }

        var isTouched3 = false

        swRelatedVideos.setOnTouchListener { _, _ ->
            isTouched3 = true
            // Set to false so we can go to OnCheckedChangeListener
            false
        }

        swRelatedVideos.setOnCheckedChangeListener { _, isChecked ->
            if (isTouched3) {
                isTouched3 = false
                if (isChecked) {
                    // I chose not to automatically generate related videos list to avoid excessive quota usage
                        // each search costs 100 units out of 10,000 per day (for free tier)
                    if (!isGeneratedRelatedVideos) {
                        getRelatedVideos()
                    }
                    rvRelatedVideos.visibility = View.VISIBLE
                }
                else {
                    rvRelatedVideos.visibility = View.GONE
                }
            }
        }

    }

    override fun onBackPressed() {
        // Call before super.onBackPressed() and return so that we only exit fullscreen instead of leaving the activity
        if (isFullscreen) {
            player.setFullscreen(false)
            isFullscreen = false
            return
        }
        super.onBackPressed()
        // Need to check since timer is not automatically initialized in onCreate
        if (this::timer.isInitialized) {
            timer.cancel()
            startTimer = false
        }

    }

    companion object {
        // So we can maintain timer between videos
        var timeLeft: Long = 0
        // So we don't have to re-generate comments and related videos when coming back from timer
        var videoList: MutableList<VideoResult> = mutableListOf()
        var isGeneratedRelatedVideos: Boolean = false
        var commentList: MutableList<CommentResult> = mutableListOf()
        var isGeneratedComments: Boolean = false
        // So we can keep video in fullscreen when next video is autoplayed
        var isFullscreen: Boolean = false
        var needsFullscreen: Boolean = false
    }

    private fun startTimer(milliseconds: Long) {
        if (milliseconds > 0) {
            timer = object : CountDownTimer(milliseconds, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = millisUntilFinished
                    // HH:MM:SS time format
                    val timerHours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60
                    val timerMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                    val timerSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                    val timeLeft =
                        String.format("%02d:%02d:%02d", timerHours, timerMinutes, timerSeconds)

                    val timerText = SpannableString(timeLeft)
                    val timerText2 = SpannableString(" remaining")
                    timerText.setSpan(RelativeSizeSpan(1f), 0, 8, 0)
                    // Sets " remaining" as grey and half the size of timer text
                    timerText2.setSpan(RelativeSizeSpan(0.5f), 0, 10, 0)
                    timerText2.setSpan(
                            ForegroundColorSpan(Color.argb(255, 182, 182, 182)),
                            0,
                            10,
                            0
                    )
                    tvTimerDisplay.text = TextUtils.concat(timerText, timerText2)

                    if (swFade) {
                        // Decreases volume by 1 stage every minute when # minutes remaining falls below volume
                        // Ex. If volume is at 5, it will start decreasing when timer reaches 4 minutes
                        // With 1 minute left volume will also be 1 (assuming timer length in minutes is set greater than or equal to volume level)
                        // Eases transition to silence, should be less likely to wake up user with sudden lack of noise (don't quote me on that I'm not a sleep scientist)
                        val currentVolume = audio.getStreamVolume(volumeControlStream)
                        if (timerMinutes < currentVolume && timerSeconds == 0L && timerMinutes != 0L) {
                            audio.adjustStreamVolume(
                                    volumeControlStream,
                                    AudioManager.ADJUST_LOWER,
                                    9
                            ) // 9 = FLAG_ACTIVE_MEDIA_ONLY --> only adjust when video is playing
                        }
                    }
                }

                override fun onFinish() {
                    if (swBluetooth) {
                        // Turn off Bluetooth if it's on
                        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        if (bluetoothAdapter.isEnabled) {
                            bluetoothAdapter.disable()
                        }
                    }
                    Toast.makeText(
                            this@VideoPage,
                            "Timer complete. Closing app...",
                            Toast.LENGTH_LONG
                    ).show()
                    // Closes app
                    timeLeft = 0
                    ActivityCompat.finishAffinity(this@VideoPage)
                }
            }
            timer.start()
        }
    }

    private fun getRelatedVideos() {
        // Reset videoList for each search
        videoList.clear()

        // Avoids android.os.NetworkOnMainThreadException
        val thread = Thread {
            try {
                SearchAndCommentFunctions.generateSearch(
                        "",
                        videoList,
                        sVideoCode,
                        this@VideoPage
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        // Wait for thread to finish before continuing so that videoList is filled before notifyDataSetChanged()
        thread.join()
        // Can't be inside thread or we get android.view.ViewRootImpl$CalledFromWrongThreadException
        videoListAdapter.notifyDataSetChanged()
        isGeneratedRelatedVideos = true
    }

    private fun retrieveComments() {
        // Reset commentList for each search
        commentList.clear()

        // Avoids android.os.NetworkOnMainThreadException
        val thread = Thread {
            try {
                SearchAndCommentFunctions.retrieveComments(
                        sVideoCode,
                        commentList,
                        this@VideoPage
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        // Wait for thread to finish before continuing so that commentList is filled before notifyDataSetChanged()
        thread.join()
        // Can't be inside thread or we get android.view.ViewRootImpl$CalledFromWrongThreadException
        commentListAdapter.notifyDataSetChanged()
        isGeneratedComments = true
    }

    override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider?,
            player: YouTubePlayer,
            wasRestored: Boolean
    ) {
        this.player = player
        if (!wasRestored) {
            player.loadVideo(sVideoCode)
            player.setPlayerStateChangeListener(this)
            player.setPlaybackEventListener(this)
            player.setOnFullscreenListener {
                if (!needsFullscreen) {
                    isFullscreen = it
                }
            }
        }
    }

    // Functions needed to implement YouTubePlayer.PlayerStateChangeListener
    override fun onLoading() {
    }

    // If coming back from timer menu, resume where you left off
    override fun onLoaded(p0: String?) {
        player.seekToMillis(intent.getIntExtra("timeStamp", 0))
    }

    override fun onAdStarted() {
    }

    override fun onVideoStarted() {
        // Lets us remain in fullscreen between videos when autoplay next video is used
        if (isFullscreen) {
            player.setFullscreen(true)
            needsFullscreen = false
        }
    }

    override fun onVideoEnded() {
        // Loops video
        if (swLoop) {
            player.seekToMillis(0)
        }
        // Lets us remain in fullscreen between videos when autoplay next video is used
        else if (swAutoplayNext) {
            if (isFullscreen) {
                player.setFullscreen(false)
                isFullscreen = true
                needsFullscreen = true
            }
            // performClick() won't work if it's still View.GONE
            rvRelatedVideos.visibility = View.INVISIBLE
            try {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    // Mimics a user click on the first related video (code's already written, no need to rewrite anything)
                    // Delayed to guarantee that rvRelatedVideos.visibility is changed before this is called
                    rvRelatedVideos.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                }, 2000)

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onError(p0: YouTubePlayer.ErrorReason?) {
    }


    // Functions needed to implement YouTubePlayer.PlaybackEventListener

    // Pause timer when video is paused, resume it when video resumes
    override fun onPlaying() {
        if (timerPaused) {
            startTimer(timeLeft)
            timerPaused = false
        }
    }

    override fun onPaused() {
        if (swPause && startTimer) {
            timer.cancel()
            timerPaused = true
        }
    }

    override fun onStopped() {
    }

    override fun onBuffering(p0: Boolean) {
    }

    override fun onSeekTo(p0: Int) {
    }


    override fun getYouTubePlayerProvider(): YouTubePlayer.Provider {
        return findViewById(R.id.youtube_player)
    }

    //TODO if i can get it working https://stackoverflow.com/questions/64097610/android-picture-in-picture-mode?rq=1 (exclude from recents)
    /** Picture in Picture is not working correctly
     *  Window appears, but no video or sound
     *  If YouTube video controls are visible when home button is pressed, they will be visible in PiP window, but video itself does not show
     *  Have tried putting player.play() in various places, setting player to fullscreen before and after entering PiP mode
     *  Nothing has worked and no error is given
     *  I am not the first person to have this problem but documentation and forums have provided no answers*/

//    // Picture in picture supported starting at Oreo (Android 8.0 aka API 26)
//        // Android Studio says that the feature requires Nougat (API 24), but documentation says 26. I don't have a device running 24/25 so I can't test this
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onUserLeaveHint() {
//
//        try {
////            val rational = youTubePlayerFragment.view?.let { Rational(it.width, it.height) }
//            val rational = Rational(youtubePlayerView.width, youtubePlayerView.height)
//            val mParams = PictureInPictureParams.Builder()
//                    .setAspectRatio(rational)
//                    .build()
//            enterPictureInPictureMode(mParams)
//        } catch (e: IllegalStateException) {
//            e.printStackTrace()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onPause() {
//        super.onPause()
//        // If called while in PIP mode, do not pause playback
//        if (isInPictureInPictureMode) {
//            player.play()
//        }
//    }

//    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean,
//                                               newConfig: Configuration) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
//        if (isInPictureInPictureMode) {
////            youtubePlayerView.bringToFront()
//            btnTimer.visibility = View.GONE
//            btnPreferences.visibility = View.GONE
//            btnBlackScreen.visibility = View.GONE
//            tvTitle.visibility = View.GONE
//            tvVideoInfo.visibility = View.GONE
////            newConfig.screenHeightDp = 300
////            newConfig.screenWidthDp = 300
////            youTubePlayerFragment.retainInstance = true
////            player.setFullscreen(true)
////            player.getPlayerUiController().showUi(true);
////            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
//            player.play()
//            Log.e("help", player.isPlaying.toString())
//        } else {
//            // Restore the full-screen UI.
//            btnTimer.visibility = View.VISIBLE
//            btnPreferences.visibility = View.VISIBLE
//            btnBlackScreen.visibility = View.VISIBLE
//            tvTitle.visibility = View.VISIBLE
//            tvVideoInfo.visibility = View.VISIBLE
//            player.play()
//        }
//    }


}