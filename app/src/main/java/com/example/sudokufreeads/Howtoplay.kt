package com.example.sudokufreeads

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.howtoplay.*
import android.view.ViewTreeObserver
import androidx.core.view.updateLayoutParams
import com.example.sudokufreeads.savedata
import com.example.sudokufreeads.R


class Howtoplay: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.howtoplay)


        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences




        val vto: ViewTreeObserver = imageView2.viewTreeObserver
        vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove after the first run so it doesn't fire forever
                imageView2.viewTreeObserver.removeOnPreDrawListener(this)
                var finalHeight = imageView2.measuredHeight
                var finalWidth = imageView2.measuredWidth
                imageView2.updateLayoutParams { width=finalWidth
                    height=finalWidth }
                imageView3.updateLayoutParams { width=finalWidth
                    height=finalWidth }
                imageView4.updateLayoutParams { width=finalWidth
                    height=finalWidth }
                imageView5.updateLayoutParams { width=finalWidth
                    height=finalWidth }
                imageView6.updateLayoutParams { width=finalWidth
                    height=finalWidth }
                //Log.d(ContentValues.TAG, "$finalHeight $finalWidth")
                return true
            }
        })

        backhowtoplayButton.setOnClickListener {
            onBackPressed()
        }


    }
}