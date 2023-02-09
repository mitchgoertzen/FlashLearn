package com.learn.flashLearnTagalog.data

data class TestWord(
    val title: String,
    var isCorrect: Boolean = true,
    var noAnswer: Boolean = false
)
