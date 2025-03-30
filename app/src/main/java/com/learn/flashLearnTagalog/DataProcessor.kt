package com.learn.flashLearnTagalog

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.util.Log
import android.widget.TextView
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class DataProcessor(val resources: Resources, val language: String) {

    fun makeList(function: Int, file: Int, delimiter: Char, start: Int, end: Int, logText : TextView): MutableList<Word> {
        var words: MutableList<Word> = mutableListOf()

        var index = 0

        if (file != -1) {

            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {


                val input: InputStream = resources.openRawResource(file)
                val reader = BufferedReader(InputStreamReader(input))

                var currentString: String? = ""

                while (index++ < end) {

                    var type = ""
                    var tra = ""
                    var eng = ""
                    var cat = ""
                    var con = ""
                    var state = 0
                    try {
                        //if the currentString is empty, exit while()
                        if (reader.readLine().also { currentString = it } == null) break
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (index > start) {
                        //for each char in currentString
                        for (i in currentString!!.indices) {
                            //state represents the data of each word that is currently being parsed
                            //once sentinel value is reached($ or #), save currentString to current variable and increment state
                            when (state) {
                                //0 = type
                                0 -> {
                                    if (currentString!![i] == delimiter) {
                                        state = 1
                                    } else {
                                        type += currentString!![i]
                                    }
                                }
                                //1 = translation
                                1 -> {
                                    if (currentString!![i] == delimiter) {
                                        state = 2
                                    } else {
                                        tra += currentString!![i]
                                    }
                                }
                                //2 = english
                                2 -> {
                                    if (currentString!![i] == '#') {
                                        state = 4
                                    } else if (currentString!![i] == delimiter) {
                                        state = 3
                                    } else {
                                        eng += currentString!![i]
                                    }
                                }
                                //3 = category
                                3 -> {
                                    if (currentString!![i] == delimiter) {
                                        state = 4
                                    } else {
                                        con += currentString!![i]
                                    }
                                }
                                //3 = category
                                4 -> {
                                    cat += currentString!![i]
                                }
                            }
                        }
                        tra = tra.lowercase().replace("_".toRegex(), ", ")
                        eng = eng.lowercase().replace("_".toRegex(), ", ")
                        con = con.lowercase().replace("_".toRegex(), ", ")

                        val word = Word(eng, listOf(tra), type, cat, con)
                      //  logText.text = "$index"
                        when (function) {
                            0 ->Log.d(TAG, "$tra")
                               // async {DataUtility.insertWord(word, language) }.await()

                            1 -> async {
                                if (cat != "") {

                                    Log.d(TAG, "$index")
                                    Log.d(TAG, "word id: ${word.id}...${word.type},${word.english}")
                                    DataUtility.updateWordCategory(
                                        language,
                                        word.id,
                                        cat
                                    )
                                }
                            }.await()
                        }
                    }
                }
                input.close()
                Log.d(TAG, "DONE")
                logText.text = "DONE"
                scope.cancel()
            }

        }
        return words
    }
}