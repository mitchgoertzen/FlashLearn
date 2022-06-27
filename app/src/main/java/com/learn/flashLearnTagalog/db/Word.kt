package com.learn.flashLearnTagalog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
data class Word(
        val type:String,
        val tagalog:String,
        val english:String,
        val category:String

//    val image:Image,

    ){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "word_practiced", defaultValue = "false")
    var practiced:Boolean = false

    @ColumnInfo(defaultValue = "true")
    var previousResult:Boolean = true

    @ColumnInfo(defaultValue = "0")
    var timesCorrect:Int = 0

    @ColumnInfo(defaultValue = "0")
    var timesAnswered:Int = 0

    @ColumnInfo(defaultValue = "0")
    var timesSkipped:Int = 0

    @ColumnInfo(defaultValue = "0")
    var timesFlipped:Int = 0
}
