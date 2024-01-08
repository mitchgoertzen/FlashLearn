package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.LessonAdapter
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants.KEY_IN_LESSONS
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.misc.ItemDecoration
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class LessonSelectFragment : Fragment() {

    private lateinit var lessonAdapter: LessonAdapter
    private val newDifficulties = mutableSetOf("1", "2", "3", "4", "5", "6")
    private val viewModel: LessonViewModel by activityViewModels()

    //TODO: replace with persistent data list
    private lateinit var dbLessons: MutableList<Lesson>

    @Inject
    lateinit var sharedPref: SharedPreferences

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lessonAdapter = LessonAdapter(viewModel, mutableListOf())
        sharedPref.edit().putBoolean(KEY_IN_LESSONS, true).apply()
        val view = inflater.inflate(R.layout.fragment_lesson_select, container, false)


        val btnFilter: ImageButton = view.findViewById(R.id.ibFilter)

        btnFilter.setOnClickListener {
            val dialog: DialogFragment = FilterLessonFragment()

            dialog.isCancelable = true
            dialog.show(childFragmentManager, "test")
        }

        val rvLessonList: RecyclerView = view.findViewById(R.id.rvLessons)
        val grid = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        rvLessonList.adapter = lessonAdapter
        rvLessonList.layoutManager = grid


        val decorator = ItemDecoration(25)
        rvLessonList.addItemDecoration(decorator)


        sharedPref.edit()
            .putStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)
            .apply()

        dbLessons = JsonUtility.getSavedLessons(requireActivity())

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            refreshList()
            swipeRefreshLayout.isRefreshing = false
        }

        createLessonList(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!)
        return view
    }

    fun refreshList() {
        val refreshScope = CoroutineScope(Job() + Dispatchers.Main)
        refreshScope.launch {
            async {

                Log.d(ContentValues.TAG, "1")
                if (dbLessons.isEmpty()) {

                    Log.d(ContentValues.TAG, "2")
                    DataUtility.updateLocalData(
                        requireActivity(),
                        signUp = false,
                        rewriteJSON = true
                    )

                    Log.d(ContentValues.TAG, "3")
                    dbLessons = JsonUtility.getSavedLessons(requireActivity())
                }

                Log.d(ContentValues.TAG, "4")
                createLessonList(
                    sharedPref.getStringSet(
                        KEY_LESSON_DIFFICULTY,
                        newDifficulties
                    )!!
                )
            }.await()

            Log.d(ContentValues.TAG, "5")
            refreshScope.cancel()
        }
    }

    @DelicateCoroutinesApi
    fun createLessonList(difficulties: MutableSet<String>) {

        lessonAdapter.deleteLessons()
        var add: Boolean
        //after database access is complete, add lessons to adapter


        for (lesson in dbLessons) {
            val lessonStats = LessonStats()

            add = true
            //only add lessons that fit within the selected filter requirements
            if (lesson.level > 0) {
                //TODO: replace with difficulty, set names for 1-5
                if (difficulties.contains((lesson.level).toString())) {

                    //  Log.d(TAG, "CONTAINS difficulty")
                    val category = sharedPref.getString(KEY_LESSON_CATEGORY, "All")

                    if (!category.equals("All")) {
                        if (lesson.category != category) {
                            add = false
                        }
                    }

                    if (sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false))
                        if (!lessonStats.practiceCompleted) {
                            add = false
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false))
                        if (!lessonStats.testPassed) {
                            add = false
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false))
                        if (!TempListUtility.unlockedLessons.contains(lesson.id)) {
                            add = false
                        }

                } else {
                    add = false
                }
            }
            if (add) {
                lessonAdapter.addLesson(lesson)
            }
        }

        //TODO: used saved variable, not hardcoded
        lessonAdapter.sortList(sharedPref.getInt(KEY_LESSON_SORTING, 1))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPref.edit().putBoolean(KEY_IN_LESSONS, false).apply()
    }
}