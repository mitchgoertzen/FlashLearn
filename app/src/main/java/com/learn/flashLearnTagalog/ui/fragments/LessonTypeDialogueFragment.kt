package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LessonTypeDialogueFragment : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private val viewModel: LessonViewModel by activityViewModels()

    //private val viewModel: MainViewModel by viewModels()
    // private lateinit var wordList: MutableList<Word>
    //private lateinit var currentLesson: Lesson

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


//        //popup window
//        val window: ConstraintLayout = view.findViewById(R.id.clMain)
//
//        //when popup is touched, but no buttons are, popup will close
//        window.setOnTouchListener { v, event ->
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    dialog?.dismiss()
//                }
//            }
//            v?.onTouchEvent(event) ?: true
//        }


        val scope = CoroutineScope(Job() + Dispatchers.Main)
        //popup window
        // val window: ConstraintLayout = view.findViewById(R.id.clMain)


        val testButton: Button = view.findViewById(R.id.btnTest)
        val practiceButton: Button = view.findViewById(R.id.btnPractice)
        val engFirst: SwitchCompat = view.findViewById(R.id.scEngFirst)


        disableButton(testButton)
        disableButton(practiceButton)

        //when popup is touched, but no buttons are, popup will close
//        window.setOnTouchListener { v, event ->
//            when (event?.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    scope.cancel()
//                    dialog?.dismiss()
//                }
//            }
//            v?.onTouchEvent(event) ?: true
//        }

        scope.launch {
            var wordList: List<Word>
            var currentLesson = Lesson()

            async {
                viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
                    currentLesson = lesson

                }
            }.await()

            if (currentLesson.wordCount > 0) {
                val id = currentLesson.id
                if (TempListUtility.viewedLessons.contains(id)) {
                    wordList = TempListUtility.viewedWords[id]!!
                } else {
                    wordList = DataUtility.getAllWordsForLesson(
                        currentLesson.category.lowercase(),
                        currentLesson.minLength,
                        currentLesson.wordCount.toLong()
                    ).toMutableList()
                    // Log.d(TAG, "reads used: ${wordList.size}")
                    TempListUtility.viewedWords[id] = wordList
                    TempListUtility.viewedLessons.add(id)
                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "viewedLessons.json",
                        TempListUtility.viewedLessons,
                        false
                    )
                    JsonUtility.writeJSON(
                        requireActivity(),
                        //TODO: save as shared pref
                        "viewedWords.json",
                        TempListUtility.viewedWords,
                        false
                    )
                }

                viewModel.updateWordList(wordList)

                enableButton(practiceButton)
                if (TempListUtility.practicedLessons.contains(id)) {
                    enableButton(testButton)
                }
            }
            scope.cancel()
        }

        testButton.setOnClickListener {
            //wordList.asSequence().shuffled().toMutableList(), currentLesson
            //TODO: make fragment transition universal fun?
            val fragment = TestFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("test")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        practiceButton.setOnClickListener {
            //wordList.asSequence().shuffled().toMutableList(), currentLesson


            val fragment = PracticeFragment()
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