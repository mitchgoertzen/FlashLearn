package com.learn.flashLearnTagalog.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.learn.flashLearnTagalog.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FilterLessonFragment() : DialogFragment() {

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
        val view = inflater.inflate(R.layout.fragment_filter_lesson, container, false)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window : FrameLayout = view.findViewById(R.id.clHintBackground)


        window.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> dialog?.dismiss()
            }
            v?.onTouchEvent(event) ?: true
        }

        val popup :LinearLayout = view.findViewById(R.id.llFilterPopup)

        popup.setOnTouchListener { _, _ ->
            true
        }

        popup.setBackgroundResource(R.drawable.filter_lesson_popup)

        val languages = resources.getStringArray(R.array.Sorting)

        val spinner: Spinner = view.findViewById(R.id.spinner)

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, languages
        )

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("nothing")
            }
        }

        return view
    }
}

