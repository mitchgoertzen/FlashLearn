package com.learn.flashLearnTagalog.ui.dialog_fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.ui.viewmodels.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HintDialogFragment : DialogFragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: DialogViewModel by activityViewModels()

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

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return inflater.inflate(R.layout.fragment_hint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val close: ImageButton = view.findViewById(R.id.ibClose)
        val hint: TextView = view.findViewById(R.id.tvHint)

        close.setOnClickListener {
            dialog?.dismiss()
        }

        viewModel.currentText.observe(viewLifecycleOwner) { text ->
            hint.text = text
        }
    }
}