package com.learn.flashLearnTagalog.ui.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_FILTERS_ACTIVE
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.dialog_fragments.FilterLessonDialogFragment
import com.learn.flashLearnTagalog.ui.misc.ItemDecoration
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LessonSelectFragment : Fragment(R.layout.fragment_lessons_select) {

    private var lessonSource = false


    private lateinit var lessonAdapter: LessonAdapter
    private val newDifficulties = mutableSetOf<String>()
    private val viewModel: LessonViewModel by activityViewModels()

    private lateinit var btnFilter: ImageButton

    //TODO: replace with persistent data list
    private var dbLessons = mutableListOf<Lesson>()

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // refreshList(null, requireActivity())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lessonAdapter = LessonAdapter(viewModel, mutableListOf())
        sharedPref.edit()
            .putStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)
            .apply()

        dbLessons = JsonUtility.getSavedLessons(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_lessons_select, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val networkErrorText: TextView = view.findViewById(R.id.tvNetworkError)
        btnFilter = view.findViewById(R.id.ibFilter)
        val rvLessonList: RecyclerView = view.findViewById(R.id.rvLessons)
        val grid = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        val decorator = ItemDecoration(25)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        val dialog: DialogFragment = FilterLessonDialogFragment()

        val lessonSwitch: SwitchCompat = view.findViewById(R.id.swLessonSource)

        lessonSwitch.setOnClickListener {
            lessonSource = !lessonSource
            Log.d(TAG, "source: $lessonSource")
            refreshList(networkErrorText, requireActivity())
        }

        btnFilter.setOnClickListener {
            if (!dialog.isAdded) {
                dialog.isCancelable = true
                dialog.show(childFragmentManager, "test")
            }
        }

        rvLessonList.adapter = lessonAdapter
        rvLessonList.layoutManager = grid

        rvLessonList.addItemDecoration(decorator)

        swipeRefreshLayout.setOnRefreshListener {
            refreshList(networkErrorText, requireActivity())
            swipeRefreshLayout.isRefreshing = false
        }

        if (dbLessons.isNotEmpty()) {
            networkErrorText.visibility = View.GONE
            createLessonList(
                sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!
            )
        } else {
            networkErrorText.visibility = View.VISIBLE
        }
    }

    //TODO: return bool if list not loaded, use this to set text rather than accept null
    @OptIn(DelicateCoroutinesApi::class)
    fun refreshList(networkErrorText: TextView?, activity: Activity) {
        val refreshScope = CoroutineScope(Job() + Dispatchers.Main)

        Log.d(TAG, "activity: $activity")

        if (!this::sharedPref.isInitialized) {
            Log.d(TAG, "not Initialized")
            sharedPref = activity.getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                AppCompatActivity.MODE_PRIVATE
            )
        }

        refreshScope.launch {
            async {

                if (!lessonSource) {
                    dbLessons = JsonUtility.getSavedLessons(requireActivity())
                } else {
                    dbLessons = mutableListOf()
                }

                if (dbLessons.isEmpty()) {

                    if (networkErrorText != null)
                        networkErrorText.visibility = View.VISIBLE

                    DataUtility.updateLocalData(
                        activity,
                        signUp = false,
                        rewriteJSON = true
                    )

//                    if (dbLessons.isEmpty()) {
//                    } else {
//                        if (networkErrorText != null)
//                            networkErrorText.visibility = View.GONE
//                        createLessonList(
//                            sharedPref.getStringSet(
//                                KEY_LESSON_DIFFICULTY,
//                                newDifficulties
//                            )!!
//                        )
//                    }

                } else {
                    if (networkErrorText != null)
                        networkErrorText.visibility = View.GONE
                }

                createLessonList(
                    sharedPref.getStringSet(
                        KEY_LESSON_DIFFICULTY,
                        newDifficulties
                    )!!
                )
            }.await()
            refreshScope.cancel()
        }
    }

    @DelicateCoroutinesApi
    fun createLessonList(difficulties: MutableSet<String>) {

        if (!this::lessonAdapter.isInitialized) {
            lessonAdapter = LessonAdapter(viewModel, mutableListOf())
        }

        Log.d(TAG, "create")
        if (sharedPref.getBoolean(KEY_IN_LESSONS, false) && sharedPref.getBoolean(
                KEY_LESSON_FILTERS_ACTIVE,
                false
            )
        ) {
            Log.d(TAG, "active")
            btnFilter.setImageResource(R.drawable.filter_active)
        } else {
            Log.d(TAG, "not ")
            btnFilter.setImageResource(R.drawable.filter)
        }

        lessonAdapter.deleteLessons()
        var add: Boolean
        //after database access is complete, add lessons to adapter
        val difficultiesEmpty = difficulties.isEmpty()

        for (lesson in dbLessons) {
            val id = lesson.id
            add = true
            //only add lessons that fit within the selected filter requirements
            if (lesson.level > 0) {
                //Log.d(TAG, "lessons level: ${lesson.level}")
                //TODO: replace with difficulty, set names for 1-5
                if (difficultiesEmpty || difficulties.contains((lesson.level).toString())) {
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

    override fun onStart() {
        super.onStart()
        sharedPref.edit().putBoolean(KEY_IN_LESSONS, true).apply()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "select stop")
        sharedPref.edit().putBoolean(KEY_IN_LESSONS, false).apply()
    }
}