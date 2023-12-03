package com.learn.flashLearnTagalog.data

import android.util.Log
import com.learn.flashLearnTagalog.R

data class Lesson(
    val category: String = "",
    val level: Int = -1,
    val minLength: Int = -1,
    val maxLength: Int = -1,
    val wordCount: Int = 0,
    //will not need, fix ui
    val maxLines: Int = -1,
    val image: Int = R.drawable.bathroom,

    ) {
    val id: String = category + "_" + level
    val difficulty: Int = when ((minLength + maxLength) / 2) {
        in 0..5 -> 1
        in 6..7 -> 2
        in 8..9 -> 3
        in 10..11 -> 4
        else -> 5
    }

}