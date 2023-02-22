package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.LessonAdapter
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.misc.ItemDecoration
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class LessonSelectFragment : Fragment() {

    private lateinit var lessonAdapter: LessonAdapter

    @Inject
    lateinit var sharedPref: SharedPreferences
    private val viewModel: MainViewModel by viewModels()


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lessonAdapter = LessonAdapter(mutableListOf())

        val view = inflater.inflate(R.layout.fragment_lesson_select, container, false)

        val btnFilter: ImageButton = view.findViewById(R.id.ibFilter)

//        val practiceCompleteIcon: TextView = view.findViewById(R.id.tvPracCompleted)
//        val testPassedIcon: TextView = view.findViewById(R.id.tvtestPassed)

        btnFilter.setOnClickListener {
            val dialog: DialogFragment = FilterLessonFragment(lessonAdapter)

            dialog.isCancelable = true
            dialog.show(childFragmentManager, "test")
        }

        val rvLessonList: RecyclerView = view.findViewById(R.id.rvLessons)

        rvLessonList.adapter = lessonAdapter
        rvLessonList.layoutManager =
            GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

        val decorator = ItemDecoration(25)
        rvLessonList.addItemDecoration(decorator)

        val newDifficulties = mutableSetOf("1", "2", "3", "4", "5")
        sharedPref.edit()
            .putStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)
            .apply()
        createLessonList(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!)

        return view
    }

    @DelicateCoroutinesApi
    fun createLessonList(difficulties: MutableSet<String>) {
        var dbLessons: MutableList<Lesson> = mutableListOf()
        CoroutineScope(Dispatchers.Main).launch {
            suspend {
                //get lessons from database
                viewModel.getAllLessons().observe(viewLifecycleOwner) {
                    dbLessons = it.toMutableList()
                }
                Handler(Looper.getMainLooper()).postDelayed({

                    var add: Boolean
                    //after database access is complete, add lessons to adapter
                    for (lesson in dbLessons) {
                        add = true
                        //only add lessons that fit within the selected filter requirements
                        if (lesson.level > 0) {
                            if (difficulties.contains((lesson.difficulty).toString())) {

                                val category = sharedPref.getString(KEY_LESSON_CATEGORY, "All")

                                if (!category.equals("All")) {
                                    if (lesson.category != category)
                                        add = false
                                }

                                if (sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false))
                                    if (!lesson.practiceCompleted)
                                        add = false

                                if (sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false))
                                    if (!lesson.testPassed)
                                        add = false

                                if (sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false))
                                    if (lesson.locked)
                                        add = false
                            } else
                                add = false
                        }
                        if (add)
                            lessonAdapter.addLesson(lesson)
                    }
                    //TODO: used saved variable, not hardcoded
                    lessonAdapter.sortList(sharedPref.getInt(KEY_LESSON_SORTING, 1))
                }, 500)
            }.invoke()
        }
    }

}