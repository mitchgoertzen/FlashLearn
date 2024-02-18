package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.DictionaryAdapter
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var dictionaryAdapter: DictionaryAdapter

    private var masterWordList: MutableList<Word> = mutableListOf()
    private var wordsPerPage = 200
    private var numPages = 1
    private var currentPage = 0
    private var wordCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dictionaryAdapter = DictionaryAdapter(mutableListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firstPage: ImageButton = view.findViewById(R.id.ibFirstPage)
        val lastPage: ImageButton = view.findViewById(R.id.ibLastPage)
        val nextPage: ImageButton = view.findViewById(R.id.ibNextPage)
        val prevPage: ImageButton = view.findViewById(R.id.ibPrevPage)
        val rvDictionary: RecyclerView = view.findViewById(R.id.rvDictionary)
        val currPage: TextView = view.findViewById(R.id.tvCurrPage)
        val totalPages: TextView = view.findViewById(R.id.tvTotalPages)

        for (wordList in TempListUtility.viewedWords) {
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
    }
}