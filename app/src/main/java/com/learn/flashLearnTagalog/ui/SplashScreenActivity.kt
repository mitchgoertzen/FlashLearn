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

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: SignInViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    //TODO: save as shared pref
    private val savedLessonJSON = "savedLessons.json"
    private val viewedLessonJSON = "viewedLessons.json"
    private val unlockedLessonJSON = "unlockedLessons.json"
    private val practicedLessonJSON = "practicedLessons.json"
    private val passedLessonJSON = "passedLessons.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val lessonScope = CoroutineScope(Job() + Dispatchers.Main)
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


        //sharedPref.edit().putBoolean(KEY_LESSON_JSON_EXISTS, false).apply()
//        val lessonScope = CoroutineScope(Job() + Dispatchers.Main)
//        lessonScope.launch {
//
//
//            //if the user has not saved a lessons list to internal storage
//            if (JsonUtility.getSavedLessons(this@SplashScreenActivity).isEmpty()) {
//                val lessons = DataUtility.getAllLessons().toMutableList()
//
//                //   TempListUtility.unlockedLessons.add("Custom\nLesson_0")
//                for (l in lessons) {
//                    if (l.level == 1)
//                        TempListUtility.unlockedLessons.add(l.id)
//                }
//                JsonUtility.writeJSON(
//                    this@SplashScreenActivity,
//                    unlockedLessonJSON,
//                    TempListUtility.unlockedLessons
//                )
//                JsonUtility.writeJSON(this@SplashScreenActivity, savedLessonJSON, lessons)
//
//                sharedPref.edit()
//                    .putBoolean(KEY_LESSON_JSON_EXISTS, true)
//                    .apply()
//            } else {
//                //locally saved data from, not tied to user account
//                TempListUtility.practicedWords =
//                    JsonUtility.getPracticedWords(this@SplashScreenActivity, "savedWords.json")
//                TempListUtility.viewedLessons =
//                    JsonUtility.getStringList(this@SplashScreenActivity, viewedLessonJSON)
//            }
//
//            lessonScope.cancel()
//        }

        Log.d(TAG, "WE ARE HERE")

        //TODO: for deleting account auth.currentUser!!.delete()
        if (auth.currentUser == null) {

            Log.d(TAG, "NO USER")
            Log.d(TAG, "no user")
            val dialog = SignInFragment()

            val bundle = bundleOf("in_profile" to false)
            dialog.arguments = bundle

            viewModel.updateCallback { goToHomeActivity() }

//            val dialog: DialogFragment = SignInFragment.new
//                SignInFragment(false, this::goToHomeActivity)
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
        //TODO: finish from next activity
        finish()
    }
}


//  finish()


//        lessonNum = viewModel.getLessonCount()
//        wordNum = viewModel.getSize()

//        //manually reset user preferences
//        //sharedPref.edit().clear().apply()
//
//        //get current version of app
//        version = sharedPref.getInt(Constants.KEY_VERSION, 0)
//
//        println("version: $version")
//
//        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
//        val view = binding.root
//
//        //progress = view.findViewById(R.id.progressBar)
//
//        setContentView(view)
//        val initText: TextView = view.findViewById(R.id.tvInit)
//
//        //if version is lower, it will be set to update's version
//        if (version < CURRENT_VERSION || viewModel.getSize() == 0) {
//            //begin update check
//            update(initText)
//        } else {
//            //continue to home activity
//            goToHomeActivity()
//        }


//
//@OptIn(DelicateCoroutinesApi::class)
//private fun update(initText: TextView) {
//
//    if (DEBUG) {
//        println("size: $wordNum")
//        println("lesson count: $lessonNum")
//    }
//
//    //set user's version to current build version
//    sharedPref.edit()
//        .putInt(Constants.KEY_VERSION, CURRENT_VERSION)
//        .apply()
//
//    initText.text = "Fetching Words..."
//    //parse words from txt file
//    dataProcessor = DataProcessor(resources)
//
//    initText.text = "Fetching Lessons..."
//    //create Lessons
//
//    var init = wordNum == 0
//
//}


//if the word list is empty
//start initialization process
//        if (init) {
//            //set first time opening to false
////            sharedPref.edit()
////                .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
////                .apply()
//            //start full update function(words and lesson), with init set to true
//            updateWords(init, dataProcessor.getWords(), initText)
//        } else if (lessonNum == 0) {
//            initLessons(initText)
//        } else {
//            if (wordUpdateAvailable) {
//                //start full update function(words and lesson), with init set to false
//                updateWords(init, dataProcessor.getWords(), initText)
//            } else if (lessonUpdateAvailable) {
//                //start lesson update function
//                updateLessons(initText)
//            }
//        }


//    @DelicateCoroutinesApi
//    fun updateWords(init: Boolean, words: MutableList<RoomWord>, initText: TextView) {
//        GlobalScope.launch {
//            suspend {
//                if (init) {
//                    initText.text = "Initializing Word Database..."
//                    println("initializing words...")
//                    //clear any data in db
//                    viewModel.nukeTable()
//                    //add all words from previously created word list to db
//                    viewModel.insertAll(words)
//                    //continue to lesson initialization
//                } else {
//                    initText.text = "Updating Word Database..."
//                    println("updating words...")
//                    for (w in words) {
//                        //progress.progress = (i++/size)*100
//                        viewModel.updateWordInfo(
//                            w.id,
//                            w.type,
//                            w.tagalog,
//                            w.english,
//                            w.category,
//                            w.uncommon,
//                            true
//                        )
//                    }
//                    viewModel.deleteIncorrectWords()
//                }
//                Handler(Looper.getMainLooper()).postDelayed({
//                    //if not initializing, and there is a lesson update available
//                    //continue to lesson update
//                    if (!init && lessonUpdateAvailable)
//                        updateLessons(initText)
//                    else {
//                        if (lessonNum == 0) {
//                            initLessons(initText)
//                        } else
//                            goToHomeActivity()
//                        this.cancel("thread no longer needed. thread cancelled")
//                    }
//                }, 500)
//            }.invoke()
//        }
//    }
//
//    @DelicateCoroutinesApi
//    private fun initLessons(initText: TextView) {
//        initText.text = "Initializing Lesson Database..."
//        println("initializing lessons...")
//        lessonCreator = LessonCreator(viewModel)
//        GlobalScope.launch {
//            suspend {
//                //clear any data in db
//                viewModel.nukeLessons()
//                Handler(Looper.getMainLooper()).postDelayed({
//
//                    //viewModel.insertAllLessons(lessonCreator.getLessons())
//
//                    goToHomeActivity()
//                }, 500)
//            }.invoke()
//        }
//
//    }
//
//    @DelicateCoroutinesApi
//    private fun updateLessons(initText: TextView) {
//
//        lessonCreator = LessonCreator(viewModel)
//        initText.text = "Updating Lesson Database..."
//        println("updating lessons...")
//        var practiceCompleted: Boolean
//        var testPassed: Boolean
//        var locked: Boolean
//        GlobalScope.launch {
//            suspend {
//
//                //if lesson name has been changed
//                //needs to be manually checked for now
//                //the old lesson in database will need to be replaced
//                //TODO: create better method of handling lesson name changes
//                if (version < 2) {
//                    //mark lessons as current
//                    //--> need to add field to lesson
//                    viewModel.deleteLesson("People", 3)
//                    for (i in 1..5) {
//                        if (viewModel.lessonCategoryLevelExists("Food", i))
//                            viewModel.deleteLesson("Food", i)
//                    }
//                }
//
//                //for each lesson in lesson list
//                for (l in lessonCreator.getLessons()) {
//
//                    //check if the lesson with exact id already exists in db
//                    if (viewModel.lessonExists(l.id)) {
//                        if (DEBUG)
//                            println("lesson exists. update it")
//                        //if the lesson level is 2 or greater and the lesson of the same category,
//                        //but prior level has not had its test passed, lock the lesson
//                        //otherwise, unlock the lesson
//                        locked = !(l.level < 2 || viewModel.previousTestPassed(l.category, l.level))
//                        //if the lesson is locked, practiceCompleted and testPassed variables must be reset
//                        if (locked) {
//                            practiceCompleted = false
//                            testPassed = false
//                        } else {
//                            //if the lesson is not locked, its progress will be kept
//                            val oldLesson = viewModel.getLessonByID(l.id)
//                            practiceCompleted = oldLesson.practiceCompleted
//                            testPassed = oldLesson.testPassed
//                        }
//                        //update all info on existing lesson
//                        viewModel.updateLessonInfo(
//                            l.id,
//                            l.category,
//                            l.imageID,
//                            l.level,
//                            l.minLength,
//                            l.maxLength,
//                            l.maxLines,
//                            l.difficulty,
//                            practiceCompleted,
//                            testPassed,
//                            locked
//                        )
//                    } else {
//                        if (DEBUG)
//                            println("lesson DOES NOT exist. add it")
//                        //if the lesson id was not found, check for combination of lesson category and level
//                        //(this is a legacy check for app version 1, since lesson id has changed from version 2 onward)
//                        if (viewModel.lessonCategoryLevelExists(l.category, l.level)) {
//
//
//                            l.testPassed = false
//                            l.locked = l.level > 1
//                            if (l.locked) {
//                                l.practiceCompleted = false
//                            } else {
//                                val oldLesson = viewModel.getLessonByData(l.category, l.level)
//                                l.practiceCompleted = oldLesson.practiceCompleted
//                            }
//
//                            if (DEBUG)
//                                println("combo exists. delete old version")
//                            //delete old lesson, which will be replaced by new lesson with updated id
//                            viewModel.deleteLesson(l.category, l.level)
//                        }
//                        if (DEBUG)
//                            println("add new lesson")
//                        //new lesson will be added to db
//                        viewModel.insertLesson(l)
//
//                    }
//                }
//                Handler(Looper.getMainLooper()).postDelayed({
//                    goToHomeActivity()
//                }, 500)
//            }.invoke()
//        }
//    }