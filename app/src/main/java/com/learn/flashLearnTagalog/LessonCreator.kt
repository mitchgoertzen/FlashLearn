package com.learn.flashLearnTagalog

import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel

class LessonCreator(var init : Boolean, var viewModel: MainViewModel) {

    private val myLessons: MutableList<Lesson> = mutableListOf()

    //if a lesson's min or max word length have not been set manually,
    //they will default to these values
    private val easyMinLength = 0
    private val easyMaxLength = 6

    private val mediumMinLength = 6
    private val mediumMaxLength = 9

    private val hardMinLength = 9
    private val hardMaxLength = 100

    //private var oldID = 1

    //lessons created here
    init {

        val lesson1 =
            Lesson("Custom\nLesson0".hashCode(), "Custom\nLesson", R.drawable.custom, 0, -1, -1, 2)
        lesson1.locked = false
        myLessons.add(lesson1)

        myLessons.add(createLessonObject(1, "Animals", R.drawable.dog, 0, 4))
        myLessons.add(createLessonObject(2, "Animals", R.drawable.dog, 4, 5))
        myLessons.add(createLessonObject(3, "Animals", R.drawable.dog, 5, 6))
        myLessons.add(createLessonObject(4, "Animals", R.drawable.dog, 6, 7))
        myLessons.add(createLessonObject(5, "Animals", R.drawable.dog, 7, 10))
        myLessons.add(createLessonObject(6, "Animals", R.drawable.dog, 10, 30))

        myLessons.add(createLessonObject(1, "Geography", R.drawable.mountain, 0, 5))
        myLessons.add(createLessonObject(2, "Geography", R.drawable.mountain, 5, 7))
        myLessons.add(createLessonObject(3, "Geography", R.drawable.mountain, 7, 30))

        myLessons.add(createLessonObject(1, "Nature", R.drawable.nature, 0, 5))
        myLessons.add(createLessonObject(2, "Nature", R.drawable.nature, 5, 30))

        myLessons.add(createLessonObject(1, "Around Town", R.drawable.city, 0, 5))
        myLessons.add(createLessonObject(2, "Around Town",R.drawable.city, 5, 7))
        myLessons.add(createLessonObject(3, "Around Town", R.drawable.city, 7, 8))
        myLessons.add(createLessonObject(4, "Around Town", R.drawable.city, 8, 30))

        myLessons.add(createLessonObject(1, "Countries", R.drawable.world, 0, 30))

        myLessons.add(createLessonObject(1, "Food & Drinks", R.drawable.food_drink, 0, 4))
        myLessons.add(createLessonObject(2, "Food & Drinks", R.drawable.food_drink, 4, 5))
        myLessons.add(createLessonObject(3, "Food & Drinks", R.drawable.food_drink, 5, 6))
        myLessons.add(createLessonObject(4, "Food & Drinks", R.drawable.food_drink, 6, 8))
        myLessons.add(createLessonObject(5, "Food & Drinks", R.drawable.food_drink, 8, 30))

        myLessons.add(createLessonObject(1, "Kitchen", R.drawable.kitchen, 0, 6))
        myLessons.add(createLessonObject(2, "Kitchen", R.drawable.kitchen, 6, 30))

        myLessons.add(createLessonObject(1, "Bathroom", R.drawable.bathroom, 0, 30))

        myLessons.add(createLessonObject(1, "Bedroom", R.drawable.bedroom, 0, 30))

        myLessons.add(createLessonObject(1, "Living Room", R.drawable.living_room, 0, 30))

        myLessons.add(createLessonObject(1, "In The Yard", R.drawable.yard, 0, 30))

        myLessons.add(createLessonObject(1, "Body", R.drawable.body, 0, 4))
        myLessons.add(createLessonObject(2, "Body", R.drawable.body, 4, 5))
        myLessons.add(createLessonObject(3, "Body", R.drawable.body, 5, 7))
        myLessons.add(createLessonObject(4, "Body", R.drawable.body, 7, 30))

        myLessons.add(createLessonObject(1, "People", R.drawable.people, 0, 7))
        myLessons.add(createLessonObject(2, "People", R.drawable.people, 7, 30))

        myLessons.add(createLessonObject(1, "Occupations", R.drawable.occupations, 0, 5))
        myLessons.add(createLessonObject(2, "Occupations", R.drawable.occupations, 5, 6))
        myLessons.add(createLessonObject(3, "Occupations", R.drawable.occupations, 6, 7))
        myLessons.add(createLessonObject(4, "Occupations", R.drawable.occupations, 7, 9))
        myLessons.add(createLessonObject(5, "Occupations", R.drawable.occupations, 9, 10))
        myLessons.add(createLessonObject(6, "Occupations", R.drawable.occupations, 10, 30))

        myLessons.add(createLessonObject(1, "Date & Time", R.drawable.date_time, 0, 5))
        myLessons.add(createLessonObject(2, "Date & Time", R.drawable.date_time, 5, 30))

        myLessons.add(createLessonObject(1, "Colours", R.drawable.colours, 0, 30))

        myLessons.add(createLessonObject(1, "Numbers", R.drawable.numbers, 0, 7))
        myLessons.add(createLessonObject(2, "Numbers", R.drawable.numbers, 7, 30))

//        myLessons.add(createLesson(1,"Speech", R.drawable.hand_wave, 0,4))
//        myLessons.add(createLesson(2,"Speech", R.drawable.hand_wave,4,100))
    }

    private fun createLessonObject(
        level: Int,
        category: String,
        imageID: Int,
        overrideMin: Int,
        overrideMax: Int
    ): Lesson {
        var minLength = overrideMin
        var maxLength = overrideMax

        val numOfWords = category.split("\\s+".toRegex()).size


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
        val id = category.plus(level).hashCode()
        val newLesson = Lesson(id, category, imageID, level, minLength, maxLength, (numOfWords + 1))

        if(!init)
            newLesson.difficulty = getLessonDifficulty(newLesson.category.lowercase(), newLesson.minLength, newLesson.maxLength)

        //if the lesson is level 2 or higher, it will initially be locked
        if (level < 2)
            newLesson.locked = false

        return newLesson
    }

//    fun setLessonDifficulties(){
//        for(l in myLessons){
//            l.difficulty = getLessonDifficulty(l.category.lowercase(), l.minLength, l.maxLength)
//        }
//    }

    private fun getLessonDifficulty(category : String, minLength : Int, maxLength : Int) : Int {
        val lessonList = viewModel.getLessonWordList(category, minLength, maxLength)

        var difficulty = 5

        println("$category: ${lessonList.size}")
        if(lessonList.isNotEmpty()){
            var sum = 0

            for(word in lessonList){
                sum += word.tagalog.length
            }

            when (sum / lessonList.size) {
                in 0..4 -> difficulty = 1
                in 5..6 -> difficulty = 2
                in 7..9 -> difficulty = 3
                in 9..10 -> difficulty = 4
            }

        }else{
            difficulty = -1
        }

        println("$difficulty")
        return difficulty
    }

    fun getLessons(): MutableList<Lesson> {
        return myLessons
    }

}