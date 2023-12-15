package com.learn.flashLearnTagalog.db

import android.app.Activity
import android.content.ContentValues.TAG
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

        fun writeJSON(activity: Activity, file: String, data: Any) {

            Log.d(TAG, "writeJSON")
            val jsonString = gson.toJson(data)

            try {
                val outputFile = File(getDirectory(activity), file)
                val fOut = FileOutputStream(outputFile)

                fOut.flush()

                fOut.write(jsonString.toByteArray())

                fOut.close()
                Log.d(TAG, "JSON WRITTEN TO $outputFile")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getPracticedWords(
            activity: Activity,
            file: String
        ): MutableMap<String, List<Word>> {

            val json = readJSON(activity, file)
            val type = object : TypeToken<Map<String, List<Word>>>() {}.type
            return if (json != "") gson.fromJson(json, type) else mutableMapOf()
        }

        fun getStringList(activity: Activity, file: String): MutableList<String> {
            val json = readJSON(activity, file)
            return if (json != "") gson.fromJson(json, Array<String>::class.java)
                .toMutableList() else mutableListOf()
        }

        fun getSavedLessons(activity: Activity, file: String): MutableList<Lesson> {
            Log.d(TAG, getLocalPath().toString())
            Log.d(TAG, getLocalPath().path)
            Log.d(TAG, "${getLocalPath()}/$file")
            return gson.fromJson(
                readJSON(activity, file),
                Array<Lesson>::class.java
            )
                .toMutableList()
        }

        private fun readJSON(activity: Activity, file: String): String? {
            val json: String?
            try {


                val inputFile = File(getDirectory(activity), file)
                val fileIn = FileInputStream(inputFile)
                //  activity.openFileInput(getDirectory(file))

                json = fileIn.bufferedReader().use { it.readText() }
                fileIn.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
                return ""
            }

            return json
        }

        private fun getLocalPath(): File {
            val usersDir = File("users/")
            return if (auth.currentUser != null) {
                File(usersDir, "${auth.currentUser!!.uid}/")
            } else {
                File(usersDir, "shared/")
            }
        }

        private fun getDirectory(activity: Activity): File {
            val usersDir = File(activity.filesDir.path, "users/")
            if (!usersDir.exists()) {
                usersDir.mkdirs()
            }
            val dir = if (auth.currentUser != null) {
                File(usersDir, "${auth.currentUser!!.uid}/")
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