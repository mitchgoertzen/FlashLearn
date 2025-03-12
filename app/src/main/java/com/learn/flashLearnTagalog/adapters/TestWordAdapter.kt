package com.learn.flashLearnTagalog.adapters

import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.TestWord
import com.learn.flashLearnTagalog.databinding.ComponentTestWordBinding

class TestWordAdapter(

    private val testWords: MutableList<TestWord>

) : RecyclerView.Adapter<TestWordAdapter.TestWordViewHolder>() {

   // lateinit var currentList: List<Word>
    private var showEngFirst: Boolean = false
    private var isAnswer: Boolean = false
    private var correctColor = 0
    private var wrongColor = 0

    class TestWordViewHolder(val binding: ComponentTestWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestWordViewHolder {
        val binding = ComponentTestWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        correctColor = parent.resources.getColor(R.color.passingGreen)
        wrongColor = parent.resources.getColor(R.color.red)
        return TestWordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestWordViewHolder, position: Int) {
        val curWord = testWords[position]
        holder.itemView.apply {
            holder.binding.tvTestWord.text = curWord.title
            if (showEngFirst || isAnswer)
                holder.binding.tvTestWord.maxLines = 2
            else
                holder.binding.tvTestWord.maxLines = 1
            toggleStrikeThrough(holder.binding.tvTestWord, curWord.isCorrect, curWord.noAnswer)
        }
    }

    fun addTestWord(testWord: TestWord, engFirst: Boolean, ans: Boolean) {
        showEngFirst = engFirst
        isAnswer = ans
        testWords.add(testWord)
        notifyItemInserted(testWords.size - 1)
    }

    fun deleteTestWords() {
        val size = testWords.size
        testWords.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getTestWordsSize(): Int {
        return testWords.size
    }

//    fun intiList(list: List<Word>) {
//        currentList = list
//    }
//
//    fun showListSize() {
//        println(currentList.size)
//    }

    private fun toggleStrikeThrough(tvTestWord: TextView, isCorrect: Boolean, noAnswer: Boolean) {
        if (showEngFirst && !isAnswer)
            tvTestWord.maxLines = 1
        else
            tvTestWord.maxLines = 2

        //word skipped
        if (noAnswer) {
            tvTestWord.paintFlags = tvTestWord.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTestWord.setTextColor(Color.parseColor("#FFCCCCCC"))
            tvTestWord.alpha = 1f
            tvTestWord.textSize = 22f
        } else
        //word not skipped
        {
            //incorrect answer
            if (!isCorrect) {
                tvTestWord.paintFlags = tvTestWord.paintFlags or STRIKE_THRU_TEXT_FLAG
                tvTestWord.setTextColor(wrongColor)
                tvTestWord.alpha = 0.6f
                tvTestWord.textSize = 18f
            } else
            //correct answer
            {
                tvTestWord.paintFlags = tvTestWord.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                tvTestWord.setTextColor(correctColor)
                tvTestWord.alpha = 1f
                tvTestWord.textSize = 22f
            }
        }
    }

    override fun getItemCount(): Int {
        return testWords.size
    }
}