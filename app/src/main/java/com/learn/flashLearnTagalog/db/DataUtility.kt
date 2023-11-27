package com.learn.flashLearnTagalog.db

import com.google.firebase.firestore.toObject
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.Word

class DataUtility {
    companion object {
        private val firestore = FirestoreUtility()

        //SORT BY ADD,GET,ETC...

        /*************************************_USERS_**********************************************/
        /*************************************_WORDS_**********************************************/
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun insertWord(word: Word) {
            firestore.addDocument("words", (word.id).toString(), word)
        }

        fun insertAllWords(words: Map<String, Word>) {
            firestore.batchAdd("words", words)
        }

        fun updateWordInfo(updatedWord: Word) {
            firestore.updateDocument("words", updatedWord.id.toString(),
                mapOf(
                    "category" to updatedWord.category,
                    "type" to updatedWord.type,
                    "correctTranslation" to updatedWord.correctTranslation!!,
                    "image" to updatedWord.image!!,
                    "uncommon" to updatedWord.uncommon!!
                )

            )
        }

        fun updateWordTranslation(previousId: String, updatedWord: Word) {
            //get wordStats of previousId
            //add to id of updated word
            firestore.deleteDocument("words", previousId)
            firestore.addDocument("words", updatedWord.id.toString(), updatedWord)
        }

        fun getWord(wordId: String): Word? {

            return firestore.getDocument("words", wordId).toObject<Word>()
        }

        fun getWordCount(): Int {
            return firestore.getCollectionCount("words").toInt()
        }


        //   @Query("SELECT * FROM word_table ORDER BY english DESC")
        fun getAllWords() {
            //firestore.get
        }


        //  @Query("SELECT * FROM word_table WHERE word_practiced == 1 ORDER BY LENGTH(tagalog) DESC")
        fun getAllPracticedWords() {


        }


        //  @Query("SELECT * FROM word_table WHERE category == :category")
        fun getAllWordsForLesson(category: String) {
            firestore.getDocumentsEqualTo("words", "category", category)
        }

        // @Query("SELECT * FROM word_table WHERE category == :category AND LENGTH(tagalog) > :min AND LENGTH(tagalog) <= :max")
        fun getLessonWordList(category: String, min: Int, max: Int) {
            firestore.getDocumentsEqualTo("words", "category", category)
            //from this get results within length bounds
        }

        //  @Query("SELECT * FROM word_table WHERE (word_practiced == :practiced OR word_practiced == 1) AND (LENGTH(tagalog) >= :minLength AND LENGTH(tagalog) <= :maxLength)")
        fun getWordsByDifficulty(practiced: Int, minLength: Int, maxLength: Int) {


        }

        fun getDictionaryWords(order: String, offset: Int, limit: Long) {
            firestore.getOrderedDocumentBlock("words", order, offset, limit)
        }

        // @Query("UPDATE word_table SET correctTranslation = 1 WHERE id == :id")
        fun setCorrect(id: Int) {


        }

        //  @Query("SELECT COUNT(*) FROM word_table WHERE correctTranslation = 0")
        fun getIncorrectWords() {


        }

        //    @Query("DELETE FROM word_table WHERE correctTranslation = 0")
        fun deleteIncorrectWords() {


        }

        fun deleteWord(wordId: String) {
            firestore.deleteDocument("words", wordId)
        }


        /*************************************_WORDSTATS_******************************************/
        //    @Query("UPDATE word_table SET word_practiced = :value WHERE id == :wordID")
        fun updatePractice(wordID: Int, value: Boolean) {


        }

        //     @Query("SELECT word_practiced FROM word_table WHERE id == :wordID")
        fun getPractice(wordID: Int) {


        }

        //    @Query("UPDATE word_table SET timesCorrect = (timesCorrect + :result), timesAnswered = (timesAnswered + 1), previousResult = :result WHERE id == :wordID")
        fun answerWord(wordID: Int, result: Boolean) {


        }

        //  @Query("UPDATE word_table SET timesSkipped = (timesSkipped + 1) WHERE id == :wordID")
        fun skipWord(wordID: Int) {


        }

        //  @Query("UPDATE word_table SET timesFlipped = (timesFlipped + 1) WHERE id == :wordID")
        fun flipWord(wordID: Int) {


        }

        // @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect DESC LIMIT 5")
        fun getMostCorrect() {


        }

        //  @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesAnswered - timesCorrect DESC LIMIT 5")
        fun getLeastCorrect() {


        }

        //   @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered DESC LIMIT 5")
        fun getBest() {


        }

        //  @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered ASC LIMIT 5")
        fun getWorst() {


        }

        //  @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesAnswered DESC LIMIT 5")
        fun getMostEncountered() {


        }

        //   @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesSkipped DESC LIMIT 5")
        fun getMostSkipped() {


        }

        //      @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesFlipped DESC LIMIT 5")
        fun getMostFlipped() {


        }

        /*************************************_LESSONS_********************************************/


        //  @Query("SELECT * FROM lesson_table ORDER BY level ASC")
        fun getAllLessons() {


        }

        //  @Query("SELECT COUNT(*) FROM lesson_table")
        fun getLessonCount() {


        }

        //   @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE id = :id)")
        fun lessonExists(id: Int) {


        }

        //   @Query("SELECT * FROM lesson_table WHERE category = :category AND level = :level")
        fun getLessonByData(category: String, level: Int) {


        }

        //   @Query("SELECT * FROM lesson_table WHERE id = :id")
        fun getLessonByID(id: Int) {


        }

        //  @Insert
        fun insertAllLessons(lessons: List<Lesson>) {


        }

        //  @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertLesson(lesson: Lesson) {


        }

        //  @Query("UPDATE lesson_table SET id = :newID WHERE category = :category AND level == :level")
        fun updateLessonID(category: String, level: Int, newID: Int) {


        }

        //        @Query(
//            "UPDATE lesson_table SET category = :newTitle, imageID = :newImageID, level = :newLevel " +
//                    ", minLength = :newMin, maxLength = :newMax, maxLines = :newLines, difficulty = :newDifficulty, practice_completed = :practiceCompleted, test_passed = :testPassed, locked = :locked WHERE id == :id"
//        )
        fun updateLessonInfo(
            id: Int,
            newTitle: String,
            newImageID: Int,
            newLevel: Int,
            newMin: Int,
            newMax: Int,
            newLines: Int,
            newDifficulty: Int,
            practiceCompleted: Boolean,
            testPassed: Boolean,
            locked: Boolean
        ) {


        }


        //   @Query("DELETE FROM lesson_table WHERE category = :category AND level = :level")
        suspend fun deleteLesson(category: String, level: Int) {


        }

        /*************************************_LESSONSTATS_****************************************/

        // @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE category = :category AND level = :level)")
        fun lessonCategoryLevelExists(category: String, level: Int) {


        }

        //  @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE category = :category AND level = (:level - 1) AND test_passed = 1)")
        fun previousTestPassed(category: String, level: Int) {


        }


        // @Query("UPDATE lesson_table SET locked = 0 WHERE category = :category AND level == (:level + 1)")
        fun unlockNextLesson(category: String, level: Int) {


        }

        //   @Query("UPDATE lesson_table SET practice_completed = 1 WHERE id == :id")
        fun completePractice(id: Int) {


        }

        // @Query("UPDATE lesson_table SET test_passed = 1 WHERE id == :id")
        fun passTest(id: Int) {


        }


        //  @Query("DELETE FROM lesson_table")
        fun nukeLessons() {


        }

        fun nukeTable() {


        }
    }
}