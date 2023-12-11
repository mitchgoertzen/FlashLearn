package com.learn.flashLearnTagalog.adapters

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.LessonStats
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.databinding.LessonBinding
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.fragments.LessonTypeDialogueFragment
import com.learn.flashLearnTagalog.ui.fragments.SettingsFragment
import dagger.hilt.android.internal.managers.FragmentComponentManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LessonAdapter @Inject constructor(
    private val lessons: MutableList<Lesson>,
    private val lessonStats: MutableList<LessonStats>
) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

//    lateinit var lessonStatsList: List<LessonStats>

    class LessonViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {

        // Log.d(TAG, "13")
        val binding = LessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        //   Log.d(TAG, "14")
        return LessonViewHolder(binding)

    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {


        // Log.d(TAG, "1")
        val currentLesson = lessons[position]
        val lessonId = currentLesson.id
        //Log.d(TAG, "2")
        val currentLessonStats = lessonStats[position]
        //Log.d(TAG, "2")

        holder.itemView.apply {
            val mContext = FragmentComponentManager.findActivity(context) as Activity
            var level = ""
            var difficulty = ""
            val holderBinding = holder.binding
            holderBinding.tvCategory.maxLines = currentLesson.maxLines
//
//            val newLayoutParams =
//                holderBinding.ibLesson.layoutParams as ConstraintLayout.LayoutParams
//
////            LinearLayout.LayoutParams(
////                169, 169
////            )
//            Log.d(TAG, "number: ${lessonNumbers[currentLesson.id]}")
//            if ((lessonNumbers[currentLesson.id] != null) && (lessonNumbers[currentLesson.id])!!.toInt() % 2 == 0) {
//                newLayoutParams.marginStart = 0
//                newLayoutParams.marginEnd = 2
//            } else {
//                newLayoutParams.marginStart = 2
//                newLayoutParams.marginEnd = 0
//            }
//
//            holderBinding.ibLesson.layoutParams = newLayoutParams

            if (currentLesson.level == 0) {
                level = ""
            } else {
                difficulty = "Difficulty: ${currentLesson.difficulty}"
                when (currentLesson.level) {
                    1 -> {
                        level = "(I)"
                    }

                    2 -> {
                        level = "(II)"
                    }

                    3 -> {
                        level = "(III)"
                    }

                    4 -> {
                        level = "(IV)"
                    }

                    5 -> {
                        level = "(V)"
                    }

                    6 -> {
                        level = "(VI)"
                    }
                }
            }

            if (TempListUtility.passedLessons.contains(lessonId)) {
                holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_tested)
            } else if (TempListUtility.practicedLessons.contains(lessonId)) {
                holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_practiced)
            } else {
                holderBinding.ibLesson.setBackgroundResource(R.drawable.lesson_background_blank)
            }
            holderBinding.tvCategory.text = currentLesson.category + " $level"
            holderBinding.tvDifficulty.text = difficulty

            //  Log.d(TAG, "image: ${currentLesson.image} from:\n ${currentLesson.category}")
            holderBinding.ivPreview.setImageResource(currentLesson.image)



            if (!TempListUtility.unlockedLessons.contains(lessonId)) {
                holderBinding.ibLesson.isEnabled = false
                holderBinding.ibLesson.alpha = .7f
            } else {
                holderBinding.ibLesson.isEnabled = true
                holderBinding.ibLesson.alpha = 1f
            }

            // Log.d(TAG, "9")

            holderBinding.ibLesson.setOnClickListener {

                //  Log.d(TAG, "10")
                if (mContext is LearningActivity) {
                    //    Log.d(TAG, "11")
                    val settingsFragment = SettingsFragment(currentLesson)
                    val transaction = mContext.supportFragmentManager.beginTransaction()
                    if (currentLesson.category == "Custom\nLesson") {
                        transaction.replace(R.id.main_nav_container, settingsFragment)
                            .addToBackStack("settings").commit()
                    } else {
                        val dialog = LessonTypeDialogueFragment(currentLesson, currentLessonStats)
                        dialog.isCancelable = true
                        dialog.show(
                            mContext.supportFragmentManager, "lesson popup"
                        )
                    }
                    (mContext as LearningActivity?)?.transitionFragment()
                }
            }
        }

        //   Log.d(TAG, "12")
    }

    fun addLesson(lesson: Lesson, stats: LessonStats) {

        //  Log.d(TAG, "current size: ${lessons.size}")
        lessons.add(lesson)
        lessonStats.add(stats)
        notifyItemInserted(lessons.size - 1)
    }

    fun deleteLessons() {
        val size = lessons.size
        lessons.clear()
        lessonStats.clear()
        notifyItemRangeRemoved(0, size)
    }

//    fun intiList(list: List<Lesson>) {
//        lessonList = list
//    }
//
//    fun showListSize() {
//        println(lessonList.size)
//    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    fun getLessons(): MutableList<Lesson> {
        return lessons
    }

    fun sortList(type: Int) {

        // Log.d(TAG, "SORT 1ST LESSON -  ${lessons[0].category} ${lessons[0].level}")
        //  Log.d(TAG, "sort 1")
        // Log.d(TAG, "sort size + ${lessons.size}")

        if (lessons.size > 0) {
            //  val customLesson: Lesson = lessons.removeAt(0)

            //   Log.d(TAG, "sort 2")
            when (type) {
                //Category
                0 -> {
                    lessons.sortWith(compareBy<Lesson> { it.category }.thenBy { it.level })
                }
                //Level
                1 -> {
                    lessons.sortWith(compareBy<Lesson> { it.level }.thenBy { it.difficulty })
                }
                //Difficulty low to high
                2 -> {
                    lessons.sortWith(compareBy<Lesson> { it.difficulty }.thenBy { it.category })
                }
                //Difficulty high to low
                3 -> {
                    lessons.sortWith(compareByDescending<Lesson> { it.difficulty }.thenBy { it.category })
                }
                //Locked
//                4 -> {
//                    lessons.sortWith(compareBy<Lesson> { it.locked }.thenBy { it.difficulty }
//                        .thenBy { it.category })
//                }
            }

            //  Log.d(TAG, "sort 3")
            lessons.add(
                0,
                //TODO: only make once
                Lesson("Custom\nLesson", 0, -1, -1, 0, 2, R.drawable.custom)
            )
            lessonStats.add(
                0,
                //TODO: only make once
                LessonStats()
            )
//            var i = 0
//            for (l in lessons) {
//                lessonNumbers[l.id] = i++
//            }

        }
    }
}