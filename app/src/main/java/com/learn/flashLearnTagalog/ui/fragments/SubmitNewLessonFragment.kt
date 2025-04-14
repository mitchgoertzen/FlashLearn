package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.OrgLessonWordAdapter
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.other.Constants.KEY_ORGANIZATION_ID
import com.learn.flashLearnTagalog.ui.LearningActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject


class SubmitNewLessonFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var wordAdapter: OrgLessonWordAdapter

    private val wordList = mutableListOf<String>()
    private var minLength = 100
    private var maxLength = 0
    private var inputStream: InputStream? = null
    private var filename: String = ""


    private lateinit var fileNameText: TextView

    private lateinit var regularColorList: ColorStateList
    private lateinit var errorColorList: ColorStateList
    private lateinit var fileErrorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordAdapter = OrgLessonWordAdapter(mutableListOf())

        sharedPref = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
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

        val chooseFile: Button = view.findViewById(R.id.btnChooseFile)
        val submit: Button = view.findViewById(R.id.btnSubmitNewLesson)
        val language: EditText = view.findViewById(R.id.etLessonLanguage_Org)
        val level: EditText = view.findViewById(R.id.etLessonLevel_Org)
        val name: EditText = view.findViewById(R.id.etLessonName_Org)
        val attributeErrorText: TextView = view.findViewById(R.id.tvAttributeError)
        val wordsTitle: TextView = view.findViewById(R.id.tvLessonWordsTitle)
        val lessonWords: RecyclerView = view.findViewById(R.id.rvOrgLessonWords)
//
//        wordsTitle.visibility = View.GONE

        fileErrorText = view.findViewById(R.id.tvFileError)

        regularColorList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )

        errorColorList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.error
            )
        )


        setTextColorReset(name)
        setTextColorReset(level)
        setTextColorReset(language)
        fileNameText = view.findViewById(R.id.tvFileName)

        chooseFile.setOnClickListener {
            getContent.launch("text/comma-separated-values/*")
            Log.d(TAG, "Lesson: ${name.text}_${level.text} created in ${language.text}")
        }


        lessonWords.adapter = wordAdapter
        lessonWords.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        submit.setOnClickListener {

            if (inputStream != null) {
                fileErrorText.visibility = View.GONE
                Log.d(TAG, "input: $inputStream")
                val reader = BufferedReader(InputStreamReader(inputStream))
                Log.d(TAG, "reader: $reader")

                var errorCode = 0


                if (name.text.isEmpty()) {
                    errorCode += 5
                    ViewCompat.setBackgroundTintList(name, errorColorList)
                }

                if (level.text.isEmpty()) {
                    errorCode += 10
                    ViewCompat.setBackgroundTintList(level, errorColorList)
                }

                if (language.text.isEmpty()) {
                    errorCode += 20
                    ViewCompat.setBackgroundTintList(language, errorColorList)
                }

                if (errorCode > 0) {
                    attributeErrorText.text = showError(errorCode)
                    attributeErrorText.visibility = View.VISIBLE
                } else {
                    attributeErrorText.visibility = View.GONE
                    val newLesson = Lesson(
                        name.text.toString(),
                        level.text.toString().toInt(),
                        minLength,
                        maxLength,
                        wordList.size, 1,
                        resources.getResourceEntryName(R.drawable.org), wordList
                    )
                    Log.d(TAG, "new lesson: $newLesson")

                    val scope = CoroutineScope(Job() + Dispatchers.Main)
                    scope.launch {
                        DataUtility.addOrgLesson(
                            sharedPref.getString(KEY_ORGANIZATION_ID, "")!!,
                            newLesson.id,
                            language.text.toString().lowercase(),
                            newLesson
                        )
                        scope.cancel()
                    }


                }

            } else {
                fileErrorText.text = "file must be chosen"
                fileErrorText.visibility = View.VISIBLE
            }
        }
    }

    private fun setTextColorReset(text: TextView) {
        text.addTextChangedListener {
            if (text.text.isNotEmpty()) {
                ViewCompat.setBackgroundTintList(text, regularColorList)
            }
        }
    }

    private fun showError(code: Int): String {
        var errorString = "Lesson "
        Log.d(TAG, "$code")
        when (code) {
            5 -> errorString += "name"
            10 -> errorString += "level"
            15 -> errorString += "name and level"
            20 -> errorString += "language"
            25 -> errorString += "name and language"
            35 -> errorString += "name, level, and language"
        }
        return "$errorString cannot be blank"

    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "uri: $uri")

            val projection = arrayOf(OpenableColumns.DISPLAY_NAME)

            try {
                val returnCursor: Cursor =
                    requireActivity().contentResolver.query(uri!!, projection, null, null, null)!!
                returnCursor.moveToFirst()
                filename = returnCursor.getString(0)
                returnCursor.close()
                if (filename.isNotEmpty()) {
                    fileNameText.text = filename
                } else {
                    fileNameText.text = "no file"
                }


                Log.d(TAG, "filename: $filename")
                fileErrorText.visibility = View.GONE
                inputStream = context?.contentResolver?.openInputStream(uri)!!


                parseFile(inputStream!!)


            } catch (e: NullPointerException) {
                Log.d(TAG, "$filename")
                Log.d(TAG, "no file selected")
            }


        }


    private fun parseFile(input: InputStream) {

        wordAdapter.deleteWords()

        val reader = BufferedReader(InputStreamReader(input))
        var length = 0
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
                else -> type.lowercase()
            }

            length = tra.length

            if (length < minLength)
                minLength = length

            if (length > maxLength)
                maxLength = length


            val word = Word(
                eng.lowercase(),
                listOf(tra.lowercase()),
                type,
                context = con.lowercase()
            )
            wordList.add(word.id)

            wordAdapter.addNewWord(word)

//                if(word id exists do nothing else add word)

            Log.d(TAG, "new word: $word")

        }
        input.close()

        Log.d(TAG, "words: $wordList")
    }

}