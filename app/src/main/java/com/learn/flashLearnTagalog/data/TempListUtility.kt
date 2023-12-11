package com.learn.flashLearnTagalog.data

import com.learn.flashLearnTagalog.db.JsonUtility

class TempListUtility {

    companion object {
        //TODO: add to user model
        var practicedWords = mutableMapOf<String, MutableList<Word>>()
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