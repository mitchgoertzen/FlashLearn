package com.learn.flashLearnTagalog.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.WordDAO
import com.learn.flashLearnTagalog.ui.fragments.HintFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Arrays
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val DEBUG = false

    @Inject
    lateinit var wordDAO: WordDAO

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var binding: ActivityHomeBinding

    private var launch = true

    var size = 0

    private val lessonCreator = LessonCreator()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        val dataProcessor = DataProcessor(resources)


        if (launch) {
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("AAFD161D953789428592D83CCA602CDC"))
            MobileAds.initialize(this) {}

            // Create an ad request.
            val adRequest = AdRequest.Builder().build()

            // Start loading the ad in the background.
            binding.adViewHome.loadAd(adRequest)
            launch = false
        }


        binding.btnAddWords.setOnClickListener {

            val words = dataProcessor.getWords()

            val lessonWords = mutableMapOf<String, Word>()

            Log.d(TAG, "COUNT: ${words.size}")

            for (i in 1..20) {

                val w = words[i]
                lessonWords[w.id] = w

            }

            DataUtility.insertAllWords(lessonWords)

            Log.d(TAG, "LESSON WORD COUNT: ${lessonWords.size}")
        }

        binding.btnAddLessons.setOnClickListener {

            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                async { lessonCreator.createLessons() }.await()

                val lessonList = lessonCreator.getLessons()

                Log.d(TAG, "lesson list size: ${lessonList.size}")
                val lessonMap = mutableMapOf<String, Lesson>()

                for (l in lessonList) {
                    lessonMap[l.id] = l
                }

                Log.d(TAG, "lesson map size: ${lessonMap.size}")
                DataUtility.insertAllLessons(lessonMap)
                scope.cancel()
            }




        }


        val learning = LearningActivity()

        //on lesson button press, start learning activity
        binding.btnLesson.setOnClickListener {
            learning.setType(2)
            val intent = Intent(this, learning::class.java)
            startActivity(intent)
        }

        //on dictionary button press, start learning activity
        binding.btnDictionary.setOnClickListener {
            learning.setType(1)
            val intent = Intent(this, learning::class.java)
            startActivity(intent)
        }

        //display app info in popup dialog
        binding.ibInfo.setOnClickListener {
            val infoText =
                "This app is intended for English speakers who are interested in learning words from the " +
                        "Filipino dialect Tagalog. Grammar lessons have not yet been implemented, but may be in the future\n\n" +
                        "I created this project as a way to practice mobile development in Android Studio, so expect many " +
                        "unpolished features and UI elements\n\n" +
                        "The dictionary database was gathered from: https://tagalog.pinoydictionary.com " +
                        "and scraped using an altered method as found on:\nhttps://github.com/raymelon/tagalog-dictionary-scraper\n\n" +
                        "To report any incorrect or insensitive words, please email mitchgoertzen@gmail.com\n\n" +
                        "2022, mitch goertzen"

            val dialog: DialogFragment = HintFragment(infoText)

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "info popup")
        }

        //go to user profile
        binding.ibProfile.setOnClickListener {
            val dialog: DialogFragment = ProfilePopupFragment(this)

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "profile popup")
        }

        //refreshButtons()
    }


    private fun refreshButtons() {
        //TODO: change?
        if (size > 0) {
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
