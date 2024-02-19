package com.learn.flashLearnTagalog.ui.dialog_fragments

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
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.SortOptionAdapter
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.fragments.LessonSelectFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FilterLessonDialogFragment : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var sortOptionAdapter: SortOptionAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private var selectPracticeCompleted: Boolean = false
    private var selectTestPassed: Boolean = false
    private var selectUnlocked: Boolean = false
    private var difficulties: MutableSet<String> = mutableSetOf()
    private var selectCategory: String = "All"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val languages = resources.getStringArray(R.array.Sorting)

        sortOptionAdapter =
            SortOptionAdapter(mutableListOf(), sharedPref.getInt(KEY_LESSON_SORTING, 1))

        spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, languages
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.fragment_filter_lesson, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apply: Button = view.findViewById(R.id.btnApplyFilters)
        val difficulty1: CheckBox = view.findViewById(R.id.cbLevel1)
        val difficulty2: CheckBox = view.findViewById(R.id.cbLevel2)
        val difficulty3: CheckBox = view.findViewById(R.id.cbLevel3)
        val difficulty4: CheckBox = view.findViewById(R.id.cbLevel4)
        val difficulty5: CheckBox = view.findViewById(R.id.cbLevel5)
        val difficulty6: CheckBox = view.findViewById(R.id.cbLevel6)
        val practiceCompleted: CheckBox = view.findViewById(R.id.cbPrac)
        val testPassed: CheckBox = view.findViewById(R.id.cbTest)
        val unlocked: CheckBox = view.findViewById(R.id.cbUnlock)
        val close: ImageButton = view.findViewById(R.id.ibClose)
        val rvSortOptions: RecyclerView = view.findViewById(R.id.rvSortingOptions)
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val spinner: Spinner = view.findViewById(R.id.spinner)


        //populate difficulties from user's saved selection of lesson difficulties to include
        sharedPref.getStringSet(Constants.KEY_LESSON_DIFFICULTY, mutableSetOf())!!.forEach {
            difficulties.add(it)
        }
        selectCategory = sharedPref.getString(Constants.KEY_LESSON_CATEGORY, "All")!!
        selectPracticeCompleted =
            sharedPref.getBoolean(Constants.KEY_LESSON_PRACTICE_COMPLETED, false)
        selectTestPassed = sharedPref.getBoolean(Constants.KEY_LESSON_TEST_PASSED, false)
        selectUnlocked = sharedPref.getBoolean(Constants.KEY_LESSON_UNLOCKED, false)
        rvSortOptions.adapter = sortOptionAdapter
        rvSortOptions.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        sortOptionAdapter.addOption("Category")
        sortOptionAdapter.addOption("Lesson Level")
        sortOptionAdapter.addOption("Difficulty: Low to High")
        sortOptionAdapter.addOption("Difficulty: High to Low")
        // sortOptionAdapter.addOption("Unlocked")

        close.setOnClickListener {
            dialog?.dismiss()
        }

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

        setDifficultyCheckBox("1", difficulty1)
        setDifficultyCheckBox("2", difficulty2)
        setDifficultyCheckBox("3", difficulty3)
        setDifficultyCheckBox("4", difficulty4)
        setDifficultyCheckBox("5", difficulty5)
        setDifficultyCheckBox("6", difficulty6)

        practiceCompleted.isChecked = selectPracticeCompleted
        practiceCompleted.setOnClickListener {
            selectPracticeCompleted = practiceCompleted.isChecked
        }

        testPassed.isChecked = selectTestPassed
        testPassed.setOnClickListener {
            selectTestPassed = testPassed.isChecked
        }

        unlocked.isChecked = selectUnlocked
        unlocked.setOnClickListener {
            selectUnlocked = unlocked.isChecked
        }

        //apply settings for lesson sort and filtering
        apply.setOnClickListener {

            sharedPref.edit()
                .putInt(KEY_LESSON_SORTING, sortOptionAdapter.getSelected())
                .apply()

            sharedPref.edit()
                .putStringSet(Constants.KEY_LESSON_DIFFICULTY, difficulties)
                .apply()

            sharedPref.edit()
                .putString(Constants.KEY_LESSON_CATEGORY, selectCategory)
                .apply()

            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_PRACTICE_COMPLETED, selectPracticeCompleted)
                .apply()

            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_TEST_PASSED, selectTestPassed)
                .apply()

            sharedPref.edit()
                .putBoolean(Constants.KEY_LESSON_UNLOCKED, selectUnlocked)
                .apply()

            dialog?.dismiss()

            scope.launch {
                //TODO: flicker on filter comes from having to delete words then recreate the list
                //find way to replace words in list rather than delete and refill
                (parentFragment as (LessonSelectFragment)).createLessonList(difficulties)
            }
        }
    }

    private fun setDifficultyCheckBox(difficulty: String, checkBox: CheckBox) {
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

