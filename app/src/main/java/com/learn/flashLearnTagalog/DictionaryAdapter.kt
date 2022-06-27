package com.learn.flashLearnTagalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.databinding.DictionaryWordBinding
import com.learn.flashLearnTagalog.db.Word
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryAdapter @Inject constructor(private val words: MutableList<Word>) : RecyclerView.Adapter<DictionaryAdapter.DictionaryViewHolder>() {

    lateinit var currentList : List<Word>

    class DictionaryViewHolder(val binding: DictionaryWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): DictionaryViewHolder {
        val binding = DictionaryWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DictionaryViewHolder(binding)

    }

    override fun onBindViewHolder(holder:DictionaryViewHolder, position:Int) {
        val currWord = words[position]
        holder.itemView.apply {
            holder.binding.tvEng.text = currWord.english
            holder.binding.tvTag.text = currWord.tagalog
        }
    }

    fun addToDo(word: Word){
        words.add(word)
        notifyItemInserted(words.size - 1)
    }

    fun deleteToDos(){
        words.clear()
        notifyDataSetChanged()
    }
    fun intiList(list:List<Word>){
        currentList = list
    }

    fun showListSize(){
        println( currentList.size)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun getLessons() : MutableList<Word>{
        return words
    }
}