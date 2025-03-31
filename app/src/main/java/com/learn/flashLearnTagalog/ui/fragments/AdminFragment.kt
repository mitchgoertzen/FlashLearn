package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.DataProcessor
import com.learn.flashLearnTagalog.LessonCreator
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {
    //TODO: edittext for this
    private val language = "tagalog"

    private lateinit var type: EditText
    private lateinit var english: EditText
    private lateinit var translations: EditText
    private lateinit var index: EditText
    private lateinit var context: EditText
    private lateinit var category: EditText


    private lateinit var typ: String
    private lateinit var eng: String
    private lateinit var con: String
    private lateinit var cat: String
    private var ind = -1
    private var allTra = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataProcessor = DataProcessor(resources, language)
        val lessonCreator = LessonCreator()


        val increaseVersion: Button = view.findViewById(R.id.btnIncreaseAppVersion_Admin)


        val words: Button = view.findViewById(R.id.btnAddWords)
        val lessons: Button = view.findViewById(R.id.btnAddLessons)
        val wordsUpdate: Button = view.findViewById(R.id.btnUpdateWords)
        val start: EditText = view.findViewById(R.id.etStartIndex)
        val end: EditText = view.findViewById(R.id.etEndIndex)
        val log: TextView = view.findViewById(R.id.tvLog)
        val updateLessons: CheckBox = view.findViewById(R.id.cbUpdateLessons)


        val addWord: Button = view.findViewById(R.id.btnAddWord_Admin)
        val deleteWord: Button = view.findViewById(R.id.btnDeleteWord_Admin)
        val updateWord: Button = view.findViewById(R.id.btnUpdateWord_Admin)


        type = view.findViewById(R.id.etWordType_Admin)
        english = view.findViewById(R.id.etWordEnglish_Admin)
        translations = view.findViewById(R.id.etWordTranslations_Admin)
        index = view.findViewById(R.id.etWordIndex_Admin)
        context = view.findViewById(R.id.etWordContext_Admin)
        category = view.findViewById(R.id.etWordCategory_Admin)


        increaseVersion.setOnClickListener {
            DataUtility.incrementAppVersion()
        }


        words.setOnClickListener {
            dataProcessor.makeList(
                0,
                R.raw.tag_to_eng_dict,
                ',',
                start.text.toString().toInt(),
                end.text.toString().toInt(),
                log
            )
        }

        wordsUpdate.setOnClickListener {
            dataProcessor.makeList(
                1, R.raw.tag_dollar, '$',
                start.text.toString().toInt(),
                end.text.toString().toInt(),
                log
            )
        }

        lessons.setOnClickListener {

            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {
                async { lessonCreator.createLessons(resources, "", "") }.await()

                val lessonList = lessonCreator.getLessons()

                Log.d(ContentValues.TAG, "lessons: $lessonList")

                if (updateLessons.isChecked) {

                    DataUtility.batchUpdateLessons(
                        "flash_learn",
                        "tagalog",
                        lessonList,
                        //0 = categories
                        //1 = image
                        //2 = maxLength
                        //3 = maxLines
                        //4 = minLength
                        //5 = wordCount
                        listOf(5)
                    )
                } else {
                    val lessonMap = mutableMapOf<String, Lesson>()

                    for (l in lessonList) {
                        if (lessonMap[l.id] != null) {
                            Log.d(ContentValues.TAG, "entry ${lessonMap[l.id]}")
                            Log.d(ContentValues.TAG, "id ${l.id}")
                            Log.d(ContentValues.TAG, "new word $l")
                        }
                        lessonMap[l.id] = l
                    }

                    Log.d(ContentValues.TAG, "lesson map: ${lessonMap.size}")


                    DataUtility.insertAllLessons(lessonMap, "flash_learn", "tagalog")
                }

                scope.cancel()
            }


        }

        addWord.setOnClickListener {
            wordFunction(0)
        }

        deleteWord.setOnClickListener {
            wordFunction(1)
        }

        updateWord.setOnClickListener {
            wordFunction(2)
        }
    }

    private fun wordFunction(function: Int) {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            if (type.text.isNotEmpty() && english.text.isNotEmpty()) {
                async { createWord() }.await()

                async {
                    when (function) {
                        0 -> {
                            val word = Word(
                                eng,
                                allTra,
                                typ,
                                cat,
                                con,
                                correctIndex = if (ind > 0) ind else 0
                            )
                            Log.d(TAG, "adding $word")
                            Log.d(TAG, "id ${word.id}")
                          //  DataUtility.insertWord(word, language)
                        }

                        1 -> {
                            val word = Word(
                                eng,
                                allTra,
                                typ,
                                cat,
                                con,
                                correctIndex = if (ind > 0) ind else 0
                            )
                            Log.d(TAG, "deleting $typ,$eng")
                            Log.d(TAG, "id should be ${word.id}")
                            DataUtility.deleteWord("$typ,$eng".hashCode().toString(), language)
                        }

                        2 -> {
                            Log.d(TAG, "updating $typ,$eng")
                            DataUtility.updateWordInfo(
                                "$typ,$eng".hashCode().toString(),
                                language,
                                cat.ifEmpty { null },
                                con.ifEmpty { null },
                                if (ind > 0) ind else null,
                                null,
                                null,
                                allTra.ifEmpty { null },
                            )
                        }

                        else -> Log.d(TAG, "else nothin")
                    }
                }.await()

            }
            scope.cancel()
        }

    }

    private fun createWord() {
        allTra.clear()
        var tempWord = ""
        for (c in translations.text) {
            if (c == ',') {
                allTra.add(tempWord.lowercase())
                tempWord = ""
            } else {
                tempWord += c
            }
        }
        allTra.add(tempWord.lowercase())

        typ = type.text.toString().lowercase()
        eng = english.text.toString().lowercase()
        con = context.text.toString().lowercase()
        cat = category.text.toString().lowercase()
        ind = index.text.toString().ifEmpty { "-1" }.toInt()

        Log.d(TAG, "type: $typ")
        Log.d(TAG, "english: $eng")
        Log.d(TAG, "translations: $allTra")
        Log.d(TAG, "correct index: $ind")
        Log.d(TAG, "context: $con")
        Log.d(TAG, "category: $cat")

    }

}