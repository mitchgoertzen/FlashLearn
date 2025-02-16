package com.learn.flashLearnTagalog.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.ads.AdRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
import com.learn.flashLearnTagalog.databinding.ActivityMainBinding
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_IN_LESSONS
import com.learn.flashLearnTagalog.ui.dialog_fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.fragments.DictionaryFragment
import com.learn.flashLearnTagalog.ui.fragments.LessonSelectFragment
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private var type: Int = -1

@AndroidEntryPoint
class LearningActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: SignInViewModel by viewModels()
    private val select = LessonSelectFragment()

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    private var inSettings: Boolean = true


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        sharedPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val profileDialog = ProfilePopupFragment()
        val count: Int = supportFragmentManager.backStackEntryCount
        val intent = Intent(this, HomeActivity::class.java)

        setContentView(view)


        profileDialog.setOnDismissFunction {
            checkUser(binding)
        }

        checkUser(binding)

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
                viewModel.updateRefreshCallback { select.refreshList(null, this) }
            }

            if(!profileDialog.isAdded){
                profileDialog.isCancelable = true
                profileDialog.show(this.supportFragmentManager, "profile popup")
            }
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

    private fun checkUser(binding : ActivityMainBinding){
        Log.d(TAG , "USER: ${auth.currentUser}")
        if (auth.currentUser == null){
            Log.d(TAG , "NULL")
            binding.ibProfile.setImageResource(R.drawable.profile_alert)
        }
        else{
            Log.d(TAG , "EXISTS")
            binding.ibProfile.setImageResource(R.drawable.profile)
        }
    }

}