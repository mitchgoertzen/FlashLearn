package com.learn.flashLearnTagalog

import android.content.res.Resources
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

class DataProcessor(val resources: Resources, val language: String, val file: Int) {

    fun getWords(): MutableList<Word> {

        var words: MutableList<Word> = mutableListOf()

        var i = 0

        if (file != -1) {

            val scope = CoroutineScope(Job() + Dispatchers.Main)
            scope.launch {


                val input: InputStream = resources.openRawResource(file)
                val reader = BufferedReader(InputStreamReader(input))

                var currentString: String? = ""

                while (i++ < 1000) {
                    var type = ""
                    var tra = ""
                    var eng = ""
                    var cat = ""
                    var state = 0

                    try {
                        //if the currentString is empty, exit while()
                        if (reader.readLine().also { currentString = it } == null) break
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    //for each char in currentString
                    for (i in currentString!!.indices) {
                        //state represents the data of each word that is currently being parsed
                        //once sentinel value is reached($ or #), save currentString to current variable and increment state
                        when (state) {
                            //0 = type
                            0 -> {
                                if (currentString!![i] == '$') {
                                    state = 1
                                } else {
                                    type += currentString!![i]
                                }
                            }
                            //1 = translation
                            1 -> {
                                if (currentString!![i] == '$') {
                                    state = 2
                                } else {
                                    tra += currentString!![i]
                                }
                            }
                            //2 = english
                            2 -> {
                                if (currentString!![i] == '#') {
                                    state = 3
                                } else {
                                    eng += currentString!![i]
                                }
                            }
                            //3 = category
                            3 -> {
                                cat += currentString!![i]
                            }
                        }
                    }
                    tra = tra.lowercase()
                    eng = eng.lowercase()
                    val word = Word(eng, listOf(tra), type, cat)
                    if (cat != "") {
                        //TODO: sharedpref
                        async { DataUtility.insertWord(word, "tagalog") }.await()
                    }
                }
                input.close()
                scope.cancel()
            }
        }
        return words
    }
}