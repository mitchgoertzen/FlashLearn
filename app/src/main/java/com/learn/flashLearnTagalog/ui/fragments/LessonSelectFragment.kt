package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.ItemDecoration
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.LessonAdapter
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lessonAdapter = LessonAdapter(mutableListOf())
        var dbLessons : MutableList<Lesson> = mutableListOf()

        val view = inflater.inflate(R.layout.fragment_lesson_select, container, false)
        val rvLessonList : RecyclerView = view.findViewById(R.id.rvLessons)

        rvLessonList.adapter = lessonAdapter
        rvLessonList.layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)

        val decorator = ItemDecoration(50)
        rvLessonList.addItemDecoration(decorator)

        GlobalScope.launch(Dispatchers.Main) {
            suspend {
                viewModel.getAllLessons().observe(viewLifecycleOwner) {
                    dbLessons = it.toMutableList()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    for(lesson in dbLessons){
                        lessonAdapter.addToDo(lesson)
                    }
                }, 250) }.invoke()
        }

        return view
    }
}