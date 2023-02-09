package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.ToDoAdapter
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestResultsFragment(wordsCorrect: Int, var adapter: ToDoAdapter) :
    Fragment(R.layout.fragment_test_results) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val score: Int = wordsCorrect
    private val listSize: Int = adapter.getToDoSize()
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

        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)
        val statsButton: Button = view.findViewById(R.id.btnStats)

        rvTodoList.adapter = adapter
        rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))

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

        statsButton.setOnClickListener {
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_RESULTS, false)
                .apply()
            val fragment = StatsFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        scoreText.text = score.toString()
        totalText.text = listSize.toString()
        percentageText.text = (100 * score / listSize).toString() + "%"

        return view
    }
}