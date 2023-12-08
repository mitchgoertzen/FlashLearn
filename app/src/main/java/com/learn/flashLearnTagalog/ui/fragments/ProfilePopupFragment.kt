package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.HomeActivity
import com.learn.flashLearnTagalog.ui.LearningActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfilePopupFragment(private var newActivity: Activity) : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private var userSignedIn = false
    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
        userSignedIn = (auth.currentUser != null)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window: ConstraintLayout = view.findViewById(R.id.clProfileBackground)
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val stats: Button = view.findViewById(R.id.btnStats)
        val signInButton: Button = view.findViewById(R.id.btnSignInOrOut)


        stats.isEnabled = !sharedPref.getBoolean(Constants.KEY_IN_TEST, false)

        if (userSignedIn) {
            signInButton.text = "Sign Out"
        } else {
            signInButton.text = "Sign In"
        }

        signInButton.setOnClickListener {
            if (userSignedIn) {
                auth.signOut()
                userSignedIn = false

                //TODO: reload dialog
                dialog?.dismiss()
            } else {
                val signInDialog: DialogFragment =
                    SignInFragment(true, this::reloadCallback)

                signInDialog.isCancelable = true
                signInDialog.show(parentFragmentManager, "user sign-in")

                dialog?.hide()
            }
        }


        stats.setOnClickListener {
            dialog?.dismiss()
            if (newActivity is HomeActivity) {
                val learning = LearningActivity()
                learning.setType(3)
                startActivity(Intent(newActivity, learning::class.java))
            } else {
                val fragment = StatsFragment()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")
                    ?.commit()
                (activity as LearningActivity?)?.transitionFragment()
            }

        }


        return view
    }

    private fun reloadCallback(b: Boolean) {
        //TODo: reload dialog
        Log.d(TAG, "reload bool: $b")
        dialog?.show()
    }
}