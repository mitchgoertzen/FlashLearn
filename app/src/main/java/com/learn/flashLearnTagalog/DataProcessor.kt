package com.learn.flashLearnTagalog

import android.content.res.Resources
import com.learn.flashLearnTagalog.db.Word
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class DataProcessor(val resources : Resources) {


    private var words : MutableList<Word> = mutableListOf()

    init{
        val input: InputStream = resources.openRawResource(R.raw.tag_dollar)
       val reader = BufferedReader(InputStreamReader(input))

        var string: String? = ""
        var id = 0

        while (true) {
            var type = ""
            var tag = ""
            var eng = ""
            var cat = ""
            var state = 0

            try {
                if (reader.readLine().also { string = it } == null) break
            } catch (e: IOException) {
                e.printStackTrace()
            }

            for (i in string?.indices!!) {
               when (state) {
                   0 -> {
                       if (string!![i] == '$') {
                           state = 1
                       } else {
                           type += string!![i]
                       }
                   }
                   1 -> {
                       if (string!![i] == '$') {
                           state = 2
                       } else {
                           tag += string!![i]
                       }
                   }
                   2 -> {
                       if (string!![i] == '#') {
                           state = 3
                       } else {
                           eng += string!![i]
                       }
                   }
                   3 -> {
                       cat += string!![i]
                   }
               }
            }
            tag = tag.lowercase()
            eng = eng.lowercase()
            val word = Word(id++, type, tag, eng, cat)
            words.add(word)
           }

           input.close()
        }

    fun getWords() : MutableList<Word>{
        return words
    }
}