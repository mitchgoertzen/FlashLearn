package com.learn.flashLearnTagalog.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.learn.flashLearnTagalog.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class SubmitNewLessonFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_new_lesson, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val submit: Button = view.findViewById(R.id.btnSubmitNewLesson)

        submit.setOnClickListener {

            val file: Int = R.raw.tagalog_animals_1
            if (file != -1) {
                val input: InputStream = resources.openRawResource(file)
                val reader = BufferedReader(InputStreamReader(input))

                var currentString: String? = ""

                while (true) {
                    try {
                        //if the currentString is empty, exit while()
                        if (reader.readLine().also { currentString = it } == null) break
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                input.close()
            }

        }
    }

}