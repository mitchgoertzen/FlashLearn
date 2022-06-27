package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.HomeActivity
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfilePopupFragment(private var newActivity: Activity) : DialogFragment() {
    @Inject
    lateinit var sharedPref : SharedPreferences

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_popup, container, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window : ConstraintLayout = view.findViewById(R.id.clProfileBackground)
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val stats : Button = view.findViewById(R.id.btnStats)


        stats.isEnabled = !sharedPref.getBoolean(Constants.KEY_IN_TEST, false)


        stats.setOnClickListener{
            dialog?.dismiss()
            if(newActivity is HomeActivity){
                val learning =  LearningActivity()
                learning.setType(3)
                startActivity(Intent(newActivity,learning::class.java))
            }
            else{
                val fragment = StatsFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")?.commit()
                (activity as LearningActivity?)?.transitionFragment()
            }

        }


        return view
    }
}