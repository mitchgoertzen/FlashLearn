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
import com.learn.flashLearnTagalog.ItemDecoration
import com.learn.flashLearnTagalog.LessonAdapter
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.other.Constants.KEY_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

        val btnFilter : ImageButton = view.findViewById(R.id.ibFilter)




        btnFilter.setOnClickListener{
            //spinner.performClick()
            val dialog : DialogFragment = FilterLessonFragment(lessonAdapter)

            dialog.isCancelable = true
            dialog.show(childFragmentManager, "test")
        }


        val rvLessonList : RecyclerView = view.findViewById(R.id.rvLessons)

        rvLessonList.adapter = lessonAdapter
        rvLessonList.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

        val decorator = ItemDecoration(25)
        rvLessonList.addItemDecoration(decorator)

        //create new coroutine
        println(sharedPref)
        val newDifficulties = mutableSetOf("1", "2", "3", "4")
        createLessonList(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, newDifficulties)!!)

        return view
    }

    fun createLessonList(difficulties: MutableSet<String>){
        println("create lesson $difficulties")
        difficulties!!.add("0")
        var dbLessons : MutableList<Lesson> = mutableListOf()
        GlobalScope.launch(Dispatchers.Main) {
            suspend {
                //get lessons from database
                viewModel.getAllLessons().observe(viewLifecycleOwner) {
                    dbLessons = it.toMutableList()
                }
                Handler(Looper.getMainLooper()).postDelayed({


                    //after database access is complete, add lessons to adapter
                    for(lesson in dbLessons){
                        if(difficulties!!.contains((lesson.level).toString()))
                            lessonAdapter.addToDo(lesson)
                    }
                    //TODO: used saved variable, not hardcoded
                    lessonAdapter.sortList(sharedPref.getInt(KEY_LESSON_SORTING, 2))
                }, 500) }.invoke()
        }
    }

}