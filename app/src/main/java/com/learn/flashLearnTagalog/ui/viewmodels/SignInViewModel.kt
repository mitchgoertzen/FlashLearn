package com.learn.flashLearnTagalog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {


    private val mutableCallback = MutableLiveData<() -> Unit>()
    val currentCallback: LiveData<() -> Unit> get() = mutableCallback

    fun updateCallback(c: () -> Unit) {
        mutableCallback.value = c
    }

}