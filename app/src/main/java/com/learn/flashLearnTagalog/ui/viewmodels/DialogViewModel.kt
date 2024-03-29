package com.learn.flashLearnTagalog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {


    private val mutableText = MutableLiveData<String>()
    val currentText: LiveData<String> get() = mutableText

    fun updateText(s: String) {
        mutableText.value = s
    }
}