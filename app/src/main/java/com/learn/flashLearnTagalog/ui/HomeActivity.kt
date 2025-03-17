//package com.learn.flashLearnTagalog.ui
//
//import android.annotation.SuppressLint
//import android.content.ContentValues.TAG
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.viewModels
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.DialogFragment
//import androidx.navigation.NavController
//import androidx.navigation.ui.AppBarConfiguration
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.RequestConfiguration
//import com.google.android.material.navigation.NavigationView
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//import com.learn.flashLearnTagalog.R
//import com.learn.flashLearnTagalog.databinding.ActivityHomeBinding
//import com.learn.flashLearnTagalog.other.Constants
//import com.learn.flashLearnTagalog.ui.dialog_fragments.HintDialogFragment
//import com.learn.flashLearnTagalog.ui.dialog_fragments.ProfilePopupFragment
//import com.learn.flashLearnTagalog.ui.viewmodels.DialogViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//
//@AndroidEntryPoint
//class HomeActivity : AppCompatActivity() {
//
//    private val dialogViewModel: DialogViewModel by viewModels()
//
//    @Inject
//    lateinit var sharedPref: SharedPreferences
//    private lateinit var auth: FirebaseAuth
//    private lateinit var navController: NavController
//    private lateinit var nav: NavigationView
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var appBar : AppBarConfiguration
//
//    private var launch = true
//
//    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN ,android.R.anim.linear_interpolator, android.R.anim.linear_interpolator)
//        sharedPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
//        auth = Firebase.auth
////        navController = findNavController(R.id.navFragment)
////        nav = findViewById(R.id.navView)
////        drawerLayout = findViewById(R.id.drawerLayout)
////        appBar = AppBarConfiguration(navController.graph, drawerLayout)
////
////
////        nav.setupWithNavController(navController)
////        setupActionBarWithNavController(navController, appBar)
//
//        val binding: ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
//        val infoDialog: DialogFragment = HintDialogFragment()
//        val profileDialog = ProfilePopupFragment()
//        val learning = LearningActivity()
//        val infoText =
//            "This app is intended for English speakers who are interested in learning words from the " +
//                    "Filipino dialect Tagalog. Grammar lessons have not yet been implemented, but may be in the future\n\n" +
//                    "I created this project as a way to practice mobile development in Android Studio, so expect many " +
//                    "unpolished features and UI elements\n\n" +
//                    "The dictionary database was gathered from: https://tagalog.pinoydictionary.com " +
//                    "and scraped using an altered method as found on:\nhttps://github.com/raymelon/tagalog-dictionary-scraper\n\n" +
//                    "To report any incorrect or insensitive words, please email mitchgoertzen@gmail.com\n\n" +
//                    "2023, mitch goertzen"
//        val view = binding.root
//
//
//        profileDialog.setOnDismissFunction {
//            checkUser(binding)
//        }
//
//        checkUser(binding)
//        setContentView(view)
//
//        if (launch) {
//
//            MobileAds.initialize(this) {}
//
//            val configurationBuilder = MobileAds.getRequestConfiguration().toBuilder()
//
//            configurationBuilder.setTagForChildDirectedTreatment(
//                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
//            )
//            configurationBuilder.setMaxAdContentRating(
//                RequestConfiguration.MAX_AD_CONTENT_RATING_G
//            )
//
//            MobileAds.setRequestConfiguration(configurationBuilder.build())
//            val adRequest = AdRequest.Builder().build()
//            binding.adViewHome.loadAd(adRequest)
//            launch = false
//        }
//
//        binding.btnLesson.setOnClickListener {
//            learning.setType(2)
//            val intent = Intent(this, learning::class.java)
//            startActivity(intent)
//        }
//
//        //on dictionary button press, start learning activity
//        binding.btnDictionary.setOnClickListener {
//            learning.setType(1)
//            val intent = Intent(this, learning::class.java)
//            startActivity(intent)
//        }
//
//        //display app info in popup dialog
//        binding.ibInfo.setOnClickListener {
//            if (!infoDialog.isAdded) {
//                viewModel.updateText(infoText)
//                infoDialog.isCancelable = true
//                infoDialog.show(this.supportFragmentManager, "info popup")
//            }
//        }
//
//        //go to user profile
//        binding.ibProfile.setOnClickListener {
//
//            if (!profileDialog.isAdded) {
//                profileDialog.isCancelable = true
//                profileDialog.show(this.supportFragmentManager, "profile popup")
//
//
//            }
//        }
//    }
//
//    @SuppressLint("MissingSuperCall")
//    override fun onBackPressed() {
//        super.onBackPressedDispatcher.onBackPressed()
//        this.finishAffinity()
//    }
//
//    private fun checkUser(binding : ActivityHomeBinding){
//        Log.d(TAG , "USER: ${auth.currentUser}")
//        if (auth.currentUser == null){
//            Log.d(TAG , "NULL")
//            binding.ibProfile.setImageResource(R.drawable.profile_alert)
//        }
//        else{
//            Log.d(TAG , "EXISTS")
//            binding.ibProfile.setImageResource(R.drawable.profile)
//        }
//    }
//}