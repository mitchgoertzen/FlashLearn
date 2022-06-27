package com.learn.flashLearnTagalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.databinding.WordStatisticBinding

class WordStatAdapter(

    private val words: MutableList<WordStat>

) : RecyclerView.Adapter<WordStatAdapter.WordStatViewHolder>() {

    lateinit var currentList : List<WordStat>
    private var isPercentage = false

    class WordStatViewHolder(val binding: WordStatisticBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): WordStatViewHolder {
        val binding = WordStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordStatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordStatViewHolder, position:Int) {
        val currentWord = words[position]
        holder.binding.tvWord.text = currentWord.word
        if(isPercentage)
            holder.binding.tvNumber.text = currentWord.Percentage.toString() + "%"
        else
            holder.binding.tvNumber.text = currentWord.Number.toString()
    }

    fun addToDo(word:WordStat, percentage: Boolean){
        isPercentage = percentage
        words.add(word)
        notifyItemInserted(words.size - 1)
    }

    fun deleteToDos(){
        words.clear()
        notifyDataSetChanged()
    }
    fun intiList(list:List<WordStat>){
        currentList = list
    }

    fun showListSize(){
        println( currentList.size)
    }

    override fun getItemCount(): Int {
        return words.size
    }
}