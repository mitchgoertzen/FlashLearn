package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivityMainBinding
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_HOME
import com.learn.flashLearnTagalog.other.Constants.KEY_IN_LESSONS
import com.learn.flashLearnTagalog.other.Constants.KEY_ORGANIZATION_ID
import com.learn.flashLearnTagalog.other.Constants.KEY_ORGANIZATION_NAME
import com.learn.flashLearnTagalog.other.UtilityFunctions
import com.learn.flashLearnTagalog.ui.dialog_fragments.HintDialogFragment
import com.learn.flashLearnTagalog.ui.dialog_fragments.ProfilePopupFragment
import com.learn.flashLearnTagalog.ui.fragments.HomeFragment
import com.learn.flashLearnTagalog.ui.fragments.LessonSelectFragment
import com.learn.flashLearnTagalog.ui.viewmodels.DialogViewModel
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private var type: Int = -1

@AndroidEntryPoint
class LearningActivity : AppCompatActivity(R.layout.activity_main) {


    private val viewModel: SignInViewModel by viewModels()
    private val dialogViewModel: DialogViewModel by viewModels()
    private val select = LessonSelectFragment()

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    private var inSettings: Boolean = true


    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var scope: CoroutineScope

        auth = Firebase.auth
        sharedPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)


//        MobileAds.initialize(this) {}
//
//        val configurationBuilder = MobileAds.getRequestConfiguration().toBuilder()
//
//        configurationBuilder.setTagForChildDirectedTreatment(
//            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
//        )
//        configurationBuilder.setMaxAdContentRating(
//            RequestConfiguration.MAX_AD_CONTENT_RATING_G
//        )
//
//        MobileAds.setRequestConfiguration(configurationBuilder.build())
//        val adRequest = AdRequest.Builder().build()
//        binding.adViewLearning.loadAd(adRequest)


        val view = binding.root
        val profileDialog = ProfilePopupFragment()
        val infoDialog: DialogFragment = HintDialogFragment()

        var signInSection: LinearLayout
        var orgName: EditText
        var orgPasscode: EditText
        var orgSignIn: Button

        var signOutSection: LinearLayout
        var orgSignOut: Button

        setContentView(view)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //  navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_nav_container, HomeFragment()).commit()


            //  navigationView.setCheckedItem(R.id.nav_home)
        }

        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

                scope = CoroutineScope(Job() + Dispatchers.Main)

                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

                signInSection = findViewById(R.id.llSignIn)
                signOutSection = findViewById(R.id.llSignOut)

                orgName = findViewById(R.id.etOrgName)
                orgPasscode = findViewById(R.id.etOrgPasscode)
                orgSignIn = findViewById(R.id.btnOrgSignIn)
                orgSignOut = findViewById(R.id.btnOrgSignOut)

                val orgTitle: TextView = findViewById(R.id.tvOrgName)
                orgTitle.text = sharedPref.getString(KEY_ORGANIZATION_NAME, "")

                orgSignIn.setOnClickListener {

                    Log.d(TAG, "sign out")
                    if (orgName.text.toString().isNotEmpty()) {
                        scope.launch {
                            val shaName = UtilityFunctions.sha256(orgName.text.toString())
                            val org = DataUtility.getOrganization(shaName)
                            if (org != null) {
                                val shaPass =
                                    UtilityFunctions.sha256(orgPasscode.text.toString())
                                if (shaPass == org.passcode) {
                                    Log.d(TAG, "LOGGED IN")
                                    view.hideKeyboard()
                                    orgName.text.clear()
                                    orgPasscode.text.clear()
                                    orgTitle.text = org.name
                                    orgSignIn(shaName, org.name)
                                    signInSection.visibility = View.GONE
                                    signOutSection.visibility = View.VISIBLE
                                }
                            } else {
                                Log.d(TAG, "no org with that name")
                            }
                        }
                    }
                }

                orgSignOut.setOnClickListener {
                    Log.d(TAG, "sign out")
                    orgTitle.text = ""
                    sharedPref.edit().putString(KEY_ORGANIZATION_NAME, "").apply()
                    sharedPref.edit().putString(KEY_ORGANIZATION_ID, "").apply()
                    signInSection.visibility = View.VISIBLE
                    signOutSection.visibility = View.GONE
                }

                if (orgTitle.text == "") {
                    signInSection.visibility = View.VISIBLE
                    signOutSection.visibility = View.GONE
                } else {
                    signInSection.visibility = View.GONE
                    signOutSection.visibility = View.VISIBLE
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                view.hideKeyboard()
                if (scope.isActive) {
                    scope.cancel()
                }
                orgName = findViewById(R.id.etOrgName)
                orgPasscode = findViewById(R.id.etOrgPasscode)

                orgName.setText("")
                orgPasscode.setText("")

                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })

        checkUser(binding)

        val infoText =
            "This app is intended for English speakers who are interested in learning words from the " +
                    "Filipino dialect Tagalog. Grammar lessons have not yet been implemented, but may be in the future\n\n" +
                    "I created this project as a way to practice mobile development in Android Studio, so expect many " +
                    "unpolished features and UI elements\n\n" +
                    "The dictionary database was gathered from: https://tagalog.pinoydictionary.com " +
                    "and scraped using an altered method as found on:\nhttps://github.com/raymelon/tagalog-dictionary-scraper\n\n" +
                    "To report any incorrect or insensitive words, please email mitchgoertzen@gmail.com\n\n" +
                    "2025, mitch goertzen"


        //start home activity on home button press
        binding.ibHome.setOnClickListener {

            if (sharedPref.getBoolean(KEY_HOME, true)) {
                if (!infoDialog.isAdded) {
                    dialogViewModel.updateText(infoText)
                    infoDialog.isCancelable = true
                    infoDialog.show(this.supportFragmentManager, "info popup")
                }
            } else {
                if (sharedPref.getBoolean(Constants.KEY_IN_TEST, true)) {
                    sharedPref.edit()
                        .putBoolean(Constants.KEY_IN_TEST, false)
                        .apply()
                }

                //clear fragment stack
                for (i in 0 until this.supportFragmentManager.backStackEntryCount) {
                    supportFragmentManager.popBackStack()
                }
            }
        }

        profileDialog.setOnDismissFunction {
            checkUser(binding)
        }

        //show profile popup dialog
        binding.ibProfile.setOnClickListener {
            Log.d(TAG, "IN LESSONS: ${sharedPref.getBoolean(KEY_IN_LESSONS, false)}")
            if (sharedPref.getBoolean(KEY_IN_LESSONS, false)) {
                viewModel.updateRefreshActive(sharedPref.getBoolean(KEY_IN_LESSONS, false))
                viewModel.updateRefreshCallback {
                    val fragment = LessonSelectFragment()
                    val transaction = this.supportFragmentManager.beginTransaction()
                    transaction.add(R.id.main_nav_container, fragment).commit()
                }
            }

            if (!profileDialog.isAdded) {
                profileDialog.isCancelable = true
                profileDialog.show(this.supportFragmentManager, "profile popup")
            }
        }
    }


    fun transitionFragment(t: Int = type) {
        inSettings = !inSettings
        setType(t)
        sharedPref.edit().putBoolean(KEY_HOME, false).apply()
        setHomeIcon(false)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        if (sharedPref.getBoolean(KEY_IN_LESSONS, false)) {
            for (i in 0..supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
        } else {
            supportFragmentManager.popBackStack()
            //if the app is in a test, set in test to false
            if (sharedPref.getBoolean(Constants.KEY_IN_TEST, true)) {
                Log.d(TAG, "count: ${supportFragmentManager?.backStackEntryCount}")

                //TODO: add warning for loss of progress, as well as confirmation to exit test and go back
                sharedPref.edit()
                    .putBoolean(Constants.KEY_IN_TEST, false)
                    .apply()
            }

            //if app is current in a results page, the back button will skip the just-completed lesson, and
            //and transition to the page visited prior
            if (sharedPref.getBoolean(Constants.KEY_IN_RESULTS, false)) {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resume")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "start")
    }


    private fun setHomeIcon(isHome: Boolean) {
        if (isHome)
            binding.ibHome.setImageResource(R.drawable.info)
        else
            binding.ibHome.setImageResource(R.drawable.home)
    }

    fun goHome() {
        setType(0)
        setHomeIcon(true)
    }


    private fun setType(t: Int) {
        type = t

        //when learning activity is started, choose background colour based on type
        //0 = home, 1 = dictionary, 2 = lessons, 3 = stats

        when (t) {
            0 -> {
                drawerLayout.setBackgroundResource(R.drawable.flag2)
            }

            1 -> {
                drawerLayout.setBackgroundResource(R.color.red)
            }

            2 -> {
                drawerLayout.setBackgroundResource(R.color.blue)
            }

            else -> {}
        }
    }

    private fun checkUser(binding: ActivityMainBinding) {
        val user = auth.currentUser
        val fragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_container)



        Log.d(TAG, "fragment: $fragment")
        if (user == null) {
            binding.ibProfile.setImageResource(R.drawable.profile_alert)

            sharedPref.edit()
                .putBoolean(Constants.KEY_USER_ADMIN, false)
                .apply()



            Log.d(TAG, "admin false")
        } else {


            Log.d(TAG, "admin true")
            binding.ibProfile.setImageResource(R.drawable.profile)

            sharedPref.edit()
                .putBoolean(Constants.KEY_USER_ADMIN, user.email == "mitchgoertzen@gmail.com")
                .apply()



            Log.d(TAG, "admin: ${user.email == "mitchgoertzen@gmail.com"}")
        }

        if (fragment != null) {
            val home = fragment as HomeFragment

            Log.d(TAG, "home: $home")
            home.reloadAdmin()
        }
    }

    private fun orgSignIn(id: String, name: String) {
        Log.d(TAG, "ORG THINGS")
        sharedPref.edit().putString(KEY_ORGANIZATION_NAME, name).apply()
        sharedPref.edit().putString(KEY_ORGANIZATION_ID, id).apply()
    }

}