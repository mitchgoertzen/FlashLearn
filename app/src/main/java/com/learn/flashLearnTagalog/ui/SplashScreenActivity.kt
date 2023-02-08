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

private const val CURRENT_VERSION = 3

private const val wordUpdateAvailable = false
private const val lessonUpdateAvailable = true

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var dataProcessor : DataProcessor
    private lateinit var lessonCreator : LessonCreator

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //sharedPref.edit().clear().apply()

        val version = sharedPref.getInt(Constants.KEY_VERSION, 0)
        println("version: $version")

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        val initText : TextView =  view.findViewById(R.id.tvInit)

        if(version < CURRENT_VERSION || viewModel.getSize() == 0){
            update(initText)
        }else{
            startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
            finish()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun update(initText : TextView){

        val isFirstOpen = sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)
        val lessonNum = viewModel.getLessonCount()
        val wordNum = viewModel.getSize()

        println("first open: $isFirstOpen")
        println("size: $wordNum")
        println("lesson count: $lessonNum")

        sharedPref.edit()
            .putInt(Constants.KEY_VERSION, CURRENT_VERSION)
            .apply()

        initText.text = "Fetching Words..."
        //parse words from txt file
        dataProcessor = DataProcessor(resources)

        initText.text = "Fetching Lessons..."
        //create Lessons
        lessonCreator = LessonCreator()

        if(wordNum == 0 || isFirstOpen) {
            sharedPref.edit()
                .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
                .apply()
            updateWords(true, dataProcessor.getWords(), initText)
        }else if(lessonNum == 0){
            initLessons(initText)
        }else{
            if(wordUpdateAvailable){
                updateWords(false, dataProcessor.getWords(), initText)
            }
            else if(lessonUpdateAvailable){
                updateLessons(initText)
                startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                finish()
            }
        }
    }

    @DelicateCoroutinesApi
    fun updateWords(init : Boolean, words : MutableList<Word>, initText : TextView){

        GlobalScope.launch {
            suspend {
                if(init){
                    initText.text = "Initializing Word Database..."
                    viewModel.nukeTable()
                    viewModel.insertAll(words)
                    initLessons(initText)
                }else{
                    initText.text = "Updating Word Database..."
                    println("updating words...")

                    //println("word size: ${words.size}")
                    for(w in words){
                        viewModel.updateWordInfo(w.id, w.type, w.tagalog, w.english, w.category, w.uncommon, true)
                    }

                    viewModel.getIncorrectWords()

                    if(lessonUpdateAvailable)
                        updateLessons(initText)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                    finish()

                }, 500)
            }.invoke()
        }
    }

    private fun initLessons(initText : TextView){

        initText.text = "Initializing Lesson Database..."
        viewModel.nukeLessons()
        //add delay on thread
        viewModel.insertAllLessons(lessonCreator.getLessons())
    }

    private fun updateLessons(initText : TextView){

        initText.text = "Updating Lesson Database..."
        println("updating lessons...")
        var practiceCompleted : Boolean
        var testPassed : Boolean
        var locked : Boolean
        GlobalScope.launch {
            suspend {

                for(l in lessonCreator.getLessons()) {
                    if(viewModel.lessonExists(l.id)){
                        println("lesson exists. update it")
                        locked = !(l.level < 2 || viewModel.previousTestPassed(l.title, l.level))
                        if(locked){
                            practiceCompleted = false
                            testPassed = false
                        }else{
                            practiceCompleted = l.practiceCompleted
                            testPassed = l.testPassed
                        }
                        viewModel.updateLessonInfo(l.id, l.title, l.imageID, l.level, l.minLength, l.maxLength, practiceCompleted, testPassed, locked)
                    }else{
                        println("lesson DOES NOT exist. add it")
                            if(viewModel.lessonCategoryLevelExists(l.title, l.level)){
                                //save lesson data before deletion
                                println("combo exists. delete old version")
                                viewModel.deleteLesson(l.title, l.level)
                            }

                            println("add new lesson")
                            viewModel.insertLesson(l)

                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({


                }, 500)
            }.invoke()
        }
    }
}