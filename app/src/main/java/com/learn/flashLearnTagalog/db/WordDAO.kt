package com.learn.flashLearnTagalog.db

import androidx.lifecycle.LiveData
import androidx.room.*

//Data Access Object
//Used for creating an object which will directly access/alter database
@Dao
interface WordDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word:Word)

    @Insert
    fun insertAll(words: List<Word>)

    @Delete
    suspend fun deleteWord(word:Word)

    @Query("SELECT COUNT(*) FROM word_table")
    fun getSize():Int

    @Query("SELECT * FROM word_table ORDER BY english ASC LIMIT :limit OFFSET :offset ")
    fun getDictionaryWords(offset : Int, limit : Int):LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE category == :category")
    fun getAllWordsForLesson(category:String):LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE category == :category AND LENGTH(tagalog) > :min AND LENGTH(tagalog) < :max")
    fun getWordsByDifficultyForLesson(category:String, min : Int, max : Int):LiveData<List<Word>>

    @Query("SELECT * FROM word_table ORDER BY english DESC")
    fun getAllWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE LENGTH(tagalog) <= 6")
    fun getEasyWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE LENGTH(tagalog) >6 AND LENGTH(tagalog) <= 11")
    fun getIntermediateWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE LENGTH(tagalog) > 11")
    fun getHardWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word_practiced == 1 AND LENGTH(tagalog) <= 6")
    fun getPracticedEasyWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word_practiced == 1 AND LENGTH(tagalog) >6 AND LENGTH(tagalog) <= 11")
    fun getPracticedIntermediateWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word_practiced == 1 AND LENGTH(tagalog) > 11")
    fun getPracticedHardWords():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE word_practiced == 1 ORDER BY LENGTH(tagalog) DESC")
    fun getAllPracticedWords():LiveData<List<Word>>

    @Query("UPDATE word_table SET word_practiced = :value WHERE id == :wordID")
    fun updatePractice(wordID: Int, value : Boolean)

    @Query("SELECT word_practiced FROM word_table WHERE id == :wordID")
    fun getPractice(wordID: Int) : Boolean

    @Query("UPDATE word_table SET timesCorrect = (timesCorrect + :result), timesAnswered = (timesAnswered + 1), previousResult = :result WHERE id == :wordID")
    fun answerWord(wordID : Int, result: Boolean)

    @Query("UPDATE word_table SET timesSkipped = (timesSkipped + 1) WHERE id == :wordID")
    fun skipWord(wordID : Int)

    @Query("UPDATE word_table SET timesFlipped = (timesFlipped + 1) WHERE id == :wordID")
    fun flipWord(wordID : Int)

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect DESC LIMIT 5")
    fun getMostCorrect():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesAnswered - timesCorrect DESC LIMIT 5")
    fun getLeastCorrect():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered DESC LIMIT 5")
    fun getBest():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered ASC LIMIT 5")
    fun getWorst():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesAnswered DESC LIMIT 5")
    fun getMostEncountered():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesSkipped DESC LIMIT 5")
    fun getMostSkipped():LiveData<List<Word>>

    @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesFlipped DESC LIMIT 5")
    fun getMostFlipped():LiveData<List<Word>>

    @Query("DELETE FROM word_table")
    fun nukeTable()



    @Query("SELECT * FROM lesson_table ORDER BY level ASC")
    fun getAllLessons():LiveData<List<Lesson>>

    @Insert
    fun insertAllLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLesson(lesson: Lesson)

    @Query("UPDATE lesson_table SET practice_completed = 1 WHERE title == :title")
    fun completePractice(title : String)

    @Query("SELECT practice_completed FROM lesson_table WHERE title == :title")
    fun getPracticeCompleted(title : String): Boolean


    @Query("UPDATE lesson_table SET test_completed = 1 WHERE title == :title")
    fun completeTest(title : String)

    @Query("SELECT test_completed FROM lesson_table WHERE title == :title")
    fun getTestCompleted(title : String): Boolean

    @Query("DELETE FROM lesson_table")
    fun nukeLessons()
}