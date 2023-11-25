package com.learn.flashLearnTagalog.data

data class User (
    val wordHistory : MutableList<UserWord> = mutableListOf(),
    val lessonHistory : MutableList<UserLesson> = mutableListOf()
)