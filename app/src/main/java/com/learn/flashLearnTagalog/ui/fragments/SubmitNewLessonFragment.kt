package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Word
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class SubmitNewLessonFragment : Fragment() {

    private val wordList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_new_lesson, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val name: EditText = view.findViewById(R.id.etLessonName_Org)
        val level: EditText = view.findViewById(R.id.etLessonLevel_Org)
        val language: EditText = view.findViewById(R.id.etLessonLanguage_Org)

        val submit: Button = view.findViewById(R.id.btnSubmitNewLesson)

        submit.setOnClickListener {

            val file: Int = R.raw.tagalog_animals_1
            val input: InputStream = resources.openRawResource(file)
            val reader = BufferedReader(InputStreamReader(input))

            var state = 0
            var currentLine: String? = ""
            var type = ""
            var tra = ""
            var eng = ""
            var con = ""


            while (true) {
                state = 0
                type = ""
                tra = ""
                eng = ""
                con = ""

                try {
                    //if the currentString is empty, exit while()
                    if (reader.readLine().also { currentLine = it } == null) break


                    for (i in currentLine!!.indices) {
                        //state represents the data of each word that is currently being parsed
                        //once sentinel value is reached($ or #), save currentString to current variable and increment state
                        when (state) {
                            //0 = type
                            0 -> {
                                if (currentLine!![i] == ',') {
                                    state = 1
                                } else {
                                    type += currentLine!![i]
                                }
                            }
                            //1 = translation
                            1 -> {
                                if (currentLine!![i] == ',') {
                                    state = 2
                                } else {
                                    tra += currentLine!![i]
                                }
                            }
                            //2 = english
                            2 -> {
                                if (currentLine!![i] == ',') {
                                    state = 3
                                } else {
                                    eng += currentLine!![i]
                                }
                            }
                            //3 = category
                            3 -> {
                                con += currentLine!![i]
                            }
                        }
                    }


                } catch (e: IOException) {
                    e.printStackTrace()
                }

                type = when (type.lowercase()) {
                    "noun" -> "n"
                    "verb" -> "v"
                    "adjective" -> "adj"
                    "composite" -> "comp"
                    "pronoun" -> "pron"
                    else -> type
                }

                val word = Word(
                    eng.lowercase(),
                    listOf(tra.lowercase()),
                    type,
                    context = con.lowercase()
                )
                wordList.add(word.id)

//                if(word id exists do nothing else add word)

                Log.d(TAG, "new word: $word")

            }
            input.close()

            Log.d(TAG, "Lesson: ${name.text}_${level.text} created in ${language.text}")
            Log.d(TAG, "words: $wordList")
        }


    }

}