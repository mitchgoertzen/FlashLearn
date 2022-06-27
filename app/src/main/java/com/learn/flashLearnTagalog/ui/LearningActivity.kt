package com.learn.flashLearnTagalog.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.ActivityMainBinding
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.fragments.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private var type : Int = -1

@AndroidEntryPoint
class LearningActivity : AppCompatActivity(R.layout.activity_main) {

    private var inSettings: Boolean = true
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        println(type)

        var bkgColor : Int = if(type > 1)
            resources.getColor(R.color.blue)
        else
            resources.getColor(R.color.red)

        binding.ivBackground.setBackgroundColor(bkgColor)

        binding.ibHome.setOnClickListener {

            if(sharedPref.getBoolean(Constants.KEY_IN_TEST, true)){
                sharedPref.edit()
                    .putBoolean(Constants.KEY_IN_TEST, false)
                    .apply()
            }

            val count: Int = supportFragmentManager.backStackEntryCount
            for (i in 0 until count){
                supportFragmentManager.popBackStack()
            }

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }



        binding.ibProfile.setOnClickListener{
            val dialog : DialogFragment = ProfilePopupFragment(this)

            dialog.isCancelable = true
            dialog.show(this.supportFragmentManager, "profile popup")
        }

        when(type){
            1->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_container, DictionaryFragment()).addToBackStack("dictionary").commit()
            }
            2->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_container, LessonSelectFragment()).addToBackStack("lesson select").commit()
            }
            3->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_container, StatsFragment()).addToBackStack("stats").commit()
            }
        }
    }

    fun transitionFragment(){
        inSettings = !inSettings
    }


    override fun onBackPressed() {
        if(sharedPref.getBoolean(Constants.KEY_IN_TEST, true)){
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_TEST, false)
                .apply()
        }
        val stack = supportFragmentManager.backStackEntryCount
        if(stack < 2){
            for (i in 0..stack){
                supportFragmentManager.popBackStack()
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }else{
            if(sharedPref.getBoolean(Constants.KEY_IN_RESULTS, false)){
                supportFragmentManager.popBackStack()
                sharedPref.edit()
                    .putBoolean(Constants.KEY_IN_RESULTS, false)
                    .apply()
            }
            supportFragmentManager.popBackStack()
        }

    }

    fun setType(t : Int){
        type = t
    }

}