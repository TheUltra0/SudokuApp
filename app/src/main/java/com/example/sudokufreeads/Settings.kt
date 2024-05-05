package com.example.sudokufreeads

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.example.sudokufreeads.R
import kotlinx.android.synthetic.main.settings.*


class Settings:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        for(i in 1..6) {
            var mode = settingsmode.findViewWithTag(
                "mode$i"
            ) as CardView
            var switch = settingsmode.findViewWithTag(
                "Switch$i"
            ) as SwitchCompat

            mode.setOnClickListener {
                switch.performClick()
            }
        }

        var nightmode=sharedpref.getBoolean("night_mode", false)

        if(nightmode){
            NightLightSwitch.performClick()
        }

        NightLightSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.apply {
                    putBoolean("night_mode", true)
                    apply()
                }
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.apply {
                    putBoolean("night_mode", false)
                    apply()
                }
            }

        }

        var sound=sharedpref.getBoolean("sound", false)

        if(sound){
            SoundSwitch.performClick()
        }

        SoundSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                editor.apply {
                    putBoolean("sound", true)
                    apply()
                }
            }else{
                editor.apply {
                    putBoolean("sound", false)
                    apply()
                }
            }

        }

        var Screenkeepon=sharedpref.getBoolean("screenkeepon", false)

        if(Screenkeepon){
            screenkeepon.performClick()
        }

        screenkeepon.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                editor.apply {
                    putBoolean("screenkeepon", true)
                    apply()
                }
            }else{
                editor.apply {
                    putBoolean("screenkeepon", false)
                    apply()
                }
            }

        }

        var Notification=sharedpref.getBoolean("notification", false)

        if(Notification){
            NotificationSwitch.performClick()
        }

        NotificationSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                editor.apply {
                    putBoolean("notification", true)
                    apply()
                }
            }else{
                editor.apply {
                    putBoolean("notification", false)
                    apply()
                }
            }

        }


        var Score=sharedpref.getBoolean("score", false)

        if(Score){
            ScoreSwitch.performClick()
        }

        ScoreSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                editor.apply {
                    putBoolean("score", true)
                    apply()
                }
            }else{
                editor.apply {
                    putBoolean("score", false)
                    apply()
                }
            }

        }

        var Number=sharedpref.getBoolean("number", false)

        if(Number){
            NumberSwitch.performClick()
        }

        NumberSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                editor.apply {
                    putBoolean("number", true)
                    apply()
                }
            }else{
                editor.apply {
                    putBoolean("number", false)
                    apply()
                }
            }

        }

        backsettingsButton.setOnClickListener {
            onBackPressed()
        }


    }
}