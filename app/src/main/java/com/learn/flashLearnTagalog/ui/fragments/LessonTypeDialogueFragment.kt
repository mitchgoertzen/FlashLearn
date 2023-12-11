package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LessonTypeDialogueFragment(
    private var currentLesson: Lesson
) : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    //private val viewModel: MainViewModel by viewModels()
    private var wordList: MutableList<Word> = mutableListOf()

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
        val view = inflater.inflate(R.layout.fragment_lesson_type_dialogue, container, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var currentTitle = currentLesson.category

        val scope = CoroutineScope(Job() + Dispatchers.Main)
        //popup window
        val window: ConstraintLayout = view.findViewById(R.id.clMain)


        val testButton: Button = view.findViewById(R.id.btnTest)
        val practiceButton: Button = view.findViewById(R.id.btnPractice)
        val engFirst: SwitchCompat = view.findViewById(R.id.scEngFirst)


        disableButton(testButton)
        disableButton(practiceButton)

        //when popup is touched, but no buttons are, popup will close
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    scope.cancel()
                    dialog?.dismiss()
            }}
            v?.onTouchEvent(event) ?: true
        }

        scope.launch {

            if (currentLesson.wordCount > 0) {
                val id = currentLesson.id
                if (TempListUtility.viewedLessons.contains(id)) {
                    wordList = TempListUtility.practicedWords[id]!!
                } else {
                    wordList = DataUtility.getAllWordsForLesson(
                        currentTitle.lowercase(),
                        currentLesson.minLength,
                        currentLesson.wordCount.toLong()
                    ).toMutableList()
                   // Log.d(TAG, "reads used: ${wordList.size}")
                    TempListUtility.practicedWords[id] = wordList
                    TempListUtility.viewedLessons.add(id)
                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "viewedLessons.json",
                        TempListUtility.viewedLessons
                    )
                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "savedWords.json",
                        TempListUtility.practicedWords
                    )
                }

                enableButton(practiceButton)
                if (TempListUtility.practicedLessons.contains(id)) {
                    enableButton(testButton)
                }
            }
            scope.cancel()
        }

        testButton.setOnClickListener {
            //TODO: make fragment transition universal fun?
            val fragment =
                TestFragment(wordList.asSequence().shuffled().toMutableList(), currentLesson)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("test")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        practiceButton.setOnClickListener {
            val fragment =
                PracticeFragment(wordList.asSequence().shuffled().toMutableList(), currentLesson)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("practice")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        //selection will be saved for future use
        var showEngFirst = sharedPref.getBoolean(Constants.KEY_ENG_FIRST, true)
        engFirst.isChecked = showEngFirst
        println("eng: $showEngFirst")
        engFirst.setOnClickListener {
            showEngFirst = !showEngFirst
            sharedPref.edit()
                .putBoolean(Constants.KEY_ENG_FIRST, showEngFirst)
                .apply()
        }

        return view
    }

    private fun disableButton(btn: Button) {
        btn.isEnabled = false
        btn.alpha = .5f
    }

    private fun enableButton(btn: Button) {
        btn.isEnabled = true
        btn.alpha = 1f
    }
}