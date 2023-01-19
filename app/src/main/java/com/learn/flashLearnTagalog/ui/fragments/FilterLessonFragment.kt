package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.DictionaryAdapter
import com.learn.flashLearnTagalog.LessonAdapter
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.SortOptionAdapter
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FilterLessonFragment(private var lessonAdapter: LessonAdapter) : DialogFragment() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var sortOptionAdapter: SortOptionAdapter

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_lesson, container, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window : FrameLayout = view.findViewById(R.id.clHintBackground)

        sortOptionAdapter = SortOptionAdapter(mutableListOf())


        //connect local variables to elements in fragment
        val rvSortOptions : RecyclerView = view.findViewById(R.id.rvSortingOptions)

        rvSortOptions.adapter = sortOptionAdapter
        rvSortOptions.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        sortOptionAdapter.addToDo("Category")
        sortOptionAdapter.addToDo("Subcategory")
        sortOptionAdapter.addToDo("Difficulty: Low to High")
        sortOptionAdapter.addToDo("Difficulty: High to Low")
        sortOptionAdapter.addToDo("Unlocked")

        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val popup :LinearLayout = view.findViewById(R.id.llFilterPopup)

        popup.setOnTouchListener { _, _ ->
            true
        }

        popup.setBackgroundResource(R.drawable.filter_lesson_popup)

        val languages = resources.getStringArray(R.array.Sorting)

        val spinner: Spinner = view.findViewById(R.id.spinner)

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, languages
        )

        val apply : Button = view.findViewById(R.id.btnApplyFilters)

        apply.setOnClickListener{
            lessonAdapter.sortList(sortOptionAdapter.getSelected())
            dialog?.dismiss()
        }

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("nothing")
            }
        }

        return view
    }
}

