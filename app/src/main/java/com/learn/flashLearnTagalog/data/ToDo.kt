package com.learn.flashLearnTagalog.data

data class ToDo(
    val title: String,
    var isCorrect: Boolean = true,
    var noAnswer: Boolean = false
)
