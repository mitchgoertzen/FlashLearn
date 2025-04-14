package com.learn.flashLearnTagalog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.data.Word
import com.learn.flashLearnTagalog.databinding.ComponentOrgLessonWordBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrgLessonWordAdapter @Inject constructor(private val words: MutableList<Word>) :
    RecyclerView.Adapter<OrgLessonWordAdapter.OrgLessonWordViewHolder>() {

    //TODO: init check
    private lateinit var currentList: List<Word>

    class OrgLessonWordViewHolder(val binding: ComponentOrgLessonWordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgLessonWordViewHolder {
        val binding =
            ComponentOrgLessonWordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return OrgLessonWordViewHolder(binding)

    }

    override fun onBindViewHolder(holder: OrgLessonWordViewHolder, position: Int) {
        val currWord = words[position]


        val type = when (currWord.type) {
            "n" -> {
                "Noun"
            }

            "comp" -> {
                "Noun"
            }

            "v" -> {
                "Verb"
            }

            "adj" -> {
                "Adjective"
            }

            "adv" -> {
                "adverb"
            }

            "inf" -> {
                "infinitive"
            }

            "intrj" -> {
                "interjection"
            }

            "prep" -> {
                "preposition"
            }

            else -> {
                currWord.type
            }
        }


        holder.itemView.apply {
            holder.binding.tvEng.text = "${currWord.english}, $type"
            holder.binding.tvTag.text = currWord.translations[currWord.correctIndex]
        }
    }

    fun addNewWord(word: Word) {
        words.add(word)
        notifyItemInserted(words.size - 1)
    }

    fun deleteWords() {
        val size = words.size
        words.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun intiList(list: List<Word>) {
        currentList = list
    }

    fun showListSize() {
        println(currentList.size)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun getWords(): MutableList<Word> {
        return words
    }

    fun sort() {
        words.sortWith(compareBy<Word> { it.english }.thenBy { it.translations[it.correctIndex] })
    }
}