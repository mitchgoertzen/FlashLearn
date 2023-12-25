package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.other.Constants.KEY_ENG_FIRST
import com.learn.flashLearnTagalog.ui.viewmodels.LessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Card : Fragment() {

    private val viewModel: LessonViewModel by activityViewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    private var front = true
    private var engFirst: Boolean = true
    private var shownWord: String = ""
    private lateinit var tvCurrWord: TextView
    private lateinit var card: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card, container, false)
        val imFlipCard: ImageButton = view.findViewById(R.id.imFlipCard)

        engFirst = sharedPref.getBoolean(KEY_ENG_FIRST, true)
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            val wordType: TextView = view.findViewById(R.id.tvType)

            val words = if (engFirst)
                word.english
            else
                word.tagalog

            val numberOfEnglishWords = words.split("\\s+".toRegex()).size

            card = view.findViewById(R.id.card)
            card.setImageResource(R.drawable.card_front_new)
            tvCurrWord = view.findViewById(R.id.tvCurrWord)

            //display current word's type
            wordType.text = when (word.type) {
                "n" -> {
                    "Noun"
                }

                "comp" -> {
                    "Compound Noun"
                }

                "v" -> {
                    "Verb"
                }

                "adj" -> {
                    "Adjective"
                }

                "adv" -> {
                    "adverb"
                }

                "inf" -> {
                    "infinitive"
                }

                "intrj" -> {
                    "interjection"
                }

                "prep" -> {
                    "preposition"
                }

                else -> {
                    word.type
                }
            }

            //if english first is set, the front of the card will show english
            //and the reverse will show tagalog
            if (engFirst) {
                shownWord = word.english
                //if the english translation has multiple words,
                //allow for a second line to display the text
                if (numberOfEnglishWords > 1)
                    tvCurrWord.maxLines = 2
                else
                    tvCurrWord.maxLines = 1
            } else {
                shownWord = word.tagalog
                tvCurrWord.maxLines = 1
            }

            tvCurrWord.text = shownWord

            //flip card to reverse side on button press
            imFlipCard.setOnClickListener {
//                TODO: word.id.let { it1 ->
//                DataUtility.flipWord(it1)
//                }

                //display translation of word that is currently shown
                //adjust max lines of text as done previously
                if (shownWord == word.english) {
                    shownWord = word.tagalog
                    tvCurrWord.maxLines = 1
                } else {
                    shownWord = word.english
                    if (numberOfEnglishWords > 1)
                        tvCurrWord.maxLines = 2
                    else
                        tvCurrWord.maxLines = 1
                }
                tvCurrWord.text = shownWord
                front = if (front) {
                    card.setImageResource(R.drawable.card_back)
                    false
                } else {
                    card.setImageResource(R.drawable.card_front_new)
                    true
                }
            }

        }

        return view
    }
}