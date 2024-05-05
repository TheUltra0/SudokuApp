package com.example.sudokufreeads

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.sudokufreeads.R
import com.example.sudokufreeads.savedata
import kotlinx.android.synthetic.main.colorbuttonsettings.*

class colorbutton:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.colorbuttonsettings)

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        lightculorbutton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Log.d(ContentValues.TAG, "yeyeyeye")
            editor.apply{
                putBoolean("night_mode", false)
                apply()
            }

        }
        nightcolorbutton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.apply{
                putBoolean("night_mode", true)
                apply()
            }

        }
    }
}