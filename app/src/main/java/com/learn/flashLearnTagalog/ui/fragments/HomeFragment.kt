package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
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

    private lateinit var adminBox: ConstraintLayout
    private lateinit var words: Button
    private lateinit var lessons: Button
    private lateinit var wordsUpdate: Button
    private lateinit var start: EditText
    private lateinit var end: EditText
    private lateinit var log: TextView
    private lateinit var updateLessons: CheckBox


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

        adminBox = view.findViewById(R.id.clAdmin)

        words = view.findViewById(R.id.btnAddWords)
        lessons = view.findViewById(R.id.btnAddLessons)
        wordsUpdate = view.findViewById(R.id.btnUpdateWords)
        start = view.findViewById(R.id.etStartIndex)
        end = view.findViewById(R.id.etEndIndex)
        log = view.findViewById(R.id.tvLog)
        updateLessons = view.findViewById(R.id.cbUpdateLessons)

        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {
            adminBox.visibility = View.VISIBLE
        }

        reloadAdmin()

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


            adminBox.visibility = View.VISIBLE

            words.setOnClickListener {
                dataProcessor.makeList(
                    0,
                    R.raw.tag_to_eng_dict,
                    ',',
                    start.text.toString().toInt(),
                    end.text.toString().toInt(),
                    log
                )
            }

            wordsUpdate.setOnClickListener {
                dataProcessor.makeList(
                    1, R.raw.tag_dollar, '$',
                    start.text.toString().toInt(),
                    end.text.toString().toInt(),
                    log
                )
            }

            lessons.setOnClickListener {

                val scope = CoroutineScope(Job() + Dispatchers.Main)
                scope.launch {
                    async { lessonCreator.createLessons(resources, "", "") }.await()

                    val lessonList = lessonCreator.getLessons()

                    Log.d(TAG, "lessons: $lessonList")

                    if (updateLessons.isChecked) {

                        DataUtility.batchUpdateLessons(
                            "flash_learn",
                            "tagalog",
                            lessonList,
                            //0 = categories
                            //1 = image
                            //2 = maxLength
                            //3 = maxLines
                            //4 = minLength
                            //5 = wordCount
                            listOf(5)
                        )
                    } else {
                        val lessonMap = mutableMapOf<String, Lesson>()

                        for (l in lessonList) {
                            if (lessonMap[l.id] != null) {
                                Log.d(TAG, "entry ${lessonMap[l.id]}")
                                Log.d(TAG, "id ${l.id}")
                                Log.d(TAG, "new word $l")
                            }
                            lessonMap[l.id] = l
                        }

                        Log.d(TAG, "lesson map: ${lessonMap.size}")


                        DataUtility.insertAllLessons(lessonMap, "flash_learn", "tagalog")
                    }

                    scope.cancel()
                }


            }
        } else {
            adminBox.visibility = View.GONE
            Log.d(TAG, "false")

        }

    }
}
