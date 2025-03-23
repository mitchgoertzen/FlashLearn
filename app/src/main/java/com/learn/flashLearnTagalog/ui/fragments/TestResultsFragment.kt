package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.color.MaterialColors
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


//var lesson: Lesson, var adapter: TestWordAdapter

@AndroidEntryPoint
class TestResultsFragment : Fragment(R.layout.fragment_lessons_test_results) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: LessonViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, true)
            .apply()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lessons_test_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val score = requireArguments().getInt("words_correct")
        val rvTodoList: RecyclerView = view.findViewById(R.id.rvWordResults)
        val scoreText: TextView = view.findViewById(R.id.tvScore)
        val totalText: TextView = view.findViewById(R.id.tvTotal)
        val percentageText: TextView = view.findViewById(R.id.tvPercentage)
        val infoText: TextView = view.findViewById(R.id.tvResultInfo)
        val retryButton: Button = view.findViewById(R.id.btnRetry)
        val practiceButton: Button = view.findViewById(R.id.btnPractice)
        val nextButton: Button = view.findViewById(R.id.btnNextLesson)
        //  val statsButton: Button = view.findViewById(R.id.btnStats)
        val lessonSelectButton: Button = view.findViewById(R.id.btnLessonSelect)
        val guideline: Guideline = view.findViewById(R.id.glRight)
        val listSize = viewModel.listSize
        val percentage = (100 * score / listSize)
        val passingScore = 50

        viewModel.currentAdapter.observe(viewLifecycleOwner) { adapter ->
            rvTodoList.adapter = adapter
            rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))
        }

        guideline.setGuidelinePercent(1f)
        nextButton.visibility = View.GONE

        scoreText.text = score.toString()
        totalText.text = listSize.toString()

        percentageText.text = "$percentage%"

        val resultText: String
        val resultColor: Int

        if (percentage >= passingScore) {

            viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
                val nextId = "${lesson.category}_${lesson.level + 1}"

                Log.d(TAG, "next id: $nextId")

                //TODO: better solution
                val lessonJSON = "savedLessons.json"
                val savedLessons = JsonUtility.getSavedLessons(requireActivity())
                var nextLesson: Lesson? = null



               // Log.d(TAG, "saved lessons: $savedLessons")


                var nextLessonWordList: List<Word> = mutableListOf()

                for (l in savedLessons) {
                    if (l.id == nextId) {

                        Log.d(TAG, "l: ${l.id},, next: $nextId")
                        nextLesson = l
                    }
                }


                Log.d(TAG, "next lesson: $nextLesson")


                if (nextLesson != null) {
                    guideline.setGuidelinePercent(0.60f)
                    nextButton.visibility = View.VISIBLE

                    val scope = CoroutineScope(Job() + Dispatchers.Main)
                    scope.launch {

                        nextLessonWordList =
                            if (TempListUtility.viewedLessons.contains(nextId) && TempListUtility.viewedWords.contains(
                                    nextId
                                )
                            ) {
                                TempListUtility.viewedWords[nextId]!!
                            } else {
                                DataUtility.getAllWordsForLesson(
                                    //TODO: replace with sharedpref,
                                    "tagalog",
                                    nextLesson.category.lowercase(),
                                    nextLesson.minLength,
                                    nextLesson.maxLength,
                                    nextLesson.wordCount.toLong()
                                ).toMutableList()
                            }
                        scope.cancel()
                    }


                    nextButton.setOnClickListener {

                        leaveResults()


                        Log.d(TAG, "SIZE: ${nextLessonWordList.size}")
                        if (nextLessonWordList.isNotEmpty()) {
                            TempListUtility.viewedWords[nextId] = nextLessonWordList
                            TempListUtility.viewedLessons.add(nextId)

                            JsonUtility.writeJSON(
                                requireActivity(),
                                //TODO: save as shared pref
                                "viewedLessons.json",
                                TempListUtility.viewedLessons,
                                false
                            )
                            JsonUtility.writeJSON(
                                requireActivity(),
                                //TODO: save as shared pref
                                "savedWords.json",
                                TempListUtility.viewedWords,
                                false
                            )
                            viewModel.updateWordList(nextLessonWordList)
                            viewModel.updateLesson(nextLesson)

                            activity?.supportFragmentManager?.popBackStack()
                            val fragment = PracticeFragment()
                            val transaction = fragmentManager?.beginTransaction()
                            transaction?.replace(R.id.main_nav_container, fragment)
                                ?.addToBackStack("test")
                                ?.commit()
                            (activity as LearningActivity?)?.transitionFragment(2)
                        }
                    }
                }

            }

            resultColor = MaterialColors.getColor(requireContext(), R.attr.colorOnTertiary, Color.GRAY)
            resultText = "You Passed!"
        } else {
            resultColor = MaterialColors.getColor(requireContext(), R.attr.colorOnSecondary, Color.GRAY)
            resultText = "Not Quite...\na passing Score is $passingScore%"
        }

        infoText.text = resultText
        infoText.setTextColor(resultColor)
        percentageText.setTextColor(resultColor)

        retryButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        practiceButton.setOnClickListener {
            leaveResults()
            activity?.supportFragmentManager?.popBackStack()
            val fragment = PracticeFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("practice")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)
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
            leaveResults()
            activity?.supportFragmentManager?.popBackStack()
            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("lesson select")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)
        }
    }

    private fun leaveResults() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun onStop() {
        super.onStop()
        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_RESULTS, false)
            .apply()
    }
}