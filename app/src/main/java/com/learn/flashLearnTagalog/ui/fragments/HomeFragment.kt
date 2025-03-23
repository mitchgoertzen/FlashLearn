package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var words: Button
    private lateinit var lessons: Button

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

        words = view.findViewById(R.id.btnAddWords)
        lessons = view.findViewById(R.id.btnAddLessons)

        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {
            words.visibility = View.VISIBLE
            lessons.visibility = View.VISIBLE
        }


        sharedPref.edit().putBoolean(Constants.KEY_IN_HOME, true).apply()
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

    fun reloadAdmin() {
        Log.d(TAG, "reload admin")
        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {

            Log.d(TAG, "true")

            val language = "tagalog"
            val dataProcessor = DataProcessor(resources, language)
            val lessonCreator = LessonCreator()


            words.visibility = View.VISIBLE
            lessons.visibility = View.VISIBLE

            words.setOnClickListener {

                val words = dataProcessor.getWords()

                val lessonWords = mutableMapOf<String, Word>()

                Log.d(ContentValues.TAG, "COUNT: ${words.size}")

                for (i in 0 until words.size) {

                    val w = words[i]

                    if (lessonWords[w.id] != null) {
                        Log.d(ContentValues.TAG, "entry ${lessonWords[w.id]}")
                        Log.d(ContentValues.TAG, "id ${w.id}")
                        Log.d(ContentValues.TAG, "new word $w")
                    }
                    lessonWords[w.id] = w


                }

                Log.d(ContentValues.TAG, "words: ${lessonWords.size}")

                DataUtility.insertAllWords(lessonWords, language)

                Log.d(ContentValues.TAG, "LESSON WORD COUNT: ${lessonWords.size}")
            }

            lessons.setOnClickListener {

                val scope = CoroutineScope(Job() + Dispatchers.Main)
                scope.launch {
                    async { lessonCreator.createLessons(resources, "", "") }.await()

                    val lessonList = lessonCreator.getLessons()

                    val lessonMap = mutableMapOf<String, Lesson>()



                    for (l in lessonList) {

                        if (lessonMap[l.id] != null) {
                            Log.d(ContentValues.TAG, "entry ${lessonMap[l.id]}")
                            Log.d(ContentValues.TAG, "id ${l.id}")
                            Log.d(ContentValues.TAG, "new word $l")
                        }

                        lessonMap[l.id] = l
                    }

                    Log.d(ContentValues.TAG, "lessons: ${lessonMap.size}")
                    DataUtility.insertAllLessons(lessonMap, "flash_learn", "tagalog")
                    scope.cancel()
                }


            }
        } else {
            words.visibility = View.GONE
            lessons.visibility = View.GONE
            Log.d(TAG, "false")

        }

    }
}