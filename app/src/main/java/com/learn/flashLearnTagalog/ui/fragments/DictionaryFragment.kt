package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.toObject
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.DictionaryAdapter
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryFragment : Fragment() {

    private lateinit var dictionaryAdapter: DictionaryAdapter


    val scope = CoroutineScope(Job() + Dispatchers.Main)

    // private val viewModel: MainViewModel by viewModels()
    private var masterWordList: MutableList<Word> = mutableListOf()
    private var wordsPerPage = 200
    private var numPages = 1
    private var currentPage = 0

    private var wordCount = 0

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dictionaryAdapter = DictionaryAdapter(mutableListOf())

        //initialize view, give reference to proper fragment
        val view = inflater.inflate(R.layout.fragment_dictionary, container, false)

        //connect local variables to elements in fragment
        val rvDictionary: RecyclerView = view.findViewById(R.id.rvDictionary)

        val currPage: TextView = view.findViewById(R.id.tvCurrPage)
        val totalPages: TextView = view.findViewById(R.id.tvTotalPages)

        val firstPage: ImageButton = view.findViewById(R.id.ibFirstPage)
        val lastPage: ImageButton = view.findViewById(R.id.ibLastPage)
        val nextPage: ImageButton = view.findViewById(R.id.ibNextPage)
        val prevPage: ImageButton = view.findViewById(R.id.ibPrevPage)


        for (wordList in TempListUtility.practicedWords) {
            wordCount += wordList.value.size
            masterWordList.addAll(wordList.value)
        }

        if (wordCount == 0) {
            numPages = 0
        } else {
            currentPage = 1
            numPages = wordCount / wordsPerPage
        }

        if (wordCount % wordsPerPage > 0)
            numPages++

        currPage.text = "$currentPage"
        totalPages.text = numPages.toString()

//        scope.launch{
//            val wordCount = DataUtility.getWordCount()
//            println("WORD COUNT: $wordCount")
//
//            numPages = wordCount / wordsPerPage.toInt()
//
//            if (wordCount % wordsPerPage > 0)
//                numPages++
//
//            totalPages.text = numPages.toString()
//        }

        deactivateSwitch(firstPage)
        deactivateSwitch(prevPage)

        if (numPages < 1 || currentPage == numPages) {
            deactivateSwitch(nextPage)
            deactivateSwitch(lastPage)
        }

        //when firstPage button is pressed, (de)/activate this and corresponding buttons
        firstPage.setOnClickListener {
            deactivateSwitch(firstPage)
            deactivateSwitch(prevPage)
            activateSwitch(nextPage)
            activateSwitch(lastPage)
            currentPage = 1
            currPage.text = "1"

            gatherWords()


        }

        //when lastPage button is pressed, (de)/activate this and corresponding buttons
        lastPage.setOnClickListener {
            deactivateSwitch(nextPage)
            deactivateSwitch(lastPage)
            activateSwitch(firstPage)
            activateSwitch(prevPage)
            currentPage = numPages
            currPage.text = "$numPages"

            gatherWords()

        }

        //on nextPage button press
        nextPage.setOnClickListener {
            if (!prevPage.isEnabled) {
                activateSwitch(prevPage)
                activateSwitch(firstPage)
            }
            currentPage++
            if (currentPage == numPages) {
                deactivateSwitch(nextPage)
                deactivateSwitch(lastPage)
            }
            currPage.text = currentPage.toString()

            gatherWords()

        }

        //on prevPage button press
        prevPage.setOnClickListener {
            if (!nextPage.isEnabled) {
                activateSwitch(nextPage)
                activateSwitch(lastPage)
            }
            currentPage--
            if (currentPage == 1) {
                deactivateSwitch(prevPage)
                deactivateSwitch(firstPage)
            }
            currPage.text = currentPage.toString()

            gatherWords()

        }

        //set adapter to be used for displaying dictionary words
        rvDictionary.adapter = dictionaryAdapter
        //set layout manager used for displaying dictionary (Linear)
        rvDictionary.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        gatherWords()

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun gatherWords() {

        dictionaryAdapter.deleteDictionaryWords()

        if (masterWordList.isNotEmpty()) {
            val start = (currentPage - 1) * wordsPerPage

            for (i in start..(start + wordsPerPage)) {
                if (i < wordCount) {
                    dictionaryAdapter.addDictionaryWord(masterWordList[i])
                }
            }
            dictionaryAdapter.sort()
        }

//        scope.launch {
//            //clear currently displayed words from the screen
//            dictionaryAdapter.deleteDictionaryWords()
//
//            //TODO: need to use for start at: ((currentPage - 1) * wordsPerPage.toInt())
//            println("1")
//            //  wordList = DataUtility.getDictionaryWords("english", wordsPerPage).toMutableList()
//            //gather next set of words, confined by current page and number of words per page
//
//            println("3")
////                viewModel.getDictionaryWords(((currentPage - 1) * wordsPerPage), wordsPerPage)
////                    .observe(viewLifecycleOwner) {
////                        wordList = it.toMutableList()
////                    }
//            Handler(Looper.getMainLooper()).postDelayed({
//
//                println("4")
//                for (word in wordList) {
//                    dictionaryAdapter.addDictionaryWord(word)
//                }
//            }, 1000)
//        }


//        GlobalScope.launch(Dispatchers.Main) {
//            suspend {
//
//            }.invoke()
//        }
    }

    private fun activateSwitch(switch: ImageButton) {
        switch.scaleX = 1f
        switch.scaleY = 1f
        switch.isEnabled = true
        switch.alpha = 1f
    }

    private fun deactivateSwitch(switch: ImageButton) {
        switch.scaleX = .8f
        switch.scaleY = .8f
        switch.isEnabled = false
        switch.alpha = .8f
    }

}