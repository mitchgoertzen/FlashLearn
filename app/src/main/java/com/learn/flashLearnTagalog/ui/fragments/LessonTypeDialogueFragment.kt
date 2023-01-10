package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LessonTypeDialogueFragment(private var currentLesson: Lesson) : DialogFragment() {

    @Inject
    lateinit var sharedPref : SharedPreferences
    private val viewModel: MainViewModel by viewModels()
    private var wordList : MutableList<Word> = mutableListOf()
    private var practiceCompleted : Boolean = false

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

        var currentTitle = currentLesson.title

        //popup window
        val window : ConstraintLayout = view.findViewById(R.id.clMain)
        //when popup is touched, but no buttons are, popup will close
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        viewModel.getWordsByDifficultyForLesson(currentTitle.lowercase(), currentLesson.minLength, currentLesson.maxLength).observe(viewLifecycleOwner) {
            wordList = it.toMutableList()
        }

        val testButton : Button = view.findViewById(R.id.btnTest)
        val practiceButton : Button = view.findViewById(R.id.btnPractice)
        val engFirst : SwitchCompat = view.findViewById(R.id.scEngFirst)

        testButton.setOnClickListener{
            val fragment = TestFragment(wordList.asSequence().shuffled().toMutableList(), currentLesson.title)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("test")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }
        println(currentTitle)
        practiceCompleted = viewModel.getPracticeCompleted(currentTitle)

        if(!practiceCompleted){
            testButton.isEnabled = false
            testButton.alpha = .5f
        }

        practiceButton.setOnClickListener{
            val fragment = PracticeFragment(wordList.asSequence().shuffled().toMutableList(), currentLesson.title)
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("practice")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        //selection will be saved for future use
        var showEngFirst = sharedPref.getBoolean(Constants.KEY_ENG_FIRST, true)
        engFirst.isChecked = showEngFirst
        engFirst.setOnClickListener {
            showEngFirst = !showEngFirst
            sharedPref.edit()
                .putBoolean(Constants.KEY_ENG_FIRST, showEngFirst)
                .apply()
        }

        return view
    }
}
