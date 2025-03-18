package com.learn.flashLearnTagalog.db

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class FirestoreUtility {
    val db = Firebase.firestore

    /****************************************_GET_*************************************************/
    suspend fun getCollection(collectionId: String): CollectionReference {
        return db.collection(collectionId)
    }

    suspend fun getSubCollection(
        collectionId: String,
        documentId: String,
        subCollectionId: String
    ): CollectionReference {
        return db.collection(collectionId).document(documentId).collection(subCollectionId)
    }

    suspend fun getDocument(collectionId: String, documentId: String): DocumentSnapshot? {

        return db.collection(collectionId).document(documentId).get().await()
    }

    suspend fun getSubDocument(
        collectionId: String, documentId: String,
        subCollectionId: String, subDocumentId: String
    ): DocumentSnapshot {
        return db.collection(collectionId).document(documentId)
            .collection(subCollectionId).document(subDocumentId)
            .get().await()
    }

    suspend fun getAllDocuments(collectionId: String): QuerySnapshot? {
        return db.collection(collectionId).get().await()
    }

    suspend fun getAllSubDocuments(
        collectionId: String, documentId: String,
        subCollectionId: String
    ): QuerySnapshot {
        return db.collection(collectionId).document(documentId)
            .collection(subCollectionId)
            .get().await()
    }

//    fun getDocumentsEqualTo(
//        collectionId: String,
//        field: String,
//        value: Any
//    ): MutableList<DocumentSnapshot> {
//
//        lateinit var query: QuerySnapshot
//        db.collection(collectionId)
//            .whereEqualTo(field, value)
//            .get().addOnSuccessListener { snapshot ->
//                query = snapshot
//            }
//
//        return query.documents
//    }

    //TODO: might not work --> filtered ref as query/ref
    suspend fun getSelectDocuments(
        collectionId: String,
        filter: Filter? = null,
        order: String = "",
        direction: Query.Direction = Query.Direction.DESCENDING,
        start: Any? = null,
        limit: Long = 10000
    ): QuerySnapshot {
        val collectionRef = if (filter != null) {
            if (order != "") {
                if (start != null) {
                    db.collection(collectionId).where(filter).orderBy(order, direction)
                        .startAt(start).limit(limit)
                } else {
                    db.collection(collectionId).where(filter).orderBy(order, direction).limit(limit)
                }
            } else {
                db.collection(collectionId).where(filter).limit(limit)
            }
        } else {
            db.collection(collectionId).limit(limit)
        }


        val words = collectionRef.get().await()
        Log.d(TAG, "reads: ${words.size()}")
        return words
//
//
//        db.collection("lessons")
//            .get()
//            .addOnSuccessListener { documents ->
//
//                Log.d(TAG, "GET SELECT DOCUMENTS 1")
//                for (document in documents) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//
//
//        Log.d(TAG, collectionId)
//        Log.d(TAG, collectionRef.toString())
//
//
//            collectionRef.get()
//                .addOnSuccessListener { snapshot ->
//
//                    Log.d(TAG, "GET SELECT DOCUMENTS 2")
//                    query = snapshot
//                }


    }

    suspend fun getSelectSubDocuments(
        collectionId: String,
        documentId: String,
        subCollectionId: String,
        filter: Filter? = null,
        order: String = "id",
        direction: Query.Direction = Query.Direction.DESCENDING,
        offset: Int = 0,
        limit: Long = 10000
    ): QuerySnapshot {
        lateinit var query: QuerySnapshot

        val collectionRef =
            db.collection(collectionId).document(documentId).collection(subCollectionId)

        val filteredRef = if (filter != null) collectionRef.where(filter) else collectionRef

        query = filteredRef.orderBy(order, direction).startAt(offset)
            .limit(limit).get().await()

        return query
    }


    suspend fun getCollectionCount(collectionId: String): Long {
        val query = db.collection(collectionId)
        val countQuery = query.count()
        var count: Long = -1

        count = countQuery.get(AggregateSource.SERVER).await().count

        return count
    }


//    suspend fun getSelectDocumentCount(){
//
//    }


    /****************************************_ADD_*************************************************/
    fun addDocument(collection: String, newId: String, data: Any) {
        db.collection(collection).document(newId).set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: `$newId`")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun addSubDocument(
        collectionId: String, document: String,
        subCollectionId: String, newId: String, data: Any
    ) {
        db.collection(collectionId)
            .document(document)
            .collection(subCollectionId)
            .document(newId)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: `$newId`")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


    fun batchAdd(
        collectionId: String,
        documentId: String,
        subCollectionId: String,
        newDocuments: Map<String, Any>
    ) {

        val collectionRef = if (documentId.isNotEmpty()) {
            db.collection(collectionId).document(documentId).collection(subCollectionId)
        } else {
            db.collection(collectionId)
        }

        Log.d(TAG, "ref: ${collectionRef.id}")


//        db.runBatch { batch ->
//            for (doc in newDocuments) {
//                batch.set(collectionRef.document(doc.key), doc.value)
//            }
//        }

        var batch = db.batch()

        for (doc in newDocuments) {
            batch.set(collectionRef.document(doc.key), doc.value)
        }

        batch.commit().addOnSuccessListener {
            Log.d(TAG, "woohoo")
        }
    }


    /***************************************_UPDATE_***********************************************/
    fun updateDocument(collectionId: String, documentId: String, data: Map<String, Any>) {
        db.collection(collectionId).document(documentId)
            .update(
                data
            )
    }

    fun updateSubDocument(
        collectionId: String, documentId: String, subCollectionId: String,
        subDocumentId: String, data: Map<String, Any>
    ) {
        db.collection(collectionId).document(documentId).collection(subCollectionId)
            .document(subDocumentId)
            .update(
                data
            )
    }

    fun addItemToArray(collectionId: String, documentId: String, field: String, value: String) {
        db.collection(collectionId).document(documentId)
            .update(field, FieldValue.arrayUnion(value)).addOnSuccessListener {
                Log.d(TAG, "ITEM ADDED TO ARRAY")
            }

    }

    fun incrementDocumentField(
        collectionId: String,
        documentId: String,
        field: String,
        incrementValue: Long
    ) {
        db.collection(collectionId).document(documentId)
            .update(mapOf(field to FieldValue.increment(incrementValue)))
    }

    fun incrementSubDocumentField(
        collectionId: String, documentId: String, subCollectionId: String,
        subDocumentId: String,
        field: String,
        incrementValue: Long
    ) {
        db.collection(collectionId).document(documentId).collection(subCollectionId)
            .document(subDocumentId)
            .update(mapOf(field to FieldValue.increment(incrementValue)))
    }

//    fun updateDocumentField(collectionId: String, documentId: String, field: String, value: Any) {
//        db.collection(collectionId).document(documentId).update(field, value)
//    }
//
//    fun updateSubDocumentField(
//        collectionId: String,
//        documentId: String,
//        subCollectionId: String,
//        subDocumentId: String,
//        field: String,
//        value: Any
//    ) {
//        db.collection(collectionId).document(documentId).collection(subCollectionId)
//            .document(subDocumentId).update(field, value)
//    }

    fun batchUpdate() {
        db.runBatch { batch ->
            // Set the value of 'NYC'
            //   batch.set(nycRef, City())
        }
    }

    /***************************************_DELETE_***********************************************/
    fun deleteDocument(collectionId: String, documentId: String) {
        db.collection(collectionId).document(documentId).delete()
    }

    fun deleteSubDocument(
        collectionId: String,
        documentId: String,
        subCollectionId: String,
        subDocumentId: String
    ) {
        db.collection(collectionId).document(documentId).collection(subCollectionId)
            .document(subDocumentId).delete()
    }

    fun deleteDocumentsEqualTo(collectionId: String, field: String, value: Any) {
        db.collection(collectionId).whereEqualTo(field, value).get()
            .addOnSuccessListener { snapshot ->
                db.runBatch { batch ->
                    for (document in snapshot) {
                        batch.delete(document.reference)
                    }
                }
            }

    }

    //TODO: must split batch size into 500 max
    fun deleteAllDocuments(collectionId: String) {
        val collectionRef = db.collection(collectionId)

        db.runBatch { batch ->
            collectionRef.get().addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    batch.delete(document.reference)
                }
            }
        }
    }


}


