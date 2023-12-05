package com.learn.flashLearnTagalog.data

class TempListUtility {

    companion object {
        var practicedWords = mutableMapOf<String, MutableList<Word>>()

        //TODO: here or shared pref
        var viewedLessons = mutableListOf<String>()
        var unlockedLessons = mutableListOf<String>()
        var practicedLessons = mutableListOf<String>()
        var passedLessons = mutableListOf<String>()

    }
}