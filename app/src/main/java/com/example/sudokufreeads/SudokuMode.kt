package com.example.sudokufreeads

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.gson.Gson
import kotlinx.android.synthetic.main.losegame.*
import kotlinx.android.synthetic.main.sudokumode.*
import kotlinx.android.synthetic.main.losegame.view.*
import kotlinx.android.synthetic.main.newgamemodes.view.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.colorbuttonsettings.*
import kotlinx.android.synthetic.main.colorbuttonsettings.view.*
import kotlinx.android.synthetic.main.resume.view.*
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.sudokumode.view.*
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.example.sudokufreeads.R
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class SudokuMode: AppCompatActivity(), SudokuDelegate {

    private var mInterstitialAd: InterstitialAd? = null

    private var mRewardedAd: RewardedAd? = null

    private var adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sudokumode)

        MobileAds.initialize(this)




        //Interstitialad
        if(mInterstitialAd==null) {
            oninterstitialload()
        }
        //Interstitialad

        ////Rewardedads

        mRewardedAd=null

        Log.d(ContentValues.TAG, "Mrewarded is $mRewardedAd")

        if(mRewardedAd==null) {
            onload()
        }

        ////Rewardedadd


        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var sudokuView=findViewById<SudokuView>(R.id.sudoku_view)
        sudokuView.SudokuDelegate=this



        var string = getresolve()


        ///Continue sudoku
        continuesudoku(sharedpref.getInt("continuesudoku", 0))

        ///Continue sudoku




        ///resume
        resumeButton.setOnClickListener {

            var simpleChronometertime=simpleChronometer.text
            editor.apply {
                putString("chronometer", simpleChronometertime.toString())
                apply()
            }

            simpleChronometer.stop()

            val RestoreView = LayoutInflater.from(applicationContext).inflate(R.layout.resume, null)

            val mBuilder = AlertDialog.Builder(this)
                .setView(RestoreView)
            val RestoreAlert = mBuilder.show()

            RestoreView.timeresumetext.text=sharedpref.getString("chronometer", "00:00").toString()
            if(sharedpref.getInt("mode", 0)==1){
                RestoreView.dificultyresumetext.text="Easy"
            }else if(sharedpref.getInt("mode", 0)==2){
                RestoreView.dificultyresumetext.text="Medium"
            }else{
                RestoreView.dificultyresumetext.text="Hard"
            }



            RestoreView.resume.setOnClickListener {

                var chronometertime=sharedpref.getString("chronometer", "00:00")!!
                simpleChronometer.base=SystemClock.elapsedRealtime() - (((chronometertime[0].toInt()-48)*10+(chronometertime[1].toInt()-48)) * 60000 + ((chronometertime[3].toInt()-48)*10+(chronometertime[4].toInt()-48)) * 1000)
                simpleChronometer.start()

                RestoreAlert.dismiss()

            }
            RestoreView.restartresume.setOnClickListener {

                Wrongs.text="Mistakes:0/3"
                score.text="Score: 0"

                editor.apply {
                    putInt("hint", 1)
                    apply()
                }

                hintcard.visibility=View.INVISIBLE

                sharedpref.edit().remove("scoreint").apply()
                sharedpref.edit().remove("greseli").apply()
                sharedpref.edit().remove("chronometer").apply()
                SudokuMode().clear()
                readstring(getproblem())

                showinterstitialad()

                sharedpref.edit().remove("continuesudoku").apply()

                var chronometertime=sharedpref.getString("chronometer", "00:00")!!
                simpleChronometer.base=SystemClock.elapsedRealtime() - (((chronometertime[0].toInt()-48)*10+(chronometertime[1].toInt()-48)) * 60000 + ((chronometertime[3].toInt()-48)*10+(chronometertime[4].toInt()-48)) * 1000)
                simpleChronometer.start()

                RestoreAlert.dismiss()

            }
        }


        ///resume
        sudoku_view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                sudoku_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                sudokuView.cellSide = min(sudoku_view.width, sudoku_view.height) * sudokuView.scaleCellSide

                when {
                    sharedpref.getInt("textsize",0)==1 -> {
                        sudokuView.smalltext()
                    }
                    sharedpref.getInt("textsize",0)==2 -> {
                        sudokuView.mediumtext()
                    }
                    sharedpref.getInt("textsize",0)==3 -> {
                        sudokuView.largetext()
                    }
                }}
        })

        ///colorButton

        colorButton.setOnClickListener {
            val inflater:LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.colorbuttonsettings,null)

            when {
                sharedpref.getInt("textsize",0)==1 -> {
                    sudokuView.smalltext()
                    view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                    view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                    view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                }
                sharedpref.getInt("textsize",0)==2 -> {
                    sudokuView.mediumtext()
                    view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                    view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                    view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                }
                sharedpref.getInt("textsize",0)==3 -> {
                    sudokuView.largetext()
                    view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                    view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                    view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                }
            }

            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(
                colorButton, // Location to display popup window
                Gravity.NO_GRAVITY, // Exact position of layout to display popup
                colorButton.x.toInt(), // X offset
                colorButton.y.toInt()+2*colorButton.height // Y offset
            )
            view.lightculorbutton.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d(ContentValues.TAG, "yeyeyeye")
                editor.apply{
                    putBoolean("night_mode", false)
                    apply()
                }

            }
            view.nightcolorbutton.setOnClickListener {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.apply{
                    putBoolean("night_mode", true)
                    apply()
                }

            }
            view.smalltext.setOnClickListener {
                sudokuView.smalltext()
                view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                editor.apply {
                    putInt("textsize", 1)
                    apply()
                }

            }
            view.mediumtext.setOnClickListener {
                sudokuView.mediumtext()
                view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                editor.apply {
                    putInt("textsize", 2)
                    apply()
                }
            }
            view.largetext.setOnClickListener {
                sudokuView.largetext()
                view.smalltextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                view.mediumtextbackground.background=resources.getDrawable(R.drawable.colorbuttonnumbers)
                view.largetextbackground.background=resources.getDrawable(R.drawable.colorbuttonpressed)
                editor.apply {
                    putInt("textsize", 3)
                    apply()
                }
            }


        }




        ///colorButton

        //Bar buttons

        backButton.setOnClickListener {
            onBackPressed()
        }

        settingsButton.setOnClickListener {
            Intent(this, Settings::class.java).also {
                startActivity(it)
            }
        }

        //Bar buttons

        ////Prepare sudoku


        for (i in 1..9) {
            editor.apply {
                putInt("num$i", 9)
                apply()
            }
        }

        fail()
        //Log.d(ContentValues.TAG, "${ SudokuGame.numbers}")

        SudokuGame.numbers.forEach { e ->
            var numbernum = savedata.sharedPreferences.getInt("num${e.number}", 0) - 1
            var cardView = constraintLayout2.findViewWithTag(
                "number${
                    e.number
                }"
            ) as CardView
            if(numbernum==0){
                cardView.visibility = View.INVISIBLE
                cardView.isEnabled = false
            }
            //Log.d(ContentValues.TAG, e.toString())
            editor.apply {
                putInt("num${e.number}", numbernum)
                apply()
            }
        }

        settings()

        var sound=sharedpref.getBoolean("sound", false)

        var numberprob = savedata.sharedPreferences.getInt("numberprob", 0)
        numberprob(numberprob)
        if(numberprob==0){
            StilouImg.setImageResource(R.drawable.pen)
            numberprob(0)
        }else{
            StilouImg.setImageResource(R.drawable.bluepen)
            numberprob(1)
        }

        if (savedata.sharedPreferences.getInt("mode", 0) == 3) {
            modeGame.text = " Hard"
        } else if (savedata.sharedPreferences.getInt("mode", 0) == 2) {
            modeGame.text = " Medium"
        } else {
            modeGame.text = " Easy"
        }

        if(sharedpref.getInt("hint", 0)==1){
            hintcard.visibility=View.INVISIBLE
        }else{
            hintcard.visibility=View.VISIBLE
        }


        var chronometertime=sharedpref.getString("chronometer", "00:00")!!
        simpleChronometer.base=SystemClock.elapsedRealtime() - (((chronometertime[0].toInt()-48)*10+(chronometertime[1].toInt()-48)) * 60000 + ((chronometertime[3].toInt()-48)*10+(chronometertime[4].toInt()-48)) * 1000)
        simpleChronometer.start()

        score.text = "Score: ${sharedpref.getInt("scoreint", 0)}"
        Wrongs.setText("Mistakes:${sharedpref.getInt("greseli", 0)}/3")


        ////Prepare sudoku







        ///Stergeti
        stergetiImg.setOnClickListener { stergeti.performClick() }
        stergeti.setOnClickListener {
            if(sound) {
                var BgMusic = MediaPlayer.create(applicationContext, R.raw.eraser)
                BgMusic?.start()
            }
            stergeti()
            stergetiImg.setImageResource(R.drawable.eraser_anim)
            var anim=stergetiImg.drawable as AnimatedVectorDrawable
            anim.start()

        }

        ///Stergeti


        ///Hint

        HintImg.setOnClickListener { Hint.performClick() }
        Hint.setOnClickListener {
            if(savedata.sharedPreferences.getInt("hint", 0) == 1) {

                SudokuGame.sudokuColorSquare.forEach { e ->
                    if (findnumber(e.col, e.row) == null) {

                        Log.d(ContentValues.TAG, "${findnumber(e.col, e.row)}")
                        HintImg.setImageResource(R.drawable.hint_anim)
                        var anim = HintImg.drawable as AnimatedVectorDrawable
                        anim.start()

                        stergeti()
                        numberplayer(e.col, e.row, string[e.col + 9 * e.row].toInt() - 48)
                        var numbernum = savedata.sharedPreferences.getInt(
                            "num${
                                SudokuGame.numberat(
                                    e.col,
                                    e.row
                                )?.number
                            }", 0
                        ) - 1
                        var cardView = constraintLayout2.findViewWithTag(
                            "number${
                                SudokuGame.numberat(
                                    e.col,
                                    e.row
                                )?.number
                            }"
                        ) as CardView
                        var textneed = cardView.findViewWithTag(
                            "TextView${
                                SudokuGame.numberat(
                                    e.col,
                                    e.row
                                )?.number
                            }"
                        ) as TextView

                        if (numbernum == 0) {
                            cardView.visibility = View.INVISIBLE
                            cardView.isEnabled = false
                        }
                        textneed.setText("$numbernum")
                        var scoreint=sharedpref.getInt("scoreint", 0)
                        editor.apply {
                            putInt("num${SudokuGame.numberat(e.col, e.row)?.number}", numbernum)
                            putInt("scoreint", scoreint+savedata.sharedPreferences.getInt("mode", 0) * 100)
                            apply()
                        }
                        score.text = "Score: ${sharedpref.getInt("scoreint", 0)}"

                        sharedpref.edit().remove("hint").apply()
                        hintcard.visibility=View.VISIBLE
                        if(sound) {
                            var BgMusic = MediaPlayer.create(applicationContext, R.raw.hint)
                            BgMusic?.start()
                        }
                    }

                }

            }else{
                runvideo(hintcard)
            }


        }

        ///Hint


        ///Stilou

        StilouImg.setOnClickListener { Stilou.performClick() }
        Stilou.setOnClickListener {
            if(sound) {
                var BgMusic = MediaPlayer.create(applicationContext, R.raw.pen)
                BgMusic?.start()

            }
            var numberprob = savedata.sharedPreferences.getInt("numberprob", 0)
            if (numberprob == 0) {
                StilouImg.setImageResource(R.drawable.pen_anim)

                var anim=StilouImg.drawable as AnimatedVectorDrawable
                anim.start()

                editor.apply {
                    putInt("numberprob", 1)
                    apply()
                }
                numberprob(1)
            } else {
                StilouImg.setImageResource(R.drawable.penblue_animation)
                var anim=StilouImg.drawable as AnimatedVectorDrawable
                anim.start()
                editor.apply {
                    putInt("numberprob", 0)
                    apply()
                }
                numberprob(0)
            }

        }

        ///Stilou


    }

    fun oninterstitialload(){
            InterstitialAd.load(
                this,
                "ca-app-pub-8325955134978078/7629240949",
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(ContentValues.TAG, adError.toString())
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(ContentValues.TAG, "InterAd was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })
    }

    fun showinterstitialad(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(ContentValues.TAG, "InterAd was clicked.")
                oninterstitialload()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(ContentValues.TAG, "InterAd dismissed fullscreen content.")
                oninterstitialload()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.e(ContentValues.TAG, "InterAd failed to show fullscreen content.")
                oninterstitialload()
            }



            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(ContentValues.TAG, "InterAd recorded an impression.")
                oninterstitialload()
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(ContentValues.TAG, "InterAd showed fullscreen content.")
                oninterstitialload()
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            oninterstitialload()
            Log.d(ContentValues.TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    fun onload(){
        Log.d(ContentValues.TAG, "${mRewardedAd}")
            RewardedAd.load(
                this,
                "ca-app-pub-8325955134978078/7763342006",
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(ContentValues.TAG, "RewAd was NOT loaded.")
                        mRewardedAd=null
                        val errorDomain = adError.domain
                        val errorCode =  adError.code
                        val errorMessage =  adError.message
                        val responseInfo =  adError.responseInfo
                        val cause =  adError.cause
                        Log.d("Ads", "$adError\n$errorDomain\n$errorCode\n$errorMessage\n" +
                                "$responseInfo\n$cause")

                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(ContentValues.TAG, "RewAd was loaded.")
                        mRewardedAd = rewardedAd
                    }
                })
    }

    fun runvideo(view:View) {

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()



        if (view == hintcard) {
            if (mRewardedAd != null) {
                mRewardedAd?.show(
                    this
                ) { rewardItem ->
                    hintcard.visibility = View.INVISIBLE
                    editor.apply {
                        putInt("hint", 1)
                        apply()
                    }
                }
            } else {
                onload()

                Toast.makeText(
                    applicationContext,
                    "Error! Try again after few seconds",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(ContentValues.TAG, "The rewarded ad wasn't ready yet.")


            }
        } else {
            if (mRewardedAd != null) {
                mRewardedAd?.show(
                    this
                ) { rewardItem ->
                    var chronometertime = sharedpref.getString("chronometer", "00:00")!!
                    simpleChronometer.base =
                        SystemClock.elapsedRealtime() - (((chronometertime[0].toInt() - 48) * 10 + (chronometertime[1].toInt() - 48)) * 60000 + ((chronometertime[3].toInt() - 48) * 10 + (chronometertime[4].toInt() - 48)) * 1000)
                    simpleChronometer.start()

                    var mistakes = sharedpref.getInt("greseli", 0)
                    editor.apply {
                        putInt("greseli", mistakes - 1)
                        apply()
                    }
                    Wrongs.setText("Mistakes:${sharedpref.getInt("greseli", 0)}/3")
                    Log.d(ContentValues.TAG, "replay")
                }

            } else {
                onload()
                Log.d(ContentValues.TAG, "The rewarded ad wasn't ready yet.")
                Toast.makeText(
                    applicationContext,
                    "Error! Try again after few seconds",
                    Toast.LENGTH_LONG
                ).show()


            }
        }
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(ContentValues.TAG, "RewAd was shown.")
                onload()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(ContentValues.TAG, "RewAd was dismissed.")
                onload()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.d(ContentValues.TAG, "RewAd failed.")
                onload()
            }
        }
    }

    ///Fun stergeti
    fun stergeti(){
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()
        SudokuGame.sudokuColorSquare.forEach { e ->
            if (SudokuGame.numberat(e.col, e.row) != null) {
                modifynumber(e.col, e.row)

                var scoreint=sharedpref.getInt("scoreint", 0)
                editor.apply {
                    putInt("scoreint", scoreint-savedata.sharedPreferences.getInt("mode", 0) * 100)
                    apply()
                }
                score.text = "Score: ${sharedpref.getInt("scoreint", 0)}"
            }
            for (i in 1..9) {
                SudokuGame.numberprob.remove(Numbersprob(e.col, e.row, i))
            }
            SudokuGame.numbersplayer.remove(
                SudokuGame.numberat(
                    e.col,
                    e.row
                )
            ) && SudokuGame.numbers.remove(
                SudokuGame.findnumber(
                    e.col,
                    e.row
                )
            ) || SudokuGame.numberswrongplayer.remove(SudokuGame.findwrongnumber(e.col, e.row))
        }
    }

    ///Fun stergeti


    ///Fun numberprob
    fun numberprob(numberprob: Int) {
        if (numberprob == 1) {
            for (i in 1..9) {
                var textneed = constraintLayout2.findViewWithTag(
                    "TextView$i"
                ) as TextView
                var textneed2 = constraintLayout2.findViewWithTag(
                    "TextView$i$i"
                ) as TextView
                textneed.setTextColor(resources.getColor(R.color.textcolor2))
                textneed2.setTextColor(resources.getColor(R.color.textcolor2))
            }
        } else {
            for (i in 1..9) {
                var textneed = constraintLayout2.findViewWithTag(
                    "TextView$i"
                ) as TextView
                var textneed2 = constraintLayout2.findViewWithTag(
                    "TextView$i$i"
                ) as TextView
                textneed.setTextColor(resources.getColor(R.color.textcolor3))
                textneed2.setTextColor(resources.getColor(R.color.blue))
            }
        }
    }

    ///Fun numberprob


    ///Fun sudokufinish()
    fun sudokufinish(): Boolean {
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var string = getresolve()
        sudoku_view.numberzero()
        for (i in 0..8) {
            for (j in 0..8) {
                if (findnumber(i, j)?.number == string[i + 9 * j].toInt() - 48)
                    sudoku_view.numberadd()
            }
        }
        if (sudoku_view.number == 81) {
            if(sharedpref.getBoolean("sound", false)) {
                var BgMusic = MediaPlayer.create(applicationContext, R.raw.win)
                BgMusic?.start()
            }
            this.finish()
            Intent(this, WinGame::class.java).also{
                startActivity(it)
            }
            return true
        }
        return false

    }

    ///Fun sudokufinish()



    ///Fun modifynumber
    fun modifynumber(col: Int, row: Int) {
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var numbernum = savedata.sharedPreferences.getInt(
            "num${
                SudokuGame.numberat(
                    col,
                    row
                )?.number
            }", 0
        ) + 1
        var cardView = constraintLayout2.findViewWithTag(
            "number${
                SudokuGame.numberat(
                    col,
                    row
                )?.number
            }"
        ) as CardView
        var textneed = cardView.findViewWithTag(
            "TextView${
                SudokuGame.numberat(
                    col,
                    row
                )?.number
            }"
        ) as TextView

        if (numbernum == 1) {
            cardView.visibility = View.VISIBLE
            cardView.isEnabled = true
        }
        textneed.setText("$numbernum")
        editor.apply {
            putInt("num${SudokuGame.numberat(col, row)?.number}", numbernum)
            apply()
        }
    }
    ///Fun modifynumber


    ///fun onclick
    fun onClick(view: View) {

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var sudokuView = findViewById<SudokuView>(R.id.sudoku_view)
        sudokuView.SudokuDelegate = this

        var stringneed = view.toString().let {
            it[it.length - 2]
        }

        var string = getresolve()
        SudokuGame.sudokuColorSquare.forEach { e ->
            if (savedata.sharedPreferences.getInt("numberprob", 0) == 0) {
                if (numberat(e.col, e.row) == null && numberproblemat(
                        e.col,
                        e.row
                    ) == null && findwrongnumber(e.col, e.row) == null
                ) {
                    if (string[e.col + 9 * e.row].toInt() == stringneed.toInt()) {
                        numberplayer(e.col, e.row, stringneed.toInt() - 48)

                        var txtView1 =
                            view.findViewWithTag("TextView${stringneed.toInt() - 48}") as TextView
                        var numbernum =
                            savedata.sharedPreferences.getInt(
                                "num${stringneed.toInt() - 48}",
                                0
                            ) - 1
                        if (numbernum == 0) {
                            view.visibility = View.INVISIBLE
                            view.isEnabled = false
                        }
                        editor.apply {
                            putInt("num${stringneed.toInt() - 48}", numbernum)
                            apply()
                        }
                        txtView1.setText(
                            savedata.sharedPreferences.getInt(
                                "num${stringneed.toInt() - 48}",
                                0
                            ).toString()
                        )

                        var scoreint=sharedpref.getInt("scoreint", 0)
                        editor.apply {
                            putInt("scoreint", scoreint+savedata.sharedPreferences.getInt("mode", 0) * 100)
                            apply()
                        }
                        score.text = "Score: ${sharedpref.getInt("scoreint", 0)}"

                        if (sudokufinish()) {
                            var simpleChronometertime = simpleChronometer.text
                            editor.apply {
                                putString("chronometer", simpleChronometertime.toString())
                                apply()
                            }
                            simpleChronometer.stop()
                        }
                        if(sharedpref.getBoolean("sound", false)) {
                            var BgMusic = MediaPlayer.create(applicationContext, R.raw.place)
                            BgMusic?.start()
                        }
                    } else {
                        if (numberat(e.col, e.row) == null && numberproblemat(
                                e.col,
                                e.row
                            ) == null && findwrongnumber(e.col, e.row) == null
                        ) {
                            numberwrongplayer(e.col, e.row, stringneed.toInt() - 48)

                            var mistakes=sharedpref.getInt("greseli", 0)
                            editor.apply {
                                putInt("greseli", mistakes+1)
                                apply()
                            }
                            Wrongs.setText("Mistakes:${sharedpref.getInt("greseli", 0)}/3")

                            fail()
                        }
                        if(sharedpref.getBoolean("sound", false)) {
                            var BgMusic = MediaPlayer.create(applicationContext, R.raw.wrong)
                            BgMusic?.start()
                        }
                    }
                    for (i in 1..9) {
                        SudokuGame.numberprob.remove(Numbersprob(e.col, e.row, i))
                    }
                }
            } else {
                Log.d(ContentValues.TAG, "I don't understand")
                if (findnumberprob(
                        e.col,
                        e.row,
                        stringneed.toInt() - 48
                    ) == null && findnumber(e.col, e.row) == null && findwrongnumber(
                        e.col,
                        e.row
                    ) == null
                ) {
                    numberprob(e.col, e.row, stringneed.toInt() - 48)
                } else if (findnumberprob(e.col, e.row, stringneed.toInt() - 48) != null) {
                    SudokuGame.numberprob.remove(Numbersprob(e.col, e.row, stringneed.toInt() - 48))
                }
            }
        }

    }
    ///fun onclick


    ///Fun fail
    fun fail(){

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        if (sharedpref.getInt("greseli", 0) == 3) {

            var simpleChronometertime=simpleChronometer.text
            editor.apply {
                putString("chronometer", simpleChronometertime.toString())
                apply()
            }
            simpleChronometer.stop()

            val RestoreView = LayoutInflater.from(applicationContext)
                .inflate(R.layout.losegame,  cl1)

            val mBuilder =  AlertDialog.Builder(this)
                .setView(RestoreView)
            val RestoreAlert = mBuilder.show()
            RestoreAlert.setCancelable(false)

            RestoreView.tryagain.setOnClickListener {

                runvideo(RestoreView.tryagain)

                if(mRewardedAd!=null) {
                    RestoreAlert.dismiss()
                }


            }
            RestoreView.newgame.setOnClickListener {
                RestoreAlert.dismiss()

                newgame()
            }
            RestoreView.restart.setOnClickListener {
                RestoreAlert.dismiss()

                editor.apply {
                    putInt("hint", 1)
                    apply()
                }
                hintcard.visibility=View.INVISIBLE


                showinterstitialad()

                sharedpref.edit().remove("scoreint").apply()
                sharedpref.edit().remove("greseli").apply()
                sharedpref.edit().remove("chronometer").apply()
                SudokuMode().clear()
                readstring(getproblem())

                sharedpref.edit().remove("continuesudoku").apply()
                this.finish()
                Intent(this, SudokuMode::class.java).also {
                    startActivity(it)
                }

            }
        }


    }
    ///Fun fail

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

        showinterstitialad()


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


    ///Fun continuesudoku()
    fun continuesudoku(i:Int){
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        if(i==1) {
            clear()
            val enums: Collection<NumbersPlayer> = Gson().fromJson(
                sharedpref.getString("numbersplayercol", null),
                object : TypeToken<Collection<NumbersPlayer?>?>() {}.type
            )
            SudokuGame.numbersplayer.addAll(enums)


            val enums2: Collection<NumbersProblem> = Gson().fromJson(
                sharedpref.getString("numbersproblemcol", null),
                object : TypeToken<Collection<NumbersProblem?>?>() {}.type
            )
            SudokuGame.numbersproblem.addAll(enums2)


            val enums3: Collection<Numbers> = Gson().fromJson(
                sharedpref.getString("numberscol", null),
                object : TypeToken<Collection<Numbers?>?>() {}.type
            )
            SudokuGame.numbers.addAll(enums3)


            val enums4: Collection<NumberWrongPlayer> = Gson().fromJson(
                sharedpref.getString("numberswrongplayercol", null),
                object : TypeToken<Collection<NumberWrongPlayer?>?>() {}.type
            )
            SudokuGame.numberswrongplayer.addAll(enums4)


            val enums5: Collection<Numbersprob> = Gson().fromJson(
                sharedpref.getString("numberprobcol", null),
                object : TypeToken<Collection<Numbersprob?>?>() {}.type
            )
            SudokuGame.numberprob.addAll(enums5)

        }
    }

    ///Fun continuesudoku()

    /////Fun settings

    fun settings(){
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var Screenkeepon=sharedpref.getBoolean("screenkeepon", false)
        var Score=sharedpref.getBoolean("score", false)

        if(Screenkeepon){
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if(Score){
            score.visibility=View.VISIBLE

        }else{
            score.visibility=View.INVISIBLE
        }

        var Number=sharedpref.getBoolean("number", false)

        if(Number) {
            for (i in 1..9) {
                var textneed = constraintLayout2.findViewWithTag(
                    "TextView$i"
                ) as TextView
                textneed.visibility=View.VISIBLE
                textneed.setText(savedata.sharedPreferences.getInt("num$i", 0).toString())
                Log.d(ContentValues.TAG, "num$i is ${savedata.sharedPreferences.getInt("num$i", 0).toString()}")
            }
        }else{
            for (i in 1..9) {
                var textneed = constraintLayout2.findViewWithTag(
                    "TextView$i"
                ) as TextView
                textneed.visibility=View.INVISIBLE
            }
        }


    }

    /////Fun settings

    ////Clear sudoku
    fun getproblem(): String {
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        return when {
            sharedpref.getInt("mode", 0)==1 -> {
                getString(resources.getIdentifier("p${sharedpref.getInt("numproblem", 0)}s1", "string", packageName))
            }
            sharedpref.getInt("mode", 0)==2 -> {
                getString(resources.getIdentifier("m${sharedpref.getInt("numproblem", 0)}s1", "string", packageName))
            }
            else -> {
                getString(resources.getIdentifier("h${sharedpref.getInt("numproblem", 0)}s1", "string", packageName))
            }
        }
    }

    fun getresolve(): String {
        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        return when {
            sharedpref.getInt("mode", 0)==1 -> {
                getString(resources.getIdentifier("p${sharedpref.getInt("numproblem", 0)}s2", "string", packageName))
            }
            sharedpref.getInt("mode", 0)==2 -> {
                getString(resources.getIdentifier("m${sharedpref.getInt("numproblem", 0)}s2", "string", packageName))
            }
            else -> {
                getString(resources.getIdentifier("h${sharedpref.getInt("numproblem", 0)}s2", "string", packageName))
            }
        }
    }

    override fun clear() {
        SudokuGame.clear()
    }

    ////Clear sudoku


    override fun sudokuColorSquare(col: Int, row: Int) {
        SudokuGame.sudokuColorSquare(col, row)
    }

    override fun numberplayer(col: Int, row: Int, number: Int) {
        SudokuGame.numberplayer(col, row, number)
    }

    override fun numberat(col: Int, row: Int): NumbersPlayer? {
        return SudokuGame.numberat(col, row)
    }

    override fun readstring(string: String) {
        SudokuGame.readstring(string)
    }

    override fun numberproblemat(col: Int, row: Int): NumbersProblem? {
        return SudokuGame.numberproblemat(col, row)
    }

    override fun findnumber(col: Int, row: Int): Numbers? {
        return SudokuGame.findnumber(col, row)
    }

    override fun numberwrongplayer(col: Int, row: Int, number: Int) {
        SudokuGame.numberwrongplayer(col, row, number)
    }

    override fun findwrongnumber(col: Int, row: Int): NumberWrongPlayer? {
        return SudokuGame.findwrongnumber(col, row)
    }

    override fun findnumberprob(col: Int, row: Int, number2: Int): Numbersprob? {
        return SudokuGame.findnumberprob(col, row, number2)
    }

    override fun numberprob(col: Int, row: Int, number: Int) {
        SudokuGame.numberprob(col, row, number)
    }






    override fun onResume() {
        super.onResume()

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()


        if(mInterstitialAd==null) {
            oninterstitialload()
        }

        if(mRewardedAd==null) {
            onload()
        }


        var chronometertime=sharedpref.getString("chronometer", "00:00")!!
        simpleChronometer.base=SystemClock.elapsedRealtime() - (((chronometertime[0].toInt()-48)*10+(chronometertime[1].toInt()-48)) * 60000 + ((chronometertime[3].toInt()-48)*10+(chronometertime[4].toInt()-48)) * 1000)
        simpleChronometer.start()

    }

    override fun onPause() {
        super.onPause()

        //oninterstitialload()

        //onload()

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var simpleChronometertime=simpleChronometer.text
        editor.apply{
            putString("chronometer", simpleChronometertime.toString())
            putString("numbersplayercol", Gson().toJson(SudokuGame.numbersplayer))
            putString("numbersproblemcol", Gson().toJson(SudokuGame.numbersproblem))
            putString("numberscol", Gson().toJson(SudokuGame.numbers))
            putString("numberswrongplayercol", Gson().toJson(SudokuGame.numberswrongplayer))
            putString("numberprobcol", Gson().toJson(SudokuGame.numberprob))
            putInt("continuesudoku", 2)
            apply()
        }
        simpleChronometer.stop()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var simpleChronometertime=simpleChronometer.text

        editor.apply{
            putString("chronometer", simpleChronometertime.toString())
            putString("numbersplayercol", Gson().toJson(SudokuGame.numbersplayer))
            putString("numbersproblemcol", Gson().toJson(SudokuGame.numbersproblem))
            putString("numberscol", Gson().toJson(SudokuGame.numbers))
            putString("numberswrongplayercol", Gson().toJson(SudokuGame.numberswrongplayer))
            putString("numberprobcol", Gson().toJson(SudokuGame.numberprob))
            putInt("continuesudoku", 2)
            apply()
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var savedata = savedata(applicationContext)
        var sharedpref: SharedPreferences = savedata.sharedPreferences
        val editor = sharedpref.edit()

        var simpleChronometertime=simpleChronometer.text

        editor.apply{
            putString("chronometer", simpleChronometertime.toString())
            putString("numbersplayercol", Gson().toJson(SudokuGame.numbersplayer))
            putString("numbersproblemcol", Gson().toJson(SudokuGame.numbersproblem))
            putString("numberscol", Gson().toJson(SudokuGame.numbers))
            putString("numberswrongplayercol", Gson().toJson(SudokuGame.numberswrongplayer))
            putString("numberprobcol", Gson().toJson(SudokuGame.numberprob))
            putInt("continuesudoku", 2)
            apply()
        }


    }



}