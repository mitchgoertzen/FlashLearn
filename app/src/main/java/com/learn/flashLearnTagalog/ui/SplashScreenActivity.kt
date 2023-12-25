package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.ui.fragments.SignInFragment
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()

    //TODO: save as shared pref
    private val unlockedLessonJSON = "unlockedLessons.json"
    private val practicedLessonJSON = "practicedLessons.json"
    private val passedLessonJSON = "passedLessons.json"

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lessonScope = CoroutineScope(Job() + Dispatchers.Main)
        auth = Firebase.auth

        lessonScope.launch {

            val unlocked =
                JsonUtility.getUserDataList(this@SplashScreenActivity, unlockedLessonJSON)
            if (unlocked.isNotEmpty()) {
                TempListUtility.unlockedLessons = unlocked
            } else {
                val lessons =
                    async { DataUtility.getLessonIDsByLevel(1) }.await()
                val unlock = mutableListOf<String>()

                for (l in lessons) {
                    if (l.level == 1)
                        unlock.add(l.id)
                }

                TempListUtility.unlockedLessons = unlock
                JsonUtility.writeJSON(this@SplashScreenActivity, unlockedLessonJSON, unlock, true)
            }

            TempListUtility.practicedLessons =
                JsonUtility.getUserDataList(this@SplashScreenActivity, practicedLessonJSON)
            TempListUtility.passedLessons =
                JsonUtility.getUserDataList(this@SplashScreenActivity, passedLessonJSON)
            TempListUtility.viewedWords = JsonUtility.getViewedWords(this@SplashScreenActivity)
            TempListUtility.viewedLessons = JsonUtility.getViewedLessons(this@SplashScreenActivity)
        }

        //TODO: for deleting account auth.currentUser!!.delete()
        if (auth.currentUser == null) {

            Log.d(TAG, "NO USER")
            val dialog = SignInFragment()
            val bundle = bundleOf("in_profile" to false)
            dialog.arguments = bundle

            viewModel.updateCallback { goToHomeActivity() }
            dialog.isCancelable = true
            dialog.show(this@SplashScreenActivity.supportFragmentManager, "user sign-in")
        } else {
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
            goToHomeActivity()
        }
    }

    //end splash screen and continue to home activity
    private fun goToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}