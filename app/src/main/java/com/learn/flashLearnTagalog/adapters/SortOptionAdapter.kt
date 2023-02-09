package com.learn.flashLearnTagalog.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.SortOptionBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortOptionAdapter @Inject constructor(private var options: MutableList<String>, savedSortPosition : Int) : RecyclerView.Adapter<SortOptionAdapter.SortOptionViewHolder>() {

    lateinit var optionsList : ArrayList<String>
    //TODO: used saved variable, not hardcoded
    var currentSelection : Int = savedSortPosition
    lateinit var  currentSelect : TextView

    class SortOptionViewHolder(val binding: SortOptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): SortOptionViewHolder {
        val binding = SortOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SortOptionViewHolder(binding)

    }

    override fun onBindViewHolder(holder: SortOptionViewHolder, position:Int) {
        val currentOption = options[position]
        if(position == currentSelection)
            select(holder.binding.textView5)
        holder.itemView.apply {
            val option = holder.binding.textView5
            option.text = currentOption
            option.setOnClickListener{
                currentSelection = position
                currentSelect.setBackgroundResource(R.color.white)
                select(option)
            }
        }
    }

    fun addToDo(option: String){
        options.add(option)
        notifyItemInserted(options.size - 1)
    }

    fun getSelected() : Int{
        return currentSelection
    }

    fun deleteToDos(){
        options.clear()
        notifyDataSetChanged()
    }

    fun select(option:TextView){
        currentSelect = option
        option.setBackgroundResource(R.drawable.selected_text_background)
    }

    fun intiList(list:List<String>){
        options = list as MutableList<String>
    }

    fun showListSize(){
        println( optionsList.size)
    }

    override fun getItemCount(): Int {
        return options.size
    }

}