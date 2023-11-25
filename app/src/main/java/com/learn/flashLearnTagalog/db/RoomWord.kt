package com.learn.flashLearnTagalog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//table in database to hold words
@Entity(tableName = "word_table")
data class RoomWord(
        @PrimaryKey(autoGenerate = false)
        val id: Int,
        val type:String,
        val tagalog:String,
        val english:String,
        val category:String

    ){


    @ColumnInfo(name = "image", defaultValue = "0")
    var image: Int? = null

    @ColumnInfo(name = "correctTranslation", defaultValue = "false")
    var correctTranslation:Boolean = false

    @ColumnInfo(name = "uncommon", defaultValue = "false")
    var uncommon:Boolean = false

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
