package com.learn.flashLearnTagalog.db

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
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
import com.learn.flashLearnTagalog.other.Constants.KEY_VERSION
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


        fun incrementAppVersion() {
            return firestore.incrementDocumentField(ADMIN_COLLECTION, "appInfo", "version", 1)
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
        suspend fun insertWord(word: Word, language: String) {

            //Log.d(TAG, "id: ${word.id}...${word.type},${word.english}")
            val ref = firestore.getSubDocument(WORD_COLLECTION, "languages", language, word.id)
            if (ref.exists()) {
                Log.d(TAG, "$word has already been added")


//                val newTranslation = word.translations[0]
//                //  Log.d(TAG, "exists, add $newTranslation to translations")
//                val existingWord = ref.toObject<Word>()
//                if (existingWord?.translations?.contains(newTranslation) == false) {
//
//                    // Log.d(TAG, "$newTranslation has not been added yet")
//                    firestore.addItemToSubArray(
//                        WORD_COLLECTION,
//                        "languages",
//                        language,
//                        word.id,
//                        "translations",
//                        newTranslation
//                    )
//                } else {
//                    // Log.d(TAG, "$newTranslation has already been added")
//                }

            } else {
                //Log.d(TAG, "does not exist, add word")
                firestore.addSubDocument(WORD_COLLECTION, "languages", language, word.id, word)
            }

        }

        fun insertAllWords(words: Map<String, Word>, language: String) {
            firestore.batchAdd(WORD_COLLECTION, "languages", language, words)
        }

        suspend fun updateWordInfo(
            id: String,
            language: String,
            category: String?,
            context: String?,
            correctIndex: Int?,
            image: String?,
            incorrectTranslation: Boolean?,
            translations: List<String>?
        ) {

            val currentWord =
                firestore.getSubDocument(WORD_COLLECTION, "languages", language, id)
                    .toObject<Word>()

            Log.d(TAG, "current: $currentWord")


            Log.d(TAG, "category: $category")
            Log.d(TAG, "context: $context")
            Log.d(TAG, "correctIndex: $correctIndex")
            Log.d(TAG, "image: $image")
            Log.d(TAG, "incorrectTranslation: $incorrectTranslation")
            Log.d(TAG, "translations: $translations")

            firestore.updateSubDocument(
                WORD_COLLECTION, "languages", language, id,
                mapOf(
                    "category" to (category ?: currentWord!!.category),
                    "context" to (context ?: currentWord!!.context!!),
                    "correctIndex" to (correctIndex ?: currentWord!!.correctIndex),
                    "incorrectTranslation" to (incorrectTranslation
                        ?: currentWord!!.incorrectTranslation!!),
                    "image" to (image ?: currentWord!!.image!!),
                    "translations" to (translations ?: currentWord!!.translations),
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
            language: String,
            category: String,
            min: Int,
            max: Int,
            limit: Long = 100
        ): List<Word> {

            val f1 = Filter.equalTo("category", category.lowercase())
            val f2 = Filter.greaterThan("length", min)
            val f3 = Filter.lessThanOrEqualTo("length", max)

            val words = firestore.getSelectSubDocuments(
                WORD_COLLECTION,
                "languages",
                language,
                Filter.and(f1, f2, f3),
                "length",
                Query.Direction.ASCENDING,
                min + 1,
                limit
            )
            Log.d(TAG, "reads: ${words.size()}")
            Log.d(TAG, "words: $words")
            Log.d(TAG, "word objects: ${words.toObjects<Word>()}")
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
//                val length = w.translation.length
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

        fun setInCorrect(id: String) {
            firestore.updateDocument(WORD_COLLECTION, id, mapOf("incorrectTranslation" to true))
        }

        suspend fun getIncorrectWords() {
            firestore.getSelectDocuments(
                WORD_COLLECTION,
                Filter.equalTo("incorrectTranslation", true)
            )

        }

        fun deleteIncorrectWords() {
            firestore.deleteDocumentsEqualTo(WORD_COLLECTION, "incorrectTranslation", true)
        }

        fun deleteWord(wordId: String, language: String) {
            firestore.deleteSubDocument(WORD_COLLECTION, "languages", language, wordId)
        }

        suspend fun updateWordCategory(language: String, wordID: String, category: String) {
            val documentRef =
                firestore.getSubDocumentRef(WORD_COLLECTION, "languages", language, wordID)

            documentRef.update("category", category)
                .addOnSuccessListener {
                    Log.d(TAG, "$wordID category updated to $category")
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "could not update $wordID category, code: $e")
                }
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

        suspend fun getAllLessons(lessonSource: String, language: String): List<Lesson> {

            val lessons = firestore.getSelectSubDocuments(
                LESSON_COLLECTION, lessonSource, language,
                order = "level",
                direction = Query.Direction.ASCENDING
            )

            Log.d(TAG, "getting lessons")
            return lessons.toObjects()
        }

        //  @Query("SELECT COUNT(*) FROM lesson_table")
        suspend fun getLessonCount() {

            firestore.getCollectionCount(LESSON_COLLECTION)
        }

        //TODO: this
        suspend fun getLessonWordCount(lessonCategory: String, min: Int, max: Int): Int {
            val list = getAllWordsForLesson(
                //TODO: replace with sharedpref,
                "tagalog", lessonCategory, min, max
            ).toMutableList()

            return list.size
        }


        suspend fun getLessonIDsByLevel(
            level: Int,
            lessonSource: String,
            language: String
        ): List<Lesson> {

            return firestore.getSelectSubDocuments(
                LESSON_COLLECTION,
                lessonSource,
                language,
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
        suspend fun getLessonByID(
            lessonId: String,
            lessonSource: String,
            language: String
        ): Lesson {
            return firestore.getSubDocument(LESSON_COLLECTION, lessonSource, language, lessonId)
                .toObject<Lesson>()!!
        }

        //  @Insert
        fun insertAllLessons(lessons: Map<String, Lesson>, lessonSource: String, language: String) {
            firestore.batchAdd(LESSON_COLLECTION, lessonSource, language, lessons)

        }

        //  @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertLesson(lesson: Lesson) {
            firestore.addDocument(LESSON_COLLECTION, lesson.id, lesson)

        }

        //TODO: choose what will be updated
        //  @Query("UPDATE lesson_table SET id = :newID WHERE category = :category AND level == :level")
        suspend fun batchUpdateLessons(
            source: String,
            language: String,
            lessons: MutableList<Lesson>,
            categoriesToUpdate: List<Int>
        ) {

            val collectionRef = firestore.getSubCollection(LESSON_COLLECTION, source, language)

            val db = Firebase.firestore
            val batch = db.batch()

            for (l in lessons) {

                when {
                    //0 = categories
                    categoriesToUpdate.contains(0) -> batch.update(
                        collectionRef.document(l.id),
                        "category",
                        l.category
                    )
                    //1 = image
                    categoriesToUpdate.contains(1) -> batch.update(
                        collectionRef.document(l.id),
                        "image",
                        l.image
                    )
                    //2 = maxLength
                    categoriesToUpdate.contains(2) -> batch.update(
                        collectionRef.document(l.id),
                        "maxLength",
                        l.maxLength
                    )
                    //3 = maxLines
                    categoriesToUpdate.contains(3) -> batch.update(
                        collectionRef.document(l.id),
                        "maxLines",
                        l.maxLines
                    )
                    //4 = minLength
                    categoriesToUpdate.contains(4) -> batch.update(
                        collectionRef.document(l.id),
                        "minLength",
                        l.minLength
                    )
                    //5 = wordCount
                    categoriesToUpdate.contains(5) -> batch.update(
                        collectionRef.document(l.id),
                        "wordCount",
                        l.wordCount
                    )
                }

            }

            batch.commit().addOnSuccessListener {
                Log.d(TAG, "woohoo")
            }
        }

        fun updateLesson(
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

            val appVersion = getAppVersion().toInt()
            var updateSavedLessons = true
            var dbLessonsEmpty = false
            // var lessonsEmpty = false
            val unlockedJSON = "unlockedLessons.json"
            val practicedJSON = "practicedLessons.json"
            val passedJSON = "passedLessons.json"

            val savedLessons = JsonUtility.getSavedLessons(activity)


            val update = activity.getPreferences(Context.MODE_PRIVATE)
                .getInt(KEY_VERSION, 0) < appVersion


            if (update || savedLessons.isEmpty() || savedLessons[0].category == "") {
                Log.d(TAG, "SAVED LESSONS DO NOT EXIST")

                if (savedLessons.isNotEmpty()) {
                    Log.d(TAG, "SAVED LESSONS ARE FROM PREVIOUS VERSIONS")
                }

                updateSavedLessons = false
                activity.getPreferences(Context.MODE_PRIVATE).edit()
                    .putBoolean(Constants.KEY_GATHERING_LESSONS, false).apply()

                val lessons = getAllLessons(
                    //TODO: replace with shared pref
                    "flash_learn", "tagalog"
                )

                Log.d(TAG, "lessons: $lessons")

                dbLessonsEmpty = lessons.isEmpty()

                JsonUtility.writeJSON(activity, "savedLessons.json", lessons, false)

                activity.getPreferences(Context.MODE_PRIVATE).edit()
                    .putBoolean(Constants.KEY_GATHERING_LESSONS, true).apply()

            } else {
                Log.d(TAG, "SAVED LESSONS EXIST")
                //   Log.d(TAG, "SAVED LESSONS: ${JsonUtility.getSavedLessons(activity)}")
            }

            if (!dbLessonsEmpty) {
                Log.d(TAG, "DB LESSONS ARE NOT EMPTY")
                val unlocked =
                    JsonUtility.getUserDataList(activity, unlockedJSON)

                if (unlocked.isNotEmpty()) {
                    Log.d(TAG, "UNLOCKED LESSONS ARE NOT EMPTY")
                    //Log.d(TAG, "$unlocked")
                    TempListUtility.unlockedLessons = unlocked
                } else {
                    Log.d(TAG, "UNLOCKED LESSONS EMPTY")
                    val lessons =
                        getLessonIDsByLevel(
                            1, //TODO: replace with shared pref
                            "flash_learn", "tagalog"
                        )

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

                    Log.d(TAG, "appVersion: $appVersion")
                    if (user.currentVersion < appVersion) {
                        TempListUtility.viewedWords.clear()
                        TempListUtility.viewedLessons.clear()
                        user.currentVersion = appVersion
                        if (updateSavedLessons) {
                            val userScope = CoroutineScope(Job() + Dispatchers.Main)
                            userScope.launch {
                                val lessons = getAllLessons(
                                    //TODO: replace with shrared pref
                                    "flash_learn", "tagalog"
                                ).toMutableList()
                                JsonUtility.writeJSON(activity, "savedLessons.json", lessons, true)
                                Log.d(TAG, "GET ONE")
                                userScope.cancel()
                            }
                        }
                    }
                    updateUserData(user)
                } else {
                    Log.d(TAG, "THERE IS NOT A USER")
                    //reset viewed words
                    if (update) {
                        Log.d(TAG, "reset")
                        TempListUtility.viewedWords.clear()
                        activity.getPreferences(Context.MODE_PRIVATE).edit()
                            .putInt(KEY_VERSION, appVersion).apply()

                        //TODO: sharedpref
                        val language = "tagalog"
                        if (TempListUtility.viewedLessons.isNotEmpty()) {
                            for (l in TempListUtility.viewedLessons) {

                                Log.d(TAG, "l: $l")
                                val lesson = getLessonByID(
                                    l,
                                    "flash_learn", language
                                )
                                Log.d(TAG, "category: ${lesson.category}")
                                val list = getAllWordsForLesson(
                                    language,
                                    lesson.category,
                                    lesson.minLength,
                                    lesson.maxLength
                                )

                                if (list.isNotEmpty()) {
                                    TempListUtility.viewedWords[l] = list
                                }
                            }
                        }
                    }
                }

                if (currentUnlocked.isNotEmpty() && currentUnlocked != unlocked) {
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
            } else {
                Log.d(TAG, "DB LESSONS ARE EMPTY, NOTHING TO SAVE")
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
