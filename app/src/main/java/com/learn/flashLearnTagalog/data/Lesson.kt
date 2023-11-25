package com.learn.flashLearnTagalog.data

data class Lesson (
    val category : String,
    val level : Int,
    val difficulty : Int,
    val image : String,
    val minLength : Int,
    val maxLength : Int,
    //will not need, fix ui
    val maxLines : Int
)