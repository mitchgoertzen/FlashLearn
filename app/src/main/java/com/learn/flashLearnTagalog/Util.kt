package com.learn.flashLearnTagalog

import android.widget.Button

class Util {

    companion object {
        fun handleButtonEnable(btn: Button, enable: Boolean) {
            btn.isEnabled = enable
            btn.alpha = if (enable) 1f else 0.5f
        }
    }
}