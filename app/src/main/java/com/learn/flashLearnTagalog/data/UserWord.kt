package com.learn.flashLearnTagalog.data

data class UserWord (
    val wordId : String,
    val stats : WordStats = WordStats()
)