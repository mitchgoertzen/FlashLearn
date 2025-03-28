package com.learn.flashLearnTagalog.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.learn.flashLearnTagalog.db.WordDatabase
import com.learn.flashLearnTagalog.other.Constants.KEY_CUSTOM_LESSON
import com.learn.flashLearnTagalog.other.Constants.KEY_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_ENABLE_PRONUNCIATION
import com.learn.flashLearnTagalog.other.Constants.KEY_ENG_FIRST
import com.learn.flashLearnTagalog.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_CATEGORY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_FILTERS_ACTIVE
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_PRACTICE_COMPLETED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_SORTING
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_TEST_PASSED
import com.learn.flashLearnTagalog.other.Constants.KEY_LESSON_UNLOCKED
import com.learn.flashLearnTagalog.other.Constants.KEY_MODE
import com.learn.flashLearnTagalog.other.Constants.KEY_NUM_WORDS
import com.learn.flashLearnTagalog.other.Constants.KEY_PRACTICE_NEW_WORDS
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_HINTS
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_IMAGE
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_WORD
import com.learn.flashLearnTagalog.other.Constants.SHARED_PREFERENCES_NAME
import com.learn.flashLearnTagalog.other.Constants.WORD_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideWordDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        WordDatabase::class.java,
        WORD_DATABASE_NAME
    ).allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build();


    @Singleton
    @Provides
    fun provideWordDao(db: WordDatabase) = db.getWordDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

//    @Singleton
//    @Provides
//    fun providesWordUpdate(sharedPref : SharedPreferences) = sharedPref.getBoolean(KEY_WORD_UPDATE,true)
//
//    @Singleton
//    @Provides
//    fun providesLessonUpdate(sharedPref : SharedPreferences) = sharedPref.getBoolean(KEY_LESSON_UPDATE,true)


    @Singleton
    @Provides
    fun providesShowWord(sharedPref: SharedPreferences) = sharedPref.getBoolean(KEY_SHOW_WORD, true)

    @Singleton
    @Provides
    fun providesShowImage(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_SHOW_IMAGE, false)

    @Singleton
    @Provides
    fun providesShowEngFirst(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_ENG_FIRST, true)

    @Singleton
    @Provides
    fun providesShowHints(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_SHOW_HINTS, false)

    @Singleton
    @Provides
    fun providesPracticeNewWords(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_PRACTICE_NEW_WORDS, false)

    @Singleton
    @Provides
    fun providesEnablePronunciation(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_ENABLE_PRONUNCIATION, false)

    @Singleton
    @Provides
    fun providesCreateCustomLesson(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_CUSTOM_LESSON, true)

    @Singleton
    @Provides
    fun providesMode(sharedPref: SharedPreferences) = sharedPref.getBoolean(KEY_MODE, false)

    @Singleton
    @Provides
    fun providesNumWords(sharedPref: SharedPreferences) = sharedPref.getInt(KEY_NUM_WORDS, 10)

    @Singleton
    @Provides
    fun providesDifficulty(sharedPref: SharedPreferences) = sharedPref.getInt(KEY_DIFFICULTY, 1)


    @Singleton
    @Provides
    fun lessonFilterSort(sharedPref: SharedPreferences) = sharedPref.getInt(KEY_LESSON_SORTING, 1)

    @Singleton
    @Provides
    fun lessonFilterCategory(sharedPref: SharedPreferences) =
        sharedPref.getString(KEY_LESSON_CATEGORY, "All")

    @Singleton
    @Provides
    fun lessonFilterDifficulty(sharedPref: SharedPreferences): MutableSet<String>? =
        sharedPref.getStringSet(KEY_LESSON_DIFFICULTY, null)

    @Singleton
    @Provides
    fun lessonFilterPracticeCompleted(sharedPref: SharedPreferences) =
        sharedPref.getInt(KEY_LESSON_PRACTICE_COMPLETED, 2)

    @Singleton
    @Provides
    fun lessonFilterTestPassed(sharedPref: SharedPreferences) =
        sharedPref.getInt(KEY_LESSON_TEST_PASSED, 2)

    @Singleton
    @Provides
    fun lessonFilterUnlocked(sharedPref: SharedPreferences) =
        sharedPref.getInt(KEY_LESSON_UNLOCKED, 2)

    @Singleton
    @Provides
    fun lessonFilterActive(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_LESSON_FILTERS_ACTIVE, false)

}