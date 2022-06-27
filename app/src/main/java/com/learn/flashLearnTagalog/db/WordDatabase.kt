package com.learn.flashLearnTagalog.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    version = 1,
    entities = [Word::class, Lesson::class],
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ]
)

abstract class WordDatabase : RoomDatabase(){
    abstract fun getWordDao(): WordDAO
}