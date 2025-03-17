package com.learn.flashLearnTagalog.ui.dialog_fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
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
import com.learn.flashLearnTagalog.other.Constants
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
class SignInFragment : DialogFragment(R.layout.dialog_fragment_sign_in) {

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    var intent: Intent? = activity?.intent

    private val roomViewModel: MainViewModel by activityViewModels()
    private val viewModel: SignInViewModel by activityViewModels()
    private val errors = mapOf(
        "The email address is badly formatted." to "Invalid email format",
        "The supplied auth credential is incorrect, malformed or has expired." to "No account with this Email/password",
        "The given password is invalid. [ Password should be at least 6 characters ]" to "Password must be at least 6 characters",
        "The email address is already in use by another account." to "Email already in use",
        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." to "No Network Connection",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain an upper case character, Password must contain a non-alphanumeric character] ]"
                to "Password must contain at least one upper case letter and one non-alphanumeric character",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain an upper case character] ]"
                to "Password must contain at least one upper case letter",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain a non-alphanumeric character] ]"
                to "Password must contain at least one non-alphanumeric character",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain at least 6 characters] ]"
                to "Password must contain at least 6 characters",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain at least 6 characters, Password must contain an upper case character, Password must contain a non-alphanumeric character] ]"
                to "Password must contain at least 6 characters",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain at least 6 characters, Password must contain a non-alphanumeric character] ]"
                to "Password must contain at least 6 characters",
        "An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain at least 6 characters, Password must contain an upper case character] ]"
                to "Password must contain at least 6 characters",

    )
    private var email = ""
    private var password = ""
    private var confirmPassword = ""
    private var signUp = false
    private val pattern = Patterns.EMAIL_ADDRESS
    private var inProfile: Boolean = false

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
        auth = Firebase.auth
        inProfile = arguments?.getBoolean("in_profile") ?: false
        Log.d(TAG, "profile: ${arguments?.getBoolean("in_profile")}")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.dialog_fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val close: ImageButton = view.findViewById(R.id.ibClose)
        val mContext = FragmentComponentManager.findActivity(context) as Activity
        val header: TextView = view.findViewById(R.id.tvHeader)
        val inputError: TextView = view.findViewById(R.id.tvSignInInputError)
        val continueWithoutAccount: TextView = view.findViewById(R.id.tvContinue)
        val confirmPasswordBox: LinearLayout = view.findViewById(R.id.llConfirmPassword)
        val emailText: EditText = view.findViewById(R.id.etEmail)
        val passwordText: EditText = view.findViewById(R.id.etPassword)
        val confirmPasswordText: EditText = view.findViewById(R.id.etConfirmPassword)
        val editTextGroup: LinearLayout = view.findViewById(R.id.llPasswordBox)
        val emailConfirmationCode: LinearLayout = view.findViewById(R.id.llEmailConfirmationCode)
        val signUpPrompt: LinearLayout = view.findViewById(R.id.llSignUpPrompt)
        val signUpText: TextView = view.findViewById(R.id.tvSignUpInfo)
        val signUpButton: TextView = view.findViewById(R.id.tvSignUpPrompt)
        val confirmButton: Button = view.findViewById(R.id.btnConfirm)
        val form: ImageView = view.findViewById(R.id.ivFormBackground)
        val passwordVisible: ImageView = view.findViewById(R.id.ivPasswordVisible)
        val noAccount: TextView = view.findViewById(R.id.tvContinue)

        val typeface: Typeface = passwordText.typeface


        passwordText.inputType =
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        confirmPasswordText.inputType = passwordText.inputType

        passwordText.typeface = typeface
        confirmPasswordText.typeface = typeface


//        val actionCodeSettings = actionCodeSettings {
//            url = "https://flash-learn-6fbc4.firebaseapp.com/"
//
//            handleCodeInApp = true
//            setAndroidPackageName(
//                "com.learn.flashLearnTagalog",
//                true, // installIfNotAvailable
//                "12", // minimumVersion
//            )
//        }


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

        form.setImageResource(R.drawable.sign_in_box)

        inputError.text = ""
        confirmPasswordBox.visibility = View.GONE

        signUpPrompt.setOnClickListener {

            inputError.text = ""
            if(signUp){
                header.text = "Sign In"
                form.setImageResource(R.drawable.sign_in_box)
                confirmPasswordBox.visibility = View.GONE
                signUpText.text = "Don't have an account? "
                signUpButton.text = "Sign Up!"
                confirmButton.text = "Sign In"
            }else{
                header.text = "Sign Up"
                form.setImageResource(R.drawable.sign_up_box)
                confirmPasswordBox.visibility = View.VISIBLE
                signUpText.text = "Back to "
                signUpButton.text = "Sign In"
                confirmButton.text = "Sign Up"
            }
            signUp = !signUp

        }

        emailText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkValidEmail(emailText.text.toString(), inputError)
            }
        }

        passwordVisible.setOnClickListener {
            if (passwordText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordText.inputType =
                    (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                passwordVisible.setImageResource(R.drawable.not_visible)
            } else {
                passwordText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordVisible.setImageResource(R.drawable.visible)
            }

            confirmPasswordText.inputType = passwordText.inputType

            passwordText.typeface = typeface
            confirmPasswordText.typeface = typeface
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


                        confirmButton.text = "Confirm"
                        //TODO: real email --> two factor?
//                        editTextGroup.visibility = View.GONE
//                        emailConfirmationCode.visibility = View.VISIBLE
//
//                        auth.signInWithEmailLink(email, actionCodeSettings)
//                            .addOnCompleteListener { task ->
//                                Log.d(TAG, "task: $task")
//                                if (task.isSuccessful) {
//                                    Log.d(TAG, "Email sent.")
//                                }
//                                else {
//                                    val exception = task.exception
//                                    Log.e("FirebaseAuth", "Error sending email:", exception)
//                                    // Handle the error appropriately - show the user a user-friendly message.
//
//                                    // Example error handling (adapt based on the types of exceptions you anticipate)
//                                    when (exception) {
//                                        is FirebaseAuthWeakPasswordException -> {
//                                            // Handle weak password
//                                        }
//                                        is FirebaseAuthInvalidCredentialsException -> {
//                                            // Handle invalid credentials
//                                        }
//                                        is FirebaseAuthUserCollisionException -> {
//                                            // Handle user collision
//                                        }
//                                        is FirebaseAuthException -> {
//                                            // Handle any other Firebase Auth exception
//                                            // Display the exception message to the user or in the log.
//                                            Log.d(TAG, "Error sending email: ${exception.message}")
//                                        }
//                                        else -> {
//                                            // Handle unexpected errors
//                                            Log.d(TAG, "An unexpected error occurred. Please try again.")
//                                            if (exception != null) {
//                                                Log.d(TAG, " ${exception.message}")
//                                            }
//                                        }
//                                    }
//                                }
//                            }


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
                                    val exception: Exception = task.exception as FirebaseException

                                    Log.d(TAG, "message: ${exception.message}")

                                    inputError.text = errors[exception.message]
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

        noAccount.setOnClickListener {
            sharedPref.edit().putBoolean(Constants.KEY_USER_SIGNED_IN, false).apply()
            val listScope = CoroutineScope(Job() + Dispatchers.Main)
            listScope.launch {
                DataUtility.updateLocalData(mContext, signUp = false, rewriteJSON = false)
                listScope.cancel()
            }
            close()
        }

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