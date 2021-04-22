package com.example.youtubetimer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TimerMenu: AppCompatActivity() {

    private lateinit var tvNumbers: TextView
    private lateinit var strTimer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_menu)

        val sVideoInfo: String? = intent.getStringExtra("videoInfo")
        val sVideoTitle: String? = intent.getStringExtra("videoTitle")
        val sVideoCode: String? = intent.getStringExtra("videoCode")
        val iTimeStamp = intent.getIntExtra("timeStamp", 0)
        val sDescription: String? = intent.getStringExtra("videoDescription")

        tvNumbers = findViewById(R.id.tvNumbers)
        strTimer = "000000"

        val btnBackspace: ImageButton = findViewById<ImageButton>(R.id.btnBackspace)
        btnBackspace.setOnClickListener {
            if (strTimer != "000000") { // No reason to backspace if all zeroes
                strTimer = "0" + strTimer.substring(0, 5) // Drop rightmost digit, add 0 to beginning
                tvNumbers.text = strTimer.substring(0,2) + "h " + strTimer.substring(2,4) + "m " + strTimer.substring(4) + "s"
            }
        }

        // Hold backspace to clear timer
        btnBackspace.setOnLongClickListener {
            strTimer = "000000"
            tvNumbers.text = "00h 00m 00s"
            return@setOnLongClickListener true
        }

        findViewById<ImageButton>(R.id.btnStart).setOnClickListener {
            val backToVideo = Intent(this@TimerMenu, VideoPage::class.java)
            var hours = strTimer.substring(0, 2).toInt()
            var minutes = strTimer.substring(2, 4).toInt()
            var seconds = strTimer.substring(4).toInt()
            // If seconds or minutes are larger than 59, adjust time accordingly (ex. 90 seconds -> 1 minute 30 seconds)
            if (seconds > 59) {
                seconds -= 60
                minutes++
            }
            if (minutes > 59) {
                minutes -= 60
                hours++
            }
            backToVideo.putExtra("hours", hours)
            backToVideo.putExtra("minutes", minutes)
            backToVideo.putExtra("seconds", seconds)
            // If nothing has been typed don't set the timer
            if (strTimer != "000000") {
                backToVideo.putExtra("startTimer", true)
            }
            // Pass back video info for playback
            backToVideo.putExtra("videoTitle", sVideoTitle)
            backToVideo.putExtra("videoInfo", sVideoInfo)
            backToVideo.putExtra("videoCode", sVideoCode)
            backToVideo.putExtra("timeStamp", iTimeStamp)
            backToVideo.putExtra("videoDescription", sDescription)
            // Without this, first time pressing back while on video page will cancel timer and make display disappear instead of going back to search page
                // Also might mess with video playback
            backToVideo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(backToVideo)
            // So that when we're on video page and press Back, we go back to search page
            finish()
        }

        fun typeNumber(int: Int) {
            if (strTimer[0].toString() == "0") { // Stop letting user type when hours section has 2 non-zero digits
                strTimer = strTimer.substring(1).plus(int) // Leftmost 0 gets dropped and typed digit is added to the end
                tvNumbers.text = strTimer.substring(0,2) + "h " + strTimer.substring(2,4) + "m " + strTimer.substring(4) + "s"
            }
        }

        // Number pad
        findViewById<Button>(R.id.btn0).setOnClickListener {
            typeNumber(0)
        }
        findViewById<Button>(R.id.btn1).setOnClickListener {
            typeNumber(1)
        }
        findViewById<Button>(R.id.btn2).setOnClickListener {
            typeNumber(2)
        }
        findViewById<Button>(R.id.btn3).setOnClickListener {
            typeNumber(3)
        }
        findViewById<Button>(R.id.btn4).setOnClickListener {
            typeNumber(4)
        }
        findViewById<Button>(R.id.btn5).setOnClickListener {
            typeNumber(5)
        }
        findViewById<Button>(R.id.btn6).setOnClickListener {
            typeNumber(6)
        }
        findViewById<Button>(R.id.btn7).setOnClickListener {
            typeNumber(7)
        }
        findViewById<Button>(R.id.btn8).setOnClickListener {
            typeNumber(8)
        }
        findViewById<Button>(R.id.btn9).setOnClickListener {
            typeNumber(9)
        }

    }
}