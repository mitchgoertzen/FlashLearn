package com.learn.flashLearnTagalog.db

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec


@Database(
    version = 2,
    entities = [RoomWord::class, RoomLesson::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = WordDatabase.MyAutoMigration::class)
//, spec = WordDatabase.MyAutoMigration::class
//        ,
//        AutoMigration (from = 2, to = 3),
//        AutoMigration (from = 3, to = 4)
    ]
)

abstract class WordDatabase : RoomDatabase() {
    abstract fun getWordDao(): WordDAO
    @RenameColumn( tableName = "lesson_table",
        fromColumnName = "title",
        toColumnName = "category")

    class MyAutoMigration : AutoMigrationSpec
}