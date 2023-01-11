package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PracticeResultsFragment(private var wordList: MutableList<Word>, private var currentLessonID: Int) : Fragment(R.layout.fragment_practice_results) {

    @Inject
    lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_practice_results, container, false)

        val testButton: Button = view.findViewById(R.id.btnLessonTest)
        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)
        val statsButton: Button = view.findViewById(R.id.btnStats)

        testButton.setOnClickListener{
            leaveResults()
            val fragment = TestFragment(wordList, currentLessonID)
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson test")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        lessonSelectButton.setOnClickListener{
            leaveResults()
            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson select")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        statsButton.setOnClickListener{
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_RESULTS, false)
                .apply()
            val fragment = StatsFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, true)
            .apply()

        return view
    }

    private fun leaveResults() {
        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, false)
            .apply()

        val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

        for (i in 0..count!!){
            activity?.supportFragmentManager?.popBackStack()
        }

    }
}