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
import com.learn.flashLearnTagalog.LessonAdapter
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.SortOptionAdapter
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FilterLessonFragment(private var lessonAdapter: LessonAdapter) : DialogFragment() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var sortOptionAdapter: SortOptionAdapter
    private var difficulties: MutableSet<String> = mutableSetOf()
    //private var difficulties : MutableSet<String> = mutableSetOf()

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

       // val newSet: Set<String> = HashSet<String>(sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, HashSet<String>()))
        //val fetch: Set<String> = sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, null)!!
        //sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, null)



    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_lesson, container, false)

        println("pref: ${sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, mutableSetOf())}")
        sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, mutableSetOf())!!.forEach {
            difficulties.add(it)
        }

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window : FrameLayout = view.findViewById(R.id.clHintBackground)

        sortOptionAdapter = SortOptionAdapter(mutableListOf(), sharedPref.getInt(KEY_LESSON_SORTING, 2))


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

        println("difficulties: $difficulties")
        //TODO: change to function,  dont repeat 4 times
        val level1 : CheckBox = view.findViewById(R.id.cbLevel1)
        if(difficulties.contains("1")){
            level1.isChecked = true
        }
        level1.setOnClickListener{
            if (level1.isChecked){
                difficulties.add("1")
            }else{
                difficulties.remove("1")
            }
        }

        val level2 : CheckBox = view.findViewById(R.id.cbLevel2)
        if(difficulties.contains("2"))
            level2.isChecked = true
        level2.setOnClickListener{
            if (level2.isChecked){
                difficulties.add("2")
            }else{
                difficulties.remove("2")
            }
        }

        val level3 : CheckBox = view.findViewById(R.id.cbLevel3)
        if(difficulties.contains("3"))
            level3.isChecked = true
        level3.setOnClickListener{
            if (level3.isChecked){
                difficulties.add("3")
            }else{
                difficulties.remove("3")
            }
        }
        val level4 : CheckBox = view.findViewById(R.id.cbLevel4)
        if(difficulties.contains("4"))
            level4.isChecked = true
        level4.setOnClickListener{
            if (level4.isChecked){
                difficulties.add("4")
            }else{
                difficulties.remove("4")
            }
        }

        val practiceCompleted : CheckBox = view.findViewById(R.id.cbPrac)
        val testPassed : CheckBox = view.findViewById(R.id.cbTest)
        val unlocked : CheckBox = view.findViewById(R.id.cbUnlock)

        val apply : Button = view.findViewById(R.id.btnApplyFilters)


        apply.setOnClickListener{
            lessonAdapter.sortList(sortOptionAdapter.getSelected())

            sharedPref.edit()
                .putInt(KEY_LESSON_SORTING, sortOptionAdapter.getSelected())
                .apply()

            sharedPref.edit()
                .putStringSet(KEY_LESSON_DIFFICULTY, difficulties)
                .apply()

            lessonAdapter.deleteToDos()

            dialog?.dismiss()

            (parentFragment as (LessonSelectFragment)).createLessonList(difficulties)
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

