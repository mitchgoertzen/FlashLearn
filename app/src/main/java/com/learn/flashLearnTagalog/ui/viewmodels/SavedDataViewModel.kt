package com.learn.flashLearnTagalog.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.JsonUtility

class SavedDataViewModel : ViewModel() {

//    var practicedWords = mutableMapOf<String, MutableList<Word>>()

    private val mutablePracticedWords = MutableLiveData<MutableMap<String, List<Word>>>()
    val practicedWords: LiveData<MutableMap<String, List<Word>>> get() = mutablePracticedWords

    private val mutableViewed = MutableLiveData<MutableList<String>>()
    val viewedLessons: LiveData<MutableList<String>> get() = mutableViewed

    private val mutableUnlocked = MutableLiveData<MutableList<String>>()
    val unlockedLessons: LiveData<MutableList<String>> get() = mutableUnlocked

    private val mutablePracticed = MutableLiveData<MutableList<String>>()
    val practicedLessons: LiveData<MutableList<String>> get() = mutablePracticed

    private val mutablePassed = MutableLiveData<MutableList<String>>()
    val passedLessons: LiveData<MutableList<String>> get() = mutablePassed

    fun updatePracticedWords(activity: Activity, index: String, wordList: List<Word>) {
        mutablePracticedWords.value!![index] = wordList
        JsonUtility.writeJSON(
            activity,
            "practicedWords.json",
            mutablePracticedWords.value!!
        )
    }

    fun updateViewedList(activity: Activity, lesson: String) {
        mutableViewed.value!!.add(lesson)
        JsonUtility.writeJSON(
            activity,
            //TODO: save as shared pref
            "viewedLessons.json",
            mutableViewed.value!!
        )
    }

    fun addToUnlockedList(lesson: String) {
        mutableUnlocked.value!!.add(lesson)
    }

    fun setPracticedWords(words: MutableMap<String, List<Word>>) {
        mutablePracticedWords.value = words
    }

    fun setViewedLessons(lessons: MutableList<String>) {
        mutableViewed.value = lessons
    }

    fun setUnlockedList(activity: Activity, lessons: MutableList<String>) {
        mutableUnlocked.value = lessons
        JsonUtility.writeJSON(
            activity,
            "unlockedLessons.json",
            mutableUnlocked.value!!
        )
    }

    fun updateUnlockedList(activity: Activity, lesson: String) {
        mutableUnlocked.value!!.add(lesson)
        JsonUtility.writeJSON(
            activity,
            "unlockedLessons.json",
            mutableUnlocked.value!!
        )
    }

    fun updatePracticedList(activity: Activity, lesson: String) {
        mutablePracticed.value!!.add(lesson)
        JsonUtility.writeJSON(
            activity,
            "practicedLessons.json",
            mutablePracticed.value!!
        )
    }

    fun updatePassedList(activity: Activity, lesson: String) {
        mutablePassed.value!!.add(lesson)
        JsonUtility.writeJSON(
            activity,
            "passedLessons.json",
            mutablePassed.value!!
        )
    }

    fun clear() {
        mutableUnlocked.value = mutableListOf()
        mutablePracticed.value = mutableListOf()
        mutablePassed.value = mutableListOf()
    }

}