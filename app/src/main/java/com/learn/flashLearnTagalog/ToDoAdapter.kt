package com.learn.flashLearnTagalog

import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.databinding.ItemTodoBinding
import com.learn.flashLearnTagalog.db.Word

class ToDoAdapter(

    private val toDos: MutableList<ToDo>

) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    lateinit var currentList : List<Word>
    private var showEngFirst : Boolean = false
    private var isAnswer : Boolean = false

    class ToDoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): ToDoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ToDoViewHolder, position:Int) {
        val curTodo = toDos[position]
        holder.itemView.apply {
            holder.binding.tvTodoTitle.text = curTodo.title
            if(showEngFirst || isAnswer)
                holder.binding.tvTodoTitle.maxLines = 2
            else
                holder.binding.tvTodoTitle.maxLines = 1
            toggleStrikeThrough(holder.binding.tvTodoTitle, curTodo.isCorrect, curTodo.noAnswer)
        }
    }

    fun addToDo(toDo:ToDo, engFirst : Boolean, ans : Boolean){
        showEngFirst = engFirst
        isAnswer = ans
        toDos.add(toDo)
        notifyItemInserted(toDos.size - 1)
    }

    fun deleteToDos(){
        toDos.clear()
        notifyDataSetChanged()
    }

    fun getToDoSize():Int{
        return toDos.size
    }

    fun intiList(list:List<Word>){
        currentList = list
    }

    fun showListSize(){
        println( currentList.size)
    }

    private fun toggleStrikeThrough(tvTodoTitle:TextView, isCorrect:Boolean, noAnswer:Boolean){
        if(showEngFirst && !isAnswer)
            tvTodoTitle.maxLines = 1
        else
            tvTodoTitle.maxLines = 2

        //word skipped
        if(noAnswer){
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            tvTodoTitle.setTextColor(Color.parseColor("#FFCCCCCC"))
            tvTodoTitle.alpha = 1f
            tvTodoTitle.textSize = 22f
        }else
        //word not skipped
        {
            //incorrect answer
            if(!isCorrect) {
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
                tvTodoTitle.setTextColor(Color.parseColor("#CC000000"))
                tvTodoTitle.alpha = 0.6f
                tvTodoTitle.textSize = 18f
            }else
            //correct answer
            {
                tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                tvTodoTitle.setTextColor(Color.parseColor("#72CC50"))
                tvTodoTitle.alpha = 1f
                tvTodoTitle.textSize = 22f
            }
        }
    }

    override fun getItemCount(): Int {
        return toDos.size
    }
}