package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.data.User
import com.learn.flashLearnTagalog.db.DataUtility
import com.learn.flashLearnTagalog.db.JsonUtility
import com.learn.flashLearnTagalog.other.Constants.KEY_USER_SIGNED_IN
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import com.learn.flashLearnTagalog.ui.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.FragmentComponentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : DialogFragment(R.layout.fragment_sign_in) {


    private var inProfile: Boolean = false

    private val roomViewModel: MainViewModel by activityViewModels()
    private val viewModel: SignInViewModel by activityViewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val errors = mapOf(
        "The email address is badly formatted." to "Invalid email format",
        "The supplied auth credential is incorrect, malformed or has expired." to "No account with this Email/password",
        "The given password is invalid. [ Password should be at least 6 characters ]" to "Password must be at least 6 characters",
        "The email address is already in use by another account." to "Email already in use"
    )

    private lateinit var mContext: Activity

    private lateinit var auth: FirebaseAuth
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private var signUp = false
    private val pattern = Patterns.EMAIL_ADDRESS

//    private val manager = parentFragmentManager

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

        auth = Firebase.auth

        inProfile = arguments?.getBoolean("in_profile") ?: false

        Log.d(TAG, "profile: ${arguments?.getBoolean("in_profile")}")
        if (!inProfile) {
            if (auth.currentUser == null) {
                val roomScope = CoroutineScope(Job() + Dispatchers.Main)
                roomScope.launch {

                    if (roomViewModel.getLessonCount() > 0) {
                        val unlocked = mutableListOf<String>()
                        val practiced = mutableListOf<String>()
                        val passed = mutableListOf<String>()

                        roomViewModel.getAllLessons().observe(viewLifecycleOwner) {

                            val lessons = it.toMutableList()

                            for (l in lessons) {

                                val id = "${l.category}_${l.level}"

                                if (!l.locked) {
                                    unlocked.add(id)
                                }

                                if (l.practiceCompleted) {
                                    practiced.add(id)
                                }

                                if (l.testPassed) {
                                    passed.add(id)
                                }

                            }

                            JsonUtility.writeJSON(
                                requireActivity(),
                                "unlockedLessons.json",
                                unlocked,
                                true
                            )
                            JsonUtility.writeJSON(
                                requireActivity(),
                                "practicedLessons.json",
                                practiced,
                                true
                            )
                            JsonUtility.writeJSON(
                                requireActivity(),
                                "passedLessons.json",
                                passed,
                                true
                            )

                            roomViewModel.nukeLessons()
                            roomViewModel.nukeTable()
                        }


                    }
                    roomScope.cancel()
                }
            }
        }
        val close: ImageButton = view.findViewById(R.id.ibClose)


        val mContext = FragmentComponentManager.findActivity(context) as Activity

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
//        tapBlocker.setOnTouchListener { _, _ ->
//            true
//        }

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
            close.visibility = View.VISIBLE
            close.setOnClickListener {
                dialog?.dismiss()
                close()
            }

        } else {
            close.visibility = View.GONE
            continueWithoutAccount.visibility = View.VISIBLE
        }

        confirmButton.setOnClickListener {
            email = emailText.text.toString()
            password = passwordText.text.toString()
            confirmPassword = confirmPasswordText.text.toString()

            inputError.text = ""
            if (email != "" && password != "") {
                if (signUp) {
                    if (password == confirmPassword) {
                        //TODO: real email --> two factor?
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    if (!inProfile) {
                                        close()
                                    }
                                    Log.d(TAG, "createUserWithEmail:success")

                                    val loadLessonsScope = CoroutineScope(Job() + Dispatchers.Main)
                                    loadLessonsScope.launch {

                                        val newUser = User(email, currentVersion = 1)

                                        async {
                                            DataUtility.addUser(
                                                newUser
                                            )
                                            Log.d(TAG, "add user done")
                                        }.await()

                                        DataUtility.updateLocalData(
                                            mContext,
                                            signUp = true,
                                            rewriteJSON = false
                                        )
                                        if (inProfile) {
                                            close()
                                        }
                                        loadLessonsScope.cancel()
                                    }

                                } else {

                                    val code: FirebaseException =
                                        task.exception as FirebaseException

                                    Log.d(TAG, "ERROR: ${code.message.toString()}")

                                    inputError.text = errors[code.message]
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
                                val userScope = CoroutineScope(Job() + Dispatchers.Main)
                                userScope.launch {
                                    DataUtility.updateLocalData(
                                        mContext,
                                        signUp = false,
                                        rewriteJSON = true
                                    )
                                    if (inProfile) {
                                        close()
                                    }
                                    userScope.cancel()
                                }

                                if (!inProfile) {
                                    close()
                                }

                                sharedPref.edit().putBoolean(KEY_USER_SIGNED_IN, true).apply()
                                Log.d(TAG, "signInWithEmail:success")
                            } else {
                                val code: FirebaseException = task.exception as FirebaseException

                                Log.d(TAG, code.message.toString())
                                inputError.text = errors[code.message]
                            }
                        }
                }
            } else {
                inputError.text = "Must enter email/password"

            }
        }

        val noAccount: TextView = view.findViewById(R.id.tvContinue)
        noAccount.setOnClickListener {
            sharedPref.edit().putBoolean(KEY_USER_SIGNED_IN, false).apply()
            val listScope = CoroutineScope(Job() + Dispatchers.Main)
            listScope.launch {
                DataUtility.updateLocalData(mContext, signUp = false, rewriteJSON = false)
                listScope.cancel()
            }
            close()
        }

        return view
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        close()
    }


    private fun close() {

        viewModel.currentCallback.value!!.invoke()
        // onClose!!.invoke()
        dialog?.dismiss()
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