package com.learn.flashLearnTagalog.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivityMainBinding
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_IN_LESSONS
import com.learn.flashLearnTagalog.ui.fragments.DictionaryFragment
import com.learn.flashLearnTagalog.ui.fragments.LessonSelectFragment
import com.learn.flashLearnTagalog.ui.fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private var type: Int = -1

@AndroidEntryPoint
class LearningActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: SignInViewModel by viewModels()
    private val select = LessonSelectFragment()

    private lateinit var binding: ActivityMainBinding
    private var inSettings: Boolean = true

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val dialog: DialogFragment = ProfilePopupFragment()
        val count: Int = supportFragmentManager.backStackEntryCount
        val intent = Intent(this, HomeActivity::class.java)

        setContentView(view)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adViewLearning.loadAd(adRequest)

        //when learning activity is started, choose background colour based on type
        //1 = dictionary, 2 = lessons, 3 = stats
        val bkgColor: Int = if (type > 1)
            resources.getColor(R.color.blue)
        else
            resources.getColor(R.color.red)

        binding.ivBackground.setBackgroundColor(bkgColor)

        //start home activity on home button press
        binding.ibHome.setOnClickListener {

            if (sharedPref.getBoolean(Constants.KEY_IN_TEST, true)) {
                sharedPref.edit()
                    .putBoolean(Constants.KEY_IN_TEST, false)
                    .apply()
            }

            //clear fragment stack
            for (i in 0 until count) {
                supportFragmentManager.popBackStack()
            }
            startActivity(intent)
        }

        //show profile popup dialog
        binding.ibProfile.setOnClickListener {
            Log.d(TAG, "IN LESSONS: ${sharedPref.getBoolean(KEY_IN_LESSONS, false)}")
            if (sharedPref.getBoolean(KEY_IN_LESSONS, false)) {
                viewModel.updateRefreshActive(sharedPref.getBoolean(KEY_IN_LESSONS, false))
                viewModel.updateRefreshCallback { select.refreshList() }
            }

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "profile popup")
        }

        //show appropriate fragment, based on type set
        when (type) {
            1 -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_container, DictionaryFragment())
                    .addToBackStack("dictionary").commit()
            }

            2 -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_container, select)
                    .addToBackStack("lesson select").commit()
            }
//            3 -> {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_nav_container, StatsFragment()).addToBackStack("stats")
//                    .commit()
//            }
        }
    }

    fun transitionFragment() {
        inSettings = !inSettings
    }

    override fun onBackPressed() {
        //if the app is in a test, set in test to false
        if (sharedPref.getBoolean(Constants.KEY_IN_TEST, true)) {
            //TODO: add warning for loss of progress, as well as confirmation to exit test and go back
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_TEST, false)
                .apply()
        }
        val stack = supportFragmentManager.backStackEntryCount
        //if stack is 1 or less, clear stack and start home activity
        if (stack < 2) {
            for (i in 0..stack) {
                supportFragmentManager.popBackStack()
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            //if app is current in a results page, the back button will skip the just-completed lesson, and
            //and transition to the page visited prior
            if (sharedPref.getBoolean(Constants.KEY_IN_RESULTS, false)) {
                supportFragmentManager.popBackStack()
                sharedPref.edit()
                    .putBoolean(Constants.KEY_IN_RESULTS, false)
                    .apply()
            }
            //remove current fragment from stack, which in turn transitions to previous fragment
            supportFragmentManager.popBackStack()
        }
    }

    fun setType(t: Int) {
        type = t
    }

}