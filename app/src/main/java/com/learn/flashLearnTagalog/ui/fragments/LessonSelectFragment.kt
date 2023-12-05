package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.LessonAdapter
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.misc.ItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class LessonSelectFragment : Fragment() {

    private lateinit var lessonAdapter: LessonAdapter

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

        lessonAdapter = LessonAdapter(mutableListOf(), mutableListOf())

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

        //TODO: dbLessons = get from static class

        val lessonJSON = "savedLessons.json"
        dbLessons =  JsonUtility.getSavedLessons(requireActivity(), lessonJSON)

        Log.d(TAG, "test size: ${dbLessons.size}")

//        for(l in dbLessons){
//            Log.d(TAG, l.toString())
//        }


        //val scope = CoroutineScope(Job() + Dispatchers.Main)

//        scope.launch {
////            if(list is empty){
////
////            }
//            dbLessons = async { DataUtility.getAllLessons().toMutableList() }.await()
//
//            Log.d(TAG, "SIZE -  ${dbLessons.size}")
            createLessonList(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!)
//            scope.cancel()
//        }

        return view
    }

    @DelicateCoroutinesApi
    fun createLessonList(difficulties: MutableSet<String>) {
//                viewModel.getAllLessons().observe(viewLifecycleOwner) {
//                    dbLessons = it.toMutableList()
//                }
        // Handler(Looper.getMainLooper()).postDelayed({
        var add: Boolean
        //after database access is complete, add lessons to adapter
        for (lesson in dbLessons) {

            //  Log.d(TAG, "lesson -  ${lesson.category} ${lesson.level}")

            //    Log.d(TAG, "title: ${lesson.category}")
            //val lessonStats = DataUtility.getLessonStats(lesson.id)
            val lessonStats = LessonStats()

            add = true
            //only add lessons that fit within the selected filter requirements
            if (lesson.level > 0) {

                // Log.d(TAG, "OVER 0")
                if (difficulties.contains((lesson.level).toString())) {

                    //  Log.d(TAG, "CONTAINS difficulty")
                    val category = sharedPref.getString(KEY_LESSON_CATEGORY, "All")

                    if (!category.equals("All")) {
                        if (lesson.category != category) {
                            add = false
                            Log.d(TAG, "not in cats")
                        }
                    }

                    if (sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false))
                        if (!lessonStats.practiceCompleted) {
                            add = false
                            Log.d(TAG, "practice not completed")
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false))
                        if (!lessonStats.testPassed) {
                            add = false
                            Log.d(TAG, "test passed")
                        }

                    if (sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false))
                        if (!TempListUtility.unlockedLessons.contains(lesson.id)) {
                            add = false
                            Log.d(TAG, "locked")
                        }
                } else {
                    add = false
                    Log.d(TAG, " difficulty ${lesson.difficulty} not in list")
                }
            } else {

                Log.d(TAG, "level under 0")
            }
            if (add) {
                //  Log.d(TAG, "ADD")
                lessonAdapter.addLesson(lesson, lessonStats)

                //  Log.d(ContentValues.TAG, "ADDED")
            } else {

                Log.d(ContentValues.TAG, "NOT ADDED")
            }

//
            //      Log.d(TAG, "SIZE -  ${dbLessons.size}")
        }


        //TODO: used saved variable, not hardcoded
        lessonAdapter.sortList(sharedPref.getInt(KEY_LESSON_SORTING, 1))
//        GlobalScope.launch(Dispatchers.Main) {
//            suspend {
//                //get lessons from database
//
//                // }, 500)
//            }.invoke()
//        }
    }



}