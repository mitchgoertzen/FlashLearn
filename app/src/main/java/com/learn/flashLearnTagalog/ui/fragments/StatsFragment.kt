package com.learn.flashLearnTagalog.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.ui.LearningActivity

class StatsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats_landing, container, false)

        val lessonsButton: Button = view.findViewById(R.id.btnLessons)
        val wordsButton: Button = view.findViewById(R.id.btnWords)
        val otherButton: Button = view.findViewById(R.id.btnOther)

        lessonsButton.setOnClickListener {
            val fragment = LessonStatsFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson stats")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        wordsButton.setOnClickListener {
            val fragment = WordStatsFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("word stats")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

        otherButton.setOnClickListener {
//            val fragment = TestResultsFragment()
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("results")?.commit()
//            (activity as LearningActivity?)?.transitionFragment()
        }

        return view
    }

}