package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN ,android.R.anim.fade_in, android.R.anim.linear_interpolator)
        sharedPref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        auth = Firebase.auth


        if (auth.currentUser != null) {
            Log.d(TAG, " USER EXISTS")
            val userScope = CoroutineScope(Job() + Dispatchers.Main)
            userScope.launch {
                DataUtility.updateLocalData(
                    this@SplashScreenActivity,
                    signUp = false,
                    rewriteJSON = true
                )
                userScope.cancel()
            }
        }else{
            sharedPref.edit().putBoolean(Constants.KEY_USER_SIGNED_IN, false).apply()
            val listScope = CoroutineScope(Job() + Dispatchers.Main)
            listScope.launch {
                DataUtility.updateLocalData(this@SplashScreenActivity, signUp = false, rewriteJSON = false)
                listScope.cancel()
            }
        }


        goToHomeActivity()

        //TODO: for deleting account auth.currentUser!!.delete()
//        if (auth.currentUser == null) {
//
//            Log.d(TAG, "NO USER")
//            val dialog = SignInFragment()
//
//            val bundle = bundleOf("in_profile" to false)
//            dialog.arguments = bundle
//
//            viewModel.updateCallback { goToHomeActivity() }
//
//            dialog.isCancelable = true
//            dialog.show(this@SplashScreenActivity.supportFragmentManager, "user sign-in")
//        } else {
//            Log.d(TAG, " USER EXISTS")
//            val userScope = CoroutineScope(Job() + Dispatchers.Main)
//            userScope.launch {
//                DataUtility.updateLocalData(
//                    this@SplashScreenActivity,
//                    signUp = false,
//                    rewriteJSON = true
//                )
//                userScope.cancel()
//            }
//            goToHomeActivity()
//        }
    }

    //end splash screen and continue to home activity
    private fun goToHomeActivity() {
        startActivity(Intent(this, LearningActivity::class.java))
        //TODO: finish from next activity
        finish()
    }
}