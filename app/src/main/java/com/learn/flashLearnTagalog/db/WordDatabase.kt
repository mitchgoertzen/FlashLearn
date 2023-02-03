package com.learn.flashLearnTagalog.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    version = 2,
    entities = [Word::class, Lesson::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
       // AutoMigration (from = 2, to = 3)
    ]
)

abstract class WordDatabase : RoomDatabase(){
    abstract fun getWordDao(): WordDAO
}