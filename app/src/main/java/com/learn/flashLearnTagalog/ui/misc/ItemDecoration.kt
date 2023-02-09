package com.learn.flashLearnTagalog.ui.misc

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = spacing / 2
        outRect.left = spacing / 3
        outRect.right = spacing / 3
        outRect.bottom = spacing / 2
    }

}