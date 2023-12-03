package com.learn.flashLearnTagalog.data

import androidx.room.ColumnInfo

data class WordStats (
    var practiced:Boolean = false,
    //what is this for??
    var previousResult : Boolean = false,
    var timesCorrect : Int = 0,
    var timesAnswered : Int = 0,
    var timesSkipped : Int = 0,
    var timesFlipped : Int = 0,
)