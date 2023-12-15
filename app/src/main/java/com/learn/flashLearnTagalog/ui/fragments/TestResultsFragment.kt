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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import com.learn.flashLearnTagalog.ui.viewmodels.SavedDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

//var lesson: Lesson, var adapter: TestWordAdapter

@AndroidEntryPoint
class TestResultsFragment : Fragment(R.layout.fragment_test_results) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: LessonViewModel by activityViewModels()
    private val savedDataModel: SavedDataViewModel by activityViewModels()
    private lateinit var textLine: String

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val score = requireArguments().getInt("words_correct")

        val rvTodoList: RecyclerView = view.findViewById(R.id.rvWordResults)
        val scoreText: TextView = view.findViewById(R.id.tvScore)
        val totalText: TextView = view.findViewById(R.id.tvTotal)
        val percentageText: TextView = view.findViewById(R.id.tvPercentage)

        val retryButton: Button = view.findViewById(R.id.btnRetry)
        val nextButton: Button = view.findViewById(R.id.btnNextLesson)
        //  val statsButton: Button = view.findViewById(R.id.btnStats)
        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)
        //  var adapter = TestWordAdapter(mutableListOf())
        //  rvTodoList.adapter = adapter
        rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        val guideline: Guideline = view.findViewById(R.id.glRight)

        guideline.setGuidelinePercent(1f)
        nextButton.visibility = View.GONE

        val listSize = viewModel.listSize

        if ((100 * score / listSize) >= 50) {

            viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
                val nextId = "${lesson.category}_${lesson.level + 1}"

                //TODO: better solution
                val lessonJSON = "savedLessons.json"
                val savedLessons = JsonUtility.getSavedLessons(requireActivity(), lessonJSON)
                var nextLesson: Lesson? = null

                var nextLessonWordList: List<Word> = listOf()

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

                        savedDataModel.viewedLessons.observe(viewLifecycleOwner) { lessons ->
                            if (lessons.contains(nextId)) {
                                savedDataModel.practicedWords.observe(viewLifecycleOwner) { words ->
                                    nextLessonWordList = words[nextId]!!
                                }
                            } else {
                                suspend {
                                    DataUtility.getAllWordsForLesson(
                                        nextLesson.category.lowercase(),
                                        nextLesson.minLength,
                                        nextLesson.wordCount.toLong()
                                    ).toMutableList()
                                }
                            }
                            scope.cancel()
                        }
                    }

                    nextButton.setOnClickListener {
                        savedDataModel.updatePracticedWords(
                            requireActivity(),
                            nextId,
                            nextLessonWordList
                        )
                        savedDataModel.updateViewedList(requireActivity(), nextId)

                        viewModel.updateLesson(nextLesson)
                        val fragment = PracticeFragment()
                        val transaction = fragmentManager?.beginTransaction()
                        transaction?.replace(R.id.main_nav_container, fragment)
                            ?.addToBackStack("test")
                            ?.commit()
                        (activity as LearningActivity?)?.transitionFragment()
                    }
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
    }
}