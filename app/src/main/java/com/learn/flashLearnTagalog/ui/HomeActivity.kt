package com.learn.flashLearnTagalog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.learn.flashLearnTagalog.db.WordDAO
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.HintFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.fragments.SetupFragment
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var wordDAO : WordDAO

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: MainViewModel by viewModels()
    var size = 0
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref.edit().clear().apply()

        size = viewModel.getSize()

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


        val initText : TextView =  view.findViewById(R.id.tvInit)

        initText.visibility = View.GONE
        refreshButtons()

        val isFirstOpen = sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)

        if(isFirstOpen) {
            writeSettingsToSharedPref()
            println("first open")
            initText.visibility = View.VISIBLE
            init(initText)
        }

    }

    @DelicateCoroutinesApi
    fun init(initText:TextView){

        GlobalScope.launch {
            suspend {
                val fragment = SetupFragment()

                supportFragmentManager.beginTransaction()
                    .replace(R.id.flInit, fragment).addToBackStack("setup").commit()

                Handler(Looper.getMainLooper()).postDelayed({
                    initText.visibility = View.GONE

                    try {
                        supportFragmentManager.popBackStack()
                    } catch (ignored: IllegalStateException) {
                        // There's no way to avoid getting this if saveInstanceState has already been called.
                    }

                    refreshButtons()

                    println("SIZE: " + viewModel.getSize())
                }, 500)
            }.invoke()
        }
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

    private fun writeSettingsToSharedPref() : Boolean{
        sharedPref.edit()
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }

    override fun onBackPressed() {
        this.finishAffinity()
    }
}
