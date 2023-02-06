package com.learn.flashLearnTagalog.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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


private const val CURRENT_VERSION = 2


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var lessonCreator : LessonCreator

    private var lessonUpdateAvailable = false

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

        if(version < CURRENT_VERSION){
            //lessonupdate sharedpref default true
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
        val dataProcessor = DataProcessor(resources)

        initText.text = "Fetching Lessons..."
        //create Lessons
        lessonCreator = LessonCreator()


        if(wordNum == 0 || isFirstOpen) {
            writeSettingsToSharedPref()
            updateWords(true, dataProcessor.getWords(), initText)
        }else if(lessonNum == 0){
            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_INIT, false)
                .apply()
            initLessons(initText)
        }else{
            initText.text = "Updating..."
            println("updating...")

            updateWords(false, dataProcessor.getWords(), initText)
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
                    /*
                    for(w in words){
                        viewModel.updateInfo(w)
                    }

                    viewModel.deleteUnusedWords
                     */

                    println("deleting unused words...")

                    if(lessonUpdateAvailable)
                        updateLessons(initText)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    initText.visibility = View.GONE
                    startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                    finish()

                }, 500)
            }.invoke()
        }
    }

    private fun writeSettingsToSharedPref() : Boolean{
        sharedPref.edit()
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }


    private fun initLessons(initText : TextView){
        initText.text = "Initializing Lesson Database..."
        if(!sharedPref.getBoolean(Constants.KEY_LESSON_INIT, false)){
            viewModel.nukeLessons()
            //add delay on thread
            viewModel.insertAllLessons(lessonCreator.getLessons())

            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_INIT, true)
                .apply()
        }
    }

    private fun updateLessons(initText : TextView){

        //make sharedPref val --> lessonUpdateAvailable = false
        initText.text = "Updating Word Database..."


        //add delay on thread
        /*
        for(l in lessonCreator.getLessons()){
            if(!viewModel.lessonExists()){
                viewModel.addLesson(l)
            }else{
                viewModel.updateLessonInfo(l)
            }
         */

        println("updating lessons...")
    }

}