package com.learn.flashLearnTagalog.ui.dialog_fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.Util
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePopupFragment : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var confirmDeletePrompt: ConstraintLayout
    private var auth = Firebase.auth

    private val viewModel: SignInViewModel by activityViewModels()

    private var deletingAccount = false
    private var userSignedIn = false
    lateinit var group: ViewGroup

    private val signInMessage: String =
        "You Are Not Signed In. \n You may lose progress if this device's data is erased."
    private val testSignInMessage: String =
        "Sign In features are disabled while in a test."


    private var onDismissFunction: () -> Unit = {}

    fun setOnDismissFunction(block: () -> Unit) {
        onDismissFunction = block
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissFunction()
        super.onDismiss(dialog)
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "user: ${auth.currentUser}")
        userSignedIn = (auth.currentUser != null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.dialog_fragment_profile_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var words = 0

        val signInDialog = SignInFragment()
        val statsButton: Button = view.findViewById(R.id.btnStats)
        val signInButton: Button = view.findViewById(R.id.btnSignInOrOut)
        val passwordText: EditText = view.findViewById(R.id.etDeleteAccountPassword)
        val close: ImageButton = view.findViewById(R.id.ibClose)
        val attention: ImageView = view.findViewById(R.id.ivAttention)
        val email: TextView = view.findViewById(R.id.tvAccountEmail)
        val deleteAccountText: TextView = view.findViewById(R.id.tvDeleteAccount)
        val inputError: TextView = view.findViewById(R.id.tvDeleteAccountInputError)
        val confirmDeleteButton: Button = view.findViewById(R.id.btnConfirmAccountDelete)
        val endDeletionText: TextView = view.findViewById(R.id.tvContinue2)
        val unlock: TextView = view.findViewById(R.id.tvUnlocked)
        val prac: TextView = view.findViewById(R.id.tvPracticedLessons)
        val pracWords: TextView = view.findViewById(R.id.tvPracticedWords)
        val test: TextView = view.findViewById(R.id.tvPassed)
        val signInMsg: TextView = view.findViewById(R.id.tvSignInMsg)

        confirmDeletePrompt = view.findViewById(R.id.clAccountDeletePrompt)
        group = view.findViewById(R.id.clProfileBackground)

        confirmDeletePrompt.visibility = View.GONE

        Util.handleButtonEnable(statsButton,!sharedPref.getBoolean(Constants.KEY_IN_TEST, false))

        if (sharedPref.getBoolean(Constants.KEY_IN_TEST, false)) {
            Util.handleButtonEnable(signInButton,false)
            signInMsg.text = testSignInMessage
        } else {
            Util.handleButtonEnable(signInButton,true)
            signInMsg.text = signInMessage
        }

        close.setOnClickListener {
            dialog?.dismiss()
            close()
        }

        for (list in TempListUtility.viewedWords) {
            words += list.value.size
        }

        "Lessons Unlocked:${TempListUtility.unlockedLessons.size}".also { unlock.text = it }
        "Lessons Practiced: ${TempListUtility.practicedLessons.size}".also { prac.text = it }
        "Lesson Tests Passed: ${TempListUtility.passedLessons.size}".also { test.text = it }
        "Words Practiced: $words".also { pracWords.text = it }
        //"Average Test Score:${TempListUtility.unlockedLessons.size}".also { testScore.text = it }

        if (userSignedIn) {

            attention.visibility = View.GONE
            signInMsg.visibility = View.GONE
            signInButton.text = "Sign Out"
            val userEmail = auth.currentUser!!.email
            email.visibility = View.VISIBLE
            email.text = auth.currentUser!!.email

            deleteAccountText.visibility = View.VISIBLE

            deleteAccountText.setOnClickListener {
                deletingAccount = true
                confirmDeletePrompt.visibility = View.VISIBLE
                confirmDeletePrompt.setOnClickListener { _ ->
                }
                endDeletionText.setOnClickListener {

                    val inputMethodManager =
                        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    deletingAccount = false
                    confirmDeletePrompt.visibility = View.GONE
                }
            }

            confirmDeleteButton.setOnClickListener {

                inputError.visibility = View.GONE

                val authUser = auth.currentUser!!


                val password = passwordText.text.toString()

                if (password.isNotEmpty()) {
                    val credential = EmailAuthProvider
                        .getCredential(userEmail.toString(), password)

                    // Prompt the user to re-provide their sign-in credentials
                    authUser.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User re-authenticated.")
                                authUser.delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            sharedPref.edit()
                                                .putBoolean(Constants.KEY_USER_SIGNED_IN, false)
                                                .apply()
                                            Log.d(TAG, "User account deleted.")
                                            reloadCallback()
                                        }
                                    }
                            } else {
                                inputError.text = "incorrect password"
                                inputError.visibility = View.VISIBLE
                            }

                        }
                } else {
                    inputError.text = "password cannot be empty"
                    inputError.visibility = View.VISIBLE
                }


            }
        } else {
            email.visibility = View.GONE
            signInButton.text = "Sign In"
            deleteAccountText.visibility = View.GONE
        }

        signInButton.setOnClickListener {
            //sign out
            if (userSignedIn) {
                sharedPref.edit().putBoolean(Constants.KEY_USER_SIGNED_IN, false).apply()
                auth.signOut()
                userSignedIn = false
                TempListUtility.unlockedLessons =
                    JsonUtility.getUserDataList(requireActivity(), "unlockedLessons.json")
                TempListUtility.practicedLessons =
                    JsonUtility.getUserDataList(requireActivity(), "practicedLessons.json")
                TempListUtility.passedLessons =
                    JsonUtility.getUserDataList(requireActivity(), "passedLessons.json")
                reloadCallback()
            } else {
                viewModel.updateCallback { reloadCallback() }
                if (!signInDialog.isAdded) {
                    val bundle = bundleOf("in_profile" to true)
                    signInDialog.arguments = bundle
                    signInDialog.isCancelable = true
                    signInDialog.show(parentFragmentManager, "user sign-in")

                    dialog?.hide()
                }

            }


            TempListUtility.viewedWords = JsonUtility.getViewedWords(requireActivity())
            TempListUtility.viewedLessons = JsonUtility.getViewedLessons(requireActivity())
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

    private fun close() {
//        viewModel.isRefreshActive.observe(viewLifecycleOwner) { active ->
//            if (active) {
//                viewModel.currentRefreshCallback.value!!.invoke()
//                viewModel.updateRefreshActive(false)
//            }
//        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        close()
    }

    private fun reloadCallback() {
        val newDialog: DialogFragment = ProfilePopupFragment()
        Log.d(TAG, "ACTIVITY: $activity")
        if (!newDialog.isAdded) {
            newDialog.isCancelable = true
            dialog?.dismiss()
            activity?.let {
                newDialog.show(
                    it.supportFragmentManager, "profile popup"
                )
            }
        }
    }
}

