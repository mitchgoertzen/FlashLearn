package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.DictionaryAdapter
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//private var wordList: MutableList<Word>
//private var currentLesson: Lesson
@AndroidEntryPoint
class PracticeResultsFragment : Fragment(R.layout.fragment_practice_results) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: LessonViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_practice_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rvTranslationList: RecyclerView = view.findViewById(R.id.rvWordTranslations)
        val testButton: Button = view.findViewById(R.id.btnLessonTest)
        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)
        val retryButton: Button = view.findViewById(R.id.btnRetryPractice)
        //  val statsButton: Button = view.findViewById(R.id.btnStats)

        viewModel.currentWordList.observe(viewLifecycleOwner) { list ->
            val adapter = DictionaryAdapter(list.toMutableList())
            rvTranslationList.adapter = adapter
            rvTranslationList.layoutManager = LinearLayoutManager((activity as LearningActivity?))
        }

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, true)
            .apply()

        testButton.setOnClickListener {
            leaveResults()
            val fragment = TestFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson test")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)

        }

        lessonSelectButton.setOnClickListener {
            leaveResults()

            val count = activity?.supportFragmentManager?.backStackEntryCount

            Log.d(TAG, "count: $count")


            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("lesson select")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)
        }


        retryButton.setOnClickListener {

            activity?.supportFragmentManager?.popBackStack()
            sharedPref.edit()
                .putBoolean(Constants.KEY_IN_RESULTS, false)
                .apply()

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

    }

    private fun leaveResults() {
        //pop practice
        activity?.supportFragmentManager?.popBackStack()
        //pop card
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onStop() {
        super.onStop()
        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, false)
            .apply()
    }
}