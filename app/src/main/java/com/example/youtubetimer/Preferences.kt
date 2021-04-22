package com.example.youtubetimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Preferences: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_menu)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.preferences_container, PreferenceFragment())
            .commit()
    }
}