package com.learn.flashLearnTagalog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//table in database to hold lessons
@Entity(tableName = "lesson_table")
data class Lesson(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val category: String,
    val imageID : Int,
    val level : Int,
    @ColumnInfo(defaultValue = "-1")
    val minLength : Int,
    @ColumnInfo(defaultValue = "-1")
    val maxLength : Int,
    @ColumnInfo(defaultValue = "1")
    val maxLines : Int
){
    @ColumnInfo(name = "difficulty", defaultValue = "false")
    var difficulty : Int = 1

    @ColumnInfo(name = "practice_completed", defaultValue = "false")
    var practiceCompleted : Boolean = false

    @ColumnInfo(name = "test_passed", defaultValue = "false")
    var testPassed : Boolean = false

    @ColumnInfo(name = "locked", defaultValue = "true")
    var locked : Boolean = true

}