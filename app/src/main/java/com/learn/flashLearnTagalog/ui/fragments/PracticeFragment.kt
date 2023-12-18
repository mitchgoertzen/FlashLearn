package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants.KEY_USER_SIGNED_IN
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PracticeFragment : Fragment(R.layout.fragment_practice) {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var currentWord: Word
    private var currentWordList: MutableList<Word> = mutableListOf()
    private var i = 0

    private val viewModel: LessonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_practice, container, false)

        viewModel.currentWordList.observe(viewLifecycleOwner) { list ->
            currentWordList = list.toMutableList()

            val index: TextView = view.findViewById(R.id.tvIndex)
            // val id = currentLesson.id

            changeCard(index)

            val prevButton: Button = view.findViewById(R.id.btPreviousWord)
            val prevWidth = prevButton.layoutParams.width
            val nextButton: Button = view.findViewById(R.id.btNextWord)
            val params: ViewGroup.LayoutParams = nextButton.layoutParams
            params.width = prevWidth
            nextButton.layoutParams = params

            val finishButton: Button = view.findViewById(R.id.btFinish)

            prevButton.isEnabled = false

            if (currentWordList.size == 1) {
                nextButton.isEnabled = false
                finishButton.visibility = View.VISIBLE
            } else {
                finishButton.visibility = View.GONE
            }

            finishButton.setOnClickListener {

                viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
                    val id = lesson.id
                    if (!TempListUtility.practicedLessons.contains(id)) {
                        if (sharedPref.getBoolean(KEY_USER_SIGNED_IN, false)) {
                            DataUtility.addPracticedLesson(id)
                        }
                        TempListUtility.practicedLessons.add(id)
                        JsonUtility.writeJSON(
                            requireActivity(),
                            "practicedLessons.json",
                            TempListUtility.practicedLessons,
                            true
                        )
                    }
                    //viewModel.completePractice(currentLesson.id)
                    val fragment = PracticeResultsFragment()
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_nav_container, fragment)
                        ?.addToBackStack("practice results")?.commit()
                    (activity as LearningActivity?)?.transitionFragment()
                }

            }

            prevButton.setOnClickListener {

                if (i == 0) {
                    if (finishButton.visibility == View.VISIBLE) {
                        i = currentWordList.size - 1
                    }
                } else {
                    --i
                    if (i == 0 && finishButton.visibility == View.GONE) {
                        prevButton.isEnabled = false
                    }
                }
                changeCard(index)
            }

            nextButton.setOnClickListener {
                if (!prevButton.isEnabled) {
                    prevButton.isEnabled = true
                }
                if (i == currentWordList.size - 1) {
                    i = 0

                } else {
                    ++i
                    if (i == currentWordList.size - 1) {
                        if (finishButton.visibility == View.GONE) {
                            finishButton.visibility = View.VISIBLE
                        }
                    }
                }
                changeCard(index)
            }
        }

        return view
    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)    super.onSaveInstanceState(outState);
//        // Save our own state now
//        outState.putInt(STATE_COUNTER, mCounter);
//    }
////
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        this = activity?.supportFragmentManager?.getFragment(this, "practice");
//    }

    private fun changeCard(index: TextView) {
        index.text = (i + 1).toString() + "/" + currentWordList.size.toString()
        currentWord = currentWordList[i]

        //TODO - Stats: DataUtility.updatePractice(currentWord.id, true)
        viewModel.updateWord(currentWord)
        val fragment = Card()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fcCard, fragment)?.commit()
    }

}