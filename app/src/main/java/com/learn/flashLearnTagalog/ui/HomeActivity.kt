package com.learn.flashLearnTagalog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.HintFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.viewmodels.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: DialogViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    private var launch = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        val binding: ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        val infoDialog: DialogFragment = HintFragment()
        val profileDialog: DialogFragment = ProfilePopupFragment()
        val learning = LearningActivity()
        val infoText =
            "This app is intended for English speakers who are interested in learning words from the " +
                    "Filipino dialect Tagalog. Grammar lessons have not yet been implemented, but may be in the future\n\n" +
                    "I created this project as a way to practice mobile development in Android Studio, so expect many " +
                    "unpolished features and UI elements\n\n" +
                    "The dictionary database was gathered from: https://tagalog.pinoydictionary.com " +
                    "and scraped using an altered method as found on:\nhttps://github.com/raymelon/tagalog-dictionary-scraper\n\n" +
                    "To report any incorrect or insensitive words, please email mitchgoertzen@gmail.com\n\n" +
                    "2023, mitch goertzen"
        val view = binding.root

        setContentView(view)

        if (launch) {
            MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            binding.adViewHome.loadAd(adRequest)
            launch = false
        }

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
            viewModel.updateText(infoText)
            infoDialog.isCancelable = true
            infoDialog.show(this.supportFragmentManager, "info popup")
        }

        //go to user profile
        binding.ibProfile.setOnClickListener {

            if (!profileDialog.isAdded) {
                profileDialog.isCancelable = true
                profileDialog.show(this.supportFragmentManager, "profile popup")
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("this.finishAffinity()"))
    override fun onBackPressed() {
        this.finishAffinity()
    }
}


//        binding.btnAll.setOnClickListener {
//            val scope = CoroutineScope(Job() + Dispatchers.Main)
//            scope.launch {
//                val someList = DataUtility.getAllWords().toMutableList()
//                Log.d(TAG, "reads used: ${someList.size}")
//                scope.cancel()
//            }
//        }

//        binding.btnFilter.setOnClickListener {
//            val scope = CoroutineScope(Job() + Dispatchers.Main)
//            scope.launch {
//              //  val someList = DataUtility.getAllWordsForLesson("animals", 4, 5).toMutableList()
//
//                Log.d(TAG, "reads used: ${someList.size}")
//                scope.cancel()
//            }
//        }
//
//        binding.btnStart.setOnClickListener {
//            val scope = CoroutineScope(Job() + Dispatchers.Main)
//            scope.launch {
//                val someList = DataUtility.getAllWordsForLesson("animals", 4).toMutableList()
//
//                Log.d(TAG, "reads used: ${someList.size}")
//                scope.cancel()
//            }
//        }


//        binding.btnAddWords.setOnClickListener {
//
//            val words = dataProcessor.getWords()
//
//            val lessonWords = mutableMapOf<String, Word>()
//
//            Log.d(TAG, "COUNT: ${words.size}")
//
//            for (i in 0 until words.size) {
//
//                val w = words[i]
//                lessonWords[w.id] = w
//
//            }
//
//            DataUtility.insertAllWords(lessonWords)
//
//            Log.d(TAG, "LESSON WORD COUNT: ${lessonWords.size}")
//        }

//        binding.btnAddLessons.setOnClickListener {
//
//            val scope = CoroutineScope(Job() + Dispatchers.Main)
//            scope.launch {
//                async { lessonCreator.createLessons(resources) }.await()
//
//                val lessonList = lessonCreator.getLessons()
//
//                val lessonMap = mutableMapOf<String, Lesson>()
//
//                for (l in lessonList) {
//                    lessonMap[l.id] = l
//                }
//
//                DataUtility.insertAllLessons(lessonMap)
//                scope.cancel()
//            }
//
//
//        }