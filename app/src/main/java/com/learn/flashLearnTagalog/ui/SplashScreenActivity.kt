package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.learn.flashLearnTagalog.BuildConfig
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.User
import com.learn.flashLearnTagalog.databinding.ActivitySplashScreenBinding
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.SignInFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


//get current version from build version
private const val CURRENT_VERSION = BuildConfig.VERSION_CODE
private const val DEBUG = false

private const val wordUpdateAvailable = true
private const val lessonUpdateAvailable = true

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    // private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var progress: ProgressBar

    private lateinit var dataProcessor: DataProcessor
    private lateinit var lessonCreator: LessonCreator

    private var version = 0

    private var lessonNum = 0
    private var wordNum = 0
    private val gson = Gson()

    private lateinit var auth: FirebaseAuth

    //TODO: save as shared pref
    private val savedLessonJSON = "savedLessons.json"
    private val viewedLessonJSON = "viewedLessons.json"
    private val unlockedLessonJSON = "unlockedLessons.json"
    private val practicedLessonJSON = "practicedLessons.json"
    private val passedLessonJSON = "passedLessons.json"

    private lateinit var unlockedList: MutableList<String>
    private lateinit var practicedList: MutableList<String>
    private lateinit var passedList: MutableList<String>


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unlockedList = JsonUtility.getStringList(this, unlockedLessonJSON)
        practicedList = JsonUtility.getStringList(this, practicedLessonJSON)
        passedList = JsonUtility.getStringList(this, passedLessonJSON)

        val lessonScope = CoroutineScope(Job() + Dispatchers.Main)
        lessonScope.launch {

            //if the user has not saved a lessons list to internal storage
            if (!sharedPref.getBoolean(Constants.KEY_LESSON_JSON_EXISTS, false)) {
                Log.d(TAG, "FIRST TIME, NO INTERNAL STORAGE")
                val lessons = DataUtility.getAllLessons().toMutableList()

                TempListUtility.unlockedLessons.add("Custom\nLesson_0")
                for (l in lessons) {
                    if (l.level == 1)
                        TempListUtility.unlockedLessons.add(l.id)
                }

                JsonUtility.writeJSON(
                    this@SplashScreenActivity,
                    unlockedLessonJSON,
                    TempListUtility.unlockedLessons
                )
                JsonUtility.writeJSON(this@SplashScreenActivity, savedLessonJSON, lessons)

                sharedPref.edit()
                    .putBoolean(Constants.KEY_LESSON_JSON_EXISTS, true)
                    .apply()

                Log.d(TAG, "DONE")

            } else {
                //locally saved data from, not tied to user account
                TempListUtility.practicedWords =
                    JsonUtility.getPracticedWords(this@SplashScreenActivity, "savedWords.json")
                TempListUtility.viewedLessons =
                    JsonUtility.getStringList(this@SplashScreenActivity, viewedLessonJSON)
            }

            lessonScope.cancel()
        }

        auth = Firebase.auth
        //auth.currentUser!!.delete()
        //TODO: for deleting account auth.currentUser!!.delete()
        if (auth.currentUser == null) {
            val dialog: DialogFragment =
                SignInFragment(
                    onClose = this@SplashScreenActivity::goToHomeActivity,
                    initUser = this@SplashScreenActivity::initializeUserData
                )

            dialog.isCancelable = true
            dialog.show(this@SplashScreenActivity.supportFragmentManager, "user sign-in")
        } else {
            goToHomeActivity(true)
        }


        //  finish()


        //to populate user based lists
//        val userScope = CoroutineScope(Job() + Dispatchers.Main)
//        userScope.launch {
//            userScope.cancel()
//        }


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
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun update(initText: TextView) {

        if (DEBUG) {
            println("size: $wordNum")
            println("lesson count: $lessonNum")
        }

        //set user's version to current build version
        sharedPref.edit()
            .putInt(Constants.KEY_VERSION, CURRENT_VERSION)
            .apply()

        initText.text = "Fetching Words..."
        //parse words from txt file
        dataProcessor = DataProcessor(resources)

        initText.text = "Fetching Lessons..."
        //create Lessons

        var init = wordNum == 0


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
    }

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

    //end splash screen and continue to home activity
    private fun goToHomeActivity(needInit: Boolean) {

        if (needInit) {
            val userScope = CoroutineScope(Job() + Dispatchers.Main)
            userScope.launch {
                val authUser = auth.currentUser

                if (authUser != null) {
                    val thisUser = DataUtility.getCurrentUser(auth.currentUser!!.uid)

                    if (thisUser != null) {
                        initializeUserData(thisUser)
                    }
                }else{
                    populateInternalStorageList(
                        unlockedList,
                        "unlocked",
                        mutableListOf(),
                        unlockedLessonJSON
                    )
                    populateInternalStorageList(
                        practicedList,
                        "practiced",
                        mutableListOf(),
                        practicedLessonJSON
                    )
                    populateInternalStorageList(
                        passedList,
                        "passed",
                        mutableListOf(),
                        passedLessonJSON
                    )
                }

                userScope.cancel()
            }
        }

        startActivity(Intent(this, HomeActivity::class.java))
        //TODO: finish from next activity
        finish()
    }

    private fun initializeUserData(thisUser: User) {

        populateInternalStorageList(
            unlockedList,
            "unlocked",
            thisUser.unlockedLessons,
            unlockedLessonJSON
        )
        populateInternalStorageList(
            practicedList,
            "practiced",
            thisUser.practicedLessons,
            practicedLessonJSON
        )
        populateInternalStorageList(
            passedList,
            "passed",
            thisUser.passedLessons,
            passedLessonJSON
        )

    }


    private fun populateInternalStorageList(
        internalList: MutableList<String>,
        listType: String,
        cloudList: MutableList<String>,
        jsonFile: String
    ) {
        if (internalList.isEmpty()) {
            Log.d(TAG, "EMPTY LIST")
            TempListUtility.setList(listType, cloudList)
            JsonUtility.writeJSON(
                this,
                jsonFile,
                cloudList
            )
        } else {
            Log.d(TAG, "NON-EMPTY LIST")
            TempListUtility.setList(listType, internalList)
        }
    }

}

