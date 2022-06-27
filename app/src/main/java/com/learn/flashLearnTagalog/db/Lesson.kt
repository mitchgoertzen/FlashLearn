package com.learn.flashLearnTagalog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_table")
data class Lesson(
    val title: String,
    val imageID : Int,
    val level : Int,
    @ColumnInfo(defaultValue = "-1")
    val minLength : Int,
    @ColumnInfo(defaultValue = "-1")
    val maxLength : Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "practice_completed", defaultValue = "false")
    var practiceCompleted : Boolean = false
}