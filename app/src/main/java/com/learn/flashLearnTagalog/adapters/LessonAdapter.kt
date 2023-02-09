package com.learn.flashLearnTagalog.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.databinding.LessonBinding
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.fragments.LessonTypeDialogueFragment
import com.learn.flashLearnTagalog.ui.fragments.SettingsFragment
import dagger.hilt.android.internal.managers.FragmentComponentManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonAdapter @Inject constructor(private val lessons: MutableList<Lesson>) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    lateinit var currentList: List<Lesson>

    class LessonViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = LessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)

    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val currentLesson = lessons[position]
        holder.itemView.apply {
            val mContext = FragmentComponentManager.findActivity(context) as Activity
            var level = ""

            val holderBinding = holder.binding

            holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_blank)
            if (currentLesson.level == 0) {
                holderBinding.tvTitle.maxLines = 2
                level = "(Any Level)"
            } else {
                level = "(Level ${currentLesson.level})"
                if (currentLesson.testPassed)
                    holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_tested)
                else if (currentLesson.practiceCompleted)
                    holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_practiced)
            }
            holderBinding.tvTitle.text = currentLesson.title
            holderBinding.tvLevel.text = level

            holderBinding.ivPreview.setImageResource(currentLesson.imageID)



            if (currentLesson.locked) {
                holderBinding.ibLesson.isEnabled = false
                holderBinding.ibLesson.alpha = .7f
            } else {
                holderBinding.ibLesson.isEnabled = true
                holderBinding.ibLesson.alpha = 1f
            }


            holderBinding.ibLesson.setOnClickListener {
                if (mContext is LearningActivity) {
                    val settingsFragment = SettingsFragment(currentLesson)
                    val transaction = mContext.supportFragmentManager.beginTransaction()
                    if (currentLesson.title == "Custom\nLesson") {
                        transaction.replace(R.id.main_nav_container, settingsFragment)
                            .addToBackStack("settings").commit()
                    } else {
                        val dialog = LessonTypeDialogueFragment(currentLesson)
                        dialog.isCancelable = true
                        dialog.show(
                            mContext.supportFragmentManager, "lesson popup"
                        )
                    }
                    (mContext as LearningActivity?)?.transitionFragment()
                }
            }
        }
    }

    fun addLesson(lesson: Lesson) {
        lessons.add(lesson)
        notifyItemInserted(lessons.size - 1)
    }

    fun deleteLessons() {
        val size = lessons.size
        lessons.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun intiList(list: List<Lesson>) {
        currentList = list
    }

    fun showListSize() {
        println(currentList.size)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    fun getLessons(): MutableList<Lesson> {
        return lessons
    }

    fun sortList(type: Int) {
        if (lessons.size > 0) {
            val customLesson: Lesson = lessons.removeAt(0)

            when (type) {
                //Category
                0 -> {
                    lessons.sortWith(compareBy<Lesson> { it.title }.thenBy { it.level })
                }
                //Subcategory
                1 -> {
                    lessons.sortWith(compareBy<Lesson> { it.title }.thenBy { it.level })
                }
                //Difficulty low to high
                2 -> {
                    lessons.sortWith(compareBy<Lesson> { it.level }.thenBy { it.title })
                }
                //Difficulty high to low
                3 -> {
                    lessons.sortWith(compareByDescending<Lesson> { it.level }.thenBy { it.title })
                }
                //Locked
                4 -> {
                    lessons.sortWith(compareBy<Lesson> { it.locked }.thenBy { it.level }
                        .thenBy { it.title })
                }
            }
            lessons.add(0, customLesson)
            notifyItemRangeChanged(0, lessons.size)
        }
    }
}