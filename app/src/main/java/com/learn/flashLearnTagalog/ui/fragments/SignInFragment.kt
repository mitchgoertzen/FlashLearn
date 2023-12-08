package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.TempListUtility
import com.learn.flashLearnTagalog.data.User
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.other.Constants.KEY_USER_SIGNED_IN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1


class SignInFragment(
    private val inProfile: Boolean = false,
    private val onClose: KFunction1<Boolean, Unit>? = null,
    private val initUser: KFunction1<User, Unit>? = null
) : DialogFragment() {


    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private var signUp = false
    private val pattern = Patterns.EMAIL_ADDRESS

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
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val window: ConstraintLayout = view.findViewById(R.id.clBackground)
        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    invokeCallbacks(true, null)
                    dialog?.dismiss()
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        val tapBlocker: ConstraintLayout = view.findViewById(R.id.clTapBlock)

        val form: ImageView = view.findViewById(R.id.ivFormBackground)
        form.setImageResource(R.drawable.sign_in_box)

        val header: TextView = view.findViewById(R.id.tvHeader)
        val inputError: TextView = view.findViewById(R.id.tvInputError)

        val continueWithoutAccount: TextView = view.findViewById(R.id.tvContinue)
        inputError.text = ""

        val confirmPasswordBox: LinearLayout = view.findViewById(R.id.llConfirmPassword)
        confirmPasswordBox.visibility = View.GONE


        val emailText: EditText = view.findViewById(R.id.etEmail)
        val passwordText: EditText = view.findViewById(R.id.etPassword)
        val confirmPasswordText: EditText = view.findViewById(R.id.etConfirmPassword)

        val signUpPrompt: LinearLayout = view.findViewById(R.id.llSignUpPrompt)
        val signUpText: TextView = view.findViewById(R.id.tvSignUpPrompt)

        val confirmButton: Button = view.findViewById(R.id.btnConfirm)

        //block closing of dialog when its own window is touched
        tapBlocker.setOnTouchListener { _, _ ->
            true
        }

        signUpText.setOnClickListener {
            header.text = "Sign Up"
            inputError.text = ""
            form.setImageResource(R.drawable.sign_up_box)
            signUpPrompt.visibility = View.GONE
            confirmPasswordBox.visibility = View.VISIBLE
            signUp = true
        }

        emailText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkValidEmail(emailText.text.toString(), inputError)
            }
        }


        if (inProfile) {
            continueWithoutAccount.visibility = View.GONE
        }


        confirmButton.setOnClickListener {
            email = emailText.text.toString()
            password = passwordText.text.toString()
            confirmPassword = confirmPasswordText.text.toString()

            auth = Firebase.auth

            inputError.text = ""
            if (checkValidEmail(email, inputError)) {
                if (signUp) {
                    if (password == confirmPassword) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    sharedPref.edit().putBoolean(KEY_USER_SIGNED_IN, true).apply()
                                    invokeCallbacks(false, null)
                                    dialog?.dismiss()
                                    Log.d(TAG, "createUserWithEmail:success")
                                    val firebaseUser = auth.currentUser

                                    val loadLessonsScope = CoroutineScope(Job() + Dispatchers.Main)
                                    loadLessonsScope.launch {

                                        val lessons =
                                            async { DataUtility.getLessonIDsByLevel(1) }.await()
                                        Log.d(TAG, "lessons: ${lessons.size}")
                                        for (l in lessons) {
                                            if (l.level == 1)
                                                TempListUtility.unlockedLessons.add(l.id)
                                        }
                                        TempListUtility.unlockedLessons.add("Custom\nLesson_0")
                                        val newUser = User(email)
                                        newUser.unlockedLessons = TempListUtility.unlockedLessons
                                        async {
                                            DataUtility.addUser(
                                                newUser,
                                                firebaseUser!!.uid
                                            )
                                        }.await()
                                        invokeCallbacks(false, newUser)
                                        loadLessonsScope.cancel()
                                    }

                                    Log.d(TAG, firebaseUser!!.email.toString())
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                }
                            }
                    } else {
                        inputError.text = "Passwords do not match"
                        //red
                        //shake button?
                    }
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                invokeCallbacks(true, null)
                                sharedPref.edit().putBoolean(KEY_USER_SIGNED_IN, true).apply()
                                dialog?.dismiss()
                                Log.d(TAG, "signInWithEmail:success")
                            } else {

                                inputError.text = "Incorrect email/password"
                                Log.w(TAG, "signInWithEmail:failure", task.exception)

                            }
                        }
                }
            }
        }

        return view
    }

    private fun invokeCallbacks(initData: Boolean, user: User?) {

        if (onClose != null) {
            if (user != null) {
                initUser!!.invoke(user)
            } else {
                onClose.invoke(initData)
            }
        }
    }

    private fun checkValidEmail(email: String, errorText: TextView): Boolean {
        return if (!pattern.matcher(email).matches()) {
            errorText.text = "Invalid Email"
            //red
            //not email text
            Log.d(TAG, "NOT AN EMAIL")
            false
        } else {
            errorText.text = ""
            true
        }
    }
}