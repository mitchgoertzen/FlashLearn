package com.learn.flashLearnTagalog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.repos.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

//directly interacts with activities and fragments, which indirectly gives access to the database via MainRepo
@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepo: MainRepo
) : ViewModel(){

    fun insertWord(word : Word) = viewModelScope.launch {
        //Log.d("INSERT", "$word has been inserted")
        val insert = async { mainRepo.insertWord(word) }
        insert.await()

    }

    fun insertAll(words : List<Word>) = viewModelScope.launch {
        //Log.d("INSERT", "$word has been inserted")
        mainRepo.insertAll(words)

    }

    fun getSize() = mainRepo.getSize()

    fun updatePractice(wordID : Int, value : Boolean) = viewModelScope.launch {
        mainRepo.updatePractice(wordID,value)
    }


    fun getPractice(wordID : Int) = mainRepo.getPractice(wordID)

    fun answerWord(wordID : Int, result : Boolean) = viewModelScope.launch {
        mainRepo.answerWord(wordID, result)
    }

    fun skipWord(wordID : Int) = viewModelScope.launch {
        mainRepo.skipWord(wordID)
    }


    fun flipWord(wordID : Int) = viewModelScope.launch {
        mainRepo.flipWord(wordID)
    }

    fun nukeTable() = viewModelScope.launch {
        mainRepo.nukeTable()
        Log.d("NUKE", "nuked")
    }

    fun getAllWordsForLesson(category:String) = mainRepo.getAllWordsForLesson(category)

    fun getWordsByDifficultyForLesson(category:String, min:Int, max:Int) = mainRepo.getWordsByDifficultyForLesson(category,min,max)

    fun getAllWords() = mainRepo.getAllWords()

    fun getDictionaryWords(offset : Int, limit : Int) = mainRepo.getDictionaryWords(offset, limit)

    fun getEasyWords() = mainRepo.getEasyWords()

    fun getIntermediateWords() = mainRepo.getIntermediateWords()

    fun getHardWords() = mainRepo.getHardWords()

    fun getPracticedEasyWords() = mainRepo.getPracticedEasyWords()

    fun getPracticedIntermediateWords() = mainRepo.getPracticedIntermediateWords()

    fun getPracticedHardWords() = mainRepo.getPracticedHardWords()

    fun getAllPracticedWords() = mainRepo.getAllPracticedWords()

    fun getMostCorrect() = mainRepo.getMostCorrect()

    fun getLeastCorrect() = mainRepo.getLeastCorrect()

    fun getBest() = mainRepo.getBest()

    fun getWorst() = mainRepo.getWorst()

    fun getMostEncountered() = mainRepo.getMostEncountered()

    fun getMostSkipped() = mainRepo.getMostSkipped()

    fun getMostFlipped() = mainRepo.getMostFlipped()

    fun getAllLessons() = mainRepo.getAllLessons()

    fun insertAllLessons(lessons : List<Lesson>) = viewModelScope.launch {
        Log.d("INSERT", "$lessons have been inserted")
        mainRepo.insertAllLessons(lessons)
    }

    fun insertLesson(lesson: Lesson) = viewModelScope.launch {
        Log.d("INSERT", "$lesson has been inserted")
        val insert = async { mainRepo.insertLesson(lesson) }
        insert.await()

    }

    fun unlockNextLesson(category : String, level : Int) = mainRepo.unlockNextLesson(category, level)

    fun completePractice(id : Int) = mainRepo.completePractice(id)

    fun nukeLessons() = viewModelScope.launch {
        mainRepo.nukeLessons()
        Log.d("NUKE", "lessons nuked")
    }
}