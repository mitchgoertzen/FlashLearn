package com.learn.flashLearnTagalog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.flashLearnTagalog.db.RoomLesson
import com.learn.flashLearnTagalog.db.RoomWord
import com.learn.flashLearnTagalog.repos.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

//directly interacts with activities and fragments, which indirectly gives access to the database via MainRepo
@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepo: MainRepo
) : ViewModel() {

    fun insertWord(word: RoomWord) = viewModelScope.launch {
        //Log.d("INSERT", "$word has been inserted")
        val insert = async { mainRepo.insertWord(word) }
        insert.await()

    }

    fun insertAll(words: List<RoomWord>) = viewModelScope.launch {
        //Log.d("INSERT", "$word has been inserted")
        mainRepo.insertAll(words)
    }

    fun getSize() = mainRepo.getSize()

    fun getWord(id: Int) = mainRepo.getWord(id)

    fun updatePractice(wordID: Int, value: Boolean) = viewModelScope.launch {
        mainRepo.updatePractice(wordID, value)
    }


    fun getPractice(wordID: Int) = mainRepo.getPractice(wordID)

    fun answerWord(wordID: Int, result: Boolean) = viewModelScope.launch {
        mainRepo.answerWord(wordID, result)
    }

    fun skipWord(wordID: Int) = viewModelScope.launch {
        mainRepo.skipWord(wordID)
    }


    fun flipWord(wordID: Int) = viewModelScope.launch {
        mainRepo.flipWord(wordID)
    }

    fun updateWordInfo(
        id: Int,
        newType: String,
        newTagalog: String,
        newEnglish: String,
        newCategory: String,
        uncommon: Boolean,
        correctTranslation: Boolean
    ) = viewModelScope.launch {
        mainRepo.updateWordInfo(
            id,
            newType,
            newTagalog,
            newEnglish,
            newCategory,
            uncommon,
            correctTranslation
        )
    }

    fun getIncorrectWords() = mainRepo.getIncorrectWords()

    fun deleteIncorrectWords() = viewModelScope.launch {
        println("deleting unused words...")
        mainRepo.deleteIncorrectWords()
    }

    fun nukeTable() = viewModelScope.launch {
        mainRepo.nukeTable()
        Log.d("NUKE", "words nuked")
    }

    fun getAllWordsForLesson(category: String) = mainRepo.getAllWordsForLesson(category)

    fun getLessonWordList(category: String, min: Int, max: Int) =
        mainRepo.getLessonWordList(category, min, max)

    fun getWordsByDifficultyForLesson(category: String, min: Int, max: Int) =
        mainRepo.getWordsByDifficultyForLesson(category, min, max)

    fun getAllWords() = mainRepo.getAllWords()

    fun getDictionaryWords(offset: Int, limit: Int) = mainRepo.getDictionaryWords(offset, limit)

    fun getWordsByDifficulty(practiced : Int, minLength : Int, maxLength : Int) = mainRepo.getWordsByDifficulty(practiced, minLength, maxLength)

    fun getAllPracticedWords() = mainRepo.getAllPracticedWords()

    fun getMostCorrect() = mainRepo.getMostCorrect()

    fun getLeastCorrect() = mainRepo.getLeastCorrect()

    fun getBest() = mainRepo.getBest()

    fun getWorst() = mainRepo.getWorst()

    fun getMostEncountered() = mainRepo.getMostEncountered()

    fun getMostSkipped() = mainRepo.getMostSkipped()

    fun getMostFlipped() = mainRepo.getMostFlipped()


    fun getAllLessons() = mainRepo.getAllLessons()

    fun getLessonCount() = mainRepo.getLessonCount()

    fun getLessonByID(id : Int) =
        mainRepo.getLessonByID(id)

    fun getLessonByData(category: String, level: Int) =
        mainRepo.getLessonByData(category, level)

    fun lessonExists(id: Int) = mainRepo.lessonExists(id)

    fun lessonCategoryLevelExists(category: String, level: Int) =
        mainRepo.lessonCategoryLevelExists(category, level)

    fun updateLessonID(category: String, level: Int, newID: Int) =
        mainRepo.updateLessonID(category, level, newID)

    fun previousTestPassed(category: String, level: Int) =
        mainRepo.previousTestPassed(category, level)

    fun insertAllLessons(lessons: List<RoomLesson>) = viewModelScope.launch {
        Log.d("INSERT", "$lessons have been inserted")
        mainRepo.insertAllLessons(lessons)
    }

    fun insertLesson(lesson: RoomLesson) = viewModelScope.launch {
        Log.d("INSERT", "$lesson has been inserted")
        val insert = async { mainRepo.insertLesson(lesson) }
        insert.await()
    }

    fun unlockNextLesson(category: String, level: Int) = viewModelScope.launch {
        mainRepo.unlockNextLesson(category, level)
    }

    fun completePractice(id: Int) = viewModelScope.launch {
        mainRepo.completePractice(id)
    }

    fun passTest(id: Int) = viewModelScope.launch {
        mainRepo.passTest(id)
    }

    fun updateLessonInfo(
        id: Int, newTitle: String, newImageID: Int, newLevel: Int,
        newMin: Int, newMax: Int, newLines : Int, newDifficulty : Int, practiceCompleted: Boolean, testPassed: Boolean, locked: Boolean
    ) = viewModelScope.launch {
        mainRepo.updateLessonInfo(
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
    }

    suspend fun deleteLesson(category: String, level: Int) = mainRepo.deleteLesson(category, level)

    fun nukeLessons() = viewModelScope.launch {
        mainRepo.nukeLessons()
        Log.d("NUKE", "lessons nuked")
    }
}