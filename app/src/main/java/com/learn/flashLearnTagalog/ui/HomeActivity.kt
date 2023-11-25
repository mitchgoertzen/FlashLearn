package com.learn.flashLearnTagalog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import com.learn.flashLearnTagalog.db.WordDAO
import com.learn.flashLearnTagalog.ui.fragments.HintFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)



        if(DEBUG){

          //  val viewModel: MainViewModel by viewModels()


            //world
//            viewModel.getWordsByDifficultyForLesson("animals", 0, 100).observe(this) {
//                println("animals: ${it.size}")//117
//            }

//            viewModel.getWordsByDifficultyForLesson("geography", 0, 5).observe(this) {
//                println("Geography1: ${it.size}")//55
//            }
//            viewModel.getWordsByDifficultyForLesson("geography", 5, 7).observe(this) {
//                println("Geography2: ${it.size}")//55
//            }
//            viewModel.getWordsByDifficultyForLesson("geography", 7, 100).observe(this) {
//                println("Geography3: ${it.size}")//55
//            }
//
//
//
//            viewModel.getWordsByDifficultyForLesson("nature", 0, 5).observe(this) {
//                println("nature1: ${it.size}")//26
//            }
//            viewModel.getWordsByDifficultyForLesson("nature", 5, 100).observe(this) {
//                println("nature2: ${it.size}")//26
//            }



//            viewModel.getWordsByDifficultyForLesson("around town", 0, 5).observe(this) {
//                println("around town1: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("around town", 5, 7).observe(this) {
//                println("around town2: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("around town", 7, 8).observe(this) {
//                println("around town3: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("around town", 8, 100).observe(this) {
//                println("around town4: ${it.size}")//63
//            }


//            viewModel.getWordsByDifficultyForLesson("countries", 0, 100).observe(this) {
//                println("countries1: ${it.size}")//19
//            }


            //at home
//            viewModel.getWordsByDifficultyForLesson("food", 0, 4).observe(this) {
//                println("food1: ${it.size}")//85
//            }
//            viewModel.getWordsByDifficultyForLesson("food", 4, 5).observe(this) {
//                println("food2: ${it.size}")//85
//            }
//            viewModel.getWordsByDifficultyForLesson("food", 5, 6).observe(this) {
//                println("food3: ${it.size}")//85
//            }
//            viewModel.getWordsByDifficultyForLesson("food", 6, 8).observe(this) {
//                println("food4: ${it.size}")//85
//            }
//            viewModel.getWordsByDifficultyForLesson("food", 8, 100).observe(this) {
//                println("food5: ${it.size}")//85
//            }


//            viewModel.getWordsByDifficultyForLesson("kitchen", 0, 6).observe(this) {
//                println("kitchen1: ${it.size}")//30
//            }
//            viewModel.getWordsByDifficultyForLesson("kitchen", 6, 100).observe(this) {
//                println("kitchen2: ${it.size}")//30
//            }


            //combine to around the house?
//            viewModel.getWordsByDifficultyForLesson("bathroom", 0, 100).observe(this) {
//                println("bathroom: ${it.size}")//12
//            }
//            viewModel.getWordsByDifficultyForLesson("in the yard", 0, 100).observe(this) {
//                println("in the yard: ${it.size}")//21
//            }
//            viewModel.getWordsByDifficultyForLesson("living room", 0, 100).observe(this) {
//                println("living room: ${it.size}")//9
//            }
//            viewModel.getWordsByDifficultyForLesson("bedroom", 0, 100).observe(this) {
//                println("bedroom: ${it.size}")//13
//            }


//            //recreation
//            viewModel.getWordsByDifficultyForLesson("sports & games", 0, 100).observe(this) {
//                println("sports & games: ${it.size}")//0
//            }


            //people
//            viewModel.getWordsByDifficultyForLesson("pronouns", 0, 100).observe(this) {
//                println("pronouns: ${it.size}")//7
//            }
//            viewModel.getWordsByDifficultyForLesson("exclamations/reactions", 0, 100).observe(this) {
//                println("exclamations/reactions: ${it.size}")//0
//            }
//            viewModel.getWordsByDifficultyForLesson("conversation", 0, 100).observe(this) {
//                println("conversation: ${it.size}")//15
//            }
//
//            viewModel.getWordsByDifficultyForLesson("body", 0, 4).observe(this) {
//                println("body1: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("body", 4, 5).observe(this) {
//                println("body2: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("body", 5, 7).observe(this) {
//                println("body3: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("body", 7, 100).observe(this) {
//                println("body4: ${it.size}")//63
//            }
//            viewModel.getWordsByDifficultyForLesson("emotions", 0, 100).observe(this) {
//                println("emotions: ${it.size}")//2
//            }
//            viewModel.getWordsByDifficultyForLesson("actions", 0, 100).observe(this) {
//                println("actions: ${it.size}")//0
//            }


//            viewModel.getWordsByDifficultyForLesson("people", 0, 7).observe(this) {
//                println("people1: ${it.size}")//29
//            }
//            viewModel.getWordsByDifficultyForLesson("people", 7, 100).observe(this) {
//                println("people2: ${it.size}")//29
//            }

//            viewModel.getWordsByDifficultyForLesson("occupations", 0, 5).observe(this) {
//                println("occupations1: ${it.size}")//90
//            }
//            viewModel.getWordsByDifficultyForLesson("occupations", 5, 6).observe(this) {
//                println("occupations2: ${it.size}")//90
//            }
//            viewModel.getWordsByDifficultyForLesson("occupations", 6, 7).observe(this) {
//                println("occupations3: ${it.size}")//90
//            }
//            viewModel.getWordsByDifficultyForLesson("occupations", 7, 8).observe(this) {
//                println("occupations4: ${it.size}")//90
//            }
//            viewModel.getWordsByDifficultyForLesson("occupations", 9, 10).observe(this) {
//                println("occupations5: ${it.size}")//90
//            }
//            viewModel.getWordsByDifficultyForLesson("occupations", 10, 100).observe(this) {
//                println("occupations6: ${it.size}")//90
//            }


            //date & time -- combine
//            viewModel.getWordsByDifficultyForLesson("date & time", 0, 5).observe(this) {
//                println("date & time1: ${it.size}")//18
//            }
//            viewModel.getWordsByDifficultyForLesson("date & time", 5, 100).observe(this) {
//                println("date & time2: ${it.size}")//18
//            }
//            viewModel.getWordsByDifficultyForLesson("days", 0, 100).observe(this) {
//                println("days: ${it.size}")//7
//            }
//            viewModel.getWordsByDifficultyForLesson("months", 0, 100).observe(this) {
//                println("months: ${it.size}")//12
//            }




            //education
//            viewModel.getWordsByDifficultyForLesson("colours", 0, 100).observe(this) {
//                println("colours: ${it.size}")//16
//            }
//            viewModel.getWordsByDifficultyForLesson("chemistry", 0, 100).observe(this) {
//                println("chemistry: ${it.size}")//23
//            }
//            viewModel.getWordsByDifficultyForLesson("numbers", 0, 7).observe(this) {
//                println("numbers1: ${it.size}")//41
//            }
//            viewModel.getWordsByDifficultyForLesson("numbers", 7, 100).observe(this) {
//                println("numbers2: ${it.size}")//41
//            }

        }


        if (launch) {

            MobileAds.initialize(this) {}

            // Create an ad request.
            val adRequest = AdRequest.Builder().build()

            // Start loading the ad in the background.
            binding.adViewHome.loadAd(adRequest)
            launch = false
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
