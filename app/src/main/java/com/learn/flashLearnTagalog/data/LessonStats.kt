package com.learn.flashLearnTagalog.data

data class LessonStats (
    val locked : Boolean,
    val practiceCompleted : Boolean,
    val testCompleted : Boolean,
    val bestResult : Double? = null,
    val averageResult : Double? = null
)