package com.learn.flashLearnTagalog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.data.WordStat
import com.learn.flashLearnTagalog.databinding.WordStatisticBinding

class WordStatAdapter(

    private val wordStats: MutableList<WordStat>

) : RecyclerView.Adapter<WordStatAdapter.WordStatViewHolder>() {

    lateinit var currentList: List<WordStat>
    private var isPercentage = false

    class WordStatViewHolder(val binding: WordStatisticBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordStatViewHolder {
        val binding =
            WordStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordStatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordStatViewHolder, position: Int) {
        val currentWord = wordStats[position]
        holder.binding.tvWord.text = currentWord.word
        if (isPercentage){
            val percentageText = currentWord.Percentage.toString() + "%"
            holder.binding.tvNumber.text = percentageText
        }
        else
            holder.binding.tvNumber.text = currentWord.Number.toString()
    }

    fun addWordStat(wordStat: WordStat, percentage: Boolean) {
        isPercentage = percentage
        wordStats.add(wordStat)
        notifyItemInserted(wordStats.size - 1)
    }

    fun deleteWordStats() {
        val size = wordStats.size
        wordStats.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun intiList(list: List<WordStat>) {
        currentList = list
    }

    fun showListSize() {
        println(currentList.size)
    }

    override fun getItemCount(): Int {
        return wordStats.size
    }
}