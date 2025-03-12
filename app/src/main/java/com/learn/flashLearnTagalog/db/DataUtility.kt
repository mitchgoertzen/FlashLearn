package com.learn.flashLearnTagalog.db

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.Organization
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.User
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.data.WordStats
import com.learn.flashLearnTagalog.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DataUtility {


    companion object {

        const val ADMIN_COLLECTION = "admin"
        const val USER_COLLECTION = "users"
        const val WORD_COLLECTION = "words"
        const val LESSON_COLLECTION = "lessons"
        const val WORDSTAT_COLLECTION = "wordStats"
        const val LESSONSTAT_COLLECTION = "lessonStats"
        const val ORGANIZATION_COLLECTION = "organizations"

        private val firestore = FirestoreUtility()

        private val auth = Firebase.auth

        val scope = CoroutineScope(Job() + Dispatchers.Main)

        //SORT BY ADD,GET,ETC????
        /****************************************_ADD_*************************************************/
        /****************************************_GET_*************************************************/
        /***************************************_UPDATE_***********************************************/
        /***************************************_UPDATE_***********************************************/

        /*************************************_ADMIN_**********************************************/
        private suspend fun getAppVersion(): Long {
            return firestore.getDocument(ADMIN_COLLECTION, "appInfo")?.get("version") as Long
        }

        /*************************************_USERS_**********************************************/
        fun addUser(user: User) {
            if (auth.currentUser != null) {
                firestore.addDocument(USER_COLLECTION, auth.currentUser!!.uid, user)
            }
        }

        fun updateUserData(user: User) {
            if (auth.currentUser != null) {
                firestore.updateDocument(
                    USER_COLLECTION, auth.currentUser!!.uid, mapOf(
                        "unlockedLessons" to user.unlockedLessons,
                        "practicedLessons" to user.practicedLessons,
                        "passedLessons" to user.passedLessons,
                        "currentVersion" to user.currentVersion
                    )
                )
            }

        }


        fun addUnlockedLesson(lessonId: String) {
            //TODO: make shared pref
            val uid = auth.currentUser!!.uid
            firestore.addItemToArray(USER_COLLECTION, uid, "unlockedLessons", lessonId)
        }


        fun addPracticedLesson(lessonId: String) {
            val uid = auth.currentUser!!.uid
            firestore.addItemToArray(USER_COLLECTION, uid, "practicedLessons", lessonId)
        }

        fun addPassedLesson(lessonId: String) {
            val uid = auth.currentUser!!.uid
            firestore.addItemToArray(USER_COLLECTION, uid, "passedLessons", lessonId)
        }


        suspend fun getCurrentUser(): User? {

            return if (auth.currentUser != null) {
                Log.d(TAG, "ID: ${auth.currentUser!!.uid}")
                firestore.getDocument(USER_COLLECTION, auth.currentUser!!.uid)!!
                    .toObject<User>()
            } else {
                null
            }
        }

        suspend fun getUserCount(): Int {
            return firestore.getCollectionCount(USER_COLLECTION).toInt()
        }


        suspend fun getUsersUnlockedLessons(userId: String) {
            //TODO: firestore.getDocument(USER_COLLECTION, userId) neeeedd????
        }

        fun deleteUser() {

        }


        /*************************************_WORDS_**********************************************/
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun insertWord(word: Word) {
            firestore.addDocument(WORD_COLLECTION, (word.id).toString(), word)
        }

        fun insertAllWords(words: Map<String, Word>) {
            firestore.batchAdd(WORD_COLLECTION, words)
        }

        fun updateWordInfo(updatedWord: Word) {
            firestore.updateDocument(
                "words", updatedWord.id.toString(),
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
            firestore.deleteDocument(WORD_COLLECTION, previousId)
            firestore.addDocument(WORD_COLLECTION, updatedWord.id.toString(), updatedWord)
        }

        suspend fun getWord(wordId: String): Word? {
            return firestore.getDocument(WORD_COLLECTION, wordId)!!.toObject<Word>()
        }

        suspend fun getWordCount(): Int {
            return firestore.getCollectionCount(WORD_COLLECTION).toInt()
        }


        suspend fun getAllWords(): List<Word> {
            return firestore.getSelectDocuments(WORD_COLLECTION, order = "english").toObjects()
        }


        //  @Query("SELECT * FROM word_table WHERE word_practiced == 1 ORDER BY LENGTH(tagalog) DESC")
        suspend fun getAllPracticedWords() {

            //get current user
            //get word stats wherer practice == true
            //get all word documents with matching id

        }

        suspend fun getAllWordsForLesson(
            category: String,
            min: Int,
            limit: Long = 100
        ): List<Word> {

            val words = firestore.getSelectDocuments(
                WORD_COLLECTION,
                Filter.equalTo("category", category),
                "length",
                Query.Direction.ASCENDING,
                min + 1,
                limit
            )

            //Log.d(TAG, "reads: ${words.size()}")

            return words.toObjects()
        }

//        // @Query("SELECT * FROM word_table WHERE category == :category AND LENGTH(tagalog) > :min AND LENGTH(tagalog) <= :max")
//        suspend fun getLessonWordList(category: String, min: Int, max: Int): List<Word> {
//            val list =
//                firestore.getSelectDocuments(WORD_COLLECTION, Filter.equalTo("category", category))
//                    .toObjects<Word>()
//            Log.d(TAG, "reads used: ${list.size}")
//            val lessonWords = mutableListOf<Word>()
//            for (w in list) {
//                val length = w.tagalog.length
//                if (length in (min + 1)..max)
//                    lessonWords.add(w)
//
//            }
//            return lessonWords
//        }

        //  @Query("SELECT * FROM word_table WHERE (word_practiced == :practiced OR word_practiced == 1) AND (LENGTH(tagalog) >= :minLength AND LENGTH(tagalog) <= :maxLength)")
        suspend fun getWordsByDifficulty(
            practiced: Int,
            minLength: Int,
            maxLength: Int
        ): List<Word> {
            val list = listOf<Word>()
//            firestore.getSelectDocuments("words",
//                Filter.and(Filter.))
            /*
            * if practiced
            * getAllpracticed words
            * else getallwords
            * filter out by length
            */
            return list
        }

        suspend fun getDictionaryWords(order: String, limit: Long): List<Word> {
            return firestore.getSelectDocuments(
                WORD_COLLECTION,
                order = order,
                limit = limit
            ).toObjects()
        }

        fun setCorrect(id: String) {
            firestore.updateDocument(WORD_COLLECTION, id, mapOf("correctTranslation" to true))
        }

        suspend fun getIncorrectWords() {
            firestore.getSelectDocuments(
                WORD_COLLECTION,
                Filter.equalTo("correctTranslation", false)
            )

        }

        fun deleteIncorrectWords() {
            firestore.deleteDocumentsEqualTo(WORD_COLLECTION, "correctTranslation", false)
        }

        fun deleteWord(wordId: String) {
            firestore.deleteDocument(WORD_COLLECTION, wordId)
        }


        /*************************************_WORDSTATS_******************************************/
        fun updatePractice(wordID: String, value: Boolean) {
            val userId = ""
            //get user
            firestore.updateSubDocument(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                wordID,
                mapOf(
                    "practiced" to true
                )
            )

        }

        suspend fun getOrganization(orgId: String): Organization? {
            return firestore.getDocument(ORGANIZATION_COLLECTION, orgId)!!.toObject<Organization>()
        }

        suspend fun getPractice(wordID: String) {
            val userId = ""
            //get user
            firestore.getSubDocument(USER_COLLECTION, userId, WORDSTAT_COLLECTION, wordID)
                .toObject<WordStats>()!!.practiced

        }

        fun answerWord(wordID: String, result: Boolean) {
            val userId = ""

            if (result) {
                firestore.incrementSubDocumentField(
                    USER_COLLECTION,
                    userId,
                    WORDSTAT_COLLECTION,
                    wordID,
                    "timesCorrect",
                    1
                )
            }

            firestore.incrementSubDocumentField(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                wordID,
                "timesAnswered",
                1
            )
        }

        fun skipWord(wordID: String) {
            val userId = ""
            firestore.incrementSubDocumentField(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                wordID,
                "timesSkipped",
                1
            )
        }

        fun flipWord(wordID: String) {
            val userId = ""
            firestore.incrementSubDocumentField(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                wordID,
                "timesFlipped",
                1
            )
        }

        suspend fun getMostCorrect(limit: Long): List<WordStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                Filter.greaterThan("timesAnswered", 0),
                "timesCorrect",
                limit = limit
            ).toObjects()

        }

        /*TODO: new field for timesAnswered - timesCorrect

        //  @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesAnswered - timesCorrect DESC LIMIT 5")
        fun getLeastCorrect(limit: Long) {
            val userId = ""
            firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                Filter.greaterThan("timesAnswered", 0),
                "timesCorrect",
                limit = limit
            )

        }

        //   @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered DESC LIMIT 5")
        fun getBest() {


        }

        //  @Query("SELECT * FROM word_table WHERE timesAnswered > 0 ORDER BY timesCorrect/timesAnswered ASC LIMIT 5")
        fun getWorst() {


        }

        */

        suspend fun getMostEncountered(limit: Long): List<WordStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                Filter.greaterThan("timesAnswered", 0),
                "timesAnswered",
                limit = limit
            ).toObjects()

        }

        suspend fun getMostSkipped(limit: Long): List<WordStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                Filter.greaterThan("timesAnswered", 0),
                "timesSkipped",
                limit = limit
            ).toObjects()

        }

        suspend fun getMostFlipped(limit: Long): List<WordStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                WORDSTAT_COLLECTION,
                Filter.greaterThan("timesAnswered", 0),
                "timesFlipped",
                limit = limit
            ).toObjects()

        }

        /*************************************_LESSONS_********************************************/

        suspend fun getAllLessons(): List<Lesson> {
            val lessons = firestore.getSelectDocuments(
                LESSON_COLLECTION,
                order = "level",
                direction = Query.Direction.ASCENDING
            )
            return lessons.toObjects()
        }

        //  @Query("SELECT COUNT(*) FROM lesson_table")
        suspend fun getLessonCount() {

            firestore.getCollectionCount(LESSON_COLLECTION)
        }

        //TODO: this
        suspend fun getLessonWordCount(lessonCategory: String, min: Int, max: Int): Int {
            val list = getAllWordsForLesson(lessonCategory, min).toMutableList()

//            val list = firestore.getSelectDocuments(
//                WORD_COLLECTION,
//                Filter.equalTo("category", lessonCategory)
//            ).toObjects<Word>()
//            Log.d(TAG, "Category: $lessonCategory")
//            Log.d(TAG, "size: ${list.size}")
//
//            Log.d(TAG, "min: $min")
//            Log.d(TAG, "max: $max")
            var count = 0

            for (w in list) {
                val length = w.tagalog.length

                if (length <= max) {
                    count++
                } else {
                    break
                }
//
//                Log.d(TAG, "length: $length")
//                if (length in (min + 1)..max) {
//                    count++
//                }
            }
            return count
        }


        suspend fun getLessonIDsByLevel(level: Int): List<Lesson> {
            return firestore.getSelectDocuments(
                LESSON_COLLECTION,
                Filter.equalTo("level", level),
                direction = Query.Direction.ASCENDING
            ).toObjects()
        }

        //   @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE id = :id)")
        suspend fun lessonExists(lessonId: String) {

            firestore.getDocument(LESSON_COLLECTION, lessonId) == null
        }

        //TODO: name
        //   @Query("SELECT * FROM lesson_table WHERE category = :category AND level = :level")
        suspend fun getLessonByData(category: String, level: Int) {

            firestore.getSelectDocuments(
                LESSON_COLLECTION,
                Filter.and(Filter.equalTo("category", category), Filter.equalTo("level", level))
            )
        }

        //   @Query("SELECT * FROM lesson_table WHERE id = :id")
        suspend fun getLessonByID(lessonId: String) {
            firestore.getDocument(LESSON_COLLECTION, lessonId)
        }

        //  @Insert
        fun insertAllLessons(lessons: Map<String, Lesson>) {
            firestore.batchAdd(LESSON_COLLECTION, lessons)

        }

        //  @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertLesson(lesson: Lesson) {
            firestore.addDocument(LESSON_COLLECTION, lesson.id, lesson)

        }

        //TODO: choose what will be updated
        //  @Query("UPDATE lesson_table SET id = :newID WHERE category = :category AND level == :level")
        fun updateLesson(
            previousLesson: Lesson,
            newCategory: String,
            newLevel: Int?,
            min: Int?,
            max: Int?,
            lines: Int?
        ) {

            val userId = ""
            firestore.deleteDocument(LESSON_COLLECTION, previousLesson.id)

            val newLesson = Lesson(
                newCategory,
                newLevel ?: previousLesson.level,
                min ?: previousLesson.minLength,
                max ?: previousLesson.maxLength,
                previousLesson.wordCount,
                lines ?: previousLesson.maxLines,
                previousLesson.image
            )


            firestore.addDocument(LESSON_COLLECTION, newLesson.id, newLesson)


            scope.launch {
                // New coroutine that can call suspend functions
                val stats = firestore.getSubDocument(
                    USER_COLLECTION, userId, LESSONSTAT_COLLECTION, previousLesson.id
                )
                    .toObject<LessonStats>()

                firestore.deleteSubDocument(
                    USER_COLLECTION, userId, LESSONSTAT_COLLECTION, previousLesson.id
                )

                firestore.addSubDocument(
                    USER_COLLECTION, userId, LESSONSTAT_COLLECTION, newLesson.id, stats!!
                )
            }

        }


        fun updateLessonInfo(
            id: String,
            newImageID: Int,
            newMin: Int,
            newMax: Int,
            newLines: Int,
            newDifficulty: Int,
        ) {
            firestore.updateDocument(
                LESSON_COLLECTION,
                id,
                mapOf(
                    "difficulty" to newDifficulty,
                    "image" to newImageID,
                    "minLength" to newMin,
                    "maxLength" to newMax,
                    "maxLines" to newLines
                )
            )

        }


        fun deleteLesson(lessonId: String) {
            firestore.deleteDocument(LESSON_COLLECTION, lessonId)
        }

        /*************************************_LESSONSTATS_****************************************/

        suspend fun getLessonStats(lessonId: String): LessonStats {
            val userId = ""
            return firestore.getSubDocument(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                lessonId
            ).toObject<LessonStats>()!!
        }

        suspend fun getUnlockedLessons(lessonId: String): List<LessonStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                Filter.equalTo("locked", false)
            ).toObjects()
        }

        suspend fun getPracticedLessons(lessonId: String): List<LessonStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                Filter.equalTo("practiceCompleted", true)
            ).toObjects()
        }

        suspend fun getPassedLessons(lessonId: String): List<LessonStats> {
            val userId = ""
            return firestore.getSelectSubDocuments(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                Filter.equalTo("testPassed", true)
            ).toObjects()
        }


        // @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE category = :category AND level = :level)")
        fun lessonCategoryLevelExists(category: String, level: Int) {


        }

        //  @Query("SELECT EXISTS(SELECT * FROM lesson_table WHERE category = :category AND level = (:level - 1) AND test_passed = 1)")
        fun previousTestPassed(category: String, level: Int) {


        }


        // @Query("UPDATE lesson_table SET locked = 0 WHERE category = :category AND level == (:level + 1)")
        fun unlockNextLesson(category: String, level: Int) {
            val userId = ""
            val nextLevel = level + 1
            firestore.updateSubDocument(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                category + "_" + nextLevel,
                mapOf("locked" to false)
            )
        }

        //   @Query("UPDATE lesson_table SET practice_completed = 1 WHERE id == :id")
        fun completePractice(lessonId: String) {
            val userId = ""
            firestore.updateSubDocument(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                lessonId,
                mapOf("practiceCompleted" to true)
            )
        }

        // @Query("UPDATE lesson_table SET test_passed = 1 WHERE id == :id")
        fun passTest(lessonId: String) {
            val userId = ""
            firestore.updateSubDocument(
                USER_COLLECTION,
                userId,
                LESSONSTAT_COLLECTION,
                lessonId,
                mapOf("testCompleted" to true)
            )
        }


        //TODO: cloud functions
        //  @Query("DELETE FROM lesson_table")
        fun nukeLessons() {


        }

        fun nukeWords() {


        }

        /*************************************_LOCAL STORAGE_****************************************/

        suspend fun updateLocalData(
            activity: Activity, signUp: Boolean, rewriteJSON: Boolean
        ) {

            Log.d(TAG, "UPDATING LOCAL DATA")

            var updateLessons = true
            var lessonsEmpty = false

            val unlockedJSON = "unlockedLessons.json"
            val practicedJSON = "practicedLessons.json"
            val passedJSON = "passedLessons.json"

            if (JsonUtility.getSavedLessons(activity).isEmpty()) {
                updateLessons = false
                activity.getPreferences(Context.MODE_PRIVATE).edit()
                    .putBoolean(Constants.KEY_GATHERING_LESSONS, false).apply()
                val lessons = getAllLessons()

                lessonsEmpty = lessons.isEmpty()

                JsonUtility.writeJSON(activity, "savedLessons.json", lessons, false)

                activity.getPreferences(Context.MODE_PRIVATE).edit()
                    .putBoolean(Constants.KEY_GATHERING_LESSONS, true).apply()

            } else {
                Log.d(TAG, "NON-EMPTY LESSONS")
            }

            if (!lessonsEmpty) {

                val unlocked =
                    JsonUtility.getUserDataList(activity, unlockedJSON)
                if (unlocked.isNotEmpty()) {
                    TempListUtility.unlockedLessons = unlocked
                } else {
                    val lessons =
                        getLessonIDsByLevel(1)
                    val unlock = mutableListOf<String>()

                    for (l in lessons) {
                        if (l.level == 1)
                            unlock.add(l.id)
                    }

                    TempListUtility.unlockedLessons = unlock
                    JsonUtility.writeJSON(activity, unlockedJSON, unlock, true)
                }

                if (TempListUtility.practicedLessons.isEmpty()) {
                    TempListUtility.practicedLessons =
                        JsonUtility.getUserDataList(activity, practicedJSON)
                }
                if (TempListUtility.passedLessons.isEmpty()) {
                    TempListUtility.passedLessons =
                        JsonUtility.getUserDataList(activity, passedJSON)
                }
                if (TempListUtility.viewedWords.isEmpty()) {
                    TempListUtility.viewedWords = JsonUtility.getViewedWords(activity)
                }
                if (TempListUtility.viewedLessons.isEmpty()) {
                    TempListUtility.viewedLessons = JsonUtility.getViewedLessons(activity)
                }


                //if user is signed in, local data will be from user
                //otherwise, local data will be from shared folder
                var currentUnlocked = TempListUtility.unlockedLessons
                var currentPracticed = TempListUtility.practicedLessons
                var currentPassed = TempListUtility.passedLessons

                val user = getCurrentUser()
                if (user != null) {

                    Log.d(TAG, "THERE IS A USER")
                    if (!signUp) {

                        Log.d(TAG, "THEY ARE SIGNED IN")
                        val unlocked = currentUnlocked.toSet() + user.unlockedLessons.toSet()
                        var practiced = currentPracticed.toSet() + user.practicedLessons.toSet()
                        var passed = currentPassed.toSet() + user.passedLessons.toSet()

                        currentUnlocked = unlocked.toMutableList()
                        currentPracticed = practiced.toMutableList()
                        currentPassed = passed.toMutableList()
                    } else {
                        Log.d(TAG, "THEY HAVE JUST SIGNED UP")
                    }

                    user.unlockedLessons = currentUnlocked
                    user.practicedLessons = currentPracticed
                    user.passedLessons = currentPassed

                    Log.d(TAG, "version: ${user.currentVersion}")

                    val appVersion = getAppVersion().toInt()
                    Log.d(TAG, "appVersion: $appVersion")
                    if (user.currentVersion < appVersion) {
                        TempListUtility.viewedWords.clear()
                        TempListUtility.viewedLessons.clear()
                        user.currentVersion = appVersion
                        if (updateLessons) {
                            val userScope = CoroutineScope(Job() + Dispatchers.Main)
                            userScope.launch {
                                val lessons = getAllLessons().toMutableList()
                                JsonUtility.writeJSON(activity, "savedLessons.json", lessons, true)
                                Log.d(TAG, "GET ONE")
                                userScope.cancel()
                            }
                        }
                    }
                    updateUserData(user)
                } else {
                    Log.d(TAG, "THERE IS NOT A USER")
                }

                if (currentUnlocked.isNotEmpty()) {
                    populateInternalStorageList(
                        activity,
                        "unlocked",
                        currentUnlocked,
                        unlockedJSON,
                        rewriteJSON
                    )
                }

                if (currentPracticed.isNotEmpty()) {
                    populateInternalStorageList(
                        activity,
                        "practiced",
                        currentPracticed,
                        practicedJSON,
                        rewriteJSON
                    )
                }

                if (currentPassed.isNotEmpty()) {
                    populateInternalStorageList(
                        activity,
                        "passed",
                        currentPassed,
                        passedJSON,
                        rewriteJSON
                    )
                }

            }

        }


        private fun populateInternalStorageList(
            activity: Activity,
            listType: String,
            list: MutableList<String>,
            jsonFile: String,
            rewriteJSON: Boolean
        ) {

            Log.d(TAG, "POPULATING TEMP")

            TempListUtility.setList(listType, list)

            if (rewriteJSON) {
                Log.d(TAG, " AND (RE)POPULATING JSON")
                JsonUtility.writeJSON(
                    activity,
                    jsonFile,
                    list,
                    true
                )
            }

        }


    }
}