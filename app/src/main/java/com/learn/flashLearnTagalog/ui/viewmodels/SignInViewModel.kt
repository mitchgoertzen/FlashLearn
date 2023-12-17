package com.learn.flashLearnTagalog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {


    private val mutableRefreshCallback = MutableLiveData<() -> Unit>()
    val currentRefreshCallback: MutableLiveData<() -> Unit> get() = mutableRefreshCallback


    private val mutableRefreshActive = MutableLiveData<Boolean>()
    val isRefreshActive: MutableLiveData<Boolean> get() = mutableRefreshActive

    private val mutableCallback = MutableLiveData<() -> Unit>()
    val currentCallback: LiveData<() -> Unit> get() = mutableCallback

    fun updateCallback(c: () -> Unit) {
        mutableCallback.value = c
    }

    fun updateRefreshCallback(c: () -> Unit) {
        mutableRefreshCallback.value = c
    }

    fun updateRefreshActive(b: Boolean) {
        mutableRefreshActive.value = b
    }

}