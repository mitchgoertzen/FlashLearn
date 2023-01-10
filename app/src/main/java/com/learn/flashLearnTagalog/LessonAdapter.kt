package com.learn.flashLearnTagalog

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.databinding.LessonBinding
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.ui.fragments.LessonTypeDialogueFragment
import com.learn.flashLearnTagalog.ui.fragments.SettingsFragment
import dagger.hilt.android.internal.managers.FragmentComponentManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonAdapter @Inject constructor(private val lessons: MutableList<Lesson>) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    lateinit var currentList : List<Lesson>

    class LessonViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int): LessonViewHolder {
        val binding = LessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)

    }

    override fun onBindViewHolder(holder:LessonViewHolder, position:Int) {
        val currentLesson = lessons[position]
        holder.itemView.apply {
            val mContext = FragmentComponentManager.findActivity(context) as Activity
            var level = ""
            if(currentLesson.level == 0){
                holder.binding.tvTitle.maxLines = 2
            }else{
               level = "(Level ${currentLesson.level})"
            }
            println("${currentLesson.title} $level: practice completed ${currentLesson.practiceCompleted}")
            holder.binding.tvTitle.text = currentLesson.title
            holder.binding.tvLevel.text = level
            holder.binding.ivPreview.setImageResource(currentLesson.imageID)

            holder.binding.ibLesson.setOnClickListener{
                if(mContext is LearningActivity){
                    val settingsFragment = SettingsFragment(currentLesson)
                    val transaction = mContext.supportFragmentManager.beginTransaction()
                    if(currentLesson.title == "Custom\nLesson"){
                        transaction.replace(R.id.main_nav_container, settingsFragment).addToBackStack("settings").commit()
                    }else{
                        val dialog = LessonTypeDialogueFragment(currentLesson)
                        dialog.isCancelable = true
                        dialog.show(
                            mContext.supportFragmentManager, "lesson popup")
                    }
                    (mContext as LearningActivity?)?.transitionFragment()
                }
            }
        }
    }

    fun addToDo(lesson: Lesson){
        lessons.add(lesson)
        notifyItemInserted(lessons.size - 1)
    }

    fun deleteToDos(){
        lessons.clear()
        notifyDataSetChanged()
    }
    fun intiList(list:List<Lesson>){
        currentList = list
    }

    fun showListSize(){
        println( currentList.size)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    fun getLessons() : MutableList<Lesson>{
        return lessons
    }
}