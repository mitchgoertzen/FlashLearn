package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private val viewModel: MainViewModel by viewModels()

    private var words:MutableList<Word> = mutableListOf()

    private val myLessons : MutableList<Lesson> = mutableListOf()

    private val easyMinLength = 0
    private val easyMaxLength = 6

    private val mediumMinLength = 6
    private val mediumMaxLength = 9

    private val hardMinLength = 9
    private val hardMaxLength = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val input:InputStream = resources.openRawResource(R.raw.tag_dollar)
        val reader = BufferedReader(InputStreamReader(input))

        //not sure if works properly
        println("1")
        viewLifecycleOwner.lifecycleScope.launch {
            println("2")
            val nuke = async { viewModel.nukeTable()
                println("3") }
            nuke.await()
            println("4")
        }
        println("5")

        var string: String? = ""

        while (true) {
            var type = ""
            var tag = ""
            var eng = ""
            var cat = ""
            var state = 0

            try {
                if (reader.readLine().also { string = it } == null) break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            for (i in string?.indices!!) {
                when (state) {
                    0 -> {
                        if (string!![i] == '$') {
                            state = 1
                        } else {
                            type += string!![i]
                        }
                    }
                    1 -> {
                        if (string!![i] == '$') {
                            state = 2
                        } else {
                            tag += string!![i]
                        }
                    }
                    2 -> {
                        if (string!![i] == '$') {
                            state = 3
                        } else {
                            eng += string!![i]
                        }
                    }
                    3 -> {
                        cat += string!![i]
                    }
                }
            }
            tag = tag.lowercase()
            eng = eng.lowercase()
            val word = Word(type, tag, eng, cat)
            words.add(word)
        }

        input.close()
        viewModel.insertAll(words)

        val lesson1 = Lesson("Custom\nLesson", R.drawable.custom,0,-1,-1)
        myLessons.add(lesson1)



        val bodyEasy = createLesson(1,"Body", R.drawable.body,0,5)
        val bodyMedium = createLesson(2,"Body", R.drawable.body,5,9)
        val bodyHard = createLesson(3,"Body", R.drawable.body,9,100)
        myLessons.add(bodyEasy)
        myLessons.add(bodyMedium)
        myLessons.add(bodyHard)

        val animalsEasy = createLesson(1,"Animals", R.drawable.dog,0,5)
        val animalsMedium = createLesson(2,"Animals", R.drawable.dog,5,7)
        val animalsHard = createLesson(3,"Animals", R.drawable.dog,7,10)
        val animalsExtraHard = createLesson(4,"Animals", R.drawable.dog,10,100)
        myLessons.add(animalsEasy)
        myLessons.add(animalsMedium)
        myLessons.add(animalsHard)
        myLessons.add(animalsExtraHard)

//        val speechEasy = createLesson(1,"Speech", R.drawable.hand_wave, 0,4)
//        val speechMedium = createLesson(2,"Speech", R.drawable.hand_wave,4,100)
//        myLessons.add(speechEasy)
//        myLessons.add(speechMedium)

        val geographyEasy = createLesson(1,"Geography", R.drawable.mountain,-1,-1)
        val geographyMedium = createLesson(2,"Geography", R.drawable.mountain,-1,-1)
        val geographyHard = createLesson(3,"Geography", R.drawable.mountain,-1,-1)
        myLessons.add(geographyEasy)
        myLessons.add(geographyMedium)
        myLessons.add(geographyHard)

        val foodEasy = createLesson(1,"Food", R.drawable.food,-1,-1)
        val foodMedium = createLesson(2,"Food", R.drawable.food,-1,-1)
        val foodHard = createLesson(3,"Food", R.drawable.food,-1,-1)
        myLessons.add(foodEasy)
        myLessons.add(foodMedium)
        myLessons.add(foodHard)

        val peopleEasy = createLesson(1,"People", R.drawable.face_icon,-1,-1)
        val peopleMedium = createLesson(2,"People", R.drawable.face_icon,-1,-1)
        val peopleHard = createLesson(3,"People", R.drawable.face_icon,-1,-1)
        myLessons.add(peopleEasy)
        myLessons.add(peopleMedium)
        myLessons.add(peopleHard)

//        val natureEasy = createLesson(1,"Animals\nI", R.drawable.dog,-1,-1)
//        val natureMedium = createLesson(2,"Animals\nII", R.drawable.dog,-1,-1)
//        val natureHard = createLesson(3,"Animals\nIII", R.drawable.dog,-1,-1)
//        myLessons.add(natureEasy)
//        myLessons.add(natureMedium)
//        myLessons.add(natureHard)
//
//        val coloursEasy = createLesson(1,"Animals\nI", R.drawable.dog,-1,-1)
//        val coloursMedium = createLesson(2,"Animals\nII", R.drawable.dog,-1,-1)
//        val coloursHard = createLesson(3,"Animals\nIII", R.drawable.dog,-1,-1)
//        myLessons.add(coloursEasy)
//        myLessons.add(coloursMedium)
//        myLessons.add(coloursHard)
//
//        val chemistryEasy = createLesson(1,"Animals\nI", R.drawable.dog,-1,-1)
//        val chemistryMedium = createLesson(2,"Animals\nII", R.drawable.dog,-1,-1)
//        val chemistryHard = createLesson(3,"Animals\nIII", R.drawable.dog,-1,-1)
//        myLessons.add(chemistryEasy)
//        myLessons.add(chemistryMedium)
//        myLessons.add(chemistryHard)


        if(!sharedPref.getBoolean(Constants.KEY_LESSON_INIT, false)){
            viewModel.nukeLessons()
            viewModel.insertAllLessons(myLessons)

            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_INIT, true)
                .apply()
        }

        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    private fun createLesson(level : Int, title : String, imageID : Int, overrideMin: Int, overrideMax: Int) : Lesson{
        var minLength = overrideMin
        var maxLength = overrideMax

        if(overrideMin == -1) {
            when(level){
                1-> {
                    minLength = easyMinLength
                    maxLength = easyMaxLength
                }
                2-> {
                    minLength = mediumMinLength
                    maxLength = mediumMaxLength
                }
                3-> {
                    minLength = hardMinLength
                    maxLength = hardMaxLength
                }
            }
        }

        return Lesson(title,imageID,level,minLength, maxLength)
    }
}
