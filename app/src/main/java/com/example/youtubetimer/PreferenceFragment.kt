package com.example.youtubetimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
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
        
        val listTheme: ListPreference = findPreference("listTheme")!!
        listTheme.setOnPreferenceChangeListener { preference, newValue ->
            when(newValue) {
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            preference.isPersistent = true
            true
        }
        
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false)
        }


}
