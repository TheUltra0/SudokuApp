package com.example.sudokufreeads

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.newgamemodes.view.*
import java.util.*
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.view.size
import com.example.sudokufreeads.R
import com.example.sudokufreeads.SudokuMode
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random
import kotlin.random.nextInt



class MainActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    private var adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this){}


        //Interstitialad
        if(mInterstitialAd==null) {
            onload()
        }

        //Interstitialad


        adView.loadAd(adRequest)






        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor: SharedPreferences.Editor = sharedpref.edit()



        var notification=sharedpref.getBoolean("notification", false)
        var boolean=sharedpref.getBoolean("night_mode", false)

        if(boolean) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if(sharedpref.getInt("textsize", 0)==0){
            editor.apply {
                putInt("textsize", 2)
                apply()
            }
        }


        HowToPlay.setOnClickListener {
            Intent(this, Howtoplay::class.java).also{
                startActivity(it)
            }
        }
        settings.setOnClickListener {
            Intent(this, Settings::class.java).also{
                startActivity(it)
            }
        }

        NewGame.setOnClickListener {
           // Log.d(ContentValues.TAG, "$string")
            newgame()


        }


        Continue.setOnClickListener {

            editor.apply {
                putInt("continuesudoku", 1)
                apply()
            }

            showad()

            Intent(this, SudokuMode::class.java).also {
                startActivity(it)
            }
        }


        if(notification) {
            createNotificationChannel()
            scheduleNotification()
        }
    }

    fun onload(){

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


        val RestoreView = LayoutInflater.from(applicationContext).inflate(R.layout.newgamemodes, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(RestoreView)
        val RestoreAlert = mBuilder.show()
        RestoreView.Easy.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(1, "p")

            showad()


        }
        RestoreView.Medium.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(2, "m")

            showad()

        }
        RestoreView.Hard.setOnClickListener {
            RestoreAlert.dismiss()

            restoreview(3, "h")

            showad()


        }
    }

    fun restoreview(int:Int, string:String){

        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


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
        Intent(this, SudokuMode::class.java).also {
            startActivity(it)
        }
    }


    private fun scheduleNotification(){
        var newintent= Intent(this, Notification::class.java)

        val pendingIntent= PendingIntent.getBroadcast(
            applicationContext,
            notificationid,
            newintent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        assert(alarmManager != null)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP , getTime() , AlarmManager.INTERVAL_DAY, pendingIntent)

    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance().apply {
            //Log.d(ContentValues.TAG, "${get(Calendar.HOUR_OF_DAY)}, ${get(Calendar.MINUTE)}")
            if (get(Calendar.HOUR_OF_DAY) < 12) {
                set(Calendar.HOUR_OF_DAY, 12)
            }else if(get(Calendar.HOUR_OF_DAY) < 20){
                set(Calendar.HOUR_OF_DAY, 20)
            }else{
                add(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 12)
            }
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calendar.timeInMillis
    }


    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif Channel"
            val desc = " A Description of the Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelid, name, importance)
            channel.description = desc

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()

        adView.loadAd(adRequest)

        var savedata= savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        if(mInterstitialAd==null) {
            onload()
        }

        if(sharedpref.getInt("continuesudoku", 0)==2){
            Continue.isEnabled=true
            Continue.visibility= View.VISIBLE
            when{
                sharedpref.getInt("mode",0)==1->{
                    Continue.text="Continue\nEasy ${sharedpref.getString("chronometer", "")}"
                }
                sharedpref.getInt("mode",0)==2->{
                    Continue.text="Continue\nMedium ${sharedpref.getString("chronometer", "")}"
                }
                sharedpref.getInt("mode",0)==3->{
                    Continue.text="Continue\nHard ${sharedpref.getString("chronometer", "")}"
                }
            }
        }else{
            Continue.isEnabled=false
            Continue.visibility= View.INVISIBLE
        }
    }





}