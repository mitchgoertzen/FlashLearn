package com.learn.flashLearnTagalog.repos

import com.learn.flashLearnTagalog.db.RoomLesson
import com.learn.flashLearnTagalog.db.RoomWord
import com.learn.flashLearnTagalog.db.WordDAO
import javax.inject.Inject

//connector class
//provides functions to be used by MainViewModel, which then call the equivalent function in WordDAO
class MainRepo @Inject constructor(
    private val wordDao: WordDAO
) {
    suspend fun insertWord(word: RoomWord) = wordDao.insertWord(word)

    fun insertAll(words: List<RoomWord>) = wordDao.insertAll(words)

    suspend fun deleteWord(word: RoomWord) = wordDao.deleteWord(word)

    fun getSize() = wordDao.getSize()

    fun getWord(id: Int) = wordDao.getWord(id)

    fun updatePractice(wordID: Int, value: Boolean) = wordDao.updatePractice(wordID, value)

    fun getPractice(wordID: Int) = wordDao.getPractice(wordID)

    fun answerWord(wordID: Int, result: Boolean) = wordDao.answerWord(wordID, result)

    fun skipWord(wordID: Int) = wordDao.skipWord(wordID)

    fun flipWord(wordID: Int) = wordDao.flipWord(wordID)

    fun updateWordInfo(
        id: Int,
        newType: String,
        newTagalog: String,
        newEnglish: String,
        newCategory: String,
        uncommon: Boolean,
        correctTranslation: Boolean
    ) =
        wordDao.updateWordInfo(
            id,
            newType,
            newTagalog,
            newEnglish,
            newCategory,
            uncommon,
            correctTranslation
        )

    fun getDictionaryWords(offset: Int, limit: Int) = wordDao.getDictionaryWords(offset, limit)

    fun getAllWordsForLesson(category: String) = wordDao.getAllWordsForLesson(category)

    fun getWordsByDifficultyForLesson(category: String, min: Int, max: Int) =
        wordDao.getWordsByDifficultyForLesson(category, min, max)

    fun getLessonWordList(category: String, min: Int, max: Int) =
        wordDao.getLessonWordList(category, min, max)

    fun getAllWords() = wordDao.getAllWords()

    fun getWordsByDifficulty(practiced : Int, minLength : Int, maxLength : Int) = wordDao.getWordsByDifficulty(practiced, minLength, maxLength)

    fun getAllPracticedWords() = wordDao.getAllPracticedWords()

    fun getMostCorrect() = wordDao.getMostCorrect()

    fun getLeastCorrect() = wordDao.getLeastCorrect()

    fun getBest() = wordDao.getBest()

    fun getWorst() = wordDao.getWorst()

    fun getMostEncountered() = wordDao.getMostEncountered()

    fun getMostSkipped() = wordDao.getMostSkipped()

    fun getMostFlipped() = wordDao.getMostFlipped()

    fun deleteIncorrectWords() = wordDao.deleteIncorrectWords()

    fun getIncorrectWords() = wordDao.getIncorrectWords()

    fun nukeTable() = wordDao.nukeTable()


    fun getAllLessons() = wordDao.getAllLessons()

    fun getLessonCount() = wordDao.getLessonCount()

    fun getLessonByID(id : Int) = wordDao.getLessonByID(id)

    fun getLessonByData(category: String, level: Int) = wordDao.getLessonByData(category, level)

    fun lessonExists(id: Int) = wordDao.lessonExists(id)

    fun lessonCategoryLevelExists(category: String, level: Int) =
        wordDao.lessonCategoryLevelExists(category, level)

    fun updateLessonID(category: String, level: Int, newID: Int) =
        wordDao.updateLessonID(category, level, newID)

    fun previousTestPassed(category: String, level: Int) =
        wordDao.previousTestPassed(category, level)

    fun insertAllLessons(lessons: List<RoomLesson>) = wordDao.insertAllLessons(lessons)

    suspend fun insertLesson(lesson: RoomLesson) = wordDao.insertLesson(lesson)

    fun unlockNextLesson(category: String, level: Int) = wordDao.unlockNextLesson(category, level)

    fun updateLessonInfo(
        id: Int, newTitle: String, newImageID: Int, newLevel: Int,
        newMin: Int, newMax: Int, newLines : Int, newDifficulty : Int, practiceCompleted: Boolean, testPassed: Boolean, locked: Boolean
    ) =
        wordDao.updateLessonInfo(
            id,
            newTitle,
            newImageID,
            newLevel,
            newMin,
            newMax,
            newLines,
            newDifficulty,
            practiceCompleted,
            testPassed,
            locked
        )

    fun completePractice(id: Int) = wordDao.completePractice(id)

    fun passTest(id: Int) = wordDao.passTest(id)

    suspend fun deleteLesson(category: String, level: Int) = wordDao.deleteLesson(category, level)

    fun nukeLessons() = wordDao.nukeLessons()
}