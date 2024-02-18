package com.learn.flashLearnTagalog.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.Lesson
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.databinding.LessonBinding
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.dialog_fragments.LessonTypeDialogueFragment
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.internal.managers.FragmentComponentManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LessonAdapter @Inject constructor(
    private val viewModel: LessonViewModel,
    private val lessons: MutableList<Lesson>
) :
    RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(val binding: LessonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = LessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {


        val currentLesson = lessons[position]
        val lessonId = currentLesson.id

        holder.itemView.apply {
            val mContext = FragmentComponentManager.findActivity(context) as Activity
            var level = ""
            var difficulty = ""
            val holderBinding = holder.binding
            holderBinding.tvCategory.maxLines = currentLesson.maxLines

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
            holderBinding.ivPreview.setImageResource(
                resources.getIdentifier(
                    currentLesson.image,
                    "drawable",
                    "com.learn.flashLearnTagalog"
                )
            )
            holderBinding.ivPreview.imageTintList = null


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
//                    val settingsFragment = SettingsFragment(currentLesson)
//                    val transaction = mContext.supportFragmentManager.beginTransaction()
//                    if (currentLesson.category == "Custom\nLesson") {
//                        transaction.replace(R.id.main_nav_container, settingsFragment)
//                            .addToBackStack("settings").commit()
//                    } else {
                    viewModel.updateLesson(currentLesson)
                    val dialog = LessonTypeDialogueFragment()
                    dialog.isCancelable = true
                    dialog.show(
                        mContext.supportFragmentManager, "lesson popup"
                    )
                    //   }
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

    override fun getItemCount(): Int {
        return lessons.size
    }

    fun getLessons(): MutableList<Lesson> {
        return lessons
    }

    fun sortList(type: Int) {
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

//                //TODO: only make once
//                Lesson("Custom\nLesson", 0, -1, -1, 0, 2, R.drawable.custom)
//            )


    }
}