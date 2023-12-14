package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.TestWordAdapter
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TestResultsFragment(var lesson: Lesson, wordsCorrect: Int, var adapter: TestWordAdapter) :
    Fragment(R.layout.fragment_test_results) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val score: Int = wordsCorrect
    private val listSize: Int = adapter.getTestWordsSize()
    private lateinit var textLine: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test_results, container, false)

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, true)
            .apply()

        val rvTodoList: RecyclerView = view.findViewById(R.id.rvWordResults)
        val scoreText: TextView = view.findViewById(R.id.tvScore)
        val totalText: TextView = view.findViewById(R.id.tvTotal)
        val percentageText: TextView = view.findViewById(R.id.tvPercentage)

        val retryButton: Button = view.findViewById(R.id.btnRetry)
        val nextButton: Button = view.findViewById(R.id.btnNextLesson)
        val statsButton: Button = view.findViewById(R.id.btnStats)
        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)

        rvTodoList.adapter = adapter
        rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        val guideline: Guideline = view.findViewById(R.id.glRight)

        guideline.setGuidelinePercent(1f)
        nextButton.visibility = View.GONE

        if ((100 * score / listSize) >= 50) {

            val nextId = "${lesson.category}_${lesson.level + 1}"
            //TODO: better solution
            val lessonJSON = "savedLessons.json"
            val savedLessons = JsonUtility.getSavedLessons(requireActivity(), lessonJSON)
            var nextLesson: Lesson? = null

            var nextLessonWordList: MutableList<Word> = mutableListOf()

            for (l in savedLessons) {
                if (l.id == nextId) {

                    nextLesson = l
                }
            }
            if (nextLesson != null) {
                guideline.setGuidelinePercent(0.60f)
                nextButton.visibility = View.VISIBLE

                val scope = CoroutineScope(Job() + Dispatchers.Main)
                scope.launch {

                    nextLessonWordList = if (TempListUtility.viewedLessons.contains(nextId)) {
                        TempListUtility.practicedWords[nextId]!!
                    } else {
                        DataUtility.getAllWordsForLesson(
                            nextLesson.category.lowercase(),
                            nextLesson.minLength,
                            nextLesson.wordCount.toLong()
                        ).toMutableList()
                    }
                    scope.cancel()
                }
                nextButton.setOnClickListener {
                    TempListUtility.practicedWords[nextId] = nextLessonWordList
                    TempListUtility.viewedLessons.add(nextId)

                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "viewedLessons.json",
                        TempListUtility.viewedLessons
                    )
                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "savedWords.json",
                        TempListUtility.practicedWords
                    )
//TODO: update lesson viewmodel
                    val fragment =
                        PracticeFragment(
                        )
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("test")
                        ?.commit()
                    (activity as LearningActivity?)?.transitionFragment()
                }
            }
        }




        retryButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_RESULTS, false)
                .apply()

//            sharedPref.edit()
//                .putBoolean(Constants.KEY_IN_RESULTS, false)
//                .apply()
//            val fragment = TestFragment(wordList, lesson)
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson test")
//                ?.commit()
//            (activity as LearningActivity?)?.transitionFragment()
        }

//        statsButton.setOnClickListener {
//            sharedPref.edit()
//                .putBoolean(Constants.KEY_IN_RESULTS, false)
//                .apply()
//            val fragment = StatsFragment()
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")
//                ?.commit()
//            (activity as LearningActivity?)?.transitionFragment()
//        }



        lessonSelectButton.setOnClickListener {
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_RESULTS, false)
                .apply()

            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            for (i in 0..count!!) {
                activity?.supportFragmentManager?.popBackStack()
            }
            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson select")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        scoreText.text = score.toString()
        totalText.text = listSize.toString()
        percentageText.text = (100 * score / listSize).toString() + "%"

        return view
    }
}