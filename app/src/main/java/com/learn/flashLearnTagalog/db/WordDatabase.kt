package com.learn.flashLearnTagalog.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    version = 4,
    entities = [Word::class, Lesson::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 3, to = 4)
    ]
)

abstract class WordDatabase : RoomDatabase(){
    abstract fun getWordDao(): WordDAO
}