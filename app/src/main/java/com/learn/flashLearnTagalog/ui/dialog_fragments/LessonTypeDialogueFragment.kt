package com.learn.flashLearnTagalog.ui.dialog_fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.Util
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.fragments.PracticeFragment
import com.learn.flashLearnTagalog.ui.fragments.TestFragment
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
    private lateinit var mActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }

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

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.dialog_fragment_lessons_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val close: ImageButton = view.findViewById(R.id.ibClose)
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val testButton: Button = view.findViewById(R.id.btnTest)
        val practiceButton: Button = view.findViewById(R.id.btnPractice)
        val engFirst: SwitchCompat = view.findViewById(R.id.scEngFirst)
        val networkErrorText: TextView = view.findViewById(R.id.tvNetworkError)
        var showEngFirst = sharedPref.getBoolean(Constants.KEY_ENG_FIRST, true)


        close.setOnClickListener {
            dialog?.dismiss()
        }

        networkErrorText.visibility = View.GONE

        Util.handleButtonEnable(testButton, false)
        Util.handleButtonEnable(practiceButton, false)

        scope.launch {
            val wordList: List<Word>
            var currentLesson = Lesson()

            async {
                viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
                    currentLesson = lesson

                }
            }.await()

            if (currentLesson.wordCount > 0) {
                val id = currentLesson.id
                if (TempListUtility.viewedLessons.contains(id) && TempListUtility.viewedWords.contains(
                        id
                    )
                ) {


                    FirebaseCrashlytics.getInstance().log("id: $id")
                    FirebaseCrashlytics.getInstance().log("temp viewed lessons & words contains id")
                    //nullpointer bug here, how can it happen?
                    //temp fix with null check on 104
                    wordList = TempListUtility.viewedWords[id]!!
                    FirebaseCrashlytics.getInstance().log("${wordList.size}")
                    enableButton(practiceButton, wordList)
                } else {
                    wordList = DataUtility.getAllWordsForLesson(
                        //TODO: replace with sharedpref,
                        "tagalog",
                        currentLesson.category.lowercase(),
                        currentLesson.minLength,
                        currentLesson.maxLength,
                        currentLesson.wordCount.toLong()
                    ).toMutableList()

                    Log.d(ContentValues.TAG, "SIZE: ${wordList.size}")
                    if (wordList.isNotEmpty()) {
                        TempListUtility.viewedWords[id] = wordList
                        TempListUtility.viewedLessons.add(id)
                        JsonUtility.writeJSON(
                            mActivity,
                            //TODO: save as shared pref
                            "viewedLessons.json",
                            TempListUtility.viewedLessons,
                            false
                        )
                        JsonUtility.writeJSON(
                            mActivity,
                            //TODO: save as shared pref
                            "viewedWords.json",
                            TempListUtility.viewedWords,
                            false
                        )
                        enableButton(practiceButton, wordList)
                    } else {
                        networkErrorText.visibility = View.VISIBLE
                        //no internet connection text visible
                    }
                }


                if (TempListUtility.practicedLessons.contains(id)) {
                    enableButton(testButton, wordList)
                }
            }
            scope.cancel()
        }

        testButton.setOnClickListener {
            //wordList.asSequence().shuffled().toMutableList(), currentLesson
            //TODO: make fragment transition universal fun?
            val fragment = TestFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("test")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        practiceButton.setOnClickListener {
            //wordList.asSequence().shuffled().toMutableList(), currentLesson

            val fragment = PracticeFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("practice")?.commit()
            (activity as LearningActivity?)?.transitionFragment()
            dialog?.dismiss()
        }

        //selection will be saved for future use
        engFirst.isChecked = showEngFirst
        println("eng: $showEngFirst")
        engFirst.setOnClickListener {
            showEngFirst = !showEngFirst
            sharedPref.edit()
                .putBoolean(Constants.KEY_ENG_FIRST, showEngFirst)
                .apply()
        }
    }



    private fun enableButton(btn: Button, wordList: List<Word>) {
        if (wordList.isNotEmpty()) {
            viewModel.updateWordList(wordList)
            Util.handleButtonEnable(btn, true)
        }
    }
}