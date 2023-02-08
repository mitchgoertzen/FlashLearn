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
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FilterLessonFragment(private var lessonAdapter: LessonAdapter) : DialogFragment() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private lateinit var sortOptionAdapter: SortOptionAdapter

    private var difficulties: MutableSet<String> = mutableSetOf()
    private var selectCategory : String = "All"
    private var selectPracticeCompleted : Boolean = false
    private var selectTestPassed : Boolean = false
    private var selectUnlocked : Boolean = false

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

        sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, mutableSetOf())!!.forEach {
            difficulties.add(it)
        }
        selectCategory = sharedPref.getString(KEY_LESSON_CATEGORY, "All")!!
        selectPracticeCompleted  = sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false)
        selectTestPassed  = sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false)
        selectUnlocked  = sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false)

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
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(spinnerAdapter.getPosition(selectCategory))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectCategory = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //TODO: change to function,  don't repeat 4 times
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

        val level5 : CheckBox = view.findViewById(R.id.cbLevel5)
        if(difficulties.contains("5"))
            level5.isChecked = true
        level5.setOnClickListener{
            if (level5.isChecked){
                difficulties.add("5")
            }else{
                difficulties.remove("5")
            }
        }

        val level6 : CheckBox = view.findViewById(R.id.cbLevel6)
        if(difficulties.contains("6"))
            level6.isChecked = true
        level6.setOnClickListener{
            if (level6.isChecked){
                difficulties.add("6")
            }else{
                difficulties.remove("6")
            }
        }
        val practiceCompleted : CheckBox = view.findViewById(R.id.cbPrac)
        practiceCompleted.isChecked = selectPracticeCompleted
        practiceCompleted.setOnClickListener{
            selectPracticeCompleted = practiceCompleted.isChecked
        }

        val testPassed : CheckBox = view.findViewById(R.id.cbTest)
        testPassed.isChecked = selectTestPassed
        testPassed.setOnClickListener{
            selectTestPassed = testPassed.isChecked
        }

        val unlocked : CheckBox = view.findViewById(R.id.cbUnlock)
        unlocked.isChecked = selectUnlocked
        unlocked.setOnClickListener{
            selectUnlocked = unlocked.isChecked
        }

        val apply : Button = view.findViewById(R.id.btnApplyFilters)


        apply.setOnClickListener{
            lessonAdapter.sortList(sortOptionAdapter.getSelected())

            sharedPref.edit()
                .putInt(KEY_LESSON_SORTING, sortOptionAdapter.getSelected())
                .apply()

            sharedPref.edit()
                .putStringSet(KEY_LESSON_DIFFICULTY, difficulties)
                .apply()

            sharedPref.edit()
                .putString(KEY_LESSON_CATEGORY, selectCategory)
                .apply()

            sharedPref.edit()
                .putBoolean(KEY_LESSON_PRACTICE_COMPLETED, selectPracticeCompleted)
                .apply()

            sharedPref.edit()
                .putBoolean(KEY_LESSON_TEST_PASSED, selectTestPassed)
                .apply()

            sharedPref.edit()
                .putBoolean(KEY_LESSON_UNLOCKED, selectUnlocked)
                .apply()

            lessonAdapter.deleteToDos()

            dialog?.dismiss()

            (parentFragment as (LessonSelectFragment)).createLessonList(difficulties)
        }



        return view
    }


}

