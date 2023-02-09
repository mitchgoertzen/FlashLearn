package com.learn.flashLearnTagalog

import com.learn.flashLearnTagalog.db.Lesson

class LessonCreator {

    private val myLessons: MutableList<Lesson> = mutableListOf()

    //if a lesson's min or max word length have not been set manually,
    //they will default to these values
    private val easyMinLength = 0
    private val easyMaxLength = 6

    private val mediumMinLength = 6
    private val mediumMaxLength = 9

    private val hardMinLength = 9
    private val hardMaxLength = 100

    //lessons created here
    init {
        val lesson1 =
            Lesson("Custom\nLesson0".hashCode(), "Custom\nLesson", R.drawable.custom, 0, -1, -1)
        lesson1.locked = false
        myLessons.add(lesson1)

        myLessons.add(createLessonObject(1, "Body", R.drawable.body, 0, 4))
        myLessons.add(createLessonObject(2, "Body", R.drawable.body, 4, 5))
        myLessons.add(createLessonObject(3, "Body", R.drawable.body, 5, 7))
        myLessons.add(createLessonObject(4, "Body", R.drawable.body, 7, 100))

        myLessons.add(createLessonObject(1, "Animals", R.drawable.dog, 0, 4))
        myLessons.add(createLessonObject(2, "Animals", R.drawable.dog, 4, 5))
        myLessons.add(createLessonObject(3, "Animals", R.drawable.dog, 5, 6))
        myLessons.add(createLessonObject(4, "Animals", R.drawable.dog, 6, 7))
        myLessons.add(createLessonObject(5, "Animals", R.drawable.dog, 7, 10))
        myLessons.add(createLessonObject(6, "Animals", R.drawable.dog, 10, 100))

//        myLessons.add(createLesson(1,"Speech", R.drawable.hand_wave, 0,4))
//        myLessons.add(createLesson(2,"Speech", R.drawable.hand_wave,4,100))

        myLessons.add(createLessonObject(1, "Geography", R.drawable.mountain, 0, 5))
        myLessons.add(createLessonObject(2, "Geography", R.drawable.mountain, 5, 7))
        myLessons.add(createLessonObject(3, "Geography", R.drawable.mountain, 7, 100))

        myLessons.add(createLessonObject(1, "Food", R.drawable.food, -1, -1))
        myLessons.add(createLessonObject(2, "Food", R.drawable.food, -1, -1))
        myLessons.add(createLessonObject(3, "Food", R.drawable.food, -1, -1))

        myLessons.add(createLessonObject(1, "People", R.drawable.face_icon, -1, -1))
        myLessons.add(createLessonObject(2, "People", R.drawable.face_icon, -1, -1))
        myLessons.add(createLessonObject(3, "People", R.drawable.face_icon, -1, -1))

        //myLessons.add(createLessonObject(4,"test", R.drawable.dog,0,100))
//        myLessons.add(createLessonObject(2,"Animals\nII", R.drawable.dog,-1,-1))
//        myLessons.add(createLessonObject(3,"Animals\nIII", R.drawable.dog,-1,-1))
//
//        myLessons.add(createLessonObject(1,"Animals\nI", R.drawable.dog,-1,-1))
//        myLessons.add(createLessonObject(2,"Animals\nII", R.drawable.dog,-1,-1))
//        myLessons.add(createLessonObject(3,"Animals\nIII", R.drawable.dog,-1,-1))
//
//        myLessons.add(createLessonObject(1,"Animals\nI", R.drawable.dog,-1,-1))
//        myLessons.add(createLessonObject(2,"Animals\nII", R.drawable.dog,-1,-1))
//        myLessons.add(createLessonObject(3,"Animals\nIII", R.drawable.dog,-1,-1))
    }

    private fun createLessonObject(
        level: Int,
        title: String,
        imageID: Int,
        overrideMin: Int,
        overrideMax: Int
    ): Lesson {
        var minLength = overrideMin
        var maxLength = overrideMax

        //the the min and max word length have not been set,
        //assign default values
        if (overrideMin == -1) {
            when (level) {
                1 -> {
                    minLength = easyMinLength
                    maxLength = easyMaxLength
                }
                2 -> {
                    minLength = mediumMinLength
                    maxLength = mediumMaxLength
                }
                3 -> {
                    minLength = hardMinLength
                    maxLength = hardMaxLength
                }
            }
        }
        //id of each lesson is the hashcode of the string containing category+level
        val id = title.plus(level).hashCode()
        val newLesson = Lesson(id, title, imageID, level, minLength, maxLength)

        //if the lesson is level 2 or higher, it will initially be locked
        if (level < 2)
            newLesson.locked = false

        return newLesson
    }

    fun getLessons(): MutableList<Lesson> {
        return myLessons
    }
}