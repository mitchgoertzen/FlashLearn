package com.learn.flashLearnTagalog.db

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class JsonUtility {

    companion object {

        private val gson = Gson()
        private val auth = Firebase.auth

        private const val savedLessons = "savedLessons.json"
        private const val viewedWords = "viewedWords.json"
        private const val viewedLessons = "viewedLessons.json"

        fun writeJSON(activity: Activity, file: String, data: Any, userData: Boolean) {

            Log.d(TAG, "writeJSON")
            val jsonString = gson.toJson(data)

            try {
                val fOut = if (userData) {
                    val outputFile = File(getDirectory(activity), file)
                    Log.d(TAG, "JSON WRITTEN TO $outputFile")
                    FileOutputStream(outputFile)

                } else {
                    Log.d(TAG, "JSON WRITTEN TO $file")
                    activity.openFileOutput(file, Context.MODE_PRIVATE)
                }

                fOut.flush()

                fOut.write(jsonString.toByteArray())

                fOut.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun getUserDataList(activity: Activity, file: String): MutableList<String> {
            val json = readJSON(activity, file, true)
            return if (json != "") gson.fromJson(json, Array<String>::class.java)
                .toMutableList() else mutableListOf()
        }

        //TODO: make global, not to specific user. same with saved words
        fun getSavedLessons(activity: Activity): MutableList<Lesson> {
            val json: MutableList<Lesson>
            try {
                json = gson.fromJson(
                    readJSON(activity, savedLessons, false),
                    Array<Lesson>::class.java
                )
                    .toMutableList()
            } catch (ex: Exception) {
                ex.printStackTrace()
                return mutableListOf()
            }

            return json
        }

        fun getViewedWords(
            activity: Activity
        ): MutableMap<String, List<Word>> {

            val json = readJSON(activity, viewedWords, false)
            val type = object : TypeToken<Map<String, List<Word>>>() {}.type
            return if (json != "") gson.fromJson(json, type) else mutableMapOf()
        }

        fun getViewedLessons(
            activity: Activity
        ): MutableList<String> {
            val json = readJSON(activity, viewedLessons, false)
            return if (json != "") gson.fromJson(json, Array<String>::class.java)
                .toMutableList() else mutableListOf()
        }

        private fun readJSON(activity: Activity, file: String, userData: Boolean): String? {
            val json: String?
            try {

                val fileIn = if (userData) {
                    val inputFile =
                        File(getDirectory(activity), file)
                    FileInputStream(inputFile)
                } else {
                    activity.openFileInput(file)
                }

                json = fileIn.bufferedReader().use { it.readText() }
                fileIn.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
                return ""
            }

            return json
        }

        private fun getDirectory(activity: Activity): File {
            val usersDir = File(activity.filesDir.path, "users/")
            if (!usersDir.exists()) {
                usersDir.mkdirs()
            }
            val dir = if (auth.currentUser != null) {
                File(usersDir, "${auth.currentUser!!.email.hashCode()}/")
            } else {
                File(usersDir, "shared/")
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }
    }
}