package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Lesson
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants.KEY_DIFFICULTY
import com.learn.flashLearnTagalog.other.Constants.KEY_ENABLE_PRONUNCIATION
import com.learn.flashLearnTagalog.other.Constants.KEY_ENG_FIRST
import com.learn.flashLearnTagalog.other.Constants.KEY_MODE
import com.learn.flashLearnTagalog.other.Constants.KEY_NUM_WORDS
import com.learn.flashLearnTagalog.other.Constants.KEY_PRACTICE_NEW_WORDS
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_HINTS
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_IMAGE
import com.learn.flashLearnTagalog.other.Constants.KEY_SHOW_WORD
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment(private var currentLesson: Lesson) : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val viewModel: MainViewModel by viewModels()


    private var practiceWordList: MutableList<Word> = mutableListOf()
    private var testWordList: MutableList<Word> = mutableListOf()
    private var practicedWordListLargeEnough: Boolean = true

    private var showImage: Boolean = false
    private var showEngFirst: Boolean = false
    private var showHints: Boolean = false
    private var enablePronunciation: Boolean = false

    private var lessonMode: Boolean = true
    private var practiceNewWords: Boolean = false
    private var showWord: Boolean = false

    private var totalWords: Int = 10
    private var wordsBarValue = 0
    private var difficultyBarValue = 0

    private var searchInProgress: Boolean = false

    private var thumbView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        thumbView = inflater.inflate(R.layout.seekbar_thumb_text, container, false)

        val scrollView: ScrollView = view.findViewById(R.id.svSettings)

        val mode: SwitchCompat = view.findViewById(R.id.scMode)
        val wordSwitch: SwitchCompat = view.findViewById(R.id.scShowWords)
        val imageSwitch: SwitchCompat = view.findViewById(R.id.sShowImage)
        val languageSwitch: SwitchCompat = view.findViewById(R.id.sEnglishFirst)
        val hintSwitch: SwitchCompat = view.findViewById(R.id.scHints)
        val pronunciationSwitch: SwitchCompat = view.findViewById(R.id.scPronunciation)

        val newWordSwitch: SwitchCompat = view.findViewById(R.id.scNewWords)
        val difficulty: SeekBar = view.findViewById(R.id.sbDifficulty)
        val numWords: SeekBar = view.findViewById(R.id.sbNumWords)

        val availableWords: TextView = view.findViewById(R.id.tvWordsAvailable)
        val confirmButton: Button = view.findViewById(R.id.btnConfirm)

        difficultyBarValue = sharedPref.getInt(KEY_DIFFICULTY, 0)
        refreshWordList(confirmButton, mode, newWordSwitch, availableWords)

        practicedWordListLargeEnough = testWordList.size > 0

        lessonMode = sharedPref.getBoolean(KEY_MODE, true)
        mode.isChecked = lessonMode

        if (lessonMode) {
            mode.thumbDrawable = getThumb("Practice")
            activateSwitch(newWordSwitch)
        } else {
            mode.thumbDrawable = getThumb("Test")
            deactivateSwitch(newWordSwitch)
        }

        mode.setOnCheckedChangeListener { _, isChecked ->
            lessonMode = isChecked

            refreshWordList(confirmButton, mode, newWordSwitch, availableWords)

            sharedPref.edit()
                .putBoolean(KEY_MODE, lessonMode)
                .apply()

            if (isChecked) {
                mode.thumbDrawable = getThumb("Practice")
                activateSwitch(newWordSwitch)
            } else {
                mode.thumbDrawable = getThumb("Test")
                deactivateSwitch(newWordSwitch)
            }
        }


        showWord = sharedPref.getBoolean(KEY_SHOW_WORD, true)
        wordSwitch.isChecked = showWord
        wordSwitch.setOnClickListener {
            showWord = !showWord
            sharedPref.edit()
                .putBoolean(KEY_SHOW_WORD, showWord)
                .apply()
        }
        deactivateSwitch(wordSwitch)

        showImage = sharedPref.getBoolean(KEY_SHOW_IMAGE, false)
        imageSwitch.isChecked = showImage
        imageSwitch.setOnClickListener {
            showImage = !showImage
            sharedPref.edit()
                .putBoolean(KEY_SHOW_IMAGE, showImage)
                .apply()
        }
        deactivateSwitch(imageSwitch)

        practiceNewWords = sharedPref.getBoolean(KEY_PRACTICE_NEW_WORDS, true)
        newWordSwitch.isChecked = practiceNewWords
        newWordSwitch.setOnClickListener {
            practiceNewWords = !practiceNewWords
            sharedPref.edit()
                .putBoolean(KEY_PRACTICE_NEW_WORDS, practiceNewWords)
                .apply()
            refreshWordList(confirmButton, mode, newWordSwitch, availableWords)

        }

        showEngFirst = sharedPref.getBoolean(KEY_ENG_FIRST, true)
        languageSwitch.isChecked = showEngFirst
        languageSwitch.setOnClickListener {
            showEngFirst = !showEngFirst
            sharedPref.edit()
                .putBoolean(KEY_ENG_FIRST, showEngFirst)
                .apply()
        }

        wordsBarValue = sharedPref.getInt(KEY_NUM_WORDS, 0)
        numWords.progress = wordsBarValue
        totalWords = (wordsBarValue + 1) * 10
        setNumWordBarText(numWords, wordsBarValue)
        numWords.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {

                setNumWordBarText(numWords, progress)
                wordsBarValue = progress
                totalWords = (progress + 1) * 10
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sharedPref.edit()
                    .putInt(KEY_NUM_WORDS, wordsBarValue)
                    .apply()
            }
        })

        difficulty.progress = difficultyBarValue
        difficulty.thumbOffset = 16
        difficulty.thumb = getThumb("${difficultyBarValue + 1}")
        difficulty.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                difficulty.thumb = getThumb("${progress + 1}")
                // setDifficultyBarText(difficulty, progress)

                difficultyBarValue = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                confirmButton.isEnabled = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sharedPref.edit()
                    .putInt(KEY_DIFFICULTY, difficultyBarValue)
                    .apply()
                refreshWordList(confirmButton, mode, newWordSwitch, availableWords)
            }
        })

        showHints = sharedPref.getBoolean(KEY_SHOW_HINTS, false)
        hintSwitch.isChecked = showHints
        hintSwitch.setOnClickListener {
            showHints = !showHints
            sharedPref.edit()
                .putBoolean(KEY_SHOW_HINTS, showHints)
                .apply()
        }
        deactivateSwitch(hintSwitch)

        enablePronunciation = sharedPref.getBoolean(KEY_ENABLE_PRONUNCIATION, false)
        pronunciationSwitch.isChecked = enablePronunciation
        pronunciationSwitch.setOnClickListener {
            enablePronunciation = !enablePronunciation
            sharedPref.edit()
                .putBoolean(KEY_ENABLE_PRONUNCIATION, enablePronunciation)
                .apply()
        }
        deactivateSwitch(pronunciationSwitch)

        availableWords.setOnClickListener {

            val practiceHint =
                "To practice more words, consider enabling \"Practice New Words\",\n" +
                        "or change the difficulty"
            val testHint = "To be tested on more words, you need practice them first"

            val dialog: DialogFragment = if (lessonMode) {
                HintFragment(practiceHint)
            } else {
                HintFragment(testHint)
            }

            dialog.isCancelable = true
            dialog.show(requireActivity().supportFragmentManager, "hint popup")
        }

        confirmButton.setOnClickListener {


            //practice
            if (lessonMode) {
                val chosenWordList = if (sharedPref.getBoolean(KEY_PRACTICE_NEW_WORDS, true)) {
                    practiceWordList.asSequence().shuffled().take(totalWords)
                        .toMutableList()
                } else {
                    if (practicedWordListLargeEnough) {
                        testWordList.asSequence().shuffled().take(totalWords)
                            .toMutableList()
                    } else {
                        testWordList.asSequence().shuffled().take(testWordList.size)
                            .toMutableList()
                    }
                }
                val fragment = PracticeFragment(chosenWordList, currentLesson)
                transitionFragment(fragment)

            }
            //test
            else {
                val fragment = TestFragment(
                    testWordList.asSequence().shuffled().take(totalWords).toMutableList(),
                    currentLesson
                )
                transitionFragment(fragment)
            }
        }

        return view
    }

    private fun setNumWordBarText(numWords: SeekBar, progress: Int) {

        numWords.thumb = getThumb(((progress + 1) * 10).toString())
        when (progress) {
            0 -> {
                numWords.thumbOffset = 0
                numWords.setPadding(0, 0, 0, 0)
            }
            1 -> {
                numWords.thumbOffset = 0
                numWords.setPadding(0, 0, 0, 0)
            }
            2 -> {
                numWords.thumbOffset = 0
                numWords.setPadding(0, 0, 12, 0)
            }
        }

    }

    private fun transitionFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.main_nav_container, fragment)?.addToBackStack("custom lesson")
            ?.commit()
        (activity as LearningActivity?)?.transitionFragment()
    }

    private fun activateSwitch(switch: SwitchCompat) {
        switch.scaleX = 1f
        switch.scaleY = 1f
        switch.isEnabled = true
        switch.alpha = 1f
    }

    private fun deactivateSwitch(switch: SwitchCompat) {
        switch.scaleX = .8f
        switch.scaleY = .8f
        switch.isEnabled = false
        switch.alpha = .8f
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun refreshWordList(
        confirmButton: Button,
        mode: SwitchCompat,
        newWordSwitch: SwitchCompat,
        availableWords: TextView
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            suspend {
                gatherTestWords()
                gatherPracticeWords()
                Handler(Looper.getMainLooper()).postDelayed({
                    practicedWordListLargeEnough = testWordList.size > 0
                    var isAreText = "is"
                    var wordsText = "word"
                    var words = 0
                    words = if (lessonMode) {
                        if (practiceNewWords) {
                            practiceWordList.size
                        } else {
                            if (practicedWordListLargeEnough) {
                                testWordList.size
                            } else {
                                practiceWordList.size
                            }
                        }
                    } else {
                        testWordList.size
                    }

                    if (words != 1) {
                        isAreText = "are"
                        wordsText += "s"
                    }

                    availableWords.text = "There $isAreText currently $words $wordsText available"

                    confirmButton.isEnabled = true
                    checkForTestingWords(mode, newWordSwitch)
                }, 1000)
            }.invoke()
        }
    }

    private fun checkForTestingWords(mode: SwitchCompat, newWord: SwitchCompat) {
        if (testWordList.size > 0) {
            mode.scaleX = 1f
            mode.scaleY = 1f
            mode.alpha = 1f

            mode.isEnabled = true
            newWord.isEnabled = lessonMode
            newWord.isChecked = practiceNewWords

        } else {
            mode.scaleX = .8f
            mode.scaleY = .8f
            mode.alpha = .8f

            mode.isChecked = true
            mode.isEnabled = false
            practiceNewWords = true
            newWord.isChecked = practiceNewWords
            newWord.isEnabled = false
        }

        sharedPref.edit()
            .putBoolean(KEY_PRACTICE_NEW_WORDS, practiceNewWords)
            .apply()
    }

    private fun gatherPracticeWords() {
        when (difficultyBarValue) {
            0 -> {
                viewModel.getWordsByDifficulty(0, 0, 5).observe(viewLifecycleOwner) {
                    practiceWordList = it.toMutableList()
                }
            }
            1 -> {
                viewModel.getWordsByDifficulty(0, 6, 7).observe(viewLifecycleOwner) {
                    practiceWordList = it.toMutableList()
                }
            }
            2 -> {
                viewModel.getWordsByDifficulty(0, 8, 9).observe(viewLifecycleOwner) {
                    practiceWordList = it.toMutableList()
                }
            }
            3 -> {
                viewModel.getWordsByDifficulty(0, 10, 11).observe(viewLifecycleOwner) {
                    practiceWordList = it.toMutableList()
                }
            }
            4 -> {
                viewModel.getWordsByDifficulty(0, 12, 100).observe(viewLifecycleOwner) {
                    practiceWordList = it.toMutableList()
                }
            }

        }
    }

    private fun gatherTestWords() {
        when (difficultyBarValue) {
            0 -> {
                viewModel.getWordsByDifficulty(1, 0, 4).observe(viewLifecycleOwner) {
                    testWordList = it.toMutableList()
                }
            }
            1 -> {
                viewModel.getWordsByDifficulty(1, 5, 6).observe(viewLifecycleOwner) {
                    testWordList = it.toMutableList()
                }
            }
            2 -> {
                viewModel.getWordsByDifficulty(1, 7, 8).observe(viewLifecycleOwner) {
                    testWordList = it.toMutableList()
                }
            }
            3 -> {
                viewModel.getWordsByDifficulty(1, 9, 10).observe(viewLifecycleOwner) {
                    testWordList = it.toMutableList()
                }
            }
            4 -> {
                viewModel.getWordsByDifficulty(1, 11, 100).observe(viewLifecycleOwner) {
                    testWordList = it.toMutableList()
                }
            }
        }
    }

    private fun getThumb(text: String): Drawable {
        (thumbView?.findViewById(R.id.tvProgress) as TextView).text = text
        thumbView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            thumbView!!.measuredWidth,
            thumbView!!.measuredHeight,
            Bitmap.Config.RGB_565
        )
        val paint = Paint()
        val filter: ColorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(requireContext(), R.color.white),
            PorterDuff.Mode.SRC_IN
        )
        paint.colorFilter = filter

        val canvas = Canvas(bitmap)

        val clipPath = Path()
        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        clipPath.addRoundRect(rect, 5f, 5f, Path.Direction.CW)
        canvas.clipPath(clipPath)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        thumbView!!.layout(0, 0, thumbView!!.measuredWidth, thumbView!!.measuredHeight)
        thumbView!!.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }
}
