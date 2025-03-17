package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import javax.inject.Inject


class HomeFragment : Fragment() {


    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lessonButton: Button = view.findViewById(R.id.btnLesson)
        val dictionaryButton: Button = view.findViewById(R.id.btnDictionary)
        val words: Button = view.findViewById(R.id.btnAddWords)
        val lessons: Button = view.findViewById(R.id.btnAddLessons)

        val dataProcessor = DataProcessor(resources)

        words.setOnClickListener {

            val words = dataProcessor.getWords()

            val lessonWords = mutableMapOf<String, Word>()

            Log.d(ContentValues.TAG, "COUNT: ${words.size}")

            for (i in 0 until words.size) {

                val w = words[i]
                lessonWords[w.id] = w

            }

            DataUtility.insertAllWords(lessonWords)

            Log.d(ContentValues.TAG, "LESSON WORD COUNT: ${lessonWords.size}")
        }

        lessons.setOnClickListener {

//            val scope = CoroutineScope(Job() + Dispatchers.Main)
//            scope.launch {
//                async { lessonCreator.createLessons(resources) }.await()
//
//                val lessonList = lessonCreator.getLessons()
//
//                val lessonMap = mutableMapOf<String, Lesson>()
//
//                for (l in lessonList) {
//                    lessonMap[l.id] = l
//                }
//
//                DataUtility.insertAllLessons(lessonMap)
//                scope.cancel()
//            }


        }


        sharedPref.edit().putBoolean(Constants.KEY_HOME, true).apply()
        (activity as LearningActivity?)?.goHome()

        lessonButton.setOnClickListener {

            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("lessons")?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)

        }

        dictionaryButton.setOnClickListener {
            val fragment = DictionaryFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("dictionary")?.commit()
            (activity as LearningActivity?)?.transitionFragment(1)
        }
    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}