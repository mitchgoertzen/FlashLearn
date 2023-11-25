package com.learn.flashLearnTagalog.adapters

import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.data.TestWord
import com.learn.flashLearnTagalog.databinding.TestWordBinding
import com.learn.flashLearnTagalog.db.RoomWord

class TestWordAdapter(

    private val testWords: MutableList<TestWord>

) : RecyclerView.Adapter<TestWordAdapter.TestWordViewHolder>() {

    lateinit var currentList: List<RoomWord>
    private var showEngFirst: Boolean = false
    private var isAnswer: Boolean = false

    class TestWordViewHolder(val binding: TestWordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestWordViewHolder {
        val binding = TestWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun intiList(list: List<RoomWord>) {
        currentList = list
    }

    fun showListSize() {
        println(currentList.size)
    }

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
                tvTestWord.setTextColor(Color.parseColor("#CC000000"))
                tvTestWord.alpha = 0.6f
                tvTestWord.textSize = 18f
            } else
            //correct answer
            {
                tvTestWord.paintFlags = tvTestWord.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                tvTestWord.setTextColor(Color.parseColor("#72CC50"))
                tvTestWord.alpha = 1f
                tvTestWord.textSize = 22f
            }
        }
    }

    override fun getItemCount(): Int {
        return testWords.size
    }
}