package com.learn.flashLearnTagalog.ui.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
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
    private var dbLessons = mutableListOf<Lesson>()

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

        val networkErrorText: TextView = view.findViewById(R.id.tvNetworkError)

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
            refreshList(networkErrorText, requireActivity())
            swipeRefreshLayout.isRefreshing = false
        }

        if (dbLessons.isNotEmpty()) {
            networkErrorText.visibility = View.GONE
            createLessonList(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!)
        } else {
            networkErrorText.visibility = View.VISIBLE
        }

        return view
    }

    //TODO: return bool if list not loaded, use this to set text rather than accept null
    @OptIn(DelicateCoroutinesApi::class)
    fun refreshList(networkErrorText: TextView?, activity: Activity) {
        val refreshScope = CoroutineScope(Job() + Dispatchers.Main)

        sharedPref = activity.getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )

        refreshScope.launch {
            async {
                if (dbLessons.isEmpty()) {

                    DataUtility.updateLocalData(
                        activity,
                        signUp = false,
                        rewriteJSON = true
                    )

                    dbLessons = JsonUtility.getSavedLessons(activity)

                    if (dbLessons.isEmpty()) {
                        if (networkErrorText != null)
                            networkErrorText.visibility = View.VISIBLE
                    } else {
                        if (networkErrorText != null)
                            networkErrorText.visibility = View.GONE
                        createLessonList(
                            sharedPref.getStringSet(
                                KEY_LESSON_DIFFICULTY,
                                newDifficulties
                            )!!
                        )
                    }
                } else {

                    if (networkErrorText != null)
                        networkErrorText.visibility = View.GONE
                    createLessonList(
                        sharedPref.getStringSet(
                            KEY_LESSON_DIFFICULTY,
                            newDifficulties
                        )!!
                    )
                }
            }.await()
            refreshScope.cancel()
        }
    }

    @DelicateCoroutinesApi
    fun createLessonList(difficulties: MutableSet<String>) {

        lessonAdapter.deleteLessons()
        var add: Boolean
        //after database access is complete, add lessons to adapter


        // Log.d(TAG, "lessons: ${dbLessons.size}")
        for (lesson in dbLessons) {
            val id = lesson.id
            add = true
            //only add lessons that fit within the selected filter requirements
            if (lesson.level > 0) {

                //Log.d(TAG, "lessons level: ${lesson.level}")
                //TODO: replace with difficulty, set names for 1-5
                if (difficulties.contains((lesson.level).toString())) {

                    //Log.d(TAG, "CONTAINS difficulty")
                    val category = sharedPref.getString(KEY_LESSON_CATEGORY, "All")

                    if (!category.equals("All")) {

                        //  Log.d(TAG, "lessons cat: ${lesson.category}")
                        if (lesson.category != category) {
                            add = false
                        }
                    }

                    if (sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false))
                        if (!TempListUtility.practicedLessons.contains(id)) {
                            add = false
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false))
                        if (!TempListUtility.passedLessons.contains(id)) {
                            add = false
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false))
                        if (!TempListUtility.unlockedLessons.contains(id)) {
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