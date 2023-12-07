package com.learn.flashLearnTagalog.db

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word

class JsonUtility {

    companion object {

        private val gson = Gson()

        fun writeJSON(activity: Activity, file: String, data: Any) {


            Log.d(TAG, "writeJSON")
            val jsonString = gson.toJson(data)

            val fOut = activity.openFileOutput(file, Context.MODE_PRIVATE)

            try {
                fOut.flush()

                fOut.write(jsonString.toByteArray())

                fOut.close()
                Log.d(TAG, "JSON WRITTEN TO $file")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getPracticedWords(
            activity: FragmentActivity,
            file: String
        ): MutableMap<String, MutableList<Word>> {
            Log.d(TAG, "GET PRACTICE")
            val json = readJSON(activity, file)
            val type = object : TypeToken<Map<String, MutableList<Word>>>() {}.type
            return if (json != "") gson.fromJson(json, type) else mutableMapOf()
        }

        fun getStringList(activity: FragmentActivity, file: String): MutableList<String> {
            val json = readJSON(activity, file)
            return if (json != "") gson.fromJson(json, Array<String>::class.java)
                .toMutableList() else mutableListOf()
        }

        fun getSavedLessons(activity: FragmentActivity, file: String): MutableList<Lesson> {
            return gson.fromJson(readJSON(activity, file), Array<Lesson>::class.java)
                .toMutableList()
        }

        private fun readJSON(activity: FragmentActivity, file: String): String? {
            val json: String?
            try {

                val fileIn = activity.openFileInput(file)

                json = fileIn?.bufferedReader().use { it?.readText() }

            } catch (ex: Exception) {
                ex.printStackTrace()
                return ""
            }

            return json
        }
    }
}