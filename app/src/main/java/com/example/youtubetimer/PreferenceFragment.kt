package com.example.youtubetimer

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        // Loop video and Autoplay next video cannot be active at the same time
        // If one of them is turned on, turn the other one off
        val swLoop: SwitchPreference = findPreference("swLoop")!!
        val swAutoplayNext: SwitchPreference = findPreference("swAutoplayNext")!!
        swLoop.setOnPreferenceChangeListener { _, _ ->
            if (!swLoop.isChecked) {
                swAutoplayNext.isChecked = false
            }
            true
        }
        swAutoplayNext.setOnPreferenceChangeListener { _, _ ->
            if (!swAutoplayNext.isChecked) {
                swLoop.isChecked = false
            }
            true
        }
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
        }


}
