package com.learn.flashLearnTagalog.data

data class LessonStats (
    val locked : Boolean = false,
    val practiceCompleted : Boolean = false,
    val testCompleted : Boolean = false,
    val testPassed : Boolean = false,
    val bestResult : Double? = null,
    val averageResult : Double? = null
)