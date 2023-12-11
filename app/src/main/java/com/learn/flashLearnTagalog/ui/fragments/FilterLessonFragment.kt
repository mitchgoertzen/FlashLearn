package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.LessonAdapter
import com.learn.flashLearnTagalog.adapters.SortOptionAdapter
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FilterLessonFragment(private var lessonAdapter: LessonAdapter) : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var sortOptionAdapter: SortOptionAdapter

    private var difficulties: MutableSet<String> = mutableSetOf()
    private var selectCategory: String = "All"
    private var selectPracticeCompleted: Boolean = false
    private var selectTestPassed: Boolean = false
    private var selectUnlocked: Boolean = false

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

        //populate difficulties from user's saved selection of lesson difficulties to include
        sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, mutableSetOf())!!.forEach {
            difficulties.add(it)
        }
        selectCategory = sharedPref.getString(KEY_LESSON_CATEGORY, "All")!!
        selectPracticeCompleted = sharedPref.getBoolean(KEY_LESSON_PRACTICE_COMPLETED, false)
        selectTestPassed = sharedPref.getBoolean(KEY_LESSON_TEST_PASSED, false)
        selectUnlocked = sharedPref.getBoolean(KEY_LESSON_UNLOCKED, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window: FrameLayout = view.findViewById(R.id.clHintBackground)

        sortOptionAdapter =
            SortOptionAdapter(mutableListOf(), sharedPref.getInt(KEY_LESSON_SORTING, 1))

        val rvSortOptions: RecyclerView = view.findViewById(R.id.rvSortingOptions)

        rvSortOptions.adapter = sortOptionAdapter
        rvSortOptions.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        sortOptionAdapter.addOption("Category")
        sortOptionAdapter.addOption("Lesson Level")
        sortOptionAdapter.addOption("Difficulty: Low to High")
        sortOptionAdapter.addOption("Difficulty: High to Low")
       // sortOptionAdapter.addOption("Unlocked")

        //close dialog when touch is detected outside of its window
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val popup: LinearLayout = view.findViewById(R.id.llFilterPopup)

        //block closing of dialog when its own window is touched
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
        //when an item is selected from the category spinner, that category will be used upon applying filter settings
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectCategory = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val difficulty1: CheckBox = view.findViewById(R.id.cbLevel1)
        setDifficultyCheckBox("1", difficulty1)

        val difficulty2: CheckBox = view.findViewById(R.id.cbLevel2)
        setDifficultyCheckBox("2", difficulty2)

        val difficulty3: CheckBox = view.findViewById(R.id.cbLevel3)
        setDifficultyCheckBox("3", difficulty3)

        val difficulty4: CheckBox = view.findViewById(R.id.cbLevel4)
        setDifficultyCheckBox("4", difficulty4)

        val difficulty5: CheckBox = view.findViewById(R.id.cbLevel5)
        setDifficultyCheckBox("5", difficulty5)

        val difficulty6: CheckBox = view.findViewById(R.id.cbLevel6)
        setDifficultyCheckBox("6", difficulty6)

        val practiceCompleted: CheckBox = view.findViewById(R.id.cbPrac)
        practiceCompleted.isChecked = selectPracticeCompleted
        practiceCompleted.setOnClickListener {
            selectPracticeCompleted = practiceCompleted.isChecked
        }

        val testPassed: CheckBox = view.findViewById(R.id.cbTest)
        testPassed.isChecked = selectTestPassed
        testPassed.setOnClickListener {
            selectTestPassed = testPassed.isChecked
        }

        val unlocked: CheckBox = view.findViewById(R.id.cbUnlock)
        unlocked.isChecked = selectUnlocked
        unlocked.setOnClickListener {
            selectUnlocked = unlocked.isChecked
        }

        val apply: Button = view.findViewById(R.id.btnApplyFilters)

        //apply settings for lesson sort and filtering
        apply.setOnClickListener {
           // lessonAdapter.sortList(sortOptionAdapter.getSelected())

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



            lessonAdapter.deleteLessons()

            dialog?.dismiss()

            val scope = CoroutineScope(Job() + Dispatchers.Main)

            scope.launch {
                //TODO: flicker on filter comes from having to delete words then recreate the list
                //find way to replace words in list rather than delete and refill
                (parentFragment as (LessonSelectFragment)).createLessonList(difficulties)
            }
        }
        return view
    }

    private fun setDifficultyCheckBox(difficulty : String, checkBox : CheckBox){
        //if this checkBoxes level is included in difficulties
        //set box to checked
        if (difficulties.contains(difficulty))
            checkBox.isChecked = true
        checkBox.setOnClickListener {
            //when this box is checked/unchecked,
            //add or remove its corresponding difficulty to difficulties
            if (checkBox.isChecked) {
                difficulties.add(difficulty)
            } else {
                difficulties.remove(difficulty)
            }
        }
    }
}

