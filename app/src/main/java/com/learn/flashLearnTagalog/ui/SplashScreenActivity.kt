package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.learn.flashLearnTagalog.BuildConfig
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivitySplashScreenBinding
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

//get current version from build version
private const val CURRENT_VERSION = BuildConfig.VERSION_CODE
private const val DEBUG = true

private const val wordUpdateAvailable = false
private const val lessonUpdateAvailable = true

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var dataProcessor: DataProcessor
    private lateinit var lessonCreator: LessonCreator

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //manually reset user preferences
        //sharedPref.edit().clear().apply()

        println(CURRENT_VERSION)
        //get current version of app
        val version = sharedPref.getInt(Constants.KEY_VERSION, 0)
        println("version: $version")

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        val initText: TextView = view.findViewById(R.id.tvInit)

        //if version is lower, it will be set to update's version
        if (version < CURRENT_VERSION || viewModel.getSize() == 0) {
            //begin update check
            update(initText)
        } else {
            //continue to home activity
            goToHomeActivity()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun update(initText: TextView) {

        val isFirstOpen = sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)
        val lessonNum = viewModel.getLessonCount()
        val wordNum = viewModel.getSize()

        if (DEBUG) {
            println("first open: $isFirstOpen")
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
        lessonCreator = LessonCreator()

        //if the word list is empty, or
        //it is the first time the user has opened the app
        //start initialization process
        if (wordNum == 0 || isFirstOpen) {
            //set first time opening to false
            sharedPref.edit()
                .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
                .apply()
            //start full update function(words and lesson), with init set to true
            updateWords(true, dataProcessor.getWords(), initText)
        } else if (lessonNum == 0) {
            initLessons(initText)
        } else {
            if (wordUpdateAvailable) {
                //start full update function(words and lesson), with init set to false
                updateWords(false, dataProcessor.getWords(), initText)
            } else if (lessonUpdateAvailable) {
                //start lesson update function
                updateLessons(initText)
            }
        }
    }

    @DelicateCoroutinesApi
    fun updateWords(init: Boolean, words: MutableList<Word>, initText: TextView) {

        GlobalScope.launch {
            suspend {
                if (init) {
                    initText.text = "Initializing Word Database..."
                    println("initializing words...")
                    //clear any data in db
                    viewModel.nukeTable()
                    //add all words from previously created word list to db
                    viewModel.insertAll(words)
                    //continue to lesson initialization
                    initLessons(initText)
                } else {
                    initText.text = "Updating Word Database..."
                    println("updating words...")
                    for (w in words) {
                        viewModel.updateWordInfo(
                            w.id,
                            w.type,
                            w.tagalog,
                            w.english,
                            w.category,
                            w.uncommon,
                            true
                        )
                    }
                    viewModel.deleteIncorrectWords()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    //if not initializing, and there is a lesson update available
                    //continue to lesson update
                    if (!init && lessonUpdateAvailable)
                        updateLessons(initText)
                    else
                        goToHomeActivity()
                }, 500)
            }.invoke()
        }
    }

    private fun initLessons(initText: TextView) {
        initText.text = "Initializing Lesson Database..."
        println("initializing lessons...")
        //clear any data in db
        viewModel.nukeLessons()
        //TODO: add delay on thread?
        //add all previously created lessons to db
        viewModel.insertAllLessons(lessonCreator.getLessons())
    }

    private fun updateLessons(initText: TextView) {

        initText.text = "Updating Lesson Database..."
        println("updating lessons...")
        var practiceCompleted: Boolean
        var testPassed: Boolean
        var locked: Boolean
        GlobalScope.launch {
            suspend {
                //for each lesson in lesson list
                for (l in lessonCreator.getLessons()) {
                    //check if the lesson with exact id already exists in db
                    if (viewModel.lessonExists(l.id)) {
                        if (DEBUG)
                            println("lesson exists. update it")
                        //if the lesson level is 2 or greater and the lesson of the same category,
                        //but prior level has not had its test passed, lock the lesson
                        //otherwise, unlock the lesson
                        locked = !(l.level < 2 || viewModel.previousTestPassed(l.title, l.level))
                        //if the lesson is locked, practiceCompleted and testPassed variables must be reset
                        if (locked) {
                            practiceCompleted = false
                            testPassed = false
                        } else {
                            //if the lesson is not locked, its progress will be kept
                            practiceCompleted = l.practiceCompleted
                            testPassed = l.testPassed
                        }
                        //update all info on existing lesson
                        viewModel.updateLessonInfo(
                            l.id,
                            l.title,
                            l.imageID,
                            l.level,
                            l.minLength,
                            l.maxLength,
                            practiceCompleted,
                            testPassed,
                            locked
                        )
                    } else {
                        if (DEBUG)
                            println("lesson DOES NOT exist. add it")
                        //if the lesson id was not found, check for combination of lesson category and level
                        //(this is a legacy check for app version 1, since lesson id has changed from version 2 onward)
                        if (viewModel.lessonCategoryLevelExists(l.title, l.level)) {
                            //save user progress of old lesson
                            //TODO: save lesson data before deletion
                            if (DEBUG)
                                println("combo exists. delete old version")
                            //delete old lesson, which will be replaced by new lesson with updated id
                            viewModel.deleteLesson(l.title, l.level)
                        }
                        if (DEBUG)
                            println("add new lesson")
                        //new lesson will be added to db
                        viewModel.insertLesson(l)

                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    goToHomeActivity()
                }, 500)
            }.invoke()
        }
    }

    //end splash screen and continue to home activity
    private fun goToHomeActivity() {
        startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
        finish()
    }
}