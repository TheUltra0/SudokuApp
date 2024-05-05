package com.example.sudokufreeads

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class savedata(context: Context) {
    var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}