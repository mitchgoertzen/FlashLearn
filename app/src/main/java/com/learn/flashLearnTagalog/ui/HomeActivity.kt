package com.learn.flashLearnTagalog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import com.learn.flashLearnTagalog.db.WordDAO
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.HintFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var wordDAO : WordDAO

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var binding: ActivityHomeBinding

    private var launch = true

    var size = 0
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)



        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root



        setContentView(view)
        //

        if(launch){
            // Log the Mobile Ads SDK version.
           println("Google Mobile Ads SDK Version: " + MobileAds.getVersion())

            // Initialize the Mobile Ads SDK with an AdMob App ID.
            MobileAds.initialize(this) {}

            // Set your test devices. Check your logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
            // to get test ads on this device."
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
            )

            // Create an ad request.
            val adRequest = AdRequest.Builder().build()

            // Start loading the ad in the background.
            binding.adView.loadAd(adRequest)
            launch = false
        }

        val learning =  LearningActivity()

        binding.btnLesson.setOnClickListener{
            learning.setType(2)
            val intent = Intent(this, learning::class.java)
            startActivity(intent)
        }

        binding.btnDictionary.setOnClickListener{

            learning.setType(1)
            val intent = Intent(this, learning::class.java)
            startActivity(intent)
        }

        binding.ibInfo.setOnClickListener{
            val infoText = "This app is intended for English speakers who are interested in learning words from the " +
                    "Filipino dialect Tagalog. Grammar lessons have not yet been implemented, but may be in the future\n\n" +
                    "I created this project as a way to practice mobile development in Android Studio, so expect many " +
                    "unpolished features and UI elements\n" +
                    "The dictionary database was gathered from: https://tagalog.pinoydictionary.com " +
                    "and scraped using an altered method as found on:\nhttps://github.com/raymelon/tagalog-dictionary-scraper\n\n" +
                    "2022, mitch goertzen"

            val dialog : DialogFragment = HintFragment(infoText)

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "info popup")
        }

        binding.ibProfile.setOnClickListener{
            val dialog : DialogFragment = ProfilePopupFragment(this)

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "profile popup")
        }


        refreshButtons()

    }


    private fun refreshButtons(){
        //need to change
        if(!sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)) {
            binding.btnLesson.isEnabled = true
            binding.btnDictionary.isEnabled = true
        } else {
            binding.btnLesson.isEnabled = false
            binding.btnDictionary.isEnabled = false
        }
    }


    override fun onBackPressed() {
        this.finishAffinity()
    }
}
