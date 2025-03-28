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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_USER_SIGNED_IN
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PracticeFragment : Fragment(R.layout.fragment_lessons_practice) {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var currentWord: Word

    private var currentWordList: MutableList<Word> = mutableListOf()
    private var currentIndex = 0

    private val viewModel: LessonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentWord = Word()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lessons_practice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prevButton: Button = view.findViewById(R.id.btnPreviousWord)
        val prevWidth = prevButton.layoutParams.width
        val nextButton: Button = view.findViewById(R.id.btnNextWord)
        val params: ViewGroup.LayoutParams = nextButton.layoutParams
        val finishButton: Button = view.findViewById(R.id.btnFinish)
        val index: TextView = view.findViewById(R.id.tvIndex)


        val endButton: Button = view.findViewById(R.id.btnEndPractice)


        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {

            endButton.visibility = View.VISIBLE

            endButton.setOnClickListener {
                currentIndex = 0
                val fragment = PracticeResultsFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_nav_container, fragment)
                    ?.addToBackStack("practice results")?.commit()
                (activity as LearningActivity?)?.transitionFragment(2)
            }
        } else {
            endButton.visibility = View.GONE
        }


        viewModel.currentWordList.observe(viewLifecycleOwner) { list ->
            currentWordList = list.toMutableList()


            FirebaseCrashlytics.getInstance().log("${list.size}")
            FirebaseCrashlytics.getInstance().log("${currentWordList.size}")


            // val id = currentLesson.id

            changeCard(index)

            params.width = prevWidth
            nextButton.layoutParams = params


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

                    currentIndex = 0
                    //viewModel.completePractice(currentLesson.id)
                    val fragment = PracticeResultsFragment()
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_nav_container, fragment)
                        ?.addToBackStack("practice results")?.commit()
                    (activity as LearningActivity?)?.transitionFragment(2)
                }

            }

            prevButton.setOnClickListener {

                if (currentIndex == 0) {
                    if (finishButton.visibility == View.VISIBLE) {
                        currentIndex = currentWordList.size - 1
                    }
                } else {
                    --currentIndex
                    if (currentIndex == 0 && finishButton.visibility == View.GONE) {
                        prevButton.isEnabled = false
                    }
                }
                changeCard(index)
            }

            nextButton.setOnClickListener {
                if (!prevButton.isEnabled) {
                    prevButton.isEnabled = true
                }
                if (currentIndex == currentWordList.size - 1) {
                    currentIndex = 0

                } else {
                    ++currentIndex
                    if (currentIndex == currentWordList.size - 1) {
                        if (finishButton.visibility == View.GONE) {
                            finishButton.visibility = View.VISIBLE
                        }
                    }
                }
                changeCard(index)
            }
        }
    }

    private fun changeCard(index: TextView) {
        index.text = (currentIndex + 1).toString() + "/" + currentWordList.size.toString()
        currentWord = currentWordList[currentIndex]
        //TODO - Stats: DataUtility.updatePractice(currentWord.id, true)
        viewModel.updateWord(currentWord)
        val fragment = Card()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.fcCard, fragment)?.commit()
    }

}