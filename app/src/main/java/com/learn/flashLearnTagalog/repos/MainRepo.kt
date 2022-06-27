package com.learn.flashLearnTagalog.repos

import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.db.WordDAO
import javax.inject.Inject

class MainRepo @Inject constructor(
    private val wordDao : WordDAO
) {
    suspend fun insertWord(word : Word) = wordDao.insertWord(word)

    fun insertAll(words : List<Word>) = wordDao.insertAll(words)

    suspend fun deleteWord(word : Word) = wordDao.deleteWord(word)

    fun getSize() = wordDao.getSize()

    fun updatePractice(wordID : Int, value : Boolean) = wordDao.updatePractice(wordID,value)

    fun getPractice(wordID : Int) = wordDao.getPractice(wordID)

    fun answerWord(wordID : Int, result : Boolean) = wordDao.answerWord(wordID, result)

    fun skipWord(wordID : Int) = wordDao.skipWord(wordID)

    fun flipWord(wordID : Int) = wordDao.flipWord(wordID)

    fun getDictionaryWords(offset : Int, limit : Int) = wordDao.getDictionaryWords(offset, limit)

    fun getAllWordsForLesson(category:String) = wordDao.getAllWordsForLesson(category)

    fun getWordsByDifficultyForLesson(category:String, min:Int, max:Int) = wordDao.getWordsByDifficultyForLesson(category, min, max)

    fun getAllWords() = wordDao.getAllWords()

    fun getEasyWords() = wordDao.getEasyWords()

    fun getIntermediateWords() = wordDao.getIntermediateWords()

    fun getHardWords() = wordDao.getHardWords()

    fun getPracticedEasyWords() = wordDao.getPracticedEasyWords()

    fun getPracticedIntermediateWords() = wordDao.getPracticedIntermediateWords()

    fun getPracticedHardWords() = wordDao.getPracticedHardWords()

    fun getAllPracticedWords() = wordDao.getAllPracticedWords()

    fun getMostCorrect() = wordDao.getMostCorrect()

    fun getLeastCorrect() = wordDao.getLeastCorrect()

    fun getBest() = wordDao.getBest()

    fun getWorst() = wordDao.getWorst()

    fun getMostEncountered() = wordDao.getMostEncountered()

    fun getMostSkipped() = wordDao.getMostSkipped()

    fun getMostFlipped() = wordDao.getMostFlipped()


    fun nukeTable() = wordDao.nukeTable()



    fun getAllLessons() = wordDao.getAllLessons()

    fun insertAllLessons(lessons : List<Lesson>) = wordDao.insertAllLessons(lessons)

    suspend fun insertLesson(lesson: Lesson) = wordDao.insertLesson(lesson)

    fun completePractice(title : String) = wordDao.completePractice(title)

    fun getPracticeCompleted(title : String) = wordDao.getPracticeCompleted(title)

    fun nukeLessons() = wordDao.nukeLessons()
}