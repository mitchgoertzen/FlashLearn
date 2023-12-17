package com.learn.flashLearnTagalog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.learn.flashLearnTagalog.adapters.TestWordAdapter
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word

class LessonViewModel : ViewModel() {


    private val mutableTestAdapter = MutableLiveData<TestWordAdapter>()
    val currentAdapter: LiveData<TestWordAdapter> get() = mutableTestAdapter

    private val mutableLesson = MutableLiveData<Lesson>()
    val currentLesson: LiveData<Lesson> get() = mutableLesson

    private val mutableWordList = MutableLiveData<List<Word>>()
    val currentWordList: LiveData<List<Word>> get() = mutableWordList

    val listSize: Int get() = mutableWordList.value!!.size

    fun updateAdapter(a: TestWordAdapter) {
        mutableTestAdapter.value = a
    }

    fun updateLesson(l: Lesson) {
        mutableLesson.value = l
    }

    fun updateWordList(list: List<Word>) {
        mutableWordList.value = list
    }

}