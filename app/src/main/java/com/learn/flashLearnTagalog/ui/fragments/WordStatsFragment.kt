package com.learn.flashLearnTagalog.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.adapters.WordStatAdapter
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.data.WordStat
import com.learn.flashLearnTagalog.data.WordStats
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordStatsFragment : Fragment() {

    //private val viewModel: MainViewModel by viewModels()

    private lateinit var correctAdapter: WordStatAdapter
    private lateinit var incorrectAdapter: WordStatAdapter
    private lateinit var bestAdapter: WordStatAdapter
    private lateinit var worstAdapter: WordStatAdapter
    private lateinit var encounteredAdapter: WordStatAdapter
    private lateinit var skippedAdapter: WordStatAdapter
    private lateinit var flippedAdapter: WordStatAdapter
    private lateinit var timeAdapter: WordStatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_word_stats, container, false)

        correctAdapter = WordStatAdapter(mutableListOf())
        incorrectAdapter = WordStatAdapter(mutableListOf())
        bestAdapter = WordStatAdapter(mutableListOf())
        worstAdapter = WordStatAdapter(mutableListOf())
        encounteredAdapter = WordStatAdapter(mutableListOf())
        skippedAdapter = WordStatAdapter(mutableListOf())
        flippedAdapter = WordStatAdapter(mutableListOf())
        timeAdapter = WordStatAdapter(mutableListOf())

        val rvCorrect: RecyclerView = view.findViewById(R.id.rvCorrect)
        rvCorrect.adapter = correctAdapter
        rvCorrect.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

        val rvIncorrect: RecyclerView = view.findViewById(R.id.rvIncorrect)
        rvIncorrect.adapter = incorrectAdapter
        rvIncorrect.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

//        val rvBest : RecyclerView = view.findViewById(R.id.rvBest)
//        rvBest.adapter = bestAdapter
//        rvBest.layoutManager = LinearLayoutManager((activity as LearningActivity?))
//
//        val rvWorst : RecyclerView = view.findViewById(R.id.rvWorst)
//        rvWorst.adapter = worstAdapter
//        rvWorst.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        val rvEncountered: RecyclerView = view.findViewById(R.id.rvEncountered)
        rvEncountered.adapter = encounteredAdapter
        rvEncountered.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

        val rvSkipped: RecyclerView = view.findViewById(R.id.rvSkipped)
        rvSkipped.adapter = skippedAdapter
        rvSkipped.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

        val rvFlipped: RecyclerView = view.findViewById(R.id.rvFlipped)
        rvFlipped.adapter = flippedAdapter
        rvFlipped.layoutManager =
            GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

//        val rvTime : RecyclerView = view.findViewById(R.id.rvTime)
//        rvTime.adapter = timeAdapter
//        rvTime.layoutManager = GridLayoutManager(requireContext(), 1, LinearLayoutManager.VERTICAL, false)

        fillStats()

        return view
    }

    private fun fillStats() {

        val scope = CoroutineScope(Job() + Dispatchers.Main)

        scope.launch {
            for (wordStat in DataUtility.getMostCorrect(5).toMutableList()) {
                //get tagalog of wordStat
                val tagalog = ""
                correctAdapter.addWordStat(WordStat(tagalog, wordStat.timesCorrect, 0.0), false)
            }

//        for (word in DataUtility.getLeastCorrect(5).toMutableList()) {
//            //get tagalog of wordStat
//            val tagalog = ""
//            incorrectAdapter.addWordStat(
//                WordStat(
//                    word.tagalog,
//                    word.timesAnswered - word.timesCorrect,
//                    0.0
//                ), false
//            )
//
//        }


            //TODO: percentage correct
//        viewModel.getBest().observe(viewLifecycleOwner) {
//            val list = it.toMutableList()
//            for(word in list){
//                val div = if(word.timesAnswered == 0)
//                    1
//                else
//                    word.timesAnswered
//                bestAdapter.addToDo(WordStat(word.tagalog, 0, 100*(word.timesCorrect/div).toDouble()), true)
//            }
//        }
//
//        viewModel.getWorst().observe(viewLifecycleOwner) {
//            val list = it.toMutableList()
//            for(word in list){
//                val div = if(word.timesAnswered == 0)
//                    1
//                else
//                    word.timesAnswered
//                worstAdapter.addToDo(WordStat(word.tagalog,0, 100*(word.timesCorrect/div).toDouble()), true)
//            }
//        }

            for (word in DataUtility.getMostEncountered(5).toMutableList()) {
                //get tagalog of wordStat
                val tagalog = ""
                encounteredAdapter.addWordStat(WordStat(tagalog, word.timesAnswered, 0.0), false)
            }


            for (word in DataUtility.getMostSkipped(5).toMutableList()) {
                //get tagalog of wordStat
                val tagalog = ""
                skippedAdapter.addWordStat(WordStat(tagalog, word.timesSkipped, 0.0), false)
            }

            for (word in DataUtility.getMostFlipped(5).toMutableList()) {
                //get tagalog of wordStat
                val tagalog = ""
                flippedAdapter.addWordStat(WordStat(tagalog, word.timesFlipped, 0.0), false)
            }

        }
    }

}