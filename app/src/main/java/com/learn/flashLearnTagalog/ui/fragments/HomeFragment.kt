package com.learn.flashLearnTagalog.ui.fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.LearningActivity
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences


    private lateinit var adminButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val lessonButton: Button = view.findViewById(R.id.btnLesson)
        val dictionaryButton: Button = view.findViewById(R.id.btnDictionary)

        adminButton = view.findViewById(R.id.btnAdmin)



        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {
            adminButton.visibility = View.VISIBLE
        }

        reloadAdmin()

        sharedPref.edit().putBoolean(Constants.KEY_IN_HOME, true).apply()
        (activity as LearningActivity?)?.goHome()

        lessonButton.setOnClickListener {

            val fragment = LessonSelectFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("lessons")?.commit()
            (activity as LearningActivity?)?.transitionFragment(2)

        }

        dictionaryButton.setOnClickListener {
            val fragment = DictionaryFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_nav_container, fragment)
                ?.addToBackStack("dictionary")?.commit()
            (activity as LearningActivity?)?.transitionFragment(1)
        }
    }

    fun reloadAdmin() {
        Log.d(TAG, "reload admin")
        if (sharedPref.getBoolean(Constants.KEY_USER_ADMIN, false)) {
            Log.d(TAG, "true")

            adminButton.visibility = View.VISIBLE

            adminButton.setOnClickListener {
                val fragment = AdminFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_nav_container, fragment)
                    ?.addToBackStack("admin")?.commit()
                (activity as LearningActivity?)?.transitionFragment(0)

            }
        } else {
            adminButton.visibility = View.GONE
            Log.d(TAG, "false")
        }

    }
}
