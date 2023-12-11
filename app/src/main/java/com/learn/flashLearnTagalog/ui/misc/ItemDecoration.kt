package com.learn.flashLearnTagalog.ui.misc

import android.content.ContentValues.TAG
import android.graphics.Rect
import android.util.Log
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

        if (parent.getChildAdapterPosition(view) % 2 == 0) {
            outRect.left = spacing / 6
            outRect.right = spacing
        } else {
            outRect.left = spacing
            outRect.right = spacing / 6
        }
        outRect.bottom = spacing / 2


    }

}