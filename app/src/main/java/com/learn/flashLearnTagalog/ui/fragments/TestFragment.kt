package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.TestWordAdapter
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.TestWord
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
import javax.inject.Inject

@AndroidEntryPoint
class TestFragment : Fragment(R.layout.fragment_test) {

    private val viewModel: LessonViewModel by activityViewModels()
    private val correctMessage = "CORRECT!"
    private val incorrectMessage = "INCORRECT"
    private val hintMessage = "ENTER TRANSLATION"
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var testWordAdapter: TestWordAdapter
    private lateinit var answeredAdapter: TestWordAdapter

    private var correctAnswer: Boolean = false
    private var answered: Boolean = false
    private var skipped: Boolean = false
    private var engFirst: Boolean = false
    private var wordsCorrect: Int = 0
    private var i = 1
    private var currentWord = Word()
    private var textLine = ""
    private var listSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testWordAdapter = TestWordAdapter(mutableListOf())
        answeredAdapter = TestWordAdapter(mutableListOf())

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_TEST, true)
            .apply()



    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listSize = viewModel.listSize

        val rvTodoList: RecyclerView = view.findViewById(R.id.rvTodoList)
        val tvCurrentWord: TextView = view.findViewById(R.id.tvCurrentWord)
        val etTodoTitle: EditText = view.findViewById(R.id.etTodoTitle)
        val btnEnter: Button = view.findViewById(R.id.btnEnter)
        val btnSkip: Button = view.findViewById(R.id.btnSkip)
        i = 1
        rvTodoList.adapter = testWordAdapter
        rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        viewModel.currentWordList.observe(viewLifecycleOwner) { list ->
            val currentWordList = list.asSequence().shuffled().toMutableList()
            currentWord = currentWordList.random()

            engFirst = sharedPref.getBoolean(Constants.KEY_ENG_FIRST, true)

            tvCurrentWord.text = getCurrentWord(engFirst)


            etTodoTitle.isEnabled = true

            val index: TextView = view.findViewById(R.id.tvIndex)

            //TODO: list size in adapter
            index.text = "${i++}/$listSize"

            etTodoTitle.doOnTextChanged { _, _, _, _ ->
                val toDoTitle = etTodoTitle.text.toString().replace(" ".toRegex(), "").uppercase()
                btnEnter.isEnabled = toDoTitle.isNotBlank()
            }

            val wordType: TextView = view.findViewById(R.id.tvType)

            setWordType(wordType)

           // btnEnter.isEnabled = false

            //TODO:MAKE NEXT WORD FUNCTION
            btnEnter.setOnClickListener {

                goToResults()

                if (skipped) {
                    answered = false
                    skipped = false
                    //go to results
                    if (currentWordList.isEmpty()) {
                        goToResults()
                    } else
                    //go to next word
                    {
                        index.text = "${i++}/$listSize"
                        testWordAdapter.deleteTestWords()
                        btnEnter.text = "Enter"
                        btnEnter.isEnabled = false
                        btnSkip.isEnabled = true
                        btnSkip.alpha = 1f
                        etTodoTitle.isEnabled = true
                        etTodoTitle.hint = hintMessage
                        correctAnswer = false
                        currentWord = currentWordList.random()
                        tvCurrentWord.text = getCurrentWord(engFirst)
                        setWordType(wordType)
                    }
                } else {
                    val toDoTitle =
                        etTodoTitle.text.toString().replace(" ".toRegex(), "").uppercase()
                    if (toDoTitle.isNotBlank()) {
                        val toDo = TestWord(toDoTitle)
                        val answer =
                            getCurrentWord(!engFirst).uppercase().replace(" ".toRegex(), "")
                        correctAnswer = (toDoTitle == answer)
                        toDo.isCorrect = correctAnswer
                        testWordAdapter.addTestWord(toDo, engFirst, false)
                        etTodoTitle.text.clear()
                        btnEnter.isActivated = false
                        rvTodoList.scrollToPosition(testWordAdapter.getTestWordsSize() - 1)
                    }

                    if (correctAnswer) {
                        if (answered) {
                            if (currentWordList.isEmpty()) {
                                goToResults()
                            } else {
                                answered = false
                                testWordAdapter.deleteTestWords()
                                btnEnter.text = "Enter"
                                btnEnter.isEnabled = false
                                btnSkip.isEnabled = true
                                btnSkip.alpha = 1f
                                etTodoTitle.isEnabled = true
                                etTodoTitle.hint = hintMessage
                                correctAnswer = false
                                currentWord = currentWordList.random()
                                tvCurrentWord.text = getCurrentWord(engFirst)
                                index.text = "${i++}/$listSize"
                            }
                        } else {
                            answerWord(true)
                            wordsCorrect++
                            answered = true
                            btnEnter.isEnabled = true
                            btnSkip.isEnabled = false
                            btnSkip.alpha = .2f
                            currentWordList.remove(currentWord)
                            if (currentWordList.isEmpty()) {
                                btnEnter.text = "Finish"
                            } else {
                                btnEnter.text = "Next Word"
                            }
                            etTodoTitle.hint = correctMessage
                            etTodoTitle.isEnabled = false
                            view.hideKeyboard()
                        }
                    }
                }
            }

            btnSkip.setOnClickListener {
                etTodoTitle.text.clear()
                btnEnter.isEnabled = false
                if (!skipped) {
                    currentWord.id.let {
                        //TODO - stats: DataUtility.skipWord(it)
                        // viewModel.skipWord(it)
                    }
                    answerWord(false)
                    skipped = true
                    btnSkip.alpha = .2f
                    currentWordList.remove(currentWord)
                    val toDo = TestWord("Answer: ${getCurrentWord(!engFirst)}")
                    toDo.noAnswer = true
                    testWordAdapter.addTestWord(toDo, engFirst, false)
                    etTodoTitle.isEnabled = false
                    etTodoTitle.hint = incorrectMessage

                    //show finish button
                    if (currentWordList.isEmpty()) {
                        btnEnter.text = "Finish"
                    } else
                    //show next word button
                    {
                        btnEnter.text = "Next Word"
                    }

                    btnEnter.isEnabled = true
                    btnSkip.isEnabled = false
                }
            }

//            etTodoTitle.setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
//                    btnEnter.performClick()
//                }
//                true
//            }
        }


    }

    private fun setWordType(wordType: TextView) {
        wordType.text = when (currentWord.type) {
            "n" -> {
                "Noun"
            }

            "comp" -> {
                "Noun"
            }

            "v" -> {
                "Verb"
            }

            "adj" -> {
                "Adjective"
            }

            "adv" -> {
                "adverb"
            }

            "inf" -> {
                "infinitive"
            }

            "intrj" -> {
                "interjection"
            }

            "prep" -> {
                "preposition"
            }

            else -> {
                currentWord.type
            }
        }
    }

    private fun getCurrentWord(eng: Boolean): String {
        return if (eng)
            currentWord.english
        else
            currentWord.tagalog
    }

    private fun goToResults() {
        viewModel.currentLesson.observe(viewLifecycleOwner) { lesson ->
         //   if (wordsCorrect.toFloat() / answeredAdapter.getTestWordsSize().toFloat() >= 0.0f) {
                val id = lesson.id
                if (!TempListUtility.passedLessons.contains(id)) {

                    val nextLevel = lesson.level + 1
                    val nextId = lesson.category + "_" + nextLevel
                    for (l in JsonUtility.getSavedLessons(requireActivity())) {
                        if (l.id == nextId) {

                            if (sharedPref.getBoolean(Constants.KEY_USER_SIGNED_IN, false)) {
                                DataUtility.addUnlockedLesson(nextId)
                            }
                            TempListUtility.unlockedLessons.add(nextId)
                            JsonUtility.writeJSON(
                                requireActivity(),
                                "unlockedLessons.json",
                                TempListUtility.unlockedLessons,
                                true
                            )
                        }
                    }

                    if (sharedPref.getBoolean(Constants.KEY_USER_SIGNED_IN, false)) {
                        DataUtility.addPassedLesson(id)
                    }


                    TempListUtility.passedLessons.add(id)
                    JsonUtility.writeJSON(
                        requireActivity(), "passedLessons.json", TempListUtility.passedLessons,
                        true
                    )
                }

                //DataUtility.passTest(currentLesson.id)
                //viewModel.unlockNextLesson(currentLesson.category, currentLesson.level)
                // viewModel.passTest(currentLesson.id)
         //   }

            val bundle = bundleOf("words_correct" to 20)

            viewModel.updateAdapter(answeredAdapter)

            val fragment = TestResultsFragment()
            fragment.arguments = bundle
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("results")
                ?.commit()
            (activity as LearningActivity?)?.transitionFragment()
        }

    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun answerWord(result: Boolean) {
        //TODO: currentWord.previousResult = result
        currentWord.id.let {
            //TODO: premium feature DataUtility.answerWord(it, result)
            //viewModel.answerWord(it, result)
            textLine = getCurrentWord(engFirst) + " : " + getCurrentWord(!engFirst)
            val toDo = TestWord(textLine, result)
            toDo.isCorrect = result
            answeredAdapter.addTestWord(toDo, engFirst, true)
        }
    }

    override fun onStop() {
        super.onStop()
        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_TEST, false)
            .apply()
    }
}
