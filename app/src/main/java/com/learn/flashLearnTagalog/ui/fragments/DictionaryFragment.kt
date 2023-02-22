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
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.DictionaryAdapter
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryFragment : Fragment() {

    private lateinit var dictionaryAdapter: DictionaryAdapter

    private val viewModel: MainViewModel by viewModels()
    private var wordList: MutableList<Word> = mutableListOf()
    private var wordsPerPage = 200
    private var numPages = 1
    private var currentPage = 1

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

        numPages = viewModel.getSize() / wordsPerPage

        if (viewModel.getSize() % wordsPerPage > 0)
            numPages++

        totalPages.text = numPages.toString()

        deactivateSwitch(firstPage)
        deactivateSwitch(prevPage)


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

        CoroutineScope(Dispatchers.Main).launch {
            suspend {
                //clear currently displayed words from the screen
                dictionaryAdapter.deleteDictionaryWords()

                //gather next set of words, confined by current page and number of words per page
                viewModel.getDictionaryWords(((currentPage - 1) * wordsPerPage), wordsPerPage)
                    .observe(viewLifecycleOwner) {
                        wordList = it.toMutableList()
                    }
                Handler(Looper.getMainLooper()).postDelayed({
                    for (word in wordList) {
                        dictionaryAdapter.addDictionaryWord(word)
                    }
                }, 1000)
            }.invoke()
        }
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