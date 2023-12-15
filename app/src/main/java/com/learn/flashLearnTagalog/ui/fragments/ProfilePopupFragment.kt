package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.viewmodels.SavedDataViewModel
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//(private var newActivity: Activity)
@AndroidEntryPoint
class ProfilePopupFragment : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: SignInViewModel by activityViewModels()
    private val savedDataModel: SavedDataViewModel by activityViewModels()

    private var userSignedIn = false
    private lateinit var auth: FirebaseAuth
    lateinit var group: ViewGroup

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


        return inflater.inflate(R.layout.fragment_profile_popup, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = view.findViewById(R.id.clProfileBackground)
        auth = Firebase.auth
        userSignedIn = (auth.currentUser != null)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window: ConstraintLayout = group as ConstraintLayout
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val stats: Button = view.findViewById(R.id.btnStats)
        val signInButton: Button = view.findViewById(R.id.btnSignInOrOut)
        val email: TextView = view.findViewById(R.id.tvAccountEmail)


        val unlock: TextView = view.findViewById(R.id.tvUnlocked)
        val prac: TextView = view.findViewById(R.id.tvPracticedLessons)
        val pracWords: TextView = view.findViewById(R.id.tvPracticedWords)
        var words = 0

        savedDataModel.practicedWords.observe(viewLifecycleOwner) { wordLists ->
            for (list in wordLists.values) {
                words += list.size
            }

        }


        val test: TextView = view.findViewById(R.id.tvPassed)
        //val testScore: TextView = view.findViewById(R.id.tvScore)

        savedDataModel.unlockedLessons.observe(viewLifecycleOwner) { lessons ->
            "Lessons Unlocked:${lessons.size}".also { unlock.text = it }
        }
        savedDataModel.practicedLessons.observe(viewLifecycleOwner) { lessons ->
            "Lessons Practiced: ${lessons.size}".also { prac.text = it }
        }
        savedDataModel.passedLessons.observe(viewLifecycleOwner) { lessons ->
            "Lesson Tests Passed: ${lessons.size}".also { test.text = it }
        }

        "Words Practiced: $words".also { pracWords.text = it }
        //"Average Test Score:${TempListUtility.unlockedLessons.size}".also { testScore.text = it }


        stats.isEnabled = !sharedPref.getBoolean(Constants.KEY_IN_TEST, false)

        if (userSignedIn) {
            signInButton.text = "Sign Out"
            email.text = auth.currentUser!!.email
        } else {
            signInButton.text = "Sign In"
        }

        signInButton.setOnClickListener {
            //sign out
            if (userSignedIn) {
                //TODO: reset lists to shared user data
                sharedPref.edit().putBoolean(Constants.KEY_USER_SIGNED_IN, false).apply()
                auth.signOut()
                userSignedIn = false
                reloadCallback()
            } else {
                viewModel.updateCallback { reloadCallback() }
                val signInDialog = SignInFragment()
                val bundle = bundleOf("in_profile" to true)
                signInDialog.arguments = bundle
                signInDialog.isCancelable = true
                signInDialog.show(parentFragmentManager, "user sign-in")

                dialog?.hide()
            }
        }

//        stats.setOnClickListener {
//            dialog?.dismiss()
//            if (newActivity is HomeActivity) {
//                val learning = LearningActivity()
//                learning.setType(3)
//                startActivity(Intent(newActivity, learning::class.java))
//            } else {
//                val fragment = StatsFragment()
//                val transaction = activity?.supportFragmentManager?.beginTransaction()
//                transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("stats")
//                    ?.commit()
//                (activity as LearningActivity?)?.transitionFragment()
//            }
//
//        }

    }

    private fun reloadCallback() {
        val newDialog: DialogFragment = ProfilePopupFragment()
        newDialog.isCancelable = true
        dialog?.dismiss()
        newDialog.show(parentFragmentManager, "profile popup")
    }
}