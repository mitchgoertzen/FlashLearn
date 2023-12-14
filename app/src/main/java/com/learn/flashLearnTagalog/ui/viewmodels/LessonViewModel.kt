package com.learn.flashLearnTagalog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word

class LessonViewModel : ViewModel() {

    private val mutableLesson = MutableLiveData<Lesson>()
    val currentLesson: LiveData<Lesson> get() = mutableLesson


    private val mutableWordList = MutableLiveData<List<Word>>()
    val currentWordList: LiveData<List<Word>> get() = mutableWordList

    fun updateLesson(l: Lesson) {
        mutableLesson.value = l
    }

    fun updateWordList(list: List<Word>) {
        mutableWordList.value = list
    }

//    fun updateLesson(l: Lesson) {
//        lesson.value = l
//    }
//
//    fun getLesson() {
//        return lesson.value
//    }

//    private val wordList = MutableLiveData<List<Word>>()


//    fun updateWordList(list: List<Word>) {
//        wordList.value = list
//    }
//
//    fun getWordList(item: Item) {
//        return wordList.value
//    }


}