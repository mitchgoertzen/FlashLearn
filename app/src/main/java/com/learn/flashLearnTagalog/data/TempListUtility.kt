package com.learn.flashLearnTagalog.data

class TempListUtility {

    companion object {
        var practicedWords = mutableMapOf<String, MutableList<Word>>()
        var viewedLessons = mutableListOf<String>()

        var unlockedLessons = mutableListOf<String>()
        var practicedLessons = mutableListOf<String>()
        var passedLessons = mutableListOf<String>()

        fun setList(name: String, data: MutableList<String>){
            when(name){
                "unlocked" -> unlockedLessons = data
                "practiced" -> practicedLessons = data
                "passed" -> passedLessons = data
            }
        }
    }
}