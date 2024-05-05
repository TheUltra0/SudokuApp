package com.example.sudokufreeads

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sudokufreeads.SudokuMode
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.newgamemodes.view.*
import kotlinx.android.synthetic.main.wingame.*
import kotlin.random.Random
import kotlin.random.nextInt

class WinGame: AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wingame)

        //Interstitialad
        if(mInterstitialAd==null) {
            onload()
        }
        //Interstitialad


        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        sharedpref.edit().remove("continuesudoku").apply()

        when {
            sharedpref.getInt("mode", 0)==1 -> {
                dificultytext.text="Easy"
            }
            sharedpref.getInt("mode", 0)==2 -> {
                dificultytext.text="Medium"
            }
            else -> {
                dificultytext.text="Hard"
            }
        }
        if(sharedpref.getString("besttime", "00:00")!="00:00") {
           if ((sharedpref.getString("besttime", "00:00")!![0].toInt()-48)*1000+(sharedpref.getString("besttime", "00:00")!![1].toInt()-48)*100+(sharedpref.getString("besttime", "00:00")!![3].toInt()-48)*10+(sharedpref.getString("besttime", "00:00")!![4].toInt()-48)
                    >(sharedpref.getString("chronometer", "00:00")!![0].toInt()-48)*1000+(sharedpref.getString("chronometer", "00:00")!![1].toInt()-48)*100+(sharedpref.getString("chronometer", "00:00")!![3].toInt()-48)*10+(sharedpref.getString("chronometer", "00:00")!![4].toInt()-48)){
                editor.apply {
                    putString("besttime", sharedpref.getString("chronometer", "00:00"))
                    apply()
                }
            }
        } else{
            editor.apply {
                putString("besttime", sharedpref.getString("chronometer", "00:00"))
                apply()
            }
        }

        timetext.text=sharedpref.getString("chronometer", "00:00")
        scoretext.text=sharedpref.getInt("scoreint", 0).toString()
        besttimetext.text=sharedpref.getString("besttime", "00:00")

        sharedpref.edit().remove("scoreint").apply()
        sharedpref.edit().remove("greseli").apply()
        sharedpref.edit().remove("chronometer").apply()
        SudokuMode().clear()

        NEWGAME.setOnClickListener {
            newgame()
        }

        home.setOnClickListener {
            this.finish()
        }


    }
    fun onload(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-8325955134978078/7629240949", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(ContentValues.TAG, adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(ContentValues.TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    fun showad(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(ContentValues.TAG, "Ad was clicked.")
                onload()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(ContentValues.TAG, "Ad dismissed fullscreen content.")
                onload()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.e(ContentValues.TAG, "Ad failed to show fullscreen content.")
                onload()
            }



            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(ContentValues.TAG, "Ad recorded an impression.")
                onload()
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                onload()
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d(ContentValues.TAG, "The interstitial ad wasn't ready yet.")
            onload()
        }
    }

    fun newgame(){
        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        val RestoreView = LayoutInflater.from(applicationContext).inflate(R.layout.newgamemodes, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(RestoreView)
        val RestoreAlert = mBuilder.show()
        RestoreView.Easy.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(1, "p")


        }
        RestoreView.Medium.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(2, "m")


        }
        RestoreView.Hard.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(3, "h")



        }
    }

    fun restoreview(int:Int, string:String){

        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        showad()

        sharedpref.edit().remove("scoreint").apply()
        sharedpref.edit().remove("greseli").apply()
        sharedpref.edit().remove("chronometer").apply()
        SudokuMode().clear()

        var randomint=Random.nextInt(1..100)

        var string=getString(resources.getIdentifier("$string${randomint}s1","string", packageName))
        SudokuMode().readstring(string)

        editor.apply {
            putInt("numproblem", randomint)
            putInt("mode", int)
            putInt("hint", 1)
            apply()
        }
        sharedpref.edit().remove("continuesudoku").apply()
        this.finish()
        Intent(this, SudokuMode::class.java).also {
            startActivity(it)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        this.finish()
    }

    override fun onResume() {
        super.onResume()

        if(mInterstitialAd==null) {
            onload()
        }

    }

}