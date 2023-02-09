package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.ToDoAdapter
import com.learn.flashLearnTagalog.data.ToDo
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestFragment(masterList: MutableList<Word>, private var currentLesson: Lesson) :
    Fragment(R.layout.fragment_test) {

    private lateinit var toDoAdapter: ToDoAdapter
    private lateinit var answeredAdapter: ToDoAdapter

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    var correctAnswer: Boolean = false
    private val correctMessage = "CORRECT!"
    private val incorrectMessage = "INCORRECT"
    private val hintMessage = "ENTER TRANSLATION"
    private var answered: Boolean = false
    private var skipped: Boolean = false
    private var engFirst: Boolean = false
    private var masterWordList = masterList
    private var currentWordList: MutableList<Word> = mutableListOf()
    private var wordsCorrect: Int = 0
    private lateinit var currentWord: Word
    private var i = 1
    private lateinit var textLine: String

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_TEST, true)
            .apply()

        toDoAdapter = ToDoAdapter(mutableListOf())

        answeredAdapter = ToDoAdapter(mutableListOf())

        val view = inflater.inflate(R.layout.fragment_test, container, false)
        val rvTodoList: RecyclerView = view.findViewById(R.id.rvTodoList)
        val tvCurrentWord: TextView = view.findViewById(R.id.tvCurrentWord)
        val etTodoTitle: EditText = view.findViewById(R.id.etTodoTitle)
        val btnEnter: Button = view.findViewById(R.id.btnEnter)
        val btnSkip: Button = view.findViewById(R.id.btnSkip)
        i = 1
        rvTodoList.adapter = toDoAdapter
        rvTodoList.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        currentWordList = masterWordList.toMutableList()
        currentWord = currentWordList.random()

        engFirst = sharedPref.getBoolean(Constants.KEY_ENG_FIRST, true)

        tvCurrentWord.text = getCurrentWord(engFirst)
        tvCurrentWord.text = if (engFirst)
            currentWord.english
        else
            currentWord.tagalog

        etTodoTitle.isEnabled = true

        val index: TextView = view.findViewById(R.id.tvIndex)
        index.text = (i++).toString() + "/" + masterWordList.size.toString()

        etTodoTitle.doOnTextChanged { _, _, _, _ ->
            val toDoTitle = etTodoTitle.text.toString().replace(" ".toRegex(), "").uppercase()
            btnEnter.isEnabled = toDoTitle.isNotBlank()
        }

        val wordType: TextView = view.findViewById(R.id.tvType)

        setWordType(wordType)

        btnEnter.isEnabled = false


        //TODO:MAKE NEXT WORD FUNCTION
        btnEnter.setOnClickListener {

            if (skipped) {
                answered = false
                skipped = false
                //go to results
                if (currentWordList.isEmpty()) {
                    goToResults()
                } else
                //go to next word
                {
                    index.text = (i++).toString() + "/" + masterWordList.size.toString()
                    toDoAdapter.deleteToDos()
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
                val toDoTitle = etTodoTitle.text.toString().replace(" ".toRegex(), "").uppercase()
                if (toDoTitle.isNotBlank()) {
                    val toDo = ToDo(toDoTitle)
                    val answer = getCurrentWord(!engFirst).uppercase().replace(" ".toRegex(), "")
                    correctAnswer = (toDoTitle == answer)
                    toDo.isCorrect = correctAnswer
                    toDoAdapter.addToDo(toDo, engFirst, false)
                    etTodoTitle.text.clear()
                    btnEnter.isActivated = false
                    rvTodoList.scrollToPosition(toDoAdapter.getToDoSize() - 1)
                }

                if (correctAnswer) {
                    if (answered) {
                        if (currentWordList.isEmpty()) {
                            goToResults()
                        } else {
                            answered = false
                            toDoAdapter.deleteToDos()
                            btnEnter.text = "Enter"
                            btnEnter.isEnabled = false
                            btnSkip.isEnabled = true
                            btnSkip.alpha = 1f
                            etTodoTitle.isEnabled = true
                            etTodoTitle.hint = hintMessage
                            correctAnswer = false
                            currentWord = currentWordList.random()
                            tvCurrentWord.text = currentWord.english
                            index.text = (i++).toString() + "/" + masterWordList.size.toString()
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
                currentWord.id?.let { it -> viewModel.skipWord(it) }
                answerWord(false)
                skipped = true
                btnSkip.alpha = .2f
                currentWordList.remove(currentWord)
                val toDo = ToDo("Answer: ${getCurrentWord(!engFirst)}")
                toDo.noAnswer = true
                toDoAdapter.addToDo(toDo, engFirst, false)
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

        etTodoTitle.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                btnEnter.performClick()
            }
            true
        }

        return view
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

        if (wordsCorrect.toFloat() / answeredAdapter.getToDoSize().toFloat() >= 0.5f) {
            viewModel.unlockNextLesson(currentLesson.title, currentLesson.level)
            viewModel.passTest(currentLesson.id)
        }

        sharedPref.edit()
            .putBoolean(Constants.KEY_IN_TEST, false)
            .apply()
        val fragment = TestResultsFragment(wordsCorrect, answeredAdapter)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("results")?.commit()
        (activity as LearningActivity?)?.transitionFragment()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun answerWord(result: Boolean) {
        currentWord.previousResult = result
        currentWord.id?.let {
            viewModel.answerWord(it, result)
            textLine = getCurrentWord(engFirst) + " : " + getCurrentWord(!engFirst)
            val toDo = ToDo(textLine, result)
            toDo.isCorrect = result
            answeredAdapter.addToDo(toDo, engFirst, true)
        }
    }
}
