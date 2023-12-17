package com.learn.flashLearnTagalog.data

class TempListUtility {

    companion object {
        //TODO: add to user model
        var viewedWords = mutableMapOf<String, List<Word>>()
        var viewedLessons = mutableListOf<String>()

        var unlockedLessons = mutableListOf<String>()
        var practicedLessons = mutableListOf<String>()
        var passedLessons = mutableListOf<String>()

        fun setList(name: String, data: MutableList<String>) {
            when (name) {
                "unlocked" -> unlockedLessons = data
                "practiced" -> practicedLessons = data
                "passed" -> passedLessons = data
            }
        }

        fun clear() {
            unlockedLessons = mutableListOf()
            practicedLessons = mutableListOf()
            passedLessons = mutableListOf()
        }
    }
}