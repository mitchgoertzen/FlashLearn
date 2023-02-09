package com.learn.flashLearnTagalog.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PracticeFragment(masterList : MutableList<Word>, private var currentLesson: Lesson) : Fragment(R.layout.fragment_practice) {

    private var masterWordList = masterList
    private lateinit var currentWord : Word
    private var currentWordList : MutableList<Word> = mutableListOf()
    private var i = 0

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_practice, container, false)

        currentWordList = masterWordList.toMutableList()

        val index : TextView = view.findViewById(R.id.tvIndex)

        changeCard(index)

        val prevButton : Button = view.findViewById(R.id.btPreviousWord)
        val prevWidth = prevButton.layoutParams.width
        val nextButton : Button = view.findViewById(R.id.btNextWord)
        val params: ViewGroup.LayoutParams = nextButton.layoutParams
        params.width = prevWidth
        nextButton.layoutParams = params

        val finishButton : Button = view.findViewById(R.id.btFinish)

        if(currentWordList.size == 1){
            nextButton.isEnabled = false
            prevButton.isEnabled = false
            finishButton.visibility = View.VISIBLE
        }
        else
            finishButton.visibility = View.GONE

        finishButton.setOnClickListener{
            viewModel.completePractice(currentLesson.id)
            val fragment = PracticeResultsFragment(masterWordList, currentLesson)
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("practice results")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        prevButton.setOnClickListener{

            if(i == 0){
                if(finishButton.visibility == View.VISIBLE){
                    i = currentWordList.size - 1
                }
            }else{
                --i
                if(i == 0 && finishButton.visibility == View.GONE){
                    prevButton.isEnabled = false
                }
            }
            changeCard(index)
        }

        nextButton.setOnClickListener{
            if(!prevButton.isEnabled){
                prevButton.isEnabled = true
            }
            if(i == currentWordList.size - 1){
                i = 0

            }else{
                ++i
                if(i == currentWordList.size - 1) {
                    if (finishButton.visibility == View.GONE) {
                        finishButton.visibility = View.VISIBLE
                    }
                }
            }
            changeCard(index)
        }

        return view
    }

    fun changeCard(index : TextView){
        index.text = (i+1).toString() + "/" + currentWordList.size.toString()
        currentWord = currentWordList[i]
        if(!viewModel.getPractice(currentWord.id)){
            viewModel.updatePractice(currentWord.id,true)
        }
        val fragment = Card(currentWord)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fcCard, fragment)?.commit()
    }

}