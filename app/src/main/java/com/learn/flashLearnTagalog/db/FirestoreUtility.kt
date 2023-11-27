package com.learn.flashLearnTagalog.db

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore


class FirestoreUtility {
    val db = Firebase.firestore

    /****************************************_GET_*************************************************/
    fun getCollection(collectionId: String): CollectionReference {
        return db.collection(collectionId)
    }

    fun getSubCollection(
        collectionId: String,
        documentId: String,
        subCollectionId: String
    ): CollectionReference {
        return db.collection(collectionId)
    }

    fun getDocument(collectionId: String, documentId: String): DocumentSnapshot {
        lateinit var document: DocumentSnapshot

        db.collection(collectionId).document(documentId).get()
            .addOnSuccessListener { result ->
                document = result
            }

        return document
    }

    fun getSubDocument(
        collectionId: String, documentId: String,
        subCollectionId: String, subDocumentId: String
    ): DocumentSnapshot {
        lateinit var document: DocumentSnapshot
        db.collection(collectionId).document(documentId)
            .collection(subCollectionId).document(subDocumentId)
            .get().addOnSuccessListener { result ->
                document = result
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return document
    }

    fun getAllDocuments(collectionId: String): MutableList<DocumentSnapshot> {
        lateinit var query: QuerySnapshot
        db.collection(collectionId).get().addOnSuccessListener { d ->
            query = d
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return query.documents
    }

    fun getAllSubDocuments(
        collectionId: String, documentId: String,
        subCollectionId: String
    ): MutableList<DocumentSnapshot> {
        lateinit var query: QuerySnapshot
        db.collection(collectionId).document(documentId)
            .collection(subCollectionId)
            .get().addOnSuccessListener { d ->
                query = d
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return query.documents
    }

    //TODO: probably combine with orderedblock
    fun getOrderedDocuments(collectionId: String, order: String): MutableList<DocumentSnapshot> {

        lateinit var query: QuerySnapshot
        db.collection(collectionId).orderBy(order, Query.Direction.DESCENDING).get()
            .addOnSuccessListener { snapshot ->
                query = snapshot
            }
        return query.documents
    }

    fun getDocumentsEqualTo(
        collectionId: String,
        field: String,
        value: Any
    ): MutableList<DocumentSnapshot> {

        lateinit var query: QuerySnapshot
        db.collection(collectionId)
            .whereEqualTo(field, value)
            .get().addOnSuccessListener { snapshot ->
                query = snapshot
            }

        return query.documents
    }

    //citiesRef.orderBy("name", Query.Direction.DESCENDING).limit(3)
    fun getOrderedDocumentBlock(
        collectionId: String, order: String,
        offset: Int, limit: Long
    ): MutableList<DocumentSnapshot> {
        lateinit var query: QuerySnapshot
        db.collection(collectionId).orderBy(order).startAt(offset).limit(limit).get()
            .addOnSuccessListener { snapshot ->
                query = snapshot
            }
        return query.documents
    }


    fun getCollectionCount(collectionId: String): Long {
        val query = db.collection(collectionId)
        val countQuery = query.count()
        var count: Long = -1
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Count fetched successfully
                val snapshot = task.result
                count = snapshot.count
                Log.d(TAG, "Count: ${snapshot.count}")
            } else {
                Log.d(TAG, "Count failed: ", task.getException())
            }
        }
        return count
    }


    /****************************************_ADD_*************************************************/
    fun addSubCollection(
        collectionId: String, document: String,
        subCollectionId: String, newId: String, data: Any
    ) {
        db.collection(collectionId)
            .document(document)
            .collection(subCollectionId)
            .document(newId)
            .set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: `$newId`")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun addDocument(collection: String, newId: String, data: Any) {
        db.collection(collection).document(newId).set(data)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: `$newId`")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun batchAdd(collectionId: String, newDocuments: Map<String, Any>) {
        val collectionRef = db.collection(collectionId)
        db.runBatch { batch ->
            for (doc in newDocuments) {
                batch.set(collectionRef.document(doc.key), doc.value)
            }
        }
    }


    /***************************************_UPDATE_***********************************************/
    fun updateDocument(collection: String, document: String, data: Map<String, Any>) {
        db.collection(collection).document(document)
            .update(
                data
            )
    }

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

    fun batchDelete() {
        db.runBatch { batch ->
            // Set the value of 'NYC'
            //  batch.set(nycRef, City())
        }
    }

}


