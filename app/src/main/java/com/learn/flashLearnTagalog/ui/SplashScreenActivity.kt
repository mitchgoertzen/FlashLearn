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
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivitySplashScreenBinding
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.SetupFragment
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


var CURRENT_VERSION = 2


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding



    var size = 0

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref.edit().clear().apply()

        val version = sharedPref.getInt(Constants.KEY_VERSION, 0)



        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        val initText : TextView =  view.findViewById(R.id.tvInit)

        initText.visibility = View.GONE

        if(version < CURRENT_VERSION){
            update(initText)
        }else{
            startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
            finish()
        }
    }


    @DelicateCoroutinesApi
    fun init(mode : Int){

        GlobalScope.launch {
            suspend {
                val fragment = SetupFragment(mode)


                supportFragmentManager.beginTransaction()
                    .replace(R.id.flInit, fragment).addToBackStack("setup").commit()

                Handler(Looper.getMainLooper()).postDelayed({
                    //initText.visibility = View.GONE

                    try {
                        supportFragmentManager.popBackStack()
                    } catch (ignored: IllegalStateException) {
                        // There's no way to avoid getting this if saveInstanceState has already been called.
                    }

                    startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                    finish()

                }, 500)
            }.invoke()
        }
    }

    private fun update(initText : TextView){


        initText.text = "Updating..."
        val isFirstOpen = sharedPref.getBoolean(Constants.KEY_FIRST_TIME_TOGGLE, true)
        val lessonNum = viewModel.getLessonCount()

        size = viewModel.getSize()

        println("first open: $isFirstOpen")
        println("size: $size")
        println("lesson count: $lessonNum")

        if(size == 0 || isFirstOpen) {
            writeSettingsToSharedPref()
            initText.visibility = View.VISIBLE
            init(1)
        }else if(lessonNum == 0){
            initText.visibility = View.VISIBLE
            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_INIT, false)
                .apply()
            init(2)
        }

        println("updating...")

        sharedPref.edit()
            .putInt(Constants.KEY_VERSION, CURRENT_VERSION)
            .apply()

        startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
        finish()
    }

    private fun writeSettingsToSharedPref() : Boolean{
        sharedPref.edit()
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }
}