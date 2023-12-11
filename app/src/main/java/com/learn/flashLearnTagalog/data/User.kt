package com.learn.flashLearnTagalog.data

data class User (
    var username: String = "",
    var unlockedLessons: MutableList<String> = mutableListOf(),
    var practicedLessons: MutableList<String> = mutableListOf(),
    var passedLessons: MutableList<String> = mutableListOf(),
    var currentVersion: Int = 0
)